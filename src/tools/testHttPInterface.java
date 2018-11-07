package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class testHttPInterface {
	 public static JsonObject doPost(String url,JsonObject json){
		    HttpClient client = HttpClients.createDefault();
		    HttpPost post = new HttpPost(url);
		    JsonObject response = null;
		    String result = null;
		    try {
		      StringEntity s = new StringEntity(json.toString());
		      s.setContentEncoding("UTF-8");
		      s.setContentType("application/json");//发送json数据需要设置contentType
		      post.setEntity(s);
		      HttpResponse res = client.execute(post);
		      if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		        HttpEntity entity = res.getEntity();
		        result = unicodeToString(EntityUtils.toString(res.getEntity()));// 返回json格式：
		      }
		    } catch (Exception e) {
		      throw new RuntimeException(e);
		    }
		    return str2Json(result);
		  }
	 
	 public static JsonObject doPostWithHeaders(String url,JsonObject json,String headerKey,String headerValue){
		    HttpClient client = HttpClients.createDefault();
		    HttpPost post = new HttpPost(url);
		    JsonObject response = null;
		    String result = null;
		    try {
		      StringEntity s = new StringEntity(json.toString());
		      s.setContentEncoding("UTF-8");
		      s.setContentType("application/json");//发送json数据需要设置contentType
		      post.setEntity(s);
		      post.setHeader(headerKey, headerValue);
		      HttpResponse res = client.execute(post);
		      if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		        HttpEntity entity = res.getEntity();
		        result = unicodeToString(EntityUtils.toString(res.getEntity()));// 返回json格式：
		      }
		    } catch (Exception e) {
		      throw new RuntimeException(e);
		    }
		    return str2Json(result);
		  }
	 public static String unicodeToString(String str) {
	        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
	        Matcher matcher = pattern.matcher(str);
	        char ch;
	        while (matcher.find()) {
	            ch = (char) Integer.parseInt(matcher.group(2), 16);
	            str = str.replace(matcher.group(1), ch+"" );
	        }
	        return str;
	    }
	 public static JsonObject str2Json(String string){
		 JsonParser parser = new JsonParser();
		 JsonObject jObject = (JsonObject) parser.parse(string);
		 return jObject;
	 }
}
