package us.cucos.media.storage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Iterator;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import us.cucos.media.main.StorageServlet;
import us.cucos.media.utils.CmdArgs;
import us.cucos.media.utils.MyLog;

public class RemoteStorage implements IMetaDataStorage {

	private String hostBase;
	private int port = CmdArgs.DEFAULT_APP_PORT;
	
	public boolean init(String repoAt){
		// check if is running
		hostBase = repoAt;
		String result = getHTML(StorageServlet.CHECK_REQUEST,"");
		if(result!=null && result.equals(StorageServlet.VALIDATION_CODE)){
			MyLog.d("Use Remote storage at: "+repoAt);

			return true;
		}
		MyLog.e("Unable to connect to: " + hostBase + ":" + port);
		return false;
	}
	
	public String getHTML(String action, String encParams) {
		URL url;
		if(encParams.length()>0){
			encParams="&"+encParams;
		}
		String urlToRead = "/repo?"+StorageServlet.PARAM_ACTION+"="+action+encParams;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		int respCode;
		try {
			url = new URL("http://"+hostBase+ urlToRead);
			
			conn = (HttpURLConnection) url.openConnection();			
			respCode = conn.getResponseCode() ;
			if(respCode!=200){
				conn.disconnect();
				return null;
			}
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private JSONObject getRemoteData(String action, JSONObject requestObj){
		// if the array is too big, split the request in multiple sub-requests
		String params = requestObj.toString();
		
		// encode the request
		String encParams=null;
		try {
			encParams = URLEncoder.encode(params.toString(), "UTF-8");			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		// send the request
		String ret = getHTML(action, StorageServlet.PARAM_JSONOBJ +"="+encParams);
		//MyLog.d("Return: " + ret);
		// decode the response
		if(ret==null||ret.startsWith("Error")){
			return null;
		}
		// parse the response 
		JSONTokener jsonTokener=new JSONTokener(ret);
		JSONObject objOut = null;
		try {
			objOut = (JSONObject) jsonTokener.nextValue();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return null;
		}

		return objOut;		
	}
	@Override
	public void loadFileInfo(JSONObject obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean checkFile(JSONObject obj) {
		JSONObject objOut = getRemoteData(StorageServlet.ACTION_CHECKFILE, obj);
		if(objOut==null){
			MyLog.e("Unable to get data from server");
			return false;
		}
		
		return objOut.getBoolean(obj.getString(IMetaDataStorage.ID_TAG));
	}

	private static int maxPerBulkInsert = 20;
	
	@Override
	public JSONObject bulkCheckFiles(JSONArray arrFiles) {
		return bulkAction(StorageServlet.ACTION_BULK_CHECKFILE, arrFiles);
	}
	
	private JSONObject bulkAction(String action, JSONArray arrFiles) {
		// break up the array
		JSONArray arrTemp = new JSONArray();
		JSONObject returnObject = new JSONObject(); 
		for(int i=0;i<arrFiles.length();i++){
			arrTemp.put(arrFiles.get(i));
			if(i> 0  && i%maxPerBulkInsert==0 || i==arrFiles.length()-1){
				// send the temp array
				JSONObject objReq = new JSONObject();
				objReq.put(StorageServlet.JSON_ARR, arrTemp);
				JSONObject objRet = getRemoteData(action, objReq);
				// merge the result
				mergeObject(returnObject, objRet); 
				arrTemp = new JSONArray();
			}
		}
		return returnObject;
	}

	private void mergeObject(JSONObject master, JSONObject temp) {
		// scan through all the elements in temp and add them in master - for duplicates, only the last one is kept
		if(temp==null){
			return;
		}
		Iterator keys = temp.keys();
		while(keys.hasNext()){
			String key = (String)keys.next();
			//MyLog.d(key + " : " + temp.get(key));
			master.put(key, temp.get(key));
		}
	}

}
