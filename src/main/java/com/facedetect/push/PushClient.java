package com.facedetect.push;

//import android.util.Log;
//
//import com.facedetect.utils.Hex;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.security.MessageDigest;
//
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.json.JSONObject;

public class PushClient {
	
	// The user agent
//	protected final String USER_AGENT = "Mozilla/5.0";
//
//	// This object is used for sending the post request to Umeng
//	protected HttpClient client = new DefaultHttpClient();
//
//	// The host
//	protected static final String host = "http://msg.umeng.com";
//
//	// The upload path
//	protected static final String uploadPath = "/upload";
//
//	// The post path
//	protected static final String postPath = "/api/send";
//
//	public boolean send(UmengNotification msg) throws Exception {
//		String timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
//		Log.i("PushClient","1");
//		msg.setPredefinedKeyValue("timestamp", timestamp);
//        String url = host + postPath;
//        String postBody = msg.getPostBody();
//		Log.i("PushClient","2");
//
//		MessageDigest msgDigest = MessageDigest.getInstance("MD5");
//		String sign = Hex.encodeHexString(msgDigest.digest(("POST" + url + postBody + msg.getAppMasterSecret()).getBytes("utf8")));
////        String sign = DigestUtils.md5Hex(("POST" + url + postBody + msg.getAppMasterSecret()));
//		Log.i("PushClient","3");
//        url = url + "?sign=" + sign;
//        HttpPost post = new HttpPost(url);
//        post.setHeader("User-Agent", USER_AGENT);
//        StringEntity se = new StringEntity(postBody, "UTF-8");
//        post.setEntity(se);
//        // Send the post request and get the response
//        HttpResponse response = client.execute(post);
//		Log.i("PushClient","response； "+response.getStatusLine().toString());
//        int status = response.getStatusLine().getStatusCode();
//        System.out.println("Response Code : " + status);
//        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//        StringBuffer result = new StringBuffer();
//        String line = "";
//        while ((line = rd.readLine()) != null) {
//            result.append(line);
//        }
//        System.out.println(result.toString());
//        if (status == 200) {
//			Log.i("PushClient","Notification sent successfully.");
//            System.out.println("Notification sent successfully.");
//        } else {
//            System.out.println("Failed to send the notification!");
//        }
//        return true;
//    }
//
//	// Upload file with device_tokens to Umeng
//	public String uploadContents(String appkey,String appMasterSecret,String contents) throws Exception {
//		// Construct the json string
//		JSONObject uploadJson = new JSONObject();
//		uploadJson.put("appkey", appkey);
//		String timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
//		uploadJson.put("timestamp", timestamp);
//		uploadJson.put("content", contents);
//		// Construct the request
//		String url = host + uploadPath;
//		String postBody = uploadJson.toString();
//		MessageDigest msgDigest = MessageDigest.getInstance("MD5");
//		String sign = Hex.encodeHexString(msgDigest.digest(("POST" + url + postBody + appMasterSecret).getBytes("utf8")));
//		url = url + "?sign=" + sign;
//		HttpPost post = new HttpPost(url);
//		post.setHeader("User-Agent", USER_AGENT);
//		StringEntity se = new StringEntity(postBody, "UTF-8");
//		post.setEntity(se);
//		// Send the post request and get the response
//		HttpResponse response = client.execute(post);
//		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
//		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//		StringBuffer result = new StringBuffer();
//		String line = "";
//		while ((line = rd.readLine()) != null) {
//			result.append(line);
//		}
//		Log.i("PushClient","response； "+response.getStatusLine().toString());
//		System.out.println(result.toString());
//		// Decode response string and get file_id from it
//		JSONObject respJson = new JSONObject(result.toString());
//		String ret = respJson.getString("ret");
//		if (!ret.equals("SUCCESS")) {
//			throw new Exception("Failed to upload file");
//		}
//		JSONObject data = respJson.getJSONObject("data");
//		String fileId = data.getString("file_id");
//		// Set file_id into rootJson using setPredefinedKeyValue
//
//		return fileId;
//	}

}
