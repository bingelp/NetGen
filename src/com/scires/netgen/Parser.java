package com.scires.netgen;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Justin on 2/19/14.
 */
public class Parser {
	private static String SPACE = "\\s";
	private static String IP_GENERIC = "[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}";
	private static String IP_HOST = ".*192\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[1-9]{1,3}.*";
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
	private String[] files = null;
	private Device d = null;
	private LineNumberReader reader = null;
	private FileOutputStream writer = null;
	private int fileIndex = 0;
	private String domainName = null;
	private String nameServer = null;
	private String secret = null;
	private String username = null;
	private String password = null;
	private String vtpPwd = null;
	private String loggingServer = null;
	private ArrayList<Device> devices = null;
	private Map<String, ArrayList<int[]>> IPs = null;
	private ArrayList<int[]> IP = null;

	public Parser(String d){
		this.directory = new File(d);
		this.files = directory.list();
		this.devices = new ArrayList<Device>();
		this.IPs = new HashMap<String, ArrayList<int[]>>();
	}

	public ArrayList<Device> getDevices(){
		return this.devices;
	}
	public Map<String, ArrayList<int[]>> getIPs(){
		return this.IPs;
	}

	public void processFiles(){
		this.fileIndex = 0;
		String text;
		String hostname;
		int command;
		for ( String s : files){
			try{
				File f = new File(this.directory + s);
				this.reader = new LineNumberReader(new FileReader(f));
				hostname = f.getName().split("-")[0];
				this.d = new Device(hostname);
				while ((text = this.reader.readLine()) != null){
					text=text.trim();
					if(!isComment(text) && (command = getCommand(text)) != -1){
						switch(command){
							case 0: processDomainName(text);
									break;
							case 1: processNameServer(text);
									break;
							case 2: processSecret(text);
									break;
							case 3: processCredentials(text);
									break;
							case 4: processKeyChain(text);
									break;
							case 5: processVTPPwd(text);
									break;
							case 6: processInterface(text);
									break;
							case 7: processRouter(text);
									break;
							case 8: processLogging(text);
									break;
							case 9: processAccessList(text);
									break;
							case 10:processNTP(d, text);
									break;
						}
					}
				}
			} catch (Exception e){
				System.out.println("Error: " + e.getMessage());
			} finally {try{if (this.reader != null)this.reader.close();} catch (IOException e){}}

			if (d != null)
				this.devices.add(d);

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

	private void processDomainName(String line){
		if(this.domainName == null)
			this.domainName = line.split(SPACE)[2];}
	private void processNameServer(String line){
		if(this.nameServer == null)
			this.nameServer = line.split(SPACE)[2];}
	private void processSecret(String line){
		if(this.secret == null)
			this.secret = line.split(SPACE)[2];}
	private void processCredentials(String line){
		if(this.username == null && this.password == null){
			String[] split = line.split(SPACE);
			this.username = split[1];
			this.password = split[3];}}
	private void processKeyChain(String line){
		KeyChain kc = new KeyChain(line.split(SPACE)[2]);
		Key k = null;
		String[] split;
		String[] commands = {"key", "key-string", "accept-lifetime", "send-lifetime"};
		try{
			while ((line = this.reader.readLine()).startsWith(" ")){
				line = line.trim();
				split = line.split(SPACE);
				this.reader.mark(BUFFER_SIZE);
				if(line.startsWith(commands[1])){
					k.setKeyString(split[1]);
				}else if(line.startsWith(commands[0])){
					k = new Key (Integer.valueOf(split[1]));
				}else if(line.startsWith(commands[2]) || split[0].matches(commands[3])){
					String time = line.substring(split[0].length());
					if(line.startsWith(commands[2]))
						k.setAcceptLifetime(time);
					else{
						k.setSendLifetime(time);
						kc.addKey(k);
					}
				}
			}
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
		}finally {
			this.d.addKeyChain(kc);
			kc=null;
			try{
				this.reader.reset();
			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	private void processInterface(String line){
		Interface i = null;
		String name = line.split(SPACE)[1];
		String[] split = null;
		String[] commands = {"ip address"};
		try{
			while ( (line = this.reader.readLine()).startsWith(" ") || line.trim().startsWith(commands[0]) ){
				line = line.trim();
				split = line.split(SPACE);
				this.reader.mark(BUFFER_SIZE);
				if ( line.startsWith(commands[0]) ){
					i = new Interface(name);
					i.setIP(split[2]);
					i.setMask(split[3]);
				}
			}
		} catch(Exception e){
			System.out.println("Error: " + e.getMessage());
		} finally{
			if( i != null){
				this.d.addInterface(i);
				i=null;
				try{
					this.reader.reset();
				} catch(Exception e){
					System.out.println("Error: " + e.getMessage());
				}
			}
		}

	}
	private void processRouter(String line){
		Router rr = new Router(line.split(SPACE)[2]);
		Route r = null;
		String[] commands = {"network", "ip route"};
		String[] split = null;
		try{
			while( (line = this.reader.readLine()).startsWith(" ") ){
				line = line.trim();
				split = line.split(SPACE);
				this.reader.mark(BUFFER_SIZE);
				if ( line.startsWith(commands[0]) ){
					rr.addNetwork(split[1]);
				}else if( line.startsWith(commands[1]) ){
					rr.addRoute(new Route(split[2], split[3], split[4]));
				}
			}
		} catch(Exception e){
			System.out.println("Error: " + e.getMessage());
		} finally{
			this.d.addRouter(rr);
			rr=null;
			try{
				this.reader.reset();
			} catch(Exception e){
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	private void processVTPPwd(String line){
		if(this.vtpPwd == null)
			this.vtpPwd = line.split(SPACE)[2];}
	private void processLogging(String line){
		if(this.loggingServer == null){
			line = line.split(SPACE)[1];
			if(line.matches(IP_GENERIC))
				this.loggingServer = line;}}
	private void processAccessList(String line){
		if(line.matches(IP_HOST)){
			String[] split = line.split(SPACE);
			for ( String word : split){
				if( word.matches(IP_HOST)){
					ArrayList<int[]> locations = null;
					int lineIndex = line.indexOf(word);
					int lineNumber = reader.getLineNumber();
					int[] location = {fileIndex, lineNumber, lineIndex};
					if (this.IPs.containsKey(word)){
						locations = this.IPs.get(word);
					}else{
						locations = new ArrayList<int[]>();
					}
					locations.add(location);
					this.IPs.put(word, locations);
				}
			}
		}
	}
	private void processNTP(Device d, String line){
		String[] split = line.split(SPACE);
		NTP ntp = new NTP(split[2], Integer.valueOf(split[4]));
		d.addNTP(ntp);
	}

	public void generate(String[] newIPs){
		//Delete files in Generated folder
		File generatedDirectory = new File(this.directory + "\\Generated");
		if(generatedDirectory.exists()){
			String[] files = generatedDirectory.list();
			for(String file: files){
				new File(generatedDirectory.getPath(), file).delete();
			}
		}


		Object[] IPs_a = IPs.keySet().toArray();
		for(int i=0; i<this.IPs.size(); i++){
			int ii = i*2;
			this.IP=this.IPs.get(newIPs[ii]);
			String line = null;

			for(int[] entry : this.IP){
				try {
					String outDirPath = this.directory + "\\Generated";
					String outFilePath = outDirPath + "\\" + this.files[entry[0]];
					File outDir = new File(outDirPath);

					boolean dirExists = true;
					if (!outDir.exists()) {
						dirExists = outDir.mkdir();
					}
					if (dirExists) {
						File outFile = new File(outFilePath);
						if (outFile.exists())
							this.reader = new LineNumberReader(new FileReader(outFilePath));
						else
							this.reader = new LineNumberReader(new FileReader(this.files[entry[0]]));

						this.writer = new FileOutputStream(outFilePath+".tmp");

						for (int linesSkipped = 0; linesSkipped < entry[1] - 1; linesSkipped++)
							this.writer.write((this.reader.readLine() + '\n').getBytes());

						line = this.reader.readLine() + '\n';
						line = line.replace(newIPs[ii], newIPs[ii + 1]);
						this.writer.write(line.getBytes());
						while ((line = this.reader.readLine()) != null) {
							this.writer.write((line + '\n').getBytes());
						}
						this.writer.flush();
						this.writer.close();
						this.reader.close();
						if (outFile.exists())
							outFile.delete();
						new File(outFilePath+".tmp").renameTo(outFile);
					}
				} catch (Exception e) {
				}
			}
		}
	}
}
