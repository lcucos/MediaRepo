package us.cucos.media.storage;
import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;


public interface IMetaDataStorage {
	
	public static final String FILE_NAME_TAG   = "fname";
	public static final String FILE_LENGTH_TAG = "fsize";
	public static final String ID_TAG = "fid";
	public static final String FILE_PATH_TAG = "fpath"; 

	
	public void loadFileInfo(JSONObject obj);
	
	public boolean checkFile(JSONObject file);
	
	public JSONObject bulkCheckFiles(JSONArray arrFiles); 
}
