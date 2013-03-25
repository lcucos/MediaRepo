package us.cucos.media.main;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import us.cucos.media.storage.IMetaDataStorage;


public class StorageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String PARAM_ACTION  = "act";
	public static final String CHECK_REQUEST = "check-23764e5d2c9a486"; 
	public static final Object VALIDATION_CODE = "OK-3ade45edacb46d37d7a63b3";
	public static final String PARAM_JSONOBJ = "pjob";
	public static final String ACTION_CHECKFILE = "checkFile";
	public static final String ACTION_BULK_CHECKFILE = "bulkCheckFile";
	public static final String JSON_ARR = "jarr";
	
	private IMetaDataStorage storage;
	
	public StorageServlet(IMetaDataStorage st) {
		storage = st;
	}

	public void init(ServletConfig config) throws ServletException
    {
         super.init(config);
    }
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	
		    throws ServletException, IOException
    {
		//MyLog.d("Got request: " + req.getQueryString());
		String action=req.getParameter(PARAM_ACTION);
		if(action==null){
			resp.getWriter().println("Invalid Action");
			return;
		}else if(action.equals(CHECK_REQUEST)){
			resp.getWriter().println(VALIDATION_CODE);
			return;
		}
		String paramJObj = req.getParameter(PARAM_JSONOBJ);
		if(paramJObj!=null){
			resp.getWriter().println(processActionJsonObject(action, paramJObj));
			return;			
		}
    }

	private String processActionJsonObject(String action, String paramJObj) {
		JSONObject obj = null;
		
		try{
			String decodedObj = URLDecoder.decode(paramJObj, "UTF-8");
			JSONTokener jsonTokener=new JSONTokener(decodedObj);
  			obj = (JSONObject) jsonTokener.nextValue();
		}catch(Exception ex){
			return "Error: Invalid JSON object!";
		}
		if(action.equals(ACTION_CHECKFILE)){
			boolean bRet = storage.checkFile(obj);
			
			JSONObject objRet = new JSONObject();
			objRet.put(obj.getString(IMetaDataStorage.ID_TAG), bRet);
			return objRet.toString();
		}else if(action.equals(ACTION_BULK_CHECKFILE)){
			JSONArray arrParams = obj.getJSONArray(JSON_ARR);
			JSONObject objRet = storage.bulkCheckFiles(arrParams);
			return objRet.toString();
		}
		
		return null;
	}

}
