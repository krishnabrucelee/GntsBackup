/**
 * 
 */
package com.lyca.api.service;

import org.json.simple.JSONObject;
import com.lyca.api.model.CallDetails;
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
}
