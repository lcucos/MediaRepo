package us.cucos.media.storage;

import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

public class MongoDBStorage implements IMetaDataStorage{

	@Override
	public void loadFileInfo(JSONObject obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean checkFile(JSONObject file) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JSONObject bulkCheckFiles(JSONArray arrFiles) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean init(String repoAt, Properties props) {
		// TODO Auto-generated method stub
		return false;
	}

}
