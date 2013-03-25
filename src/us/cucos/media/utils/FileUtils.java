package us.cucos.media.utils;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import us.cucos.media.storage.IMetaDataStorage;


public class FileUtils {
	
	public static JSONObject getFileInfo(File file){
		// TODO Auto-generated method stub
		JSONObject obj = new JSONObject();
		obj.put(IMetaDataStorage.FILE_NAME_TAG, file.getName());
		obj.put(IMetaDataStorage.FILE_LENGTH_TAG, file.length());
		/*
		try {			
			obj.put(IMetaDataStorage.FILE_PATH_TAG, file.getCanonicalPath());
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		String id = getFileUID(file);
		obj.put(IMetaDataStorage.ID_TAG, id);
		return obj;
	}
	
	public static String getFileUID(File file){
		return file.getName() + ":"+file.length();

	}
}
