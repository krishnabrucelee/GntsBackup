/**
 * 
 */
package com.lyca.api.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
/**
 * @author Krishna
 *
 */
@RestController
@RequestMapping("/lycaApi")
public class LycaApiController {

	@Autowired
	@Qualifier("responseMessage")
	private Properties responseMessage;
	
	public JSONObject generateAccessToken() {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject authJson = new JSONObject();
		authJson.put("userId", responseMessage.get("lyca.userId"));
		authJson.put("password", responseMessage.get("lyca.password"));
		
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<HashMap<String, Object>>(authJson, headers);
		JSONObject result = new JSONObject();
		try {
			JSONObject response = restTemplate.postForObject(responseMessage.get("lyca.url") + "auth", request,
					JSONObject.class);
			
			System.out.println(response);
			result.put("responseStatus", true);
			result.put("responseMessage", responseMessage.get("lyca.authenication.message.success"));
			result.put("lycaAuth", response);
			
		} catch (RestClientException e) {
			e.printStackTrace();
			result.put("responseStatus", false);
			result.put("responseMessage", responseMessage.get("lyca.authenication.message.failure"));
		} 
		return result;
	}
	
	@RequestMapping(value = "/validateSerial", method = RequestMethod.POST)
	public @ResponseBody JSONObject validateSerial(@RequestBody JSONObject validateSerial) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject accessToken = generateAccessToken();
		JSONObject authJson = new JSONObject();
		if (accessToken.get("responseStatus") != null && accessToken.get("responseStatus").equals(true)) {
			JSONObject authToken = (JSONObject) accessToken.get("lycaAuth");
			if (validateSerial.get("lycaSubscriberId") != null
					|| validateSerial.get("lycaSubscriberId").toString().isEmpty()) {
				authJson.put("serial", validateSerial.get("lycaSubscriberId"));
				authJson.put("access_token", authToken.get("access_token"));
			} else {
				authJson.put("responseStatus", false);
				authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
				return authJson;
			}
		} else {
			authJson.put("responseStatus", false);
			authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
			return authJson;
		}
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<HashMap<String, Object>>(authJson, headers);
		JSONObject result = new JSONObject();
		try {
			JSONObject response = restTemplate.postForObject(responseMessage.get("lyca.url") + "validateSerial", request,
					JSONObject.class);

			System.out.println(response);
			if (response != null) {
				JSONObject stbJson = new JSONObject();
				
				stbJson.put("countryIsoCode", "IN");
				stbJson.put("lycaSubscriberId", validateSerial.get("lycaSubscriberId"));
				stbJson.put("firstName", response.get("fname"));
				stbJson.put("lastName", response.get("lname"));
				stbJson.put("mobileNumber", response.get("mobileAuth"));
				stbJson.put("stbUser", true);
				HttpEntity<HashMap<String, Object>> stbRequest = new HttpEntity<HashMap<String, Object>>(stbJson, headers);
				JSONObject stbResponse = restTemplate.postForObject(responseMessage.get("api.url") + "user/add", stbRequest,
						JSONObject.class);
				return stbResponse;
			}
			
		} catch (RestClientException e) {
			e.printStackTrace();
			result.put("responseStatus", false);
			result.put("responseMessage", responseMessage.get("lyca.validate.serial.message.failure"));
		} 
		return result;
	}
	
	@RequestMapping(value = "/validateMobileAppNo", method = RequestMethod.POST)
	public @ResponseBody JSONObject validateMobileAppNo(@RequestBody JSONObject validateMobileAppNo) {
		
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		JSONObject authJson = new JSONObject();
		if (!validateMobileAppNo.containsKey("fcmToken")) {
			authJson.put("responseStatus", false);
			authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
			return authJson;
		}
		JSONObject accessToken = generateAccessToken();
		if (accessToken.get("responseStatus") != null && accessToken.get("responseStatus").equals(true)) {

			JSONObject authToken = (JSONObject) accessToken.get("lycaAuth");

			if (validateMobileAppNo.get("mobileNumber") != null || validateMobileAppNo.get("mobileNumber").toString().isEmpty()) {
				authJson.put("mobile", validateMobileAppNo.get("mobileNumber"));
				authJson.put("access_token", authToken.get("access_token"));
			} else {
				authJson.put("responseStatus", false);
				authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
				return authJson;
			}
		} else {
			authJson.put("responseStatus", false);
			authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
			return authJson;
		}
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<HashMap<String, Object>>(authJson, headers);
		JSONObject result = new JSONObject();
		try {
			JSONObject response = restTemplate.postForObject(responseMessage.get("lyca.url") + "validateMobileAppNo", request,
					JSONObject.class);

			System.out.println(response);
			if (response != null) {
				JSONObject stbJson = new JSONObject();
				if (response.get("userAuth") != null && response.get("userAuth").toString().equals("Valid User")) {
					
				
				stbJson.put("mobileNumber", validateMobileAppNo.get("mobileNumber"));
				stbJson.put("mobileAuth", response.get("mobileAuth"));
				stbJson.put("otpCode", response.get("otpCode"));
				stbJson.put("countryId", validateMobileAppNo.get("countryId"));
				if (validateMobileAppNo.get("fcmToken") != null && !validateMobileAppNo.get("fcmToken").toString().isEmpty()) {
					stbJson.put("fcmToken", validateMobileAppNo.get("fcmToken"));
				}
				stbJson.put("stbUser", true);
				HttpEntity<HashMap<String, Object>> stbRequest = new HttpEntity<HashMap<String, Object>>(stbJson, headers);
				JSONObject stbResponse = restTemplate.postForObject(responseMessage.get("api.url") + "user/add", stbRequest,
						JSONObject.class);
				return stbResponse;
				} else {
					authJson.put("responseStatus", false);
					authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
					return authJson;
				}
			}
			
		} catch (RestClientException e) {
			e.printStackTrace();
			result.put("responseStatus", false);
			result.put("responseMessage", responseMessage.get("lyca.validate.serial.message.failure"));
		} 
		return result;
	}
	
	@RequestMapping(value = "/validateOtp", method = RequestMethod.POST)
	public @ResponseBody JSONObject validateOtp(@RequestBody JSONObject validateOtp) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject accessToken = generateAccessToken();
		JSONObject authJson = new JSONObject();
		if (accessToken.get("responseStatus") != null && accessToken.get("responseStatus").equals(true)) {
			JSONObject authToken = (JSONObject) accessToken.get("lycaAuth");

			if ((validateOtp.get("mobileNumber") != null
					|| validateOtp.get("mobileNumber").toString().isEmpty()) && (validateOtp.get("otp") != null
					|| validateOtp.get("otp").toString().isEmpty())) {
				authJson.put("mobile", validateOtp.get("mobileNumber"));
				authJson.put("otpCode", validateOtp.get("otp"));
				authJson.put("access_token", authToken.get("access_token"));
			} else {
				authJson.put("responseStatus", false);
				authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
				return authJson;
			}
		} else {
			authJson.put("responseStatus", false);
			authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
			return authJson;
		}
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<HashMap<String, Object>>(authJson, headers);
		JSONObject result = new JSONObject();
		try {
			JSONObject response = restTemplate.postForObject(responseMessage.get("lyca.url") + "validateOtp", request,
					JSONObject.class);

			System.out.println(response);
			result.put("responseStatus", true);
			result.put("responseMessage", responseMessage.get("otp.verified"));
			result.put("validateResponse", response);
			
		} catch (RestClientException e) {
			e.printStackTrace();
			result.put("responseStatus", false);
			result.put("responseMessage", responseMessage.get("otp.verfication.failed"));
		} 
		return result;
	}
	
	@RequestMapping(value = "/addNonLyca", method = RequestMethod.POST)
	public @ResponseBody JSONObject addNonLyca(@RequestBody JSONObject addNonLyca) {
		
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject accessToken = generateAccessToken();
		JSONObject authJson = new JSONObject();
		if (!addNonLyca.containsKey("fcmToken")) {
			authJson.put("responseStatus", false);
			authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
			return authJson;
		}
		if (accessToken.get("responseStatus") != null && accessToken.get("responseStatus").equals(true)) {
			JSONObject authToken = (JSONObject) accessToken.get("lycaAuth");
			if (addNonLyca.get("mobileNumber") != null
					|| addNonLyca.get("mobileNumber").toString().isEmpty() && addNonLyca.get("firstName") != null
					|| addNonLyca.get("firstName").toString().isEmpty() && addNonLyca.get("lastName") != null
					|| addNonLyca.get("lastName").toString().isEmpty()) {
				authJson.put("fname", addNonLyca.get("firstName"));
				authJson.put("lname", addNonLyca.get("lastName"));
				authJson.put("mobileNumber", addNonLyca.get("mobileNumber"));
				authJson.put("access_token", authToken.get("access_token"));
			} else {
				authJson.put("responseStatus", false);
				authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
				return authJson;
			}
		} else {
			authJson.put("responseStatus", false);
			authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
			return authJson;
		}
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<HashMap<String, Object>>(authJson, headers);
		JSONObject result = new JSONObject();
		try {
			JSONObject response = restTemplate.postForObject(responseMessage.get("lyca.url") + "addNonLyca", request,
					JSONObject.class);

			System.out.println(response);
			if (response != null) {
				JSONObject stbJson = new JSONObject();
				if (response.get("status") != null && response.get("status").toString().equals("201")) {
					
					stbJson.put("firstName", addNonLyca.get("firstName"));
					stbJson.put("lastName", addNonLyca.get("lastName"));
					stbJson.put("mobileNumber", addNonLyca.get("mobileNumber"));
					stbJson.put("countryId", addNonLyca.get("countryId"));
					if (addNonLyca.get("fcmToken") != null && !addNonLyca.get("fcmToken").toString().isEmpty()) {
						stbJson.put("fcmToken", addNonLyca.get("fcmToken"));
					}
				stbJson.put("stbUser", false);
				HttpEntity<HashMap<String, Object>> stbRequest = new HttpEntity<HashMap<String, Object>>(stbJson, headers);
				JSONObject stbResponse = restTemplate.postForObject(responseMessage.get("api.url") + "user/add", stbRequest,
						JSONObject.class);
				return stbResponse;
				} else {
					authJson.put("responseStatus", false);
					authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
					return authJson;
				}
			}
			
		} catch (RestClientException e) {
			e.printStackTrace();
			result.put("responseStatus", false);
			result.put("responseMessage", responseMessage.get("already.subscribed"));
		} 
		return result;
	}
	
	@RequestMapping(value = "/sendSMS", method = RequestMethod.POST)
	public @ResponseBody JSONObject sendSMS(@RequestBody JSONObject sendSMS) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject accessToken = generateAccessToken();
		JSONObject authJson = new JSONObject();
		if (accessToken.get("responseStatus") != null && accessToken.get("responseStatus").equals(true)) {
			JSONObject authToken = (JSONObject) accessToken.get("lycaAuth");

			if (sendSMS.get("mobileNumber") != null
					|| sendSMS.get("mobileNumber").toString().isEmpty()) {
				authJson.put("mobileNumber", sendSMS.get("mobileNumber"));
				authJson.put("message", sendSMS.get("message"));
				authJson.put("access_token", authToken.get("access_token"));
			} else {
				authJson.put("responseStatus", false);
				authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
				return authJson;
			}
		} else {
			authJson.put("responseStatus", false);
			authJson.put("responseMessage", responseMessage.get("lyca.validate.serial.invalid"));
			return authJson;
		}
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<HashMap<String, Object>>(authJson, headers);
		JSONObject result = new JSONObject();
		try {
			JSONObject response = restTemplate.postForObject(responseMessage.get("lyca.url") + "sendSMS", request,
					JSONObject.class);

			System.out.println(response);
			result.put("responseStatus", true);
			result.put("responseMessage", responseMessage.get("otp.verified"));
			result.put("validateResponse", response);
			
		} catch (RestClientException e) {
			e.printStackTrace();
			result.put("responseStatus", false);
			result.put("responseMessage", responseMessage.get("otp.verfication.failed"));
		} 
		return result;
	}
}
