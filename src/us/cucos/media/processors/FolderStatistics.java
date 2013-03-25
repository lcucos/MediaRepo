package us.cucos.media.processors;

import java.io.File;
import java.util.ArrayList;

public class FolderStatistics {
	private int totalFiles = 0;
	private int fileExists = 0;
	private long totalSize = 0;
	private static final double MB_SIZE = 1024*1024.0;
	private static final double GB_SIZE = MB_SIZE * 1024;
	private ArrayList<FolderStatistics> children = new ArrayList<FolderStatistics>();
	private File folder;
	public FolderStatistics(File f){
		folder = f;
	}
	public void addFile(File file){
		totalFiles++;
		totalSize  += file.length();
	}
	
	public String toString(){
		return toString("");
	}
	public String toString(String prefix){
		String retVal = prefix + folder.getAbsolutePath()+"\n";
		for(FolderStatistics kid: children){
			retVal += kid.toString(prefix + "  ") + "\n";
		}

		retVal += "New Files: " + totalFiles  + ",  Size = " + getPrettySize(totalSize)  + ", Subfolders: " + children.size() + ", Existing Files: " + fileExists;
		return retVal;
	}

	private String getPrettySize(long size) {
		if(size > 1024 && size < MB_SIZE){
			return String.format("%.2g Kb (%d b)", size/1024.0, size);
		}else if(size >= MB_SIZE && size < GB_SIZE ){
			return String.format("%.2g Mb (%d b)", size/MB_SIZE, size);
		}else if(size >= GB_SIZE ){
			return String.format("%.2g Gb (%d b)", size/GB_SIZE, size);
		}
		return size +" b";
	}

	public void addFile(FolderStatistics fSubFStats) {
		if(fSubFStats!=null){
			
		}
		totalFiles+= fSubFStats.totalFiles;
		totalSize += fSubFStats.totalSize;
		fileExists+= fSubFStats.fileExists;
		children.add(fSubFStats);
	}
	
	public void fileExists(File file) {
		fileExists++;
		
	}
}
