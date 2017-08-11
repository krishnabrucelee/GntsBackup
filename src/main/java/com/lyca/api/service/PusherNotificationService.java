/**
 * 
 */
package com.lyca.api.service;

import org.json.simple.JSONObject;

/**
 * @author Krishna
 *
 */
public interface PusherNotificationService {

	JSONObject listCredentials();

	public JSONObject pushMessasge(String strUserId, String strEvent, JSONObject strMessage);
}
