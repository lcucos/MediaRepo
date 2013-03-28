package us.cucos.media.main;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


import us.cucos.media.processors.FolderScanner;
import us.cucos.media.processors.FolderStatistics;
import us.cucos.media.storage.IMetaDataStorage;
import us.cucos.media.storage.StorageFactory;
import us.cucos.media.utils.CmdArgs;
import us.cucos.media.utils.MyLog;


public class MainImportMedia {
	
	public IMetaDataStorage getStorage(Properties properties){
		StorageFactory storageFactory = new StorageFactory(); 
		return storageFactory.getStorage(properties);
	}
		
	public void startServer(CmdArgs cmdArgs){
		IMetaDataStorage storage = getStorage(cmdArgs.getProperties());
		if(storage==null){
			return;
		}

		// start server
		int port = cmdArgs.getIntProperty(CmdArgs.SERVER_PORT, CmdArgs.DEFAULT_APP_PORT);
		Server server = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		context.addServlet(new ServletHolder(new StorageServlet(storage)), "/repo");
        try {
        	MyLog.d("Start Server at port: "+port);
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void runClient(CmdArgs cmdArgs) {
		if(cmdArgs.getProperty(CmdArgs.IMPORT_FOLDER) == null){
			MyLog.e("Please specify a folder to process!");
			return;
		}

		IMetaDataStorage storage = getStorage(cmdArgs.getProperties());
		if(storage==null){
			return;
		}
		FolderScanner scanner = new FolderScanner();		
		String folder = cmdArgs.getProperty(CmdArgs.IMPORT_FOLDER);
		StringTokenizer stTok = new StringTokenizer(folder, ",");
		
		while(stTok.hasMoreTokens()){
			String crtFolder = stTok.nextToken();
			FolderStatistics fStats = scanner.loadFolder(crtFolder, storage);
			MyLog.d("Stats: " + fStats.toString());
		}
	}

	public static void main(String args[]){
		CmdArgs cmdArgs = new CmdArgs();
		if(! cmdArgs.init(args)){
			return;
		}
		MainImportMedia app = new MainImportMedia();
		
		if(cmdArgs.getBooleanProperty(CmdArgs.SERVER_FLAG, false)){
			app.startServer(cmdArgs);
		}else{
			app.runClient(cmdArgs);
		}
	}
}
