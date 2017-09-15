/**
 * 
 */
package com.lyca.api.service;

import java.io.IOException;

import org.json.simple.JSONObject;

/**
 * @author Krishna
 *
 */
public interface FCMNotificationService {

	JSONObject pushFCMNotification(String deviceId, JSONObject jsonFcmMsg) throws IOException;

}
