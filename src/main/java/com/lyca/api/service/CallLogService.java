/**
 * 
 */
package com.lyca.api.service;

import java.util.List;
import org.json.simple.JSONObject;
import com.lyca.api.util.CRUDService;
import com.lyca.api.model.CallLog;

/**
 * @author Krishna
 *
 */
public interface CallLogService extends CRUDService<CallLog> {

	/**
	 * Create CallLog.
	 * 
	 * @param callLog
	 * @return callLog
	 */
	public JSONObject addCallLog(JSONObject callLog);

	/**
	 * List CallLog.
	 * 
	 * @param callLog
	 * @return callLog
	 */
	public JSONObject listCallLog();

	/**
	 * Update CallLog.
	 * 
	 * @param callLog
	 * @return callLog
	 */
	public JSONObject updateCallLog(JSONObject callLog);

	/**
	 * Delete CallLog.
	 * 
	 * @param callLog
	 * @return callLog
	 */
	public JSONObject deleteCallLog(JSONObject callLogId);

	/**
	 * @param callLog
	 * @return
	 */
	public JSONObject getCallLogById(JSONObject callLog);

	public CallLog getCallLogByCallIdAndUserId(Integer callDetailsId, Integer callToId);

	public CallLog updateCallLog(CallLog callLogDetails);

	public List<CallLog> getCallLogCallDetailsId(Integer callDetailsId);

	public List<CallLog> getOnCallLiveUsers(Integer callDetailsId, Boolean outCallTimeStatus);

	public CallLog updateCallLogInitiater(CallLog callLogDetails);

	public CallLog checkActiveCallFromOutTime(Integer callerId);

	public CallLog updateActiveCall(CallLog callLogDetails, Integer integer);

	public List<CallLog> getOnCallLiveUsersWithOutTimeNOTNull(Integer callDetailsId);

	public List<CallLog> getOnCallLiveUsersWithOutRejAndMisCall(Integer callDetailsId, Boolean outCallTimeStatus);

	public List<CallLog> getCallLogByCallIdAndUserIdList(Integer callDetailsId, Integer callUserId);
}
