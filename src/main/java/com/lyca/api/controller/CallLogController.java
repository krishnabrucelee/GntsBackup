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
import com.lyca.api.service.CallLogService;

/**
 * @author Krishna
 *
 */
@RestController
@RequestMapping("/callLog")
public class CallLogController {

	/**
	 * CallLog Service.
	 */
	@Autowired
	private CallLogService callLogService;

	/**
	 * Create CallLog.
	 * 
	 * @param callLog
	 * @return callLog
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody JSONObject addCallLog(@RequestBody JSONObject callLog) {
		return callLogService.addCallLog(callLog);
	}
	
	/**
	 * Update CallLog.
	 * 
	 * @param callLog
	 * @return callLog
	 */
	@RequestMapping(value = "/update")
	public @ResponseBody JSONObject updateCallLog(@RequestBody JSONObject callLog) {
		return callLogService.updateCallLog(callLog);
	}

	/**
	 * Delete callLog.
	 * 
	 * @param callLog
	 * @return callLog
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public @ResponseBody JSONObject deleteCallLog(@RequestBody JSONObject callLogId) {
		return callLogService.deleteCallLog(callLogId);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public @ResponseBody JSONObject getCallLogById(@RequestBody JSONObject callLog) {
		System.out.println(callLog);
		if (callLog.get("key") != null && callLog.get("key").equals("listAll")) {
			return callLogService.listCallLog();
		} else {
			return callLogService.getCallLogById(callLog);
		}
	}
}
