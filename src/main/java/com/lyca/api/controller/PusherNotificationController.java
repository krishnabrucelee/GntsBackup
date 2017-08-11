/**
 * 
 */
package com.lyca.api.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.lyca.api.service.PusherNotificationService;

/**
 * @author Krishna
 *
 */
@RestController
@RequestMapping("/pusher")
public class PusherNotificationController {

	/**
	 * Pusher Notification Service.
	 */
	@Autowired
	private PusherNotificationService pusherNotificationService;
	
	@RequestMapping(value = "/getCredentials")
	public @ResponseBody JSONObject listCredentials() {
		return pusherNotificationService.listCredentials();
	}
}
