/**
 * 
 */
package com.lyca.api.service.impl;

import java.util.Properties;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.lyca.api.service.PusherNotificationService;
import com.pusher.rest.Pusher;

/**
 * @author Krishna
 *
 */
@Service
@SuppressWarnings("unchecked")
public class pusherNotificationServiceImpl implements PusherNotificationService {

	@Autowired
	@Qualifier("responseMessage")
	private Properties responseMessage;
	
	@Override
	public JSONObject listCredentials() {
		JSONObject pusherJson = new JSONObject();
		try {
			pusherJson.put("appId", responseMessage.getProperty("app.id"));
			pusherJson.put("appKey", responseMessage.getProperty("app.key"));
			pusherJson.put("appSecret", responseMessage.getProperty("app.secret"));
			pusherJson.put("appCluster", responseMessage.getProperty("app.cluster"));
			pusherJson.put("responseStatus", true);
			pusherJson.put("responseMessage", responseMessage.getProperty("pusher.credentials"));
		} catch (Exception e) {
			pusherJson.put("responseStatus", false);
			pusherJson.put("responseMessage", responseMessage.getProperty("pusher.credentials.failed"));
			e.printStackTrace();
		}
		return pusherJson;
	}

    public JSONObject pushMessasge(String strUserId, String strEvent, JSONObject jsonMessage) {
    	JSONObject jsonResponse = new JSONObject();
        try {
            String APP_ID = responseMessage.getProperty("app.id");
            String APP_KEY = responseMessage.getProperty("app.key");
            String APP_SECRET = responseMessage.getProperty("app.secret");
            String APP_CLUSTER = responseMessage.getProperty("app.cluster");
            System.out.println("Message Before pushing..." + strUserId +"--"+ strEvent +"--"+ jsonMessage);
            Pusher pusher = new Pusher(APP_ID, APP_KEY, APP_SECRET);
            pusher.setCluster(APP_CLUSTER);
            pusher.trigger(strUserId, strEvent, jsonMessage);
            System.out.println("Message pushed..." + strUserId +"--"+ strEvent +"--"+ jsonMessage);
            jsonResponse.put("callToNumber", strUserId);
            jsonResponse.put("event", strEvent);
            jsonResponse.put("resDetails", jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return jsonResponse;

	}
}
