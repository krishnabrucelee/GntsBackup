/**
 * 
 */
package com.lyca.api.service;

import org.json.simple.JSONObject;
import com.lyca.api.model.User;
import com.lyca.api.util.CRUDService;

/**
 * @author Krishna
 *
 */
public interface UserService extends CRUDService<User> {
	
	/**
	 * Create User.
	 * 
	 * @param user
	 * @return user
	 */
	public JSONObject addUser(JSONObject user);

	/**
	 * List User.
	 * 
	 * @param user
	 * @return user
	 */
	public JSONObject listUser();

	/**
	 * Update User.
	 * 
	 * @param user
	 * @return user
	 */
	public JSONObject updateUser(JSONObject user);

	/**
	 * Delete User.
	 * 
	 * @param user
	 * @return user
	 */
	public JSONObject deleteUser(JSONObject userId);

	/**
	 * @param user
	 * @return
	 */
	public JSONObject getUserById(JSONObject user);

	public JSONObject otpVerification(JSONObject otp);

	public User getUserByMobileNumber(String mobileNumber);

	public JSONObject sendOtp(JSONObject otp);

	public JSONObject smsGateway(String isdCode, String mobileNumber, String  code, String message);

	public JSONObject storeUserFcmToken(String deviceId, String userId);
	
}
