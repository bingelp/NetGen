package com.scires.netgen;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Justin on 3/7/14.
 *
 * <P>Parser Thread to generate gui based on CISCO config files</P>
 *
 * @author Justin Robinson
 * @version 0.0.2
 *
 */
public class ParserWorker extends SwingWorker<Integer, Integer> {

	public static String SPACE = "\\s";
	public static String ERROR = "Error: ";
	private static int BUFFER_SIZE = 1000;
	private static String[] COMMAND_LIST =
			{
					"ip domain-name",
					"ip name-server",
					"enable secret",
					"username",
					"key chain",
					"vtp password",
					"interface",
					"router",
					"logging",
					"access-list",
					"ntp peer",
					"hostname"
			};
	private File directory = null;
	private String inputPath;
	private String fileName = null;
	public String[] files = null;
	private LineNumberReader reader = null;
	private int fileIndex = 0;
	public Map<String, ContainerPanel> containers = null;
	public ContainerPanel cp = null;
	ProgressWindow progressWindow;

	public ParserWorker(String d, ProgressWindow progressWindow){
		inputPath = d;
		containers = new HashMap<>();
		directory = new File(inputPath);
		this.progressWindow = progressWindow;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		if(this.directory.isDirectory()){
			directory = new File(inputPath);
			cleanDirectory();
			files=directory.list();
			progressWindow.reset(this.files.length);
			processDirectory();
		}
		else{
			directory = new File(inputPath);
			files = new String[]{directory.getName()};
			directory = directory.getParentFile();
			progressWindow.reset(this.files.length);
			processFile(new File(inputPath).getName());
		}


		return 1;
	}

	private void cleanDirectory(){
		File generatedDirectory = new File(this.directory + "\\Generated");
		if(generatedDirectory.exists()){
			String[] files = generatedDirectory.list();
			for(String file: files){
				boolean result = new File(generatedDirectory.getPath(), file).delete();
				if(!result)
					System.out.println("Error deleting file");
			}
			boolean success = generatedDirectory.delete();
			if(!success)
				System.out.println("Error deleting Generated directory");
		}
	}
	private void processDirectory(){
		this.fileIndex = 0;
		for ( String s : files){
			processFile(s);
		}
	}
	private void processFile(String filePath){
		try{
			String text;
			File f = new File(this.directory + "\\" + filePath);
			fileName = f.getName().split("-")[0];
			this.reader = new LineNumberReader(new FileReader(f));
			this.reader.skip(Long.MAX_VALUE);
			this.reader = new LineNumberReader(new FileReader(f));

			while ((text = this.reader.readLine()) != null){
				text=text.trim();
				if(!isComment(text)){
					switch(getCommand(text)){
						case 0: processGlobal(text, "Domain Name", ElementPanel.NOT_BLANK);
							break;
						case 1: processGlobal(text, "Name Server", ElementPanel.IP_HOST);
							break;
						case 2: processGlobal(text, "Secret", ElementPanel.COC_PWD);
							break;
						case 3: processCredentials(text);
							break;
						case 4: processKeyChain();
							break;
						case 5: processGlobal(text, "VTP Password", ElementPanel.NOT_BLANK);
							break;
						case 6: processInterface(text);
							break;
						case 7: processRouter(text);
							break;
						case 8: processLogging(text);
							break;
						case 9: processAccessList(text);
							break;
						case 10:processNTP(text);
							break;
						case 11:processHostName(text);
							break;
						default:
							break;
					}
				}
			}
		} catch (Exception e){
			System.out.println("Command: " + fileIndex + "." + reader.getLineNumber() + " " + ERROR + e.getMessage());
		} finally {
			try{
				if (this.reader != null)
					this.reader.close();
			} catch (IOException e){
				System.out.println(ERROR + e.getMessage());
			}
		}
		//publish(fileIndex);
		setProgress(++fileIndex);
	}

	private int getCommand(String line){
		int out = -1;
		for( int i=0; i< COMMAND_LIST.length; i++){
			if (line.startsWith(COMMAND_LIST[i])){
				out = i;
				i = COMMAND_LIST.length;
			}
		}
		return out;
	}
	private boolean isComment(String line){
		boolean out = false; if (line.startsWith("!")){ out = true;} return out;}

	private void processGlobal(String line, String labelText, String regex){
		markElement(null, line.split(SPACE)[2], labelText, labelText, regex);
		containerize(null, ContainerPanel.GLOBAL);
	}
	private void processCredentials(String line){
		String[] split = line.split(SPACE);
		markElement(null, split[1], "Username", "Username", ElementPanel.NOT_BLANK);
		markElement(null, split[3], "Password", "Password", ElementPanel.COC_PWD);
		containerize(null, ContainerPanel.GLOBAL);
	}
	private void processKeyChain(){
		String line;
		String[] split;
		String[] commands = {"key ", "key-string", "accept-lifetime", "send-lifetime"};
		try{

			String keyNumber = "";
			while ((line = this.reader.readLine()).startsWith(" ")){
				line = line.trim();
				split = line.split(SPACE);
				this.reader.mark(BUFFER_SIZE);
				//key
				if(line.startsWith(commands[0])){
					keyNumber = "key " + split[1];
					//key-string
				}else if(line.startsWith(commands[1])){
					keyNumber += " - " +split[1];
					//accept-lifetime  send-lifetime
				}else if(line.startsWith(commands[2]) || split[0].matches(commands[3])){
					//parse out the start and end times
					Matcher m = Pattern.compile(ElementPanel.KEY_TIME)
							.matcher(line);
					String startDate=null, endDate=null;
					if( m.find() ){
						startDate = m.group();}
					if ( m.find() ){
						endDate = m.group();}

					//make gui elements for both fields
					if(startDate != null && endDate != null){
						markElement(new RouterDatePicker(), startDate, "Begin Life", "Start Life", ElementPanel.KEY_TIME);
						markElement(new RouterDatePicker(), endDate, "End Life", "End Life", ElementPanel.KEY_TIME);
						containerize(keyNumber, ContainerPanel.KEY_CHAIN);
					}
				}
			}
		}catch(Exception e){
			System.out.println("KeyChain Read:" + ERROR + e.getMessage());
		}finally {
			try{
				this.reader.reset();
			}catch(Exception e){
				System.out.println("KeyChain Reader Reset:" + ERROR + e.getMessage());
			}
		}
	}
	private void processInterface(String line){
		String name = line.split(SPACE)[1];
		String[] split;
		String[] commands = {"ip address"};
		boolean addedInterface = false;
		try{
			while ( (line = this.reader.readLine()).startsWith(" ") || line.trim().startsWith(commands[0]) ){
				line = line.trim();
				split = line.split(SPACE);
				this.reader.mark(BUFFER_SIZE);
				if ( line.startsWith(commands[0]) ){
					markElement(null, split[2], name, name, ElementPanel.IP_HOST);
					addedInterface=true;
				}
			}
		} catch(Exception e){
			System.out.println("Interface: " + ERROR + e.getMessage());
		} finally{
			if( addedInterface ){
				containerize(fileName, ContainerPanel.INTERFACE);
				try{
					this.reader.reset();
				} catch(Exception e){
					System.out.println("Interface: Reset: " + ERROR + e.getMessage());
				}
			}
		}

	}
	private void processRouter(String line){
		String routerNumber = fileName + " - " + line.split(SPACE)[2];
		String[] commands = {"network", "ip route"};
		String[] split;
		try{
			while( (line = this.reader.readLine()).startsWith(" ") ){
				line = line.trim();
				split = line.split(SPACE);
				this.reader.mark(BUFFER_SIZE);
				if ( line.startsWith(commands[0]) ){
					markElement(null, split[1],commands[0],commands[0]+split[1],ElementPanel.IP_GATEWAY);
				}else if( line.startsWith(commands[1]) ){
					String ip = split[split.length-1];
					markElement(null, ip, commands[1], commands[1], ElementPanel.IP_HOST);
				}
			}
			containerize(routerNumber, ContainerPanel.ROUTER);
		} catch(Exception e){
			System.out.println(ERROR + e.getMessage());
		} finally{
			try{
				this.reader.reset();
			} catch(Exception e){
				System.out.println(ERROR + e.getMessage());
			}
		}
	}
	private void processLogging(String line){
		line = line.split(SPACE)[1];
		if(line.matches(ElementPanel.IP_GENERIC)){
			markElement(null, line, "Logging Server", "Logging Server", ElementPanel.IP_HOST);
			containerize(null, ContainerPanel.GLOBAL);
		}
	}
	private void processAccessList(String line){
		if(line.matches(ElementPanel.IP_GENERIC_LINE)){
			String[] split = line.split(SPACE);
			final String permit = "permit";
			final String deny	= "deny";
			final String PERMIT_DENY = permit + "|" + deny;
			for(int i=0; i<split.length; i++){
				String word = split[i];
				//host
				if( word.matches(PERMIT_DENY) ){
					boolean checked;
					String target;
					if(word.matches(permit)){
						checked=true;
						target=permit;
					}
					else{
						checked=false;
						target=deny;
					}
					markElement(new JCheckBox("", checked), target, null, PERMIT_DENY, null);
				}
				else if( word.matches("host") ){
					markElement(null, split[++i], "host", "host" + reader.getLineNumber(), ElementPanel.IP_GENERIC);
				}
				//network and subnet
				else if( word.matches(ElementPanel.IP_GENERIC) ){
					markElement(null, word, "network", "network" + reader.getLineNumber(), ElementPanel.IP_GENERIC);
					markElement(null, split[i++], "wildcard", "wildcard" + reader.getLineNumber(), ElementPanel.IP_GENERIC);
				}
			}
			containerize(fileName, ContainerPanel.ACCESS_LIST);
		}
	}
	private void processNTP(String line){
		String[] split = line.split(SPACE);
		markElement(null, split[2], "Peer", "Peer" + split[2], ElementPanel.IP_GENERIC);
		containerize(null, ContainerPanel.NTP_PEER);
	}
	private void processHostName(String line){
		markElement(null, line.split(SPACE)[1], fileName, fileName, ElementPanel.NOT_BLANK);
		containerize(null, ContainerPanel.HOST_NAME);
	}


	private void markElement(JComponent component, String target, String labelText, String type, String regex){
		if(component == null)
			component = new JTextField(15);
		Location l = new Location();
		l.setFileIndex(fileIndex);
		l.setLineNumber(reader.getLineNumber());
		ElementPanel ep = new ElementPanel(component, labelText, l, target, regex);
		if(ep.isText()){
			JTextField tf = (JTextField)ep.component;
			tf.setText(target);
		}
		else if( ep.isDate() ){
			RouterDatePicker rdp = (RouterDatePicker)ep.component;
			rdp.makeTimeFromRouter(target);
		}
		if(this.cp == null)
			this.cp = new ContainerPanel();
		this.cp.elements.put(type, ep);
		this.cp.add(this.cp.elements.get(type));
	}
	private void containerize(String group, int tab){
		String key = this.cp.elements.keySet().toString();
		if ( group != null )
			key += group;
		cp.group = group;
		cp.tab = tab;
		if(!this.containers.containsKey(key))
			this.containers.put(key, this.cp);
		else{
			ContainerPanel storedCP = this.containers.get(key);
			for(Map.Entry<String, ElementPanel> elementPanelEntry: cp.elements.entrySet()){
				String elementKey = elementPanelEntry.getKey();
				Location l = elementPanelEntry.getValue().locations.get(0);
				storedCP.elements.get(elementKey).locations.add(l);
			}
		}

		this.cp=null;
	}
}
