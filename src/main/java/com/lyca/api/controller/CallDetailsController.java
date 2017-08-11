/**
 * 
 */
package com.lyca.api.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.lyca.api.service.CallDetailsService;

/**
 * @author Krishna
 *
 */
@RestController
@RequestMapping("/callDetails")
public class CallDetailsController {

	/**
	 * CallDetails Service.
	 */
	@Autowired
	private CallDetailsService callDetailsService;

	/**
	 * Create CallDetails.
	 * 
	 * @param callDetails
	 * @return callDetails
	 * @throws Exception
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody JSONObject addCallDetails(@RequestBody JSONObject callDetails) throws Exception {
		return callDetailsService.addCallDetails(callDetails);
	}

	/**
	 * Update CallDetails.
	 * 
	 * @param callDetails
	 * @return callDetails
	 */
	@RequestMapping(value = "/update")
	public @ResponseBody JSONObject updateCallDetails(@RequestBody JSONObject callDetails) {
		return callDetailsService.updateCallDetails(callDetails);
	}

	/**
	 * Delete callDetails.
	 * 
	 * @param callDetails
	 * @return callDetails
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public @ResponseBody JSONObject deleteCallDetails(@RequestBody JSONObject callDetailsId) {
		return callDetailsService.deleteCallDetails(callDetailsId);
	}

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public @ResponseBody JSONObject getCallDetailsById(@RequestBody JSONObject callDetails) {
		System.out.println(callDetails);
		if (callDetails.get("key") != null && callDetails.get("key").equals("listAll")) {
			return callDetailsService.listCallDetails();
		} else {
			return callDetailsService.getCallDetailsById(callDetails);
		}
	}
}
