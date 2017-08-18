/**
 * 
 */
package com.lyca.api.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyca.api.model.CallLog;
import com.lyca.api.model.Contacts;
import com.lyca.api.repository.CallLogRepository;
import com.lyca.api.service.CallDetailsService;
import com.lyca.api.service.CallLogService;
import com.lyca.api.service.ContactsService;
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
	private ContactsService contactsService;

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
		List<CallLog> callLogList = null;
		List<CallLog> callLogDetailsIdList = null;
//		List<CallLog> favList = null;
		List<JSONObject> callList = new ArrayList<>();
		List<JSONObject> favCallList = new ArrayList<>();
		if (callLog.get("userId") != null && callLog.get("favCount") != null) {

			Integer userId = Integer.parseInt(callLog.get("userId").toString());
			Integer favCount = Integer.parseInt(callLog.get("favCount").toString());
			DateFormat f = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss' '");
			try {
				callLogList = callLogRepository.getUserFromCallLog(userId, new PageRequest(0,50));
//				callLogDetailsIdList = callLogRepository.getCallDetailsIdByUserId(CallLog.CallStatus.GROUPCALL, userId, new PageRequest(0,5));
				
				List<Object[]> favList = callLogRepository.getFavList(CallDetails.CallStatus.GROUPCALL, userId, new PageRequest(0,5));
				System.out.println(favList);
				if (!callLogList.isEmpty()) {
					for (CallLog callLogDetails : callLogList) {

						JSONObject callLogJson = new JSONObject();
						if (callLogDetails.getUser().getUserId() != callLogDetails.getCallDetails().getCallerId()
								.getUserId()) {
							//Missed call & Accept
							User callUser = callLogDetails.getCallDetails().getCallerId();
							Contacts contactUser = contactsService.getContactsByCallUsersAndMobile(userId, callUser.getMobileNumber(), callUser.getUserId());
							callLogJson.put("userId", callUser.getUserId());
							callLogJson.put("mobileNumber", callUser.getMobileNumber());
							if (contactUser != null) {
								callLogJson.put("nickName", contactUser.getNickName());
								if (contactUser.getContactBlocked() == true) {
									callLogJson.put("onlineStatus", User.OnlineStatus.OFFLINE);
								} else {
									callLogJson.put("onlineStatus", callUser.getOnlineStatus());
								}
							} else {
								callLogJson.put("firstName", callUser.getFirstName());
								callLogJson.put("lastName", callUser.getLastName());
								callLogJson.put("onlineStatus", callUser.getOnlineStatus());
							}
							if (callLogDetails.getInCallTime() != null) {
								Date date = new Date(callLogDetails.getInCallTime().getTime());
								callLogJson.put("inCallTime", f.format(date));
							} else {
								callLogJson.put("inCallTime", callLogDetails.getInCallTime());
							}
							
							if (callLogDetails.getOutCallTime() != null) {
								Date date2 = new Date(callLogDetails.getOutCallTime().getTime());
								callLogJson.put("outCallTime", f.format(date2));
							} else {
								callLogJson.put("outCallTime", callLogDetails.getOutCallTime());
							}
							callLogJson.put("lycaSubscriberId", callUser.getLycaSubscriberId());
							if (callLogDetails.getOutCallTime() != null && callLogDetails.getInCallTime() != null) {
								callLogJson.put("callDuration", 
										DateConvertUtil.getDateDiff(callLogDetails.getOutCallTime().toString(), 
												callLogDetails.getInCallTime().toString()));
							}
							callLogJson.put("profileStatus", callUser.getProfileStatus());
							if (callLogDetails.getCallStatus() != null) {
								if (callLogDetails.getCallStatus().toString().equals(CallLog.CallStatus.MISSEDCALL.toString())) {
								callLogJson.put("callStatus", callLogDetails.getCallStatus());
								} else if (callLogDetails.getCallStatus().toString().equals(CallLog.CallStatus.GROUPCALL.toString())) {
									callLogJson.put("callStatus", callLogDetails.getCallStatus());
								} else {
									callLogJson.put("callStatus", "INCOMING");
								}
							}
							
							
							if (callUser.getCountry() != null) {
								callLogJson.put("countryId", callUser.getCountry().getCountryId());
								callLogJson.put("countryIsdCode", callUser.getCountry().getCountryIsdCode());
								callLogJson.put("countryName", callUser.getCountry().getCountryName());
							}
							callList.add(callLogJson);
						} else {
							// Outgoing
							User callToUser = callLogDetails.getCallDetails().getCallTo();
							Contacts contactUser = contactsService.getContactsByCallUsersAndMobile(userId, callToUser.getMobileNumber(), callToUser.getUserId());
							callLogJson.put("userId", callToUser.getUserId());
							callLogJson.put("mobileNumber", callToUser.getMobileNumber());
							if (contactUser != null) {
								callLogJson.put("nickName", contactUser.getNickName());
								if (contactUser.getContactBlocked() == true) {
									callLogJson.put("onlineStatus", User.OnlineStatus.OFFLINE);
								} else {
									callLogJson.put("onlineStatus", callToUser.getOnlineStatus());
								}
							} else {
								callLogJson.put("firstName", callToUser.getFirstName());
								callLogJson.put("lastName", callToUser.getLastName());
								callLogJson.put("onlineStatus", callToUser.getOnlineStatus());
							}
							callLogJson.put("userId", callToUser.getUserId());
							callLogJson.put("mobileNumber", callToUser.getMobileNumber());
//							
							if (callLogDetails.getInCallTime() != null) {
								Date date = new Date(callLogDetails.getInCallTime().getTime());
								callLogJson.put("inCallTime", f.format(date));
							} else {
								callLogJson.put("inCallTime", callLogDetails.getInCallTime());
							}
							
							if (callLogDetails.getOutCallTime() != null) {
								Date date2 = new Date(callLogDetails.getOutCallTime().getTime());
								callLogJson.put("outCallTime", f.format(date2));
							} else {
								callLogJson.put("outCallTime", callLogDetails.getOutCallTime());
							}
							callLogJson.put("lycaSubscriberId", callToUser.getLycaSubscriberId());
							if (callLogDetails.getOutCallTime() != null && callLogDetails.getInCallTime() != null) {
								callLogJson.put("callDuration", 
										DateConvertUtil.getDateDiff(callLogDetails.getOutCallTime().toString(), 
												callLogDetails.getInCallTime().toString()));
							}
							callLogJson.put("profileStatus", callToUser.getProfileStatus());
							if (callLogDetails.getCallDetails().getCallStatus() != null) {
								if (callLogDetails.getCallDetails().getCallStatus().toString().equals(CallLog.CallStatus.GROUPCALL.toString())) {
									callLogJson.put("callStatus", callLogDetails.getCallDetails().getCallStatus());
								} else {
									callLogJson.put("callStatus", "OUTGOING");
								}
							} else if (callLogDetails.getCallStatus() != null) {
								callLogJson.put("callStatus", callLogDetails.getCallStatus());
							}
							if (callToUser.getCountry() != null) {
								callLogJson.put("countryId", callToUser.getCountry().getCountryId());
								callLogJson.put("countryIsdCode", callToUser.getCountry().getCountryIsdCode());
								callLogJson.put("countryName", callToUser.getCountry().getCountryName());
							}
							callList.add(callLogJson);
						}
						status.put("responseStatus", true);
						status.put("callLog", callList);
						status.put("responseCallLogMessage", "Call log list");
					}
//
//						for (CallLog callLogDetails : callLogDetailsIdList) {
//							favList = callLogRepository.getCallLogFav(CallLog.CallStatus.GROUPCALL,
//									callLogDetails.getCallDetails().getId(), userId, new PageRequest(0,5));

							if (!favList.isEmpty()) {
								for (Object[] callLogDetails : favList) {

									JSONObject callLogJson = new JSONObject();

									callLogJson.put("userId", String.valueOf(callLogDetails[1]));
									callLogJson.put("callStatus", String.valueOf(callLogDetails[2]));
									callLogJson.put("firstName", String.valueOf(callLogDetails[3]));
									callLogJson.put("lastName", String.valueOf(callLogDetails[4]));
									callLogJson.put("mobileNumber", String.valueOf(callLogDetails[5]));
									callLogJson.put("profileStatus", String.valueOf(callLogDetails[6]));
									callLogJson.put("onlineStatus", String.valueOf(callLogDetails[7]));
									callLogJson.put("countryId", String.valueOf(callLogDetails[8]));
									callLogJson.put("countryIsdCode", String.valueOf(callLogDetails[9]));
									callLogJson.put("countryName", String.valueOf(callLogDetails[10]));
									callLogJson.put("callCount", String.valueOf(callLogDetails[11]));
									callLogJson.put("nickName", String.valueOf(callLogDetails[12]));

									favCallList.add(callLogJson);
									status.put("responseStatus", true);
									status.put("favoriteList", favCallList);
									status.put("responseFavMessage", "Favorite list");

								}
//								for (CallLog favLogDetails : favList) {
//
//									JSONObject callLogJson = new JSONObject();
//
//									callLogJson.put("userId", favLogDetails.getUser().getUserId());
//									callLogJson.put("callStatus", favLogDetails.getCallStatus());
//									callLogJson.put("firstName", favLogDetails.getUser().getFirstName());
//									callLogJson.put("lastName", favLogDetails.getUser().getLastName());
//									callLogJson.put("mobileNumber", favLogDetails.getUser().getMobileNumber());
//									callLogJson.put("profileStatus", favLogDetails.getUser().getProfileStatus());
//									callLogJson.put("onlineStatus", favLogDetails.getUser().getOnlineStatus());
//									callLogJson.put("countryId", favLogDetails.getUser().getCountry().getCountryId());
//									callLogJson.put("countryIsdCode",
//											favLogDetails.getUser().getCountry().getCountryIsdCode());
//									callLogJson.put("countryName",
//											favLogDetails.getUser().getCountry().getCountryName());
//									// callLogJson.put("callCount", favLogDetails.getCallDetails().);
//									favCallList.add(callLogJson);
//								}
//							}
//							status.put("responseStatus", true);
//							status.put("favoriteList", favCallList);
//							status.put("responseFavMessage", "Favorite list");
					} else {
						status.put("responseStatus", true);
						status.put("favoriteList", favCallList);
						status.put("responseFavMessage", responseMessage.get("favorite.list.is.empty"));
						return status;
					}
					return status;
				} else {
					status.put("responseStatus", false);
					status.put("responseFavMessage", responseMessage.get("favorite.list.is.empty"));
					status.put("responseCallLogMessage", "Call log list is empty");
					System.out.println(" Inside Rest DAO calllog Status=" + status);
					return status;
				}
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseStatus", false);
				status.put("responseCallLogMessage", responseMessage.get("call.list.retrive.error"));
				status.put("responseFavMessage", "Favorite list retrive error");
				status.put("Error", e.getMessage());
			}
		} else {
			status.put("responseStatus", false);
			status.put("responseFavMessage", responseMessage.get("id.or.key/value.is.null.or.incorrect"));
			status.put("responseCallLogMessage", responseMessage.get("call.list.retrive.error"));
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
