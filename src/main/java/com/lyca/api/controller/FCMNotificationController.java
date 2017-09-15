/**
 * 
 */
package com.lyca.api.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lyca.api.service.FCMNotificationService;
import com.lyca.api.service.UserService;

/**
 * @author Krishna
 *
 */
@RestController
@RequestMapping("/fcm")
public class FCMNotificationController {

	@Autowired
	private UserService userService;
	
	@Autowired
	@Qualifier("responseMessage")
	private Properties responseMessage;
	
//	@RequestMapping(value = "/send", method = RequestMethod.POST)
//	public @ResponseBody JSONObject pushFCMNotification(@RequestParam("token") String deviceId)
//			throws IOException {
//		JSONObject ss = new JSONObject();
//		ss.put("ddd", deviceId);
//		return ss;
//	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/addDeviceToken", method = RequestMethod.POST)
	public @ResponseBody JSONObject storeUserFcmToken(@RequestBody JSONObject token) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		
		if (token.get("token") != null && token.get("token") != null) {
			return userService.storeUserFcmToken(token.get("token").toString(), token.get("userId").toString());
		} else {
			status.put("responseStatus", false);
			status.put("responseMessage", responseMessage.get("id.or.key/value.is.null.or.incorrect"));
			return status;
		}
	}
}
