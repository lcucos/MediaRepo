package us.cucos.media.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import us.cucos.media.storage.StorageFactory;


public class CmdArgs {
	private String configFile = null;
	
	private Properties props = new Properties();

	public static final String DEFAULT_CONFIG_FILE = "configPhotosImport.txt ";
	public static final String IMPORT_FOLDER = "importFolder";
	public static final String MASTER_FOLDER = "masterFolder";
	public static final String NEWFILES_FOLDER = "newFilesFolder";
	public static final String STORAGE        = "repo";
	public static final String SERVER_FLAG    = "isServer";

	public static final String SERVER_PORT   = "port";

	public static final int DEFAULT_APP_PORT = 27056;
	
	public String getProperty(String name){
		return props.getProperty(name);
	}
	
	public boolean init(String args[]){
		if(!readConfigFile(args)){
			return false;
		}

		for(int i=0; i< args.length;i++){
			String arg = args[i];
			if(arg.equals("-h")){
				printHelp();
				return false;
			}
			// all the following need to have a following parameter
			if(arg.equals("-f")){
				if(i < args.length){
					props.put(IMPORT_FOLDER, args[++i]);
				}else{
					MyLog.e("Missing folder name after: -f");
					return false;
				}
			}else if(arg.equals("-r")){
				if(i < args.length){
					props.put(STORAGE, args[++i]);
				}else{
					MyLog.e("Missing server name after: -s");
					return false;
				}				
			}else if(arg.equals("-S")){
				props.put(SERVER_FLAG, "true");
			}
		}
		// some validation
		
		String repo = getProperty(STORAGE);
		repo = StorageFactory.normalizeRepo(getProperty(STORAGE), this.getIntProperty(SERVER_PORT, DEFAULT_APP_PORT));
		if(repo==null){
			MyLog.d("Invalid Storage: "+getProperty(STORAGE));
			return false;
		}
		this.props.setProperty(STORAGE, repo);
		
		return true;
	}

	private boolean readConfigFile(String[] args) {
		for(int i=0;i<args.length;i++){
			String arg = args[i];
			if(arg.equals("-c")){
				if(i < args.length){
					configFile = args[++i];
				}else{
					MyLog.e("Missing config file after: -c");
					return false;
				}				
			}
		}
		if(configFile!=null){
			File fConfig = new File(configFile);
			if(!fConfig.exists()){
				MyLog.e("Unable to read config File: " + configFile);
				return false;
			}
			return loadConfig(fConfig);
		}
		
		File fConfig = new File(getHomeFolder()+ File.separator + DEFAULT_CONFIG_FILE);
		
		if(fConfig.exists()){
			return loadConfig(fConfig);			
		}		
		return true;
	}

	private String getHomeFolder(){
		Map<String, String> env = System.getenv();
		String home = env.get("HOME");
		return home;
	}
	private boolean loadConfig(File fConfig) {
		try {
			MyLog.d("Read configuration file: " + fConfig.getAbsolutePath());
			props.load(new FileInputStream(fConfig));			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	private void printHelp() {
		System.out.println("Options: \n" +
				"   -h                : Print this help\n" +
				"   -f <folder>       : Scan folder <folder>\n" + 
				"   -S                : Start server\n" +
				"   -r <repo>         : Connect to repository <repo>\n" +
				"                       where <repo> can be:\n" +
				"                        a). 'mem'\n" +
				"                        b). 'mongo[@<server[:port>]] (default localhost:27017)'\n" +
				"                        c). 'app[@<server[:port>]] (default localhost:"+DEFAULT_APP_PORT+")'\n"+ 
				"   -c <configFile>   : Use configuration file <configFile>\n" +
				"                       (default: "+getHomeFolder()+File.separator+DEFAULT_CONFIG_FILE+")\n" +
				"");
	}

	public Properties getProperties() {
		return this.props;
	}

	public int getIntProperty(String prop, int defaultValue) {
		String strProp = getProperty(prop);
		if(strProp == null){
			return defaultValue;
		}
		try{
			int iProp = Integer.parseInt(strProp);
			return iProp;
		}catch(Exception ex){
			MyLog.e("Invalid integer value for '" + prop + "' : "+ strProp);
		}
		return defaultValue;
	}

	public boolean getBooleanProperty(String prop, boolean defaultValue) {
		String strProp = getProperty(prop);
		if(strProp == null){
			return defaultValue;
		}
		try{
			Boolean iProp = Boolean.parseBoolean(strProp);
			return iProp;
		}catch(Exception ex){
			MyLog.e("Invalid boolean value for '" + prop + "' : "+ strProp);
		}
		return defaultValue;
	}
}
