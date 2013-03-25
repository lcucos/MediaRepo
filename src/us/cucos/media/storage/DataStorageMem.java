package us.cucos.media.storage;
import java.io.File;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import us.cucos.media.processors.FolderScanner;
import us.cucos.media.processors.FolderStatistics;
import us.cucos.media.utils.CmdArgs;
import us.cucos.media.utils.MyLog;



public class DataStorageMem implements IMetaDataStorage{
	private HashMap<String, JSONObject> files = new HashMap<String, JSONObject>();
		
	@Override
	public void loadFileInfo(JSONObject obj) {
		// TODO Auto-generated method stub
		files.put(obj.getString(ID_TAG), obj);
	}

	@Override
	public boolean checkFile(JSONObject obj) {
		String id = obj.getString(ID_TAG);
		// double check 
		return files.get(id)!=null;
	}

	@Override
	public JSONObject bulkCheckFiles(JSONArray arrFiles) {
		JSONObject outObj = new JSONObject();
		for(int i=0;i<arrFiles.length();i++){
			JSONObject tmpObj = arrFiles.getJSONObject(i);
			String id = tmpObj.getString(ID_TAG);
			outObj.put(id, checkFile(tmpObj));
		}
		return outObj;
	}

	public boolean init(Properties props) {		
		String masterFolder= props.getProperty(CmdArgs.MASTER_FOLDER);
		if(masterFolder==null){
			MyLog.e("Missing master folder");
			return false;
		}
		MyLog.d("Use: MemoryStore");
		FolderScanner scanner = new FolderScanner();		
		FolderStatistics fStats = scanner.loadFolder(masterFolder, this);
		MyLog.d("Master Folder: " + fStats!=null?fStats.toString():"");
		return true;
	}

}
