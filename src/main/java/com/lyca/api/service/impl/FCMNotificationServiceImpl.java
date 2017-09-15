/**
 * 
 */
package com.lyca.api.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyca.api.model.CallDetails;
import com.lyca.api.model.User;
import com.lyca.api.service.FCMNotificationService;

/**
 * @author Krishna
 *
 */
@Service
public class FCMNotificationServiceImpl implements FCMNotificationService {

	@Autowired
	@Qualifier("responseMessage")
	private Properties responseMessage;
	
	@Override
	public JSONObject pushFCMNotification(String deviceRegistrationId, JSONObject jsonFcmMsg) throws IOException {
		JSONObject status = new JSONObject();
		
	   	 String authKey = responseMessage.getProperty("fcm.server.key"); // You FCM AUTH key
	   	   String FMCurl = responseMessage.getProperty("fcm.url"); 

	   	int responseCode = -1;
        String responseBody = null;
        try
        {
            System.out.println("Sending FCM request");
            byte[] postData = getPostData(deviceRegistrationId, jsonFcmMsg);
            
            URL url = new URL(FMCurl);
            HttpsURLConnection httpURLConnection = (HttpsURLConnection)url.openConnection();
 
            //set timeputs to 10 seconds
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
 
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
            httpURLConnection.setRequestProperty("Authorization", "key="+authKey);
 
             
 
            OutputStream out = httpURLConnection.getOutputStream();
            out.write(postData);
            out.close();
            responseCode = httpURLConnection.getResponseCode();
            //success
            if (responseCode == HttpStatus.SC_OK)
            {
                responseBody = convertStreamToString(httpURLConnection.getInputStream());
                System.out.println("FCM message sent : " + responseBody);
                status.put("responseStatus", true);
 	    	   status.put("responseCode", responseCode);
 	    	   status.put("responseMessage", responseBody);
            }
            //failure
            else
            {
                responseBody = convertStreamToString(httpURLConnection.getErrorStream());
                System.out.println("Sending FCM request failed for regId: " + deviceRegistrationId + " response: " + responseBody);
 	    	   status.put("responseStatus", false);
 	    	   status.put("responseCode", responseCode);
 	    	   status.put("responseMessage", responseBody);
            }
        }
        catch (IOException ioe)
        {
            System.out.println("IO Exception in sending FCM request. regId: " + deviceRegistrationId);
            ioe.printStackTrace();
        	   status.put("responseStatus", false);
 	    	   status.put("responseCode", responseCode);
 	    	   status.put("responseMessage", responseBody);
        }
        catch (Exception e)
        {
            System.out.println("Unknown exception in sending FCM request. regId: " + deviceRegistrationId);
            e.printStackTrace();
        	   status.put("responseStatus", false);
 	    	   status.put("responseCode", responseCode);
 	    	   status.put("responseMessage", responseBody);
        }
		return status;
    }
     
    public static byte[] getPostData(String registrationId, JSONObject jsonFcmMsg) throws JSONException, JsonProcessingException {
    	
    	
    	
    		JSONObject json = new JSONObject();
    		if (registrationId != null) {
    			json.put("to", registrationId.trim());
    		} else {
    			json.put("registration_ids", jsonFcmMsg.get("registrationId"));
    		}
	   	   JSONObject info = new JSONObject();
	   	   info.put("title", "Lyca Chat App"); // Notification title
	   	   info.put("body", jsonFcmMsg.get("message")); // Notification body
	   	   json.put("notification", info);
	   	   json.put("priority", "high");
        HashMap<String, Object> dataMap = new HashMap<>();
 
        dataMap.put("callerNickName", jsonFcmMsg.get("callerNickName"));
        dataMap.put("callDetailsId", jsonFcmMsg.get("callDetailsId"));
        dataMap.put("message", jsonFcmMsg.get("message"));
        dataMap.put("event", jsonFcmMsg.get("event"));
        dataMap.put("callUsersCount", jsonFcmMsg.get("callUsersCount"));
        dataMap.put("inviteCount", jsonFcmMsg.get("inviteCount"));
        dataMap.put("timeStamp", new java.sql.Timestamp(System.currentTimeMillis()).getTime());
        if (jsonFcmMsg.containsKey("callMembers")) {
			List msg = (List) jsonFcmMsg.get("callMembers");
//			for (int i = 0; i <= msg.size(); i++) {
				ObjectMapper om = new ObjectMapper();
				dataMap.put("callMembers", om.writeValueAsString(msg.get(0)).toString());
//			}
		}
        JSONObject data = new JSONObject(dataMap);
        info.put("data", data);
        json.put("notification", info);
        json.put("data", data);
        if (registrationId != null) {
			json.put("to", registrationId.trim());
		} else {
			json.put("registration_ids", jsonFcmMsg.get("registrationId"));
		}
        return json.toString().getBytes();
    }
     
    public static String convertStreamToString (InputStream inStream) throws Exception
    {
        InputStreamReader inputStream = new InputStreamReader(inStream);
        BufferedReader bReader = new BufferedReader(inputStream);
 
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = bReader.readLine()) != null)
        {
            sb.append(line);
        }
 
        return sb.toString();
    }
}
