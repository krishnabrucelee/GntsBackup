/**
 * 
 */
package com.lyca.api.service.impl;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyca.api.model.User;
import com.lyca.api.repository.UserRepository;
import com.lyca.api.service.UserService;
import com.lyca.api.service.CountryService;
import com.lyca.api.service.InvitieService;
import com.lyca.api.model.Country;
import com.lyca.api.model.Invities;

/**
 * @author Krishna
 *
 */
@Service
@Transactional
@SuppressWarnings("unchecked")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InvitieService invitiesservice;

	@Autowired
	private CountryService countryservice;

	@Autowired
	@Qualifier("responseMessage")
	private Properties responseMessage;

	@Override
	public JSONObject addUser(JSONObject user) {
		JSONObject status = new JSONObject();
		ObjectMapper om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		if (user.get("mobileNumber") != null || user.get("lycaSubscriberId") != null) {
			User userDetails = om.convertValue(user, User.class);
			try {
				if (user.get("lycaSubscriberId") == null) {

					if ((Boolean) user.get("stbUser") != true) {
						// Non-Lyca user
						User userr = getUserByMobileNumber(user.get("mobileNumber").toString());
						if (userr == null) {
							if (user.get("firstName") != null && user.get("lastName") != null) {
								Invities invities = invitiesservice
										.getInvitiesByMobileNumber(user.get("mobileNumber").toString());
								if (invities != null) {
									userDetails.setOtp(invities.getInvitationCode());
									userDetails.setOtpStatus(false);
								} else {
									status.put("responseStatus", false);
									status.put("responseMessage", responseMessage.get("mobile.number.not.invited"));
									return status;
								}
								if (user.get("countryId") != null) {
									Integer countryId = Integer.parseInt(user.get("countryId").toString());
									Country countryDetails = countryservice.find(countryId);
									userDetails.setCountry(countryDetails);
								}
								userDetails.setOnlineStatus(User.OnlineStatus.AVAILABLE);
								userDetails.setProfileStatus("AVAILABLE");
								userDetails.setCreatedDateTime(new Date());
								userRepository.save(userDetails);
								status.put("responseStatus", true);
								status.put("responseMessage", responseMessage.get("userSaveSuccess"));
								status.put("user", userDetails);
							} else {
								status.put("responseStatus", false);
								status.put("responseMessage", responseMessage.get("name.cannot.be.blank"));
								return status;
							}
						} else {
							status.put("responseStatus", true);
							status.put("responseMessage", responseMessage.get("already.registered.as.non.Lyca.user"));
							status.put("user", userr);
							return status;
						}
					} else {
						if (user.get("countryId") != null) {
							Integer countryId = Integer.parseInt(user.get("countryId").toString());
							Country countryDetails = countryservice.find(countryId);
							userDetails.setCountry(countryDetails);
						}
						User userr = getUserByMobileNumber(user.get("mobileNumber").toString());
						if (userr != null && userr.getStbUser() && userr.getLycaSubscriberId() != null) {
							userr.setOnlineStatus(User.OnlineStatus.AVAILABLE);
							userDetails.setProfileStatus("AVAILABLE");
							userr.setOtp(UUID.randomUUID().toString().substring(0, 6));
							userr.setOtpStatus(false);
							userr.setUpdatedDateTime(new Date());
							userRepository.save(userr);
							status.put("responseStatus", true);
							status.put("responseMessage", responseMessage.get("userSaveSuccess"));
							status.put("user", userr);
						} else {
							status.put("responseStatus", false);
							status.put("responseMessage",
									responseMessage.get("mobile.number.not.registered.as.Lyca.user"));
							return status;
						}
					}
				} else {
					if ((Boolean) user.get("stbUser") == true) {
						if (user.get("countryId") != null) {
							Integer countryId = Integer.parseInt(user.get("countryId").toString());
							Country countryDetails = countryservice.find(countryId);
							userDetails.setCountry(countryDetails);
						}
						if (!user.get("lycaSubscriberId").toString().isEmpty()) {
							User userr = getUserBySubscriberId(user.get("lycaSubscriberId").toString());
							if (userr == null) {
								userDetails.setMobileNumber(
										String.valueOf(new java.sql.Timestamp(System.currentTimeMillis()).getTime()));
								userDetails.setFirstName("F-" + user.get("lycaSubscriberId").toString());
								userDetails.setLastName("L-" + user.get("lycaSubscriberId").toString());
								userDetails.setOnlineStatus(User.OnlineStatus.AVAILABLE);
								userDetails.setProfileStatus("AVAILABLE");
								userDetails.setOtpStatus(false);
								userDetails.setCreatedDateTime(new Date());
								userRepository.save(userDetails);
								status.put("responseStatus", true);
								status.put("responseMessage", responseMessage.get("userSaveSuccess"));
								status.put("user", userDetails);
							} else {
								status.put("responseStatus", true);
								status.put("responseMessage", responseMessage.get("already.registered.as.Lyca.user"));
								status.put("user", userr);
								return status;
							}
						} else {
							status.put("responseStatus", false);
							status.put("responseMessage", responseMessage.get("subscriber.id.cannot.be.blank"));
						}
					} else {
						status.put("responseStatus", false);
						status.put("responseMessage", responseMessage.get("not.a.Lyca.user"));
					}
				}

			} catch (Exception e) {
				Throwable tt = e.getCause();
//				if (tt instanceof SQLIntegrityConstraintViolationException) {
//					tt.printStackTrace();
//					status.put("responseStatus", false);
//					status.put("responseMessage",
//							responseMessage.get("duplicate.mobile.number.entered.User.details.not.saved"));
//					status.put("Error", tt.getMessage());
//					return status;
//				} else {
					e.printStackTrace();
					status.put("responseStatus", false);
					status.put("responseMessage", responseMessage.get("userSaveFailed"));
					status.put("Error", e.getMessage());
					return status;
//				}
			}
		} else {
			status.put("responseStatus", false);
			status.put("responseMessage", responseMessage.get("mobile.number.cannot.be.blank"));
			return status;
		}
		return status;
	}

	private User getUserBySubscriberId(String lycaSubscriberId) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		List<User> userList = null;
		if (!lycaSubscriberId.isEmpty()) {
			try {
				userList = userRepository.getUserBySubscriberId(lycaSubscriberId);
				if (userList.size() == 1) {
					for (User userDetails : userList) {

						List<User> users = new ArrayList<>();
						users.add(userDetails);
						return users.get(0);
					}
				} else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseStatus", false);
			}
		} else {
			status.put("responseMessage", responseMessage.get("id.or.key/value.is.null.or.incorrect"));
		}
		return userList.get(0);
	}

	@Override
	public JSONObject listUser() {
		return null;
	}

	@Override
	public JSONObject updateUser(JSONObject user) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		try {
			if (user.get("userId") != null && !user.get("userId").toString().isEmpty()) {
				if ((user.get("onlineStatus") != null && !user.get("onlineStatus").toString().isEmpty())
						|| (user.get("profilePicUrl") != null)) {
					User userDetails = find((Integer) user.get("userId"));
					if (user.get("firstName") != null) {
						userDetails.setFirstName(user.get("firstName").toString());
					}
					if (user.get("lastName") != null) {
						userDetails.setLastName(user.get("lastName").toString());
					}
					if (user.get("countryId") != null) {
						ObjectMapper om = new ObjectMapper();
						om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
						Country countryDetails = countryservice.find((Integer) user.get("countryId"));
						userDetails.setCountry(countryDetails);
					}
					if (user.get("profilePicUrl") != null) {
						userDetails.setProfilePicUrl(user.get("profilePicUrl").toString());
						status.put("responseMessage", responseMessage.get("user.profile.image.uploaded"));
						status.put("user", userDetails);
					}
					if (user.get("onlineStatus") != null
							&& user.get("onlineStatus").toString().equals(User.OnlineStatus.AVAILABLE.toString())) {
						userDetails.setOnlineStatus(User.OnlineStatus.AVAILABLE);
						status.put("responseMessage", responseMessage.get("user.details.updated"));
					}

					if (user.get("onlineStatus") != null
							&& user.get("onlineStatus").toString().equals(User.OnlineStatus.OFFLINE.toString())) {
						userDetails.setOnlineStatus(User.OnlineStatus.OFFLINE);
						status.put("responseMessage", responseMessage.get("user.details.updated"));
					}

					if (user.get("onlineStatus") != null
							&& user.get("onlineStatus").toString().equals(User.OnlineStatus.DONOTDISTURB.toString())) {
						userDetails.setOnlineStatus(User.OnlineStatus.DONOTDISTURB);
						status.put("responseMessage", responseMessage.get("user.details.updated"));
					}
					userDetails.setUpdatedDateTime(new Date());
					userRepository.save(userDetails);
				} else {
					status.put("responseMessage", responseMessage.get("online.status.cannot.be.blank"));
					status.put("responseStatus", false);
				}
			} else {
				status.put("responseMessage", responseMessage.get("id.or.key/value.is.null.or.incorrect"));
				status.put("responseStatus", false);
			}
		} catch (Exception e) {
			status.put("responseStatus", false);
			status.put("responseMessage", responseMessage.get("user.details.not.updated"));
			status.put("Error", e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public JSONObject deleteUser(JSONObject userId) {
		return null;
	}

	@Override
	public JSONObject getUserById(JSONObject user) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		List<User> userList = null;
		if (user.get("id") != null) {

			Integer userId = Integer.parseInt(user.get("id").toString());
			try {
				userList = userRepository.getUserById(userId);
				if (userList.size() == 1) {
					for (User userDetails : userList) {
						List<User> users = new ArrayList<>();
						users.add(userDetails);
						status.put("responseMessage", responseMessage.get("user.list"));
						status.put("user", users.get(0));
						return status;
					}
				} else if (userList.isEmpty()) {
					status.put("responseStatus", false);
					status.put("responseMessage", responseMessage.get("no.user.in.Lyca.database"));
					return status;
				}
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseStatus", false);
				status.put("responseMessage", e.getMessage());
			}
		} else {
			status.put("responseMessage", responseMessage.get("id.or.key/value.is.null.or.incorrect"));
		}
		return status;
	}

	@Override
	public JSONObject otpVerification(JSONObject otp) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		List<User> userList = null;
		if ((otp.get("mobileNumber") != null && !otp.get("mobileNumber").toString().isEmpty())) {
				if (otp.get("otp") != null && !otp.get("otp").toString().isEmpty()) {
					
			try {
				userList = userRepository.otpVerification(otp.get("mobileNumber").toString(),
						otp.get("otp").toString());
				if (userList.size() == 1) {
					for (User userDetails : userList) {
						userDetails.setOtpStatus(true);
						userRepository.save(userDetails);
						List<User> users = new ArrayList<>();
						users.add(userDetails);
						status.put("user", users.get(0));
						status.put("responseMessage", responseMessage.get("otp.verified"));
						if (users.get(0).getStbUser() == false) {
							if (users.get(0).getMobileNumber() != null) {
								Invities myInvitie = invitiesservice
										.getInvitiesByMobileNumber(users.get(0).getMobileNumber());
								JSONObject json = new JSONObject();
								json.put("invitieId", myInvitie.getInvitieId());
								json.put("inviteeStatus", Invities.InviteeStatus.ACCEPTED);
								json.put("baseUserId", users.get(0).getUserId());
								json.put("mobileNumber", myInvitie.getInviteeMobileNumber());
								json.put("contactMobileNumber", myInvitie.getBaseUser().getMobileNumber());
								if (myInvitie.getBaseUser().getFirstName() != null) {
									json.put("nickName", myInvitie.getBaseUser().getFirstName());
								} else {
									json.put("nickName", myInvitie.getBaseUser().getLycaSubscriberId());
								}
								json.put("countryId", myInvitie.getBaseUser().getCountry().getCountryId());
								JSONObject invitieAcceptJson = invitiesservice.updateInvitie(json);
								System.out.println(invitieAcceptJson);
							}
						}
					}
				} else {
					status.put("responseStatus", false);
					status.put("responseMessage", responseMessage.get("otp.verfication.failed"));
					return status;
				}
				return status;
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseStatus", false);
			}
		} else {
			status.put("responseStatus", false);
			status.put("responseMessage", responseMessage.get("otp.number.cannot.be.blank"));
		}
		} else {
			status.put("responseStatus", false);
			status.put("responseMessage", responseMessage.get("mobile.number.cannot.be.blank"));
		}
		return status;
	}

	@Override
	public JSONObject sendOtp(JSONObject otp) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		if (otp.get("mobileNumber") != null && !otp.get("mobileNumber").toString().isEmpty()) {
			try {
				User user = getUserByMobileNumber(otp.get("mobileNumber").toString());
				if (user != null && user.getStbUser() == true) {

					user.setMobileNumber(otp.get("mobileNumber").toString());
					user.setOtp(UUID.randomUUID().toString().substring(0, 6));
					user.setUpdatedDateTime(new Date());
					// String smsGateway =
					// smsGateway(otp.get("mobileNumber").toString(),
					// user.getOtp());
					userRepository.save(user);
					status.put("otp", user.getOtp());
					status.put("User", user);
					status.put("responseMessage",
							responseMessage.get("otp.for.mobile.number.successfully.generated.and.sent.to")
									+ user.getMobileNumber());
					return status;
				} else {
					status.put("responseStatus", false);
					status.put("responseMessage",
							responseMessage.get("mobile.number.given.incorrect.cannot.find.any.lyca.user"));
					return status;
				}
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseStatus", false);
				status.put("Error", e.getMessage());
			}
		} else {
			status.put("responseStatus", false);
			status.put("responseMessage", responseMessage.get("mobile.number.cannot.be.blank"));
		}
		return status;
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public User find(Integer id) {
		return userRepository.findOne(id);
	}

	@Override
	public User update(User user) {
		return userRepository.save(user);
	}

	@Override
	public void delete(User user) throws Exception {
		userRepository.delete(user);
	}

	@Override
	public void delete(Integer id) throws Exception {
		userRepository.delete(find(id));
	}

	@Override
	public List<User> findAll() throws Exception {
		return userRepository.findAll();
	}

	@Override
	public User getUserByMobileNumber(String mobileNumber) {
		return userRepository.getUserByMobileNumber(mobileNumber);
	}
}
