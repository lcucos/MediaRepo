package us.cucos.media.processors;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.eclipse.jetty.util.ajax.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

import us.cucos.media.storage.IMetaDataStorage;
import us.cucos.media.utils.FileUtils;
import us.cucos.media.utils.MyLog;


public class FolderScanner {

	class MyFileFilter implements FilenameFilter{
		@Override
		public boolean accept(File dir, String name) {
			String nameCI = name.toLowerCase();
			File subfold = new File(dir, name);
			if( nameCI.endsWith(".jpg") || nameCI.endsWith(".png") || nameCI.endsWith(".tiff") || 
				nameCI.endsWith(".mts") || nameCI.endsWith(".mov") || nameCI.endsWith(".mp4") 
					|| subfold.isDirectory()){
				return true;
			}
			return false;
		}		
	}
	
	MyFileFilter filter = new MyFileFilter();
	
	public FolderScanner(){
		
	}
	
	public FolderStatistics loadFolder(String folderName, IMetaDataStorage storage){
		File folder =  new File(folderName);
		FolderStatistics fStats = new FolderStatistics(folder);
		
		if(!folder.isDirectory()){
			MyLog.e("Please specify a folder to scan for media files: " + folderName);
			return null;
		}
		File files[]  = folder.listFiles(filter);
		if(files==null || files.length==0){
			MyLog.d("Folder with no media files: " + folder.getAbsolutePath());
			return null;
		}
		MyLog.d("Scan folder: " + folder.getAbsolutePath());
		ArrayList<File> subfolders = new ArrayList<File>();
		JSONArray newFiles = new JSONArray(); 
		bulkCheckFiles(storage, files, subfolders, newFiles, fStats);
		for(int i=0;i<newFiles.length();i++){
			storage.loadFileInfo(newFiles.getJSONObject(i));
		}
		if(newFiles.length()==files.length){
			MyLog.d("All new media files in folder: " + folderName);
		}else if(newFiles.length()>0){
			MyLog.d("Found new media files in folder: " + folderName);
			for(int i=0;i<newFiles.length();i++){
				MyLog.d(newFiles.getJSONObject(i).getString(IMetaDataStorage.FILE_NAME_TAG));
			}
		}
		// scan the subfolders 
		for(File file : subfolders){
			FolderStatistics fSubFStats = loadFolder(file.getAbsolutePath(), storage);
			if(fSubFStats!=null){
				fStats.addFile(fSubFStats);
			}
		}
		return fStats;
	}

	private void bulkCheckFiles(IMetaDataStorage storage, File[] files, ArrayList<File> subfolders,
			JSONArray newFiles, FolderStatistics fStats) {
		// TODO Auto-generated method stub
		JSONArray tmpArr = new JSONArray(); 
		for(File file : files){			
			if(file.isDirectory()){
				subfolders.add(file);
				continue;
			}
			
			JSONObject objFile = FileUtils.getFileInfo(file);
			tmpArr.put(objFile);
		}
		JSONObject objOut = storage.bulkCheckFiles(tmpArr);
		for(int i=0;i<tmpArr.length();i++){
			JSONObject tmpObj = ((JSONObject)tmpArr.get(i)); 
			String id = tmpObj.getString(IMetaDataStorage.ID_TAG);
			if(objOut.has(id)){
				Boolean bExists = (Boolean)objOut.get(id);
				if(!bExists){
					newFiles.put(tmpObj);
					fStats.addFile(files[i]);
				}else{
					fStats.fileExists(files[i]);
				}
			}
		}

	}
}
