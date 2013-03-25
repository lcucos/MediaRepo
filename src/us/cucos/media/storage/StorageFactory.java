package us.cucos.media.storage;

import java.util.Properties;

import us.cucos.media.utils.CmdArgs;
import us.cucos.media.utils.MyLog;

public class StorageFactory {
	public static final String APP   = "app";
	public static final String MONGO = "mongo";
	public static final String MEM   = "mem";
	

	public static String normalizeRepo(String repo, int intProperty) {
		if(repo==null || repo.equals(MEM)){
			return MEM;
		}
		int atPos = repo.indexOf('@');
		if(repo.startsWith(MONGO)&&atPos==-1){
			repo = MONGO+"@localhost";
		}else if(repo.startsWith(APP)){
			if(atPos ==-1){
				repo = APP+"@localhost:"+CmdArgs.DEFAULT_APP_PORT;
			}else if(repo.indexOf(':')==-1){
				repo = repo + ":"+CmdArgs.DEFAULT_APP_PORT;
			}
		}else{
			return null;
		}
		
		return repo;
	}
	private String getRepoAt(String repo){
		if(repo==null || repo.equals(MEM)){
			return MEM;
		}
		int atPos = repo.indexOf('@');
		if(atPos >-1){
			repo = repo.substring(atPos+1);
		}
		
		return repo;
	}
	
	public IMetaDataStorage getStorage(Properties props){
		String propRepo = props.getProperty(CmdArgs.STORAGE);
		
		if(propRepo==null || propRepo.equalsIgnoreCase(MEM)){
			DataStorageMem storage = new DataStorageMem();
			if(!storage.init(props)){
				MyLog.e("Unable to initialize Memory storage!");
				return null;
			}
			return storage;
		}
		if(propRepo.startsWith(MONGO)){
			MongoDBStorage storage = new MongoDBStorage();
			if(! storage.init(getRepoAt(propRepo), props)){
				MyLog.e("Unable to initialize APP connection!");
				return null; 
			}
			return storage;
		}else if(propRepo.startsWith(APP)){
			RemoteStorage storage = new RemoteStorage();
			
			if(! storage.init(getRepoAt(propRepo))){
				MyLog.e("Unable to initialize APP connection!");
				return null; 
			}
			
			return storage;
		}
		return null;
	}

}
