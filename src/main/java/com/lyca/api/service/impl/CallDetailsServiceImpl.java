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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyca.api.model.CallDetails;
import com.lyca.api.repository.CallDetailsRepository;
import com.lyca.api.service.CallDetailsService;
import com.lyca.api.service.UserService;
import com.lyca.api.service.ContactsService;
import com.lyca.api.model.Contacts;
import com.lyca.api.model.CallDetails.CallStatus;
import com.lyca.api.service.CallLogService;
import com.lyca.api.service.PusherNotificationService;
import com.lyca.api.model.CallLog;
import com.lyca.api.model.User;

/**
 * @author Krishna
 *
 */
@Service
@SuppressWarnings("unchecked")
public class CallDetailsServiceImpl implements CallDetailsService {

	@Autowired
	private CallDetailsRepository callDetailsRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private CallLogService callLogService;

	@Autowired
	private ContactsService contactsService;

	@Autowired
	private PusherNotificationService pusherNotificationService;

	@Autowired
	@Qualifier("responseMessage")
	private Properties responseMessage;

	@Override
	public JSONObject addCallDetails(JSONObject callDetails) throws Exception {
		JSONObject jsonObj = new JSONObject();
		JSONObject callDetailsStatus = new JSONObject();
		CallDetails callDetailsObj = new CallDetails();
		User user = new User();
		if (callDetails.get("callStatus") != null
				&& callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.CALLING.toString())) {

			if (callDetails.get("callDetailsId") == null) {
				callDetailsStatus = save(callDetails);
				ObjectMapper om = new ObjectMapper();
				om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				callDetailsObj = om.convertValue(callDetailsStatus.get("callDetails"), CallDetails.class);
				Integer callerId = Integer.parseInt(callDetails.get("callerId").toString());
				Integer callToId = Integer.parseInt(callDetails.get("callTo").toString());

				User callToUser = userService.find(callToId);
				user = userService.find(callerId);
				if (callToUser != null && user != null) {
					if ((Boolean) callDetailsStatus.get("responseStatus") == true) {
						JSONObject jsonPushMsg = new JSONObject();
						List<User> callerList = new ArrayList<>();
						callerList.add(user);
						Contacts contactUser = contactsService.getContactsByCallUsersAndMobile(callToUser.getUserId(),
								user.getMobileNumber(), user.getUserId());
						jsonPushMsg.put("callMembers", callerList);
						jsonPushMsg.put("callerNickName", contactUser.getNickName());
						jsonPushMsg.put("callDetailsId", callDetailsObj.getId());
						jsonPushMsg.put("message", "Calling...");
						JSONObject jsonpusher = pusherNotificationService.pushMessasge(callToUser.getMobileNumber(),
								"CALL", jsonPushMsg);
						callDetailsStatus.put("pusherResponse", jsonpusher);
					}
				} else {
					jsonObj.put("responseStatus", false);
					jsonObj.put("responseMessage", responseMessage.get("no.user.in.Lyca.database"));
					return jsonObj;
				}

			} else {
				// Group call
				callDetailsStatus.put("responseStatus", true);
				Integer callDetailsId = Integer.parseInt(callDetails.get("callDetailsId").toString());
				callDetailsObj = find(callDetailsId);
				User callerUser = new User();
				Integer callerId = Integer.parseInt(callDetails.get("callerId").toString());
				Integer callToId = Integer.parseInt(callDetails.get("callTo").toString());
				CallLog checkActiveCallTo = callLogService.checkActiveCallFromOutTime(callToId);
				if (checkActiveCallTo != null) {
					jsonObj.put("responseStatus", false);
					jsonObj.put("responseMessage", responseMessage.get("line.busy"));
					return jsonObj;
				}
				List<CallLog> checkActiveCall = callLogService.getOnCallLiveUsersWithOutRejAndMisCall(callDetailsId,
						false);
				user = userService.find(callToId);
				callerUser = userService.find(callerId);
				// check online status b4 calling
				if (user != null && user.getOnlineStatus().equals(User.OnlineStatus.AVAILABLE)) {

					User callUser1 = new User();
					User callUser2 = new User();
					if (checkActiveCall.size() >= 3) {
						// when all out call time is null
						System.out.println("callLog update failed.");
						jsonObj.put("responseStatus", true);
						jsonObj.put("responseMessage",
								responseMessage.getProperty("only.3.members.in.group.call.allowed"));
						return jsonObj;
					} else if (checkActiveCall.size() <= 2) {
						for (int i = 0; i <= checkActiveCall.size(); i++) {
							if (i == 0) {
								callUser1 = checkActiveCall.get(i).getUser();
							}
							if (i == 1) {
								callUser2 = checkActiveCall.get(i).getUser();
							}
						}
						if (user != null && callUser1 != null && callUser2 != null && callerUser != null) {
							if ((Boolean) callDetailsStatus.get("responseStatus") == true) {
								JSONObject jsonPushMsg = new JSONObject();
								List<User> callerList = new ArrayList<>();
								callerList.add(callUser1);
								callerList.add(callUser2);
								Contacts contactUser = contactsService.getContactsByCallUsersAndMobile(user.getUserId(),
										callerUser.getMobileNumber(), callerUser.getUserId());
								jsonPushMsg.put("callerNickName", contactUser.getNickName());
								jsonPushMsg.put("callDetailsId", callDetailsId);
								jsonPushMsg.put("callMembers", callerList);
								jsonPushMsg.put("message", "Calling...");
								JSONObject jsonpusher = pusherNotificationService.pushMessasge(user.getMobileNumber(),
										"CALL", jsonPushMsg);
								callDetailsStatus.put("pusherResponse", jsonpusher);
							}
						} else {
							jsonObj.put("responseStatus", false);
							jsonObj.put("responseMessage", responseMessage.get("no.user.in.Lyca.database"));
							return jsonObj;
						}
					}
				} else {
					jsonObj.put("responseStatus", false);
					jsonObj.put("responseMessage", responseMessage.get("user.is.offline.now"));
					return jsonObj;
				}
			}
			if ((Boolean) callDetailsStatus.get("responseStatus") == true) {

				if (user != null && callDetailsObj != null) {
					jsonObj.put("callDetails", callDetailsObj);
					jsonObj.put("user", user);
					jsonObj.put("callStatus", callDetailsObj.getCallStatus());
					jsonObj.put("callDetailsUpdateFlag", true);
					jsonObj.put("inCallTime", new java.sql.Timestamp(System.currentTimeMillis()));
					JSONObject callLogStatus = callLogService.addCallLog(jsonObj);
					callDetailsStatus.put("callLogStatus", callLogStatus);
					callDetailsStatus.put("responseMessage", "Call log saved.");
				}
			}
			return callDetailsStatus;

		} else if ((callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.ATTENDED.toString()))
				&& callDetails.get("callDetailsId") != null) {

			JSONObject callLogStatus = null;
			Integer callToId = Integer.parseInt(callDetails.get("callTo").toString());
			Integer callDetailsId = Integer.parseInt(callDetails.get("callDetailsId").toString());
			List<CallLog> callLogDetailsList = callLogService.getOnCallLiveUsers(callDetailsId, null);
			if (callLogDetailsList != null && callLogDetailsList.size() > 2) {
				callDetails.put("callStatus", CallDetails.CallStatus.GROUPCALL.toString());
			}
			User callToUser = userService.find(callToId);
			CallDetails callDetailsObj1 = find(callDetailsId);
			if (callToUser != null && callDetailsObj1 != null) {
				if (callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.GROUPCALL.toString())) {
					callDetailsObj1.setCallStatus(CallDetails.CallStatus.GROUPCALL);
					jsonObj.put("callStatus", CallLog.CallStatus.GROUPCALL);
					jsonObj.put("callDetailsUpdateFlag", false);
				}
				if (callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.ATTENDED.toString())) {
					callDetailsObj1.setCallStatus(CallDetails.CallStatus.ATTENDED);
					jsonObj.put("callStatus", CallLog.CallStatus.ATTENDED);
					jsonObj.put("callDetailsUpdateFlag", true);
					JSONObject jsonPushMsg = new JSONObject();

					jsonPushMsg.put("callDetailsId", callDetailsId);
					jsonPushMsg.put("message", "On Call Started..");
					JSONObject jsonpusher = pusherNotificationService.pushMessasge(callToUser.getMobileNumber(),
							"CALLSTARTED", jsonPushMsg);
					jsonObj.put("pusherResponse", jsonpusher);

				}
				jsonObj.put("callDetails", callDetailsObj1);
				jsonObj.put("user", callToUser);
				jsonObj.put("inCallTime", new java.sql.Timestamp(System.currentTimeMillis()));
				callLogStatus = callLogService.addCallLog(jsonObj);
				JSONObject updateStatus = updateCallDetails(callDetails);
			}
			return callLogStatus;
		} else if (callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.MISSEDCALL.toString())
				|| callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.REJECTED.toString())
				|| callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.CALLEND.toString())
				|| callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.DISCONNECTED.toString())
				|| callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.GROUPCALL.toString())) {

			if (callDetails.get("callDetailsId") != null
					&& (callDetails.get("callTo") != null || callDetails.get("userId") != null)) {

				Integer callDetailsId = Integer.parseInt(callDetails.get("callDetailsId").toString());
				Integer callUserId = null;
				// call end
				if (callDetails.get("callTo") != null
						&& (!callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.REJECTED.toString())
								&& !callDetails.get("callStatus").toString()
										.equals(CallDetails.CallStatus.MISSEDCALL.toString())
								&& !callDetails.get("callStatus").toString()
										.equals(CallDetails.CallStatus.DISCONNECTED.toString()))) {
					callUserId = Integer.parseInt(callDetails.get("callTo").toString());
				} else if (callDetails.get("userId") != null) {
					callUserId = Integer.parseInt(callDetails.get("userId").toString());
					callDetails.put("callTo", callUserId);
				}
				if (callDetails.get("callerId") != null
						&& (callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.REJECTED.toString())
								|| callDetails.get("callStatus").toString()
										.equals(CallDetails.CallStatus.MISSEDCALL.toString())
								|| callDetails.get("callStatus").toString()
										.equals(CallDetails.CallStatus.DISCONNECTED.toString()))) {
					callUserId = Integer.parseInt(callDetails.get("callerId").toString());
				}

				List<CallLog> callLogDetailsLists = callLogService.getCallLogByCallIdAndUserIdList(callDetailsId,
						callUserId);
				if (callLogDetailsLists != null) {
					for (CallLog callLogDetails : callLogDetailsLists) {
						if (callLogDetails != null) {
							User callerUser = userService.find(callUserId);

							if (callDetails.get("callStatus").toString()
									.equals(CallLog.CallStatus.MISSEDCALL.toString())) {
								Integer callToId = Integer.parseInt(callDetails.get("callTo").toString());
								User callToUser = userService.find(callToId);
								CallDetails callDetailsObj1 = find(callDetailsId);
								if (callToUser != null && callDetailsObj1 != null) {
									CallLog callLogUser = callLogService
											.getCallLogByCallIdAndUserId(callDetailsObj1.getId(), callToId);
									if (callLogUser == null) {
										JSONObject jsonObj1 = new JSONObject();
										jsonObj1.put("callStatus", CallLog.CallStatus.MISSEDCALL);
										jsonObj1.put("callDetailsUpdateFlag", true);
										jsonObj1.put("inCallTime", new java.sql.Timestamp(System.currentTimeMillis()));
										jsonObj1.put("outCallTime", new java.sql.Timestamp(System.currentTimeMillis()));
										jsonObj1.put("updatedDateTime", new Date());
										jsonObj1.put("callDetails", callDetailsObj1);
										jsonObj1.put("user", callToUser);
										callLogService.addCallLog(jsonObj1);
									} else {
										callLogUser.setOutCallTime(new java.sql.Timestamp(System.currentTimeMillis()));
										callLogUser.setUpdatedDateTime(new Date());
										callLogUser.setCallStatus(CallLog.CallStatus.MISSEDCALL);
										callLogService.update(callLogUser);
									}

								}
								// callLogDetails.setCallStatus(CallLog.CallStatus.MISSEDCALL);
								jsonObj.put("responseStatus", true);
								jsonObj.put("responseMessage", "Missed Call.");
								List<CallLog> callLogDetailsList = callLogService
										.getOnCallLiveUsersWithOutTimeNOTNull(callDetailsId);
								JSONObject jsonPushMsg = new JSONObject();

								jsonPushMsg.put("callDetailsId", callDetailsId);
								jsonPushMsg.put("message", "Missed Call");
								jsonPushMsg.put("callUsersCount", callLogDetailsList.size());
								JSONObject jsonpusher = pusherNotificationService
										.pushMessasge(callerUser.getMobileNumber(), "MCALL", jsonPushMsg);
								jsonObj.put("pusherResponse", jsonpusher);

							}

							if (callDetails.get("callStatus").toString()
									.equals(CallLog.CallStatus.REJECTED.toString())) {
								Integer callToId = Integer.parseInt(callDetails.get("callTo").toString());
								User callToUser = userService.find(callToId);
								CallDetails callDetailsObj1 = find(callDetailsId);
								if (callToUser != null && callDetailsObj1 != null) {
									List<CallLog> callLogUserLists = callLogService
											.getCallLogByCallIdAndUserIdList(callDetailsObj1.getId(), callToId);
									if (callLogUserLists != null) {
										for (CallLog callLogUser : callLogUserLists) {
											if (callLogUser == null) {
												JSONObject jsonObj1 = new JSONObject();
												jsonObj1.put("callStatus", CallLog.CallStatus.REJECTED);
												jsonObj1.put("callDetailsUpdateFlag", true);
												jsonObj1.put("inCallTime",
														new java.sql.Timestamp(System.currentTimeMillis()));
												jsonObj1.put("outCallTime",
														new java.sql.Timestamp(System.currentTimeMillis()));
												jsonObj1.put("updatedDateTime", new Date());
												jsonObj1.put("callDetails", callDetailsObj1);
												jsonObj1.put("user", callToUser);
												callLogService.addCallLog(jsonObj1);
											} else {
												callLogUser.setOutCallTime(
														new java.sql.Timestamp(System.currentTimeMillis()));
												callLogUser.setUpdatedDateTime(new Date());
												callLogUser.setCallStatus(CallLog.CallStatus.REJECTED);
												callLogService.update(callLogUser);
											}
										}
									} else {
										CallLog callLogUser = callLogService
												.getCallLogByCallIdAndUserId(callDetailsObj1.getId(), callToId);
										if (callLogUser == null) {
											JSONObject jsonObj1 = new JSONObject();
											jsonObj1.put("callStatus", CallLog.CallStatus.REJECTED);
											jsonObj1.put("callDetailsUpdateFlag", true);
											jsonObj1.put("inCallTime",
													new java.sql.Timestamp(System.currentTimeMillis()));
											jsonObj1.put("outCallTime",
													new java.sql.Timestamp(System.currentTimeMillis()));
											jsonObj1.put("updatedDateTime", new Date());
											jsonObj1.put("callDetails", callDetailsObj1);
											jsonObj1.put("user", callToUser);
											callLogService.addCallLog(jsonObj1);
										} else {
											callLogUser.setOutCallTime(
													new java.sql.Timestamp(System.currentTimeMillis()));
											callLogUser.setUpdatedDateTime(new Date());
											callLogUser.setCallStatus(CallLog.CallStatus.REJECTED);
											callLogService.update(callLogUser);
										}
										
										// cut the initiator
										List<CallLog> callLogInitiatorUserLists = callLogService
												.getCallLogByCallIdAndUserIdList(callDetailsObj1.getId(), callUserId);
										if (callLogInitiatorUserLists != null) {
											for (CallLog callLogInitiatorUser : callLogInitiatorUserLists) {
												callLogInitiatorUser.setOutCallTime(
														new java.sql.Timestamp(System.currentTimeMillis()));
												callLogInitiatorUser.setUpdatedDateTime(new Date());
												callLogInitiatorUser.setCallStatus(CallLog.CallStatus.CALLEND);
												callLogService.update(callLogInitiatorUser);
											}
										}

									}
								}
								// callLogDetails.setCallStatus(CallLog.CallStatus.REJECTED);
								jsonObj.put("responseStatus", true);
								jsonObj.put("responseMessage", "Call Rejected.");
								JSONObject jsonPushMsg = new JSONObject();
								List<CallLog> callLogDetailsList = callLogService
										.getOnCallLiveUsersWithOutTimeNOTNull(callDetailsId);
								jsonPushMsg.put("callDetailsId", callDetailsId);
								jsonPushMsg.put("message", "Call Rejected");
								jsonPushMsg.put("callUsersCount", callLogDetailsList.size());
								JSONObject jsonpusher = pusherNotificationService
										.pushMessasge(callerUser.getMobileNumber(), "REJECT", jsonPushMsg);
								jsonObj.put("pusherResponse", jsonpusher);
							}

							if (callDetails.get("callStatus").toString()
									.equals(CallDetails.CallStatus.CALLEND.toString())) {
								callLogDetails.setCallStatus(CallLog.CallStatus.CALLEND);
								if (callDetails.get("userId") != null && callDetails.get("callDetailsId") != null) {
									Integer userId = Integer.parseInt(callDetails.get("userId").toString());
									// Check whether user id is Initiator or
									// Receiver
									callDetailsObj = find(callDetailsId);
									if (callDetailsObj != null) {
										if (callDetailsObj.getCallerId().getUserId().intValue() == userId) {
											// Initiator
											callLogDetails
													.setOutCallTime(new java.sql.Timestamp(System.currentTimeMillis()));
											callLogDetails.setUpdatedDateTime(new Date());
											CallLog callLogUpdate = callLogService
													.updateCallLogInitiater(callLogDetails);
											if (callLogUpdate != null) {
												// Pusher
												List<CallLog> callLogDetailsList = callLogService
														.getOnCallLiveUsersWithOutTimeNOTNull(callDetailsId);
												if (callLogDetailsList != null && callLogDetailsList.size() > 2) {
													for (CallLog callL : callLogDetailsList) {
														if (callL.getUser().getUserId().intValue() != userId) {
															JSONObject jsonPushMsg = new JSONObject();

															jsonPushMsg.put("callDetailsId", callDetailsId);
															jsonPushMsg.put("message", "Call End");
															jsonPushMsg.put("callUsersCount",
																	callLogDetailsList.size());
															JSONObject jsonpusher = pusherNotificationService
																	.pushMessasge(callL.getUser().getMobileNumber(),
																			"CALLEND", jsonPushMsg);
															jsonObj.put("pusherResponse", jsonpusher);
														}
													}
												} else if (callLogDetailsList != null
														&& callLogDetailsList.size() == 1) {
													for (CallLog callL : callLogDetailsList) {
														if (callL.getUser().getUserId().intValue() == userId) {
															JSONObject jsonPushMsg = new JSONObject();

															jsonPushMsg.put("callDetailsId", callDetailsId);
															jsonPushMsg.put("message", "Call End");
															jsonPushMsg.put("callUsersCount",
																	callLogDetailsList.size());
															JSONObject jsonpusher = pusherNotificationService
																	.pushMessasge(
																			callDetailsObj.getCallTo()
																					.getMobileNumber(),
																			"CALLEND", jsonPushMsg);
															jsonObj.put("pusherResponse", jsonpusher);
														}
													}
												}
												System.out.println("callLog updated.");
												jsonObj.put("responseStatus", true);
												jsonObj.put("responseMessage", "Call End.");
											} else {
												System.out.println("callLog update failed.");
												jsonObj.put("responseStatus", false);
												jsonObj.put("responseMessage", "callLog update failed.");
											}
										} else {
											// Receiver
											callLogDetails
													.setOutCallTime(new java.sql.Timestamp(System.currentTimeMillis()));
											callLogDetails.setUpdatedDateTime(new Date());
											CallLog callLogUpdate = callLogService.update(callLogDetails);
											List<CallLog> checkActiveCall = callLogService
													.getOnCallLiveUsers(callLogDetails.getCallDetails().getId(), false);
											if (checkActiveCall.size() == 1) {
												CallLog callLogUpdateActiveCall = callLogService.updateActiveCall(
														callLogDetails,
														callLogDetails.getCallDetails().getCallerId().getUserId());

											}
											if (callLogUpdate != null) {
												System.out.println("callLog updated.");
												jsonObj.put("responseStatus", true);
												jsonObj.put("responseMessage", "Call End.");
											} else {
												System.out.println("callLog update failed.");
												jsonObj.put("responseStatus", false);
												jsonObj.put("responseMessage", "callLog update failed.");
											}
										}
									}
								} else {
									jsonObj.put("responseStatus", false);
									jsonObj.put("responseMessage",
											responseMessage.get("id.or.key/value.is.null.or.incorrect"));
									return jsonObj;
								}
								callLogDetails.setCallStatus(CallLog.CallStatus.CALLEND);
							}
							if (callDetails.get("callStatus").toString()
									.equals(CallLog.CallStatus.DISCONNECTED.toString())) {
								callLogDetails.setCallStatus(CallLog.CallStatus.DISCONNECTED);
								jsonObj.put("responseStatus", true);
								jsonObj.put("responseMessage", "Call Disconnected.");
							}
							if (!callDetails.get("callStatus").toString()
									.equals(CallDetails.CallStatus.CALLEND.toString())) {
								if (callLogDetails.getUser().getUserId().intValue() != callUserId) {
									callLogDetails.setOutCallTime(new java.sql.Timestamp(System.currentTimeMillis()));
									callLogDetails.setUpdatedDateTime(new Date());
								}

								CallLog callLogUpdate = callLogService.update(callLogDetails);
								if (callLogUpdate != null) {
									System.out.println("callLog updated.");
								} else {
									System.out.println("callLog update failed.");
									jsonObj.put("responseStatus", false);
									jsonObj.put("responseMessage", "callLog update failed.");
									return jsonObj;
								}
							}
						} else {
							System.out.println("callLog get failed.");
							jsonObj.put("responseStatus", false);
							jsonObj.put("responseMessage", responseMessage.get("call.did.not.initiate"));
							return jsonObj;
						}
					}
				}
			}
		}
		return jsonObj;
	}

	private JSONObject save(JSONObject callDetails) throws Exception {
		JSONObject status = new JSONObject();
		Integer callerId = null;
		Integer callTo = null;
		User calleruser = null;
		User callTouser = null;
		if (callDetails.get("callerId") != null && callDetails.get("callTo") != null) {
			callerId = Integer.parseInt(callDetails.get("callerId").toString());

			// Check caller in Active
			CallLog checkActiveCall = callLogService.checkActiveCallFromOutTime(callerId);

			if (checkActiveCall == null) {

				calleruser = userService.find(callerId);

				callTo = Integer.parseInt(callDetails.get("callTo").toString());
				CallLog checkActiveCallTo = callLogService.checkActiveCallFromOutTime(callTo);
				if (checkActiveCallTo == null) {

					callTouser = userService.find(callTo);

					if (calleruser != null && callTouser != null) {
						// Check stb user and user calling only to his contact.
						if (calleruser.getStbUser() == true || callTouser.getStbUser() == true) {
							Contacts contact = contactsService.checkContactToCallFromCaller(calleruser.getUserId(),
									callTouser.getMobileNumber());
							if (contact != null) {
								// check online status of call to user
								if (callTouser.getOnlineStatus().equals(User.OnlineStatus.AVAILABLE)) {
									try {
										CallDetails callDetailsDetails = new CallDetails();
										callDetailsDetails.setCallerId(calleruser);
										callDetailsDetails.setCallTo(callTouser);
										callDetailsDetails.setCallStatus(CallStatus.CALLING);
										callDetailsDetails.setCreatedDateTime(new Date());
										CallDetails callObj = save(callDetailsDetails);
										status.put("callDetails", callObj);
										status.put("responseStatus", true);
										status.put("responseMessage", "CallDetails details saved");

									} catch (Exception e) {
										e.printStackTrace();
										status.put("status", false);
									}
								} else {
									status.put("responseStatus", false);
									status.put("responseMessage", responseMessage.get("user.is.offline.now"));
									return status;
								}
							} else {
								status.put("responseStatus", false);
								status.put("responseMessage", responseMessage.get("calling.user.not.in.your.contact"));
								return status;
							}
						} else {
							status.put("responseStatus", false);
							status.put("responseMessage", responseMessage.get("anyone.user.must.be.an.Lyca.STB.user"));
							return status;
						}
					} else {
						status.put("responseStatus", false);
						status.put("responseMessage", responseMessage.get("no.user.in.Lyca.database"));
						return status;
					}
				} else {
					status.put("responseStatus", false);
					status.put("responseMessage", responseMessage.get("line.busy"));
					return status;
				}
			} else {
				status.put("responseStatus", false);
				status.put("responseMessage", responseMessage.get("on.call.active"));
				checkActiveCall.setOutCallTime(new java.sql.Timestamp(System.currentTimeMillis()));
				checkActiveCall.setUpdatedDateTime(new Date());
				checkActiveCall.setCallStatus(CallLog.CallStatus.CALLEND);
				CallLog callLogUpdateActiveCall = callLogService.updateActiveCall(checkActiveCall,
						checkActiveCall.getCallDetails().getCallerId().getUserId());
				return status;
			}
		} else {
			status.put("responseStatus", false);
			status.put("responseMessage", responseMessage.get("id.or.key/value.is.null.or.incorrect"));
			return status;
		}

		return status;
	}

	@Override
	public JSONObject listCallDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject updateCallDetails(JSONObject callDetails) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		try {
			Integer callDetailsId = Integer.parseInt(callDetails.get("callDetailsId").toString());
			CallDetails callDetailsDetails = find(callDetailsId);
			callDetailsDetails.setUpdatedDateTime(new Date());

			if (callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.MISSEDCALL.toString())) {
				callDetailsDetails.setCallStatus(CallDetails.CallStatus.MISSEDCALL);
				callDetailsRepository.save(callDetailsDetails);
				status.put("responseMessage", responseMessage.get("call.not.attended.missed.call"));
			}

			if (callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.REJECTED.toString())) {
				callDetailsDetails.setCallStatus(CallDetails.CallStatus.REJECTED);
				callDetailsRepository.save(callDetailsDetails);
				status.put("responseMessage", responseMessage.get("call.rejected"));
			}

			if (callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.CALLEND.toString())) {
				callDetailsDetails.setCallStatus(CallDetails.CallStatus.CALLEND);
				callDetailsRepository.save(callDetailsDetails);
				status.put("responseMessage", responseMessage.get("call.ended"));
			}
			if (callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.DISCONNECTED.toString())) {
				callDetailsDetails.setCallStatus(CallDetails.CallStatus.DISCONNECTED);
				callDetailsRepository.save(callDetailsDetails);
				status.put("responseMessage", responseMessage.get("call.disconnected"));
			}
			if (callDetails.get("callStatus").toString().equals(CallDetails.CallStatus.GROUPCALL.toString())) {
				callDetailsDetails.setCallStatus(CallDetails.CallStatus.GROUPCALL);
				callDetailsRepository.save(callDetailsDetails);
				status.put("responseMessage", responseMessage.get("group.call.activated"));
			}
		} catch (Exception e) {
			status.put("responseStatus", false);
			status.put("reason", "Error happend");
			status.put("originalErrorMsg", e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public JSONObject deleteCallDetails(JSONObject callDetailsId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getCallDetailsById(JSONObject callDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CallDetails save(CallDetails t) throws Exception {
		return callDetailsRepository.save(t);
	}

	@Override
	public CallDetails update(CallDetails t) throws Exception {
		return callDetailsRepository.save(t);
	}

	@Override
	public void delete(CallDetails t) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Integer id) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public CallDetails find(Integer id) throws Exception {
		return callDetailsRepository.findOne(id);
	}

	@Override
	public List<CallDetails> findAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CallDetails> getFavlist(com.lyca.api.model.CallLog.CallStatus groupcall, Integer userId,
			PageRequest pageRequest) {
		return null;
	}
}
