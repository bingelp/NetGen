package com.scires.netgen;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Justin on 2/19/14.
 *
 * <P>Takes directory from {@link com.scires.netgen.IPGUI } and parses config files for useful
 * Cisco configuration data</P>
 *
 * @author Justin Robinson
 * @version 0.0.3
 */
public class Parser {
	private static String SPACE = "\\s";
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
					"ntp peer"
			};
	private File directory = null;
	private String fileName = null;
	private String[] files = null;
	private LineNumberReader reader = null;
	private int fileIndex = 0;
	private Map<String, Entry> updateables = null;

	public Parser(String d){
		this.directory = new File(d);
		this.files = directory.list();
		this.updateables = new HashMap<String, Entry>();
	}

	public String[] getFiles(){ return this.files; }
	public Map<String, Entry> getUpdateables(){return this.updateables;}

	public void processFiles(){
		this.fileIndex = 0;
		String text;
		int command;
		for ( String s : files){
			try{
				File f = new File(this.directory + "\\" + s);
				fileName = f.getName().split("-")[0];
				this.reader = new LineNumberReader(new FileReader(f));
				while ((text = this.reader.readLine()) != null){
					text=text.trim();
					if(!isComment(text) && (command = getCommand(text)) != -1){

						switch(command){
							case 0: processGlobal(text, "Domain Name", null);
									break;
							case 1: processGlobal(text, "Name Server", Entry.IP_HOST);
									break;
							case 2: processGlobal(text, "Secret", Entry.COC_PWD);
									break;
							case 3: processCredentials(text);
									break;
							case 4: processKeyChain();
									break;
							case 5: processGlobal(text, "VTP Password", null);
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
						}
					}
				}
			} catch (Exception e){
				System.out.println(ERROR + e.getMessage());
			} finally {try{if (this.reader != null)this.reader.close();} catch (IOException e){System.out.println(ERROR + e.getMessage());}}
			this.fileIndex++;
		}
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
		addLocation(line.split(SPACE)[2], Location.GLOBAL, labelText, null, regex);
	}
	private void processCredentials(String line){
		String[] split = line.split(SPACE);
		addLocation(split[1], Location.GLOBAL, "Username", null, null);
		addLocation(split[3], Location.GLOBAL, "Password", null, Entry.COC_PWD);
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
				if(line.startsWith(commands[0])){
					keyNumber = "key " + split[1];
				}else if(line.startsWith(commands[1])){
					keyNumber += " - " +split[1];
				}else if(line.startsWith(commands[2]) || split[0].matches(commands[3])){
					String labelText;
					if(line.startsWith(commands[2]))
						labelText = commands[2];
					else
						labelText = commands[3];
					addLocation(line.substring(line.indexOf(' ')+1, line.length()), Location.KEY_CHAIN, labelText, keyNumber, Entry.KEY_TIME);
				}
			}
		}catch(Exception e){
			System.out.println(ERROR + e.getMessage());
		}finally {
			try{
				this.reader.reset();
			}catch(Exception e){
				System.out.println(ERROR + e.getMessage());
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
					addLocation(split[2], Location.INTERFACE, name, fileName, Entry.IP_HOST);
					addedInterface=true;
				}
			}
		} catch(Exception e){
			System.out.println(ERROR + e.getMessage());
		} finally{
			if( addedInterface ){
				try{
					this.reader.reset();
				} catch(Exception e){
					System.out.println(ERROR + e.getMessage());
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
					addLocation(split[1],Location.ROUTER,commands[0],routerNumber, Entry.IP_GATEWAY);
				}else if( line.startsWith(commands[1]) ){
					String ip = split[split.length-1];
					addLocation(ip, Location.ROUTER,commands[1],routerNumber, Entry.IP_HOST);
				}
			}
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
			if(line.matches(Entry.IP_GENERIC))
				addLocation(line, Location.GLOBAL, "Logging Server", null, null);
	}
	private void processAccessList(String line){
		if(line.matches(Entry.IP_GENERIC_LINE)){
			String[] split = line.split(SPACE);
			//final String PERMIT_DENY = "permit|deny";
			for(int i=0; i<split.length; i++){
				String word = split[i];
/*				if( word.matches(PERMIT_DENY) ){
					//System.out.println(word);
				}*/
				if( word.matches(Entry.IP_GENERIC) ){
					addLocation(word, Location.ACCESS_LIST, split[2], fileName, Entry.IP_GENERIC);
					i=split.length;
				}
			}
		}
	}
	private void processNTP(String line){
		String[] split = line.split(SPACE);
		addLocation(split[2], Location.NTP_PEER, "Peer", fileName, null);
	}

	private void addLocation(String target, int tab, String labelText, String group, String regex){
		Location location = new Location();
		location.setFileIndex(fileIndex);
		location.setLineNumber(reader.getLineNumber());
		location.setTab(tab);
		location.setGroup(group);
		Entry entry;
		if(this.updateables.containsKey(target+labelText)){
			entry = this.updateables.get(target+labelText);
			entry.locations.add(location);
		}else{
			entry = new Entry();
			entry.setTarget(target);
			entry.setLabelText(labelText);
			entry.setRegex(regex);
			entry.locations.add(location);
		}
		this.updateables.put(target+labelText, entry);
	}

}
