package com.finstone;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.fins.html.Application;
import com.fins.html.utils.FileUtils;

public class UrlTest {
	//private static Log log = LogFactory.getLog(UrlTest.class);

    private Properties setings ;
    private String rootPath;
    public UrlTest(){
    	initRootPath();
    	initProperties();
    }
    
    public String getRootPath(){
    	return rootPath;
    }
    public void initProperties(){
    	try{
    		setings = Application.getApplicationSettings();
    	}catch(Exception e){
    		
			InputStream in = FileUtils.readFileAsStream(new File(rootPath+"/config/application.properties"));
			try {
				setings = new Properties();
				setings.load(in);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    	}
    }
    public String getProperValue(String key){
    	return setings.getProperty(key);
    }
	public void initRootPath(){
		String path = UrlTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if(path.startsWith("file:")){
			path = path.substring(5);
		}
		if(path.startsWith("/")&&path.indexOf(":")!=-1){
			path = path.substring(1);
		}

		rootPath = path;
		int index1 = path.indexOf(".jar");
		if(index1!=-1){
			rootPath = "";
			path = path.split(".jar")[0];
			String[] paths = path.split("/");
			for(int i=0;i<paths.length-1;i++){
				rootPath = rootPath+paths[i]+File.separator;
			}
		}else{
			rootPath = path.split("WEB-INF")[0];
			if(rootPath.endsWith("/")){
				rootPath = rootPath+"WEB-INF";
			}else{
				rootPath = rootPath+"/WEB-INF";
			}
		}
		if(rootPath.endsWith("/")){
			rootPath = rootPath.substring(0,rootPath.length()-1);
		}
		if(rootPath.endsWith("\\")){
			rootPath = rootPath.substring(0,rootPath.length()-1);
		}
		rootPath = (new File(rootPath)).getAbsolutePath();

	}
	
    public String getPubicPay(){
    	return setings.getProperty("publicPay");
    }
    
    public String getFsService(){
    	return setings.getProperty("fsService");
    }
    
    public String getJson(String path){
    	String laststr = "";
    	try {
    		InputStream in = FileUtils.readFileAsStream(new File(path));
			laststr = InputStreamTOString(in,"utf-8");
		}catch (Exception e) {
			e.printStackTrace();
		}
    	return laststr;
    }
    
    public String InputStreamTOString(InputStream in,String encoding) throws Exception{  
    	return new JSONObject(new JSONTokener(new InputStreamReader(in, encoding))).toString();
    } 
    
	public String doGet(String url, String queryString) {
		return doPost(url, queryString);
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @return 返回请求响应的HTML
	 */
	public String doPost(String url, String queryString) {
		HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
		try{
			httpClient =new DefaultHttpClient();
			if(url.startsWith("https")){
				httpClient = wrapClient(httpClient);
			}
            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
            StringEntity se = new StringEntity(queryString,"utf-8");
            se.setContentType("application/json; charset=utf-8");
            httpPost.setEntity(se);
            
            HttpResponse response = httpClient.execute(httpPost);
            
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if(entity != null){
					result = EntityUtils.toString(entity, "utf-8"); 
				}
			}
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
        	if(httpPost!=null)
        		httpPost.abort();  
        	if(httpClient!=null && httpClient.getConnectionManager()!=null)
        		httpClient.getConnectionManager().shutdown(); 
        }
        return result;
	}

	public HttpClient wrapClient(HttpClient base) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");  
	        X509TrustManager tm = new X509TrustManager() {  
	                @Override  
	                public void checkClientTrusted(X509Certificate[] chain,  
	                        String authType) throws CertificateException {  
	                }  
	                @Override  
	                public void checkServerTrusted(X509Certificate[] chain,  
	                        String authType) throws CertificateException {  
	                }  
	                @Override  
	                public X509Certificate[] getAcceptedIssuers() {  
	                    return null;  
	                }  
	        };  
	        ctx.init(null, new TrustManager[]{tm}, null);  
	        SSLSocketFactory ssf = new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
	        ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			int port = Integer.parseInt(getProperValue("publicPort"));
			sr.register(new Scheme("https", port, ssf));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取POST请求数据
	 * 
	 * @param request
	 * @return
	 */
	public String getRequestContent(HttpServletRequest request,String encoding) {
		String reqcontent = "";
		// 获取接收到的请求信息
		int totalbytes = request.getContentLength();
		byte[] dataOrigin = new byte[totalbytes];
		DataInputStream in;
		try {
			in = new DataInputStream(request.getInputStream());
			in.readFully(dataOrigin);
			in.close(); // 关闭数据流
		} catch (IOException e1) {
			return reqcontent;
		}
		try {
			reqcontent = new String(dataOrigin, encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return reqcontent;
		}
		return reqcontent;
	}
	

	public static void main(String[] args) {
		UrlTest t = new UrlTest();
		System.out.println("-----------  fs --->>> public  -------------------");
		String publicPay = t.getPubicPay();
		String publicParams = t.getJson(t.getRootPath()+"/config/wp.bill.sync.json"); //fs  --->  public
		System.out.println("请求路径："+publicPay);
		System.out.println("请求参数："+publicParams);
		String publicresult = t.doPost(publicPay, "");
		System.out.println("返回值："+publicresult);
//		System.out.println("-----------  fs --->>> piblic  -------------------");
//		String fsPay = t.getFsService();
//		String fsparams = t.getJson(t.getRootPath()+"/config/wp.outer.bill.query.json"); // public --->  fs 
//		System.out.println("请求路径："+fsPay);
//		System.out.println("请求参数："+fsparams);
//		String resultfs = t.doPost(fsPay, fsparams);
//		System.out.println("返回值："+resultfs);
	}
}
