/**
 * 
 */
package com.lyca.api.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyca.api.model.CallLog;
import com.lyca.api.repository.CallLogRepository;
import com.lyca.api.service.CallDetailsService;
import com.lyca.api.service.CallLogService;
import com.lyca.api.util.DateConvertUtil;
import com.lyca.api.model.User;
import com.lyca.api.model.CallDetails;

/**
 * @author Krishna
 *
 */
@Service
@SuppressWarnings("unchecked")
public class CallLogServiceImpl implements CallLogService {

	@Autowired
	private CallLogRepository callLogRepository;

	@Autowired
	private CallDetailsService callDetailsService;

	@Autowired
	@Qualifier("responseMessage")
	private Properties responseMessage;

	@Override
	public JSONObject addCallLog(JSONObject callLog) {
		JSONObject status = new JSONObject();

		ObjectMapper om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		CallLog callLogDetails = om.convertValue(callLog, CallLog.class);
		try {
			if (callLog.get("callDetailsUpdateFlag") != null
					&& (Boolean) callLog.get("callDetailsUpdateFlag") == true) {

				CallDetails callDetailsDetails = callDetailsService.find(callLogDetails.getCallDetails().getId());
				callDetailsDetails.setUpdatedDateTime(new Date());

				if (callLog.get("callStatus").toString().equals(CallDetails.CallStatus.GROUPCALL.toString())) {
					callDetailsDetails.setCallStatus(CallDetails.CallStatus.GROUPCALL);
					callDetailsService.save(callDetailsDetails);
					status.put("responseMessage", responseMessage.get("group.call.activated"));
				}

				if (callLog.get("callStatus").toString().equals(CallDetails.CallStatus.ATTENDED.toString())) {
					callDetailsDetails.setCallStatus(CallDetails.CallStatus.ATTENDED);
					callDetailsService.save(callDetailsDetails);
					status.put("responseMessage", responseMessage.get("call.attended"));
				}

				if (callLog.get("callStatus").toString().equals(CallDetails.CallStatus.MISSEDCALL.toString())) {
					callDetailsDetails.setCallStatus(CallDetails.CallStatus.MISSEDCALL);
					callDetailsService.save(callDetailsDetails);
					status.put("responseMessage", responseMessage.get("call.not.attended.missed.call"));
				}

				if (callLog.get("callStatus").toString().equals(CallDetails.CallStatus.REJECTED.toString())) {
					callDetailsDetails.setCallStatus(CallDetails.CallStatus.REJECTED);
					callDetailsService.save(callDetailsDetails);
					status.put("responseMessage", responseMessage.get("call.rejected"));
				}

				if (callLog.get("callStatus").toString().equals(CallDetails.CallStatus.CALLEND.toString())) {
					callDetailsDetails.setCallStatus(CallDetails.CallStatus.CALLEND);
					callDetailsService.save(callDetailsDetails);
					status.put("responseMessage", responseMessage.get("call.ended"));
				}
				if (callLog.get("callStatus").toString().equals(CallDetails.CallStatus.DISCONNECTED.toString())) {
					callDetailsDetails.setCallStatus(CallDetails.CallStatus.DISCONNECTED);
					callDetailsService.save(callDetailsDetails);
					status.put("responseMessage", responseMessage.get("call.disconnected"));
				}
				callLogDetails.setCreatedDateTime(new Date());
				System.out.println("Inside Dao11 Add Call log");
				callLogRepository.save(callLogDetails);

				// Update call status
				callLogRepository.updateCallStatus(callLogDetails.getCallDetails().getId(),
						callLogDetails.getCallStatus());
				status.put("responseStatus", true);
				status.put("responseMessage", "CallLog details saved");
			} else if (callLog.get("callDetailsUpdateFlag") != null
					&& (Boolean) callLog.get("callDetailsUpdateFlag") == false) {
				CallDetails callDetailsDetails = callDetailsService.find(callLogDetails.getCallDetails().getId());
				callDetailsDetails.setUpdatedDateTime(new Date());

				if (callLog.get("callStatus").toString().equals(CallDetails.CallStatus.GROUPCALL.toString())) {

					callDetailsDetails.setCallStatus(CallDetails.CallStatus.GROUPCALL);
					callDetailsService.save(callDetailsDetails);
					status.put("responseMessage", responseMessage.get("group.call.activated"));
				}
				System.out.println("Inside Dao11 Add Call log");
				// Update call status
				callLogRepository.updateCallStatusWithDateTime(callLogDetails.getCallDetails().getId(),
						callLogDetails.getCallStatus(), new Date());
				status.put("responseStatus", true);
				status.put("responseMessage", "CallLog details updated");
			}
		} catch (Exception e) {
			e.printStackTrace();
			status.put("responseStatus", false);
			status.put("responseSuccess", "CallLog details save failed");
		}
		return status;
	}

	@Override
	public JSONObject listCallLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject updateCallLog(JSONObject callLog) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject deleteCallLog(JSONObject callLogId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getCallLogById(JSONObject callLog) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		List<CallLog> callLogList = null;
		List<CallLog> callLogDetailsIdList = null;
		List<CallLog> favList = null;
		List<JSONObject> callList = new ArrayList<>();
		List<JSONObject> favCallList = new ArrayList<>();
		if (callLog.get("userId") != null && callLog.get("favCount") != null) {

			Integer userId = Integer.parseInt(callLog.get("userId").toString());
			Integer favCount = Integer.parseInt(callLog.get("favCount").toString());
			try {
				callLogList = callLogRepository.getUserFromCallLog(userId);
				callLogDetailsIdList = callLogRepository.getCallDetailsIdByUserId(CallLog.CallStatus.GROUPCALL, userId);
				if (!callLogList.isEmpty()) {
					for (CallLog callLogDetails : callLogList) {

						JSONObject callLogJson = new JSONObject();
						if (callLogDetails.getUser().getUserId() != callLogDetails.getCallDetails().getCallerId()
								.getUserId()) {
							// Outgoing
							User callUser = callLogDetails.getCallDetails().getCallerId();
							callLogJson.put("userId", callUser.getUserId());
							callLogJson.put("mobileNumber", callUser.getMobileNumber());
							callLogJson.put("firstName", callUser.getFirstName());
							callLogJson.put("lastName", callUser.getLastName());
							callLogJson.put("inCallTime", callLogDetails.getInCallTime());
							callLogJson.put("outCallTime", callLogDetails.getOutCallTime());
							callLogJson.put("lycaSubscriberId", callUser.getLycaSubscriberId());
							if (callLogDetails.getOutCallTime() != null && callLogDetails.getInCallTime() != null) {
								
//								
//								Long hh = callLogDetails.getOutCallTime().getTime() - callLogDetails.getInCallTime().getTime();
//								
//								int seconds = (int) (hh / 1000) % 60 ;
//								int minutes = (int) ((hh / (1000*60)) % 60);
//								int hours   = (int) ((hh / (1000*60*60)) % 24);
								
								callLogJson.put("callDuration", 
										DateConvertUtil.getDateDiff(callLogDetails.getOutCallTime().toString(), 
												callLogDetails.getInCallTime().toString()));
							}
							callLogJson.put("profileStatus", callUser.getProfileStatus());
							if (callLogDetails.getCallDetails().getCallStatus() != null) {
								if (callLogDetails.getCallDetails().getCallStatus().toString().equals(CallLog.CallStatus.GROUPCALL.toString())) {
									callLogJson.put("callStatus", callLogDetails.getCallDetails().getCallStatus());
								} else {
									callLogJson.put("callStatus", "OUTGOING");
								}
							} else if (callLogDetails.getCallStatus() != null) {
								callLogJson.put("callStatus", callLogDetails.getCallStatus());
							}
							if (callUser.getCountry() != null) {
								callLogJson.put("countryId", callUser.getCountry().getCountryId());
								callLogJson.put("countryIsdCode", callUser.getCountry().getCountryIsdCode());
								callLogJson.put("countryName", callUser.getCountry().getCountryName());
							}
							callLogJson.put("onlineStatus", callUser.getOnlineStatus());
							callList.add(callLogJson);
						} else {
							//Missed call & Accept
							User callToUser = callLogDetails.getCallDetails().getCallTo();
							callLogJson.put("userId", callToUser.getUserId());
							callLogJson.put("mobileNumber", callToUser.getMobileNumber());
							callLogJson.put("firstName", callToUser.getFirstName());
							callLogJson.put("lastName", callToUser.getLastName());
							callLogJson.put("inCallTime", callLogDetails.getInCallTime());
							callLogJson.put("outCallTime", callLogDetails.getOutCallTime());
							callLogJson.put("lycaSubscriberId", callToUser.getLycaSubscriberId());
							if (callLogDetails.getOutCallTime() != null && callLogDetails.getInCallTime() != null) {
//								callLogJson.put("callDuration", callLogDetails.getOutCallTime().getTime()
//										- callLogDetails.getInCallTime().getTime());
//								Long hh = callLogDetails.getOutCallTime(). - callLogDetails.getInCallTime().getTime();
//								
//								int seconds = (int) (hh / 1000) % 60 ;
//								int minutes = (int) ((hh / (1000*60)) % 60);
								
								callLogJson.put("callDuration", 
										DateConvertUtil.getDateDiff(callLogDetails.getOutCallTime().toString(), 
												callLogDetails.getInCallTime().toString()));
							}
							callLogJson.put("profileStatus", callToUser.getProfileStatus());
							if (callLogDetails.getCallStatus() != null) {
								if (callLogDetails.getCallStatus().toString().equals(CallLog.CallStatus.MISSEDCALL.toString())) {
								callLogJson.put("callStatus", callLogDetails.getCallStatus());
								} else if (callLogDetails.getCallStatus().toString().equals(CallLog.CallStatus.GROUPCALL.toString())) {
									callLogJson.put("callStatus", callLogDetails.getCallStatus());
								} else {
									callLogJson.put("callStatus", "INCOMING");
								}
							}
							if (callToUser.getCountry() != null) {
								callLogJson.put("countryId", callToUser.getCountry().getCountryId());
								callLogJson.put("countryIsdCode", callToUser.getCountry().getCountryIsdCode());
								callLogJson.put("countryName", callToUser.getCountry().getCountryName());
							}
							callLogJson.put("onlineStatus", callToUser.getOnlineStatus());
							callList.add(callLogJson);
						}

						status.put("callLog", callList);
						status.put("responseCallLogMessage", "Call log list");
					}
					if (!callLogDetailsIdList.isEmpty()) {

						for (CallLog callLogDetails : callLogDetailsIdList) {
							favList = callLogRepository.getCallLogFav(CallLog.CallStatus.GROUPCALL,
									callLogDetails.getCallDetails().getId(), userId);

							if (!favList.isEmpty()) {
								for (CallLog favLogDetails : favList) {

									JSONObject callLogJson = new JSONObject();

									callLogJson.put("userId", favLogDetails.getUser().getUserId());
									callLogJson.put("callStatus", favLogDetails.getCallStatus());
									callLogJson.put("firstName", favLogDetails.getUser().getFirstName());
									callLogJson.put("lastName", favLogDetails.getUser().getLastName());
									callLogJson.put("mobileNumber", favLogDetails.getUser().getMobileNumber());
									callLogJson.put("profileStatus", favLogDetails.getUser().getProfileStatus());
									callLogJson.put("onlineStatus", favLogDetails.getUser().getOnlineStatus());
									callLogJson.put("countryId", favLogDetails.getUser().getCountry().getCountryId());
									callLogJson.put("countryIsdCode",
											favLogDetails.getUser().getCountry().getCountryIsdCode());
									callLogJson.put("countryName",
											favLogDetails.getUser().getCountry().getCountryName());
									// callLogJson.put("callCount", favLogDetails.getCallDetails().);
									favCallList.add(callLogJson);
								}
							}
							status.put("favoriteList", favCallList);
							status.put("responseFavMessage", "Favorite list");
						}
					} else {
						status.put("responseStatus", true);
						status.put("responseFavMessage", responseMessage.get("favorite.list.is.empty"));
						return status;
					}
					return status;
				} else {
					status.put("responseStatus", false);
					status.put("responseFavMessage", responseMessage.get("call.did.not.initiate"));
					System.out.println(" Inside Rest DAO calllog Status=" + status);
					return status;
				}
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseResult", false);
				status.put("responseErrorMessage", responseMessage.get("call.list.retrive.error"));
				status.put("Error", e.getMessage());
			}
		} else {
			status.put("responseStatus", false);
			status.put("responseFavMessage", responseMessage.get("id.or.key/value.is.null.or.incorrect"));
			return status;
		}
		return status;
	}

	@Override
	public CallLog getCallLogByCallIdAndUserId(Integer callDetailsId, Integer callToId) {
		JSONObject status = new JSONObject();
		status.put("responseResult", true);
		List<CallLog> callLogList = null;
		if (callDetailsId != null && callToId != null) {

			try {
				callLogList = callLogRepository.getCallLogByCallIdAndUserId(callDetailsId, callToId);
				if (callLogList.size() == 1) {
					for (CallLog callLogDetails : callLogList) {

						List<CallLog> call = new ArrayList<>();
						call.add(callLogDetails);
						return call.get(0);
					}
				} else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseResult", false);
			}
		} else {
			return null;
		}
		return callLogList.get(0);
	}

	@Override
	public CallLog updateCallLog(CallLog callLogDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CallLog> getCallLogCallDetailsId(Integer callDetailsId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CallLog> getOnCallLiveUsers(Integer callDetailsId, Boolean outCallTimeStatus) {
		JSONObject status = new JSONObject();
		status.put("responseResult", true);
		List<CallLog> callLogList = null;
		if (callDetailsId != null) {
			try {
				callLogList = callLogRepository.getOnCallLiveUsers(callDetailsId);
				return callLogList;
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseResult", false);
			}
		} else {
			return null;
		}
		return callLogList;
	}

	@Override
	public CallLog updateCallLogInitiater(CallLog callLogDetails) {
		try {
			callLogRepository.updateCallLogInitiater(callLogDetails.getCallDetails().getId(),
					callLogDetails.getCallStatus(), callLogDetails.getOutCallTime(),
					callLogDetails.getUpdatedDateTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return callLogDetails;
	}

	@Override
	public CallLog checkActiveCallFromOutTime(Integer callerId) {
		JSONObject status = new JSONObject();
		status.put("responseResult", true);
		List<CallLog> callLogList = null;
		if (callerId != null) {
			try {
				callLogList = callLogRepository.checkActiveCallFromOutTime(callerId);
				if (callLogList.size() > 0) {
					for (CallLog callLogDetails : callLogList) {

						List<CallLog> call = new ArrayList<>();
						call.add(callLogDetails);
						return call.get(0);
					}
				} else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseResult", false);
			}
		} else {
			return null;
		}
		return callLogList.get(0);
	}

	@Override
	public CallLog updateActiveCall(CallLog callLogDetails, Integer callerId) {
		try {
			callLogRepository.updateActiveCall(callerId, callLogDetails.getCallDetails().getId(),
					callLogDetails.getCallStatus(), callLogDetails.getOutCallTime(),
					callLogDetails.getUpdatedDateTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return callLogDetails;
	}

	@Override
	public CallLog save(CallLog t) throws Exception {
		return callLogRepository.save(t);
	}

	@Override
	public CallLog update(CallLog t) throws Exception {
		return callLogRepository.save(t);
	}

	@Override
	public void delete(CallLog t) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Integer id) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public CallLog find(Integer id) throws Exception {
		return callLogRepository.findOne(id);
	}

	@Override
	public List<CallLog> findAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CallLog> getOnCallLiveUsersWithOutTimeNOTNull(Integer callDetailsId) {
		JSONObject status = new JSONObject();
		status.put("responseResult", true);
		List<CallLog> callLogList = null;
		if (callDetailsId != null) {
			try {
				callLogList = callLogRepository.getOnCallLiveUsersWithOutTimeNOTNull(callDetailsId);
				return callLogList;
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseResult", false);
			}
		} else {
			return null;
		}
		return callLogList;
	}
}
