/**
 * 
 */
package com.lyca.api.service;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.data.domain.PageRequest;

import com.lyca.api.model.CallDetails;
import com.lyca.api.model.CallLog;
import com.lyca.api.model.CallLog.CallStatus;
import com.lyca.api.util.CRUDService;

/**
 * @author Krishna
 *
 */
public interface CallDetailsService extends CRUDService<CallDetails> {

	/**
	 * Create CallDetails.
	 * 
	 * @param callDetails
	 * @return callDetails
	 * @throws Exception 
	 */
	public JSONObject addCallDetails(JSONObject callDetails) throws Exception;

	/**
	 * List CallDetails.
	 * 
	 * @param callDetails
	 * @return callDetails
	 */
	public JSONObject listCallDetails();

	/**
	 * Update CallDetails.
	 * 
	 * @param callDetails
	 * @return callDetails
	 */
	public JSONObject updateCallDetails(JSONObject callDetails);

	/**
	 * Delete CallDetails.
	 * 
	 * @param callDetails
	 * @return callDetails
	 */
	public JSONObject deleteCallDetails(JSONObject callDetailsId);

	/**
	 * @param callDetails
	 * @return
	 */
	public JSONObject getCallDetailsById(JSONObject callDetails);

	public List<CallDetails> getFavlist(CallStatus groupcall, Integer userId, PageRequest pageRequest);
}
