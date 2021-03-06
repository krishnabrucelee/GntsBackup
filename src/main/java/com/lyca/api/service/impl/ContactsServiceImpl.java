/**
 * 
 */
package com.lyca.api.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyca.api.model.Contacts;
import com.lyca.api.repository.ContactsRepository;
import com.lyca.api.service.ContactsService;
import com.lyca.api.service.CountryService;
import com.lyca.api.service.FCMNotificationService;
import com.lyca.api.service.InvitieService;
import com.lyca.api.service.PusherNotificationService;
import com.lyca.api.service.UserService;
import com.lyca.api.model.Country;
import com.lyca.api.model.Invities;
import com.lyca.api.model.Invities.InviteeStatus;
import com.lyca.api.model.User;

/**
 * @author Krishna
 *
 */
@Service
@SuppressWarnings("unchecked")
public class ContactsServiceImpl implements ContactsService {

	@Autowired
	private ContactsRepository contactsRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private InvitieService invitieService;

	@Autowired
	private CountryService countryService;

	@Autowired
	private PusherNotificationService pusherNotificationService;

	@Autowired
	private FCMNotificationService fCMNotificationService;
	
	@Autowired
	@Qualifier("responseMessage")
	private Properties responseMessage;

	@Override
	public JSONObject addContacts(JSONObject contacts) {
		JSONObject status = new JSONObject();
		ObjectMapper om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		if (contacts.get("nickName") != null) {
			Contacts contactsDetails = om.convertValue(contacts, Contacts.class);
			User contactUserDetails = null;
			Contacts contactOfBaseUser = null;
			Country countryDetails = null;
			try {
				if (contacts.get("baseUserId") != null) {
					User baseUserDetails = userService.find((Integer) contacts.get("baseUserId"));
					if (baseUserDetails != null) {
						contactsDetails.setBaseUser(baseUserDetails);
					} else {
						status.put("responseStatus", false);
						status.put("responseMessage", responseMessage.get("please.provide.correct.Base.user.id"));
						return status;
					}
				} else {
					status.put("responseStatus", false);
					status.put("responseMessage", responseMessage.get("base.user.cannot.be.blank"));
					return status;
				}
				if (contacts.get("countryId") != null) {
					countryDetails = countryService.find((Integer) contacts.get("countryId"));
					if (countryDetails != null) {
						contactsDetails.setCountry(countryDetails);
					} else {
						status.put("responseStatus", false);
						status.put("responseMessage", responseMessage.get("invalid.Country"));
						return status;
					}
				}
				if (contacts.get("mobileNumber") != null) {
					// check two and fro scenario inviting vise-sersa
					User checkUserAdd = userService.getUserByMobileNumber(contacts.get("mobileNumber").toString());
					User baseUserDetails = userService.find((Integer) contacts.get("baseUserId"));
					if (contacts.get("contactMobileNumber") == null) {
						if (checkUserAdd != null) {
							Invities twoFroCheck = invitieService.getInvitieTwoAndFroAndNotRejected(
									checkUserAdd.getUserId(), baseUserDetails.getMobileNumber(),
									Invities.InviteeStatus.REJECTED);
							if (twoFroCheck != null) {
								status.put("responseStatus", false);
								status.put("responseMessage", responseMessage.get("this.person.invited.you"));
								return status;
							}
						}
					}

					// Check if himself as contact
					if (contacts.get("contactMobileNumber") != null) {
						User checkContactUserAdd = userService
								.getUserByMobileNumber(contacts.get("contactMobileNumber").toString());
						if (checkContactUserAdd != null) {
							if (contacts.get("baseUserId").equals(checkContactUserAdd.getUserId())) {
								status.put("responseStatus", false);
								status.put("responseMessage", responseMessage.get("you.cant.add.yourself"));
								return status;
							}
						}
					} else {
						if (checkUserAdd != null) {
							if (contacts.get("baseUserId").equals(checkUserAdd.getUserId())) {
								status.put("responseStatus", false);
								status.put("responseMessage", responseMessage.get("you.cant.add.yourself"));
								return status;
							}
						}
					}

					// Check if base user adding same contact number
					Contacts duplicateContact = null;
					if (contacts.get("contactMobileNumber") == null) {
						duplicateContact = getDuplicateContactByBaseUserId(contacts.get("mobileNumber").toString(),
								contacts.get("baseUserId").toString());
					} else {
						duplicateContact = getDuplicateContactByBaseUserId(
								contacts.get("contactMobileNumber").toString(), contacts.get("baseUserId").toString());
					}
					if (duplicateContact != null && duplicateContact.getContactRemoved() == true) {
						if (duplicateContact.getContactUser().getStbUser() == true
								&& duplicateContact.getBaseUser().getStbUser() == true) {
							duplicateContact.setContactRemoved(false);
							duplicateContact.setContactBlocked(false);
							contactsRepository.save(duplicateContact);
							status.put("responseStatus", true);
							// status.put("Contact", contactsDetails);
							System.out.println("Save contactss");
							status.put("responseMessage", responseMessage.get("contactSaveSuccess"));
							return status;
						} else if (duplicateContact.getContactUser().getStbUser() == false
								&& duplicateContact.getBaseUser().getStbUser() == true) {
							duplicateContact.setContactRemoved(false);
							duplicateContact.setContactBlocked(false);
							contactsRepository.save(duplicateContact);
							status.put("responseStatus", true);
							// status.put("Contact", contactsDetails);
							System.out.println("Save contactss");
							status.put("responseMessage", responseMessage.get("contactSaveSuccess"));
							return status;
						}
//						} else {
//							// check if non stb user also removes ur contact
//							if (duplicateContact.getBaseUser().getStbUser() == true) {
//								Contacts nonStbContact = getDuplicateContactByBaseUserId(
//										duplicateContact.getContactUser().getMobileNumber(),
//										contacts.get("baseUserId").toString());
//								if (nonStbContact != null && nonStbContact.getContactRemoved() == true) {
//
//									Invities invitieDetails = new Invities();
//									invitieDetails.setBaseUser(contactsDetails.getBaseUser());
//									invitieDetails.setInviteeMobileNumber(contacts.get("mobileNumber").toString());
//									if (contacts.get("inviteeStatus") != null) {
//										if (contacts.get("inviteeStatus").toString()
//												.equals(Contacts.InviteeStatus.ACCEPTED.toString())) {
//											invitieDetails.setInviteeStatus(Invities.InviteeStatus.ACCEPTED);
//											contactsDetails.setInviteeStatus(Contacts.InviteeStatus.ACCEPTED);
//										}
//									} else {
//										invitieDetails.setInviteeStatus(Invities.InviteeStatus.PENDING);
//										contactsDetails.setInviteeStatus(Contacts.InviteeStatus.PENDING);
//										// Pusher
//										Integer inviteCount = invitieService
//												.getMyInvitieListByMobileNumberCount(contacts);
//										JSONObject jsonPushMsg = new JSONObject();
//										jsonPushMsg.put("message", "You have an invite request");
//										jsonPushMsg.put("inviteCount", inviteCount);
//										JSONObject jsonpusher = pusherNotificationService.pushMessasge(
//												contacts.get("mobileNumber").toString(), "INVITE", jsonPushMsg);
//										status.put("pusherResponse", jsonpusher);
//									}
//									invitieDetails.setInvitationCode(UUID.randomUUID().toString().substring(0, 6));
//									invitieDetails.setCreatedDateTime(new Date());
//									List<Invities> addInvitieDetails = new ArrayList<Invities>();
//									contactsDetails.setContactUser(contactUserDetails);
//									invitieService.save(invitieDetails);
//									addInvitieDetails.add(invitieDetails);
//									contactsDetails.setInvities(addInvitieDetails);
//									status.put("invitieResponseMessage", responseMessage.get("invitie.added"));
//								} else {
//									duplicateContact.setContactRemoved(false);
//									duplicateContact.setContactBlocked(false);
//									contactsRepository.save(duplicateContact);
//									status.put("responseStatus", true);
//									// status.put("Contact", contactsDetails);
//									System.out.println("Save contactss");
//									status.put("responseMessage", responseMessage.get("contactSaveSuccess"));
//									return status;
//								}
//							} else {
//								duplicateContact.setContactRemoved(false);
//								duplicateContact.setContactBlocked(false);
//								contactsRepository.save(duplicateContact);
//								status.put("responseStatus", true);
//								// status.put("Contact", contactsDetails);
//								System.out.println("Save contactss");
//								status.put("responseMessage", responseMessage.get("contactSaveSuccess"));
//								return status;
//							}
//
//						}
					}
					if (duplicateContact == null) {
						contactUserDetails = userService.getUserByMobileNumber(contacts.get("mobileNumber").toString());
						contactsDetails.setMobileNumber(contacts.get("mobileNumber").toString());
						// if (contactUserDetails == null) {
						if (contacts.get("contactMobileNumber") != null) {
							User contactMobileUserDetails = userService
									.getUserByMobileNumber(contacts.get("contactMobileNumber").toString());
							contactsDetails.setMobileNumber(contacts.get("contactMobileNumber").toString());
							contactOfBaseUser = getDuplicateContactByBaseUserId(contacts.get("mobileNumber").toString(),
									contactMobileUserDetails.getUserId().toString());
							if (contactOfBaseUser != null) {
								User contactUserDetails1 = userService
										.getUserByMobileNumber(contacts.get("mobileNumber").toString());
								contactOfBaseUser.setContactUser(contactUserDetails1);
								if (contacts.get("inviteeStatus") != null) {
									if (contacts.get("inviteeStatus").toString()
											.equals(Contacts.InviteeStatus.ACCEPTED.toString())) {
										contactOfBaseUser.setInviteeStatus(Contacts.InviteeStatus.ACCEPTED);
										contactsDetails.setInviteeStatus(Contacts.InviteeStatus.ACCEPTED);
									} else if (contacts.get("inviteeStatus").toString()
											.equals(Contacts.InviteeStatus.REJECTED.toString())) {
										contactOfBaseUser.setInviteeStatus(Contacts.InviteeStatus.REJECTED);
										contactsDetails.setInviteeStatus(Contacts.InviteeStatus.REJECTED);
									}
								}
								contactsRepository.save(contactOfBaseUser);
								contactsDetails.setContactUser(contactMobileUserDetails);
							}

						} else {
							Invities invitieDetails = new Invities();
							invitieDetails.setBaseUser(contactsDetails.getBaseUser());
							invitieDetails.setInviteeMobileNumber(contacts.get("mobileNumber").toString());
							invitieDetails.setInvitationCode(UUID.randomUUID().toString().substring(0, 6));
							if (contacts.get("inviteeStatus") != null) {
								if (contacts.get("inviteeStatus").toString()
										.equals(Contacts.InviteeStatus.ACCEPTED.toString())) {
									invitieDetails.setInviteeStatus(Invities.InviteeStatus.ACCEPTED);
									contactsDetails.setInviteeStatus(Contacts.InviteeStatus.ACCEPTED);
								}
							} else {
								invitieDetails.setInviteeStatus(Invities.InviteeStatus.PENDING);
								contactsDetails.setInviteeStatus(Contacts.InviteeStatus.PENDING);
								User contactUser = userService.getUserByMobileNumber(contacts.get("mobileNumber").toString());
								if (contactUser == null) {
									JSONObject smsGateway = userService.smsGateway(contactsDetails.getCountry().getCountryIsdCode(), contacts.get("mobileNumber").toString(),
											invitieDetails.getInvitationCode(), "You have an invite request. Lyca Inivite code is");
									if (smsGateway.get("responseStatus").equals(false)) {
										status.put("responseStatus", false);
										status.put("responseMessage", smsGateway.get("responseMessage").toString());
										return status;
									}
								}
								// Pusher
								Integer inviteCount = invitieService.getMyInvitieListByMobileNumberCount(contacts);
								JSONObject jsonPushMsg = new JSONObject();
								jsonPushMsg.put("message", "You have an invite request");
								jsonPushMsg.put("inviteCount", inviteCount);
								JSONObject jsonpusher = pusherNotificationService
										.pushMessasge(contacts.get("mobileNumber").toString(), "INVITE", jsonPushMsg);
								status.put("pusherResponse", jsonpusher);
								User userContact = userService.getUserByMobileNumber(contacts.get("mobileNumber").toString());
								if (userContact != null) {
									if (userContact.getFcmToken() != null) {
										
										String[] data = userContact.getFcmToken().split("\\|");
										System.out.println(data.length);
										for (int i = 0; i < data.length; i++) {
											System.out.println(data[i]);
											jsonPushMsg.put("event", "INVITE");
											JSONObject fcmpusher = fCMNotificationService.pushFCMNotification(data[i], jsonPushMsg);
											status.put("fcmResponse", fcmpusher);
										}
										
//										jsonPushMsg.put("event", "INVITE");
//										JSONObject fcmpusher =	fCMNotificationService.pushFCMNotification(userContact.getFcmToken(), jsonPushMsg);
//										status.put("fcmResponse", fcmpusher);
									}
								}
								
							}
							
							invitieDetails.setCreatedDateTime(new Date());
							List<Invities> addInvitieDetails = new ArrayList<Invities>();
							contactsDetails.setContactUser(contactUserDetails);
							invitieService.save(invitieDetails);
							addInvitieDetails.add(invitieDetails);
							contactsDetails.setInvities(addInvitieDetails);
							status.put("invitieResponseMessage", responseMessage.get("invitie.added"));

						}
					} else {
						status.put("responseStatus", false);
						status.put("responseMessage", responseMessage.get("you.already.invited.this.contact"));
						return status;
					}
				} else {
					status.put("responseStatus", false);
					status.put("responseMessage", responseMessage.get("mobile.number.cannot.be.blank"));
					return status;
				}
				contactsDetails.setContactRemoved(false);
				contactsDetails.setContactBlocked(false);
				contactsDetails.setCreatedDateTime(new Date());
				contactsRepository.save(contactsDetails);
				status.put("responseStatus", true);
				System.out.println("Save contactss");
				status.put("responseMessage", responseMessage.get("contactSaveSuccess"));

			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseStatus", false);
				status.put("responseMessage", responseMessage.get("contactSaveFailed"));
			}
		} else {
			status.put("responseMessage", responseMessage.get("nickname.cannot.be.blank"));
			return status;
		}
		return status;
	}

	private Contacts getDuplicateContactByBaseUserId(String mobileNumber, String baseUserId) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		List<Contacts> contactList = null;
		if (!mobileNumber.isEmpty()) {
			Integer userId = Integer.parseInt(baseUserId);
			try {
				contactList = contactsRepository.getDuplicateContactByBaseUserId(mobileNumber, userId);
				if (!contactList.isEmpty()) {
					for (Contacts contactDetails : contactList) {

						List<Contacts> contacts = new ArrayList<>();
						contacts.add(contactDetails);
						return contacts.get(0);
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
		return contactList.get(0);
	}

	@Override
	public JSONObject listContacts() {
		return null;
	}

	@Override
	public JSONObject updateContacts(JSONObject contacts) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		try {
			if (contacts.get("contactId") != null) {

				Contacts contactsDetails = contactsRepository.findOne((Integer) contacts.get("contactId"));
				if (contactsDetails != null) {
					if (contacts.get("contactBlocked") != null) {
						if (contacts.get("contactBlocked") != null && (Boolean) contacts.get("contactBlocked") == true
								&& !contacts.get("contactBlocked").toString().isEmpty()) {
							contactsDetails.setContactBlocked((Boolean) contacts.get("contactBlocked"));
							status.put("responseMessage", responseMessage.get("contactBlock"));
						} else if (contacts.get("contactBlocked") != null
								&& (Boolean) contacts.get("contactBlocked") == false
								&& !contacts.get("contactBlocked").toString().isEmpty()) {
							contactsDetails.setContactBlocked((Boolean) contacts.get("contactBlocked"));
							status.put("responseMessage", responseMessage.get("contactUnBlock"));
						} else {
							status.put("responseStatus", false);
							status.put("responseMessage", responseMessage.get("id.or.key/value.is.null.or.incorrect"));
						}
					}

					if (contacts.get("contactRemoved") != null) {
						if (contacts.get("contactRemoved") != null
								&& !contacts.get("contactRemoved").toString().isEmpty()) {
							contactsDetails.setContactRemoved((Boolean) contacts.get("contactRemoved"));
							if (contactsDetails.getContactUser() != null) {
								Invities invities = invitieService.getInvitieTwoAndFroAndNotRejected(
										contactsDetails.getContactUser().getUserId(),
										contactsDetails.getBaseUser().getMobileNumber(), InviteeStatus.REJECTED);
								if (invities != null) {
									invitieService.delete(invities);
								}
							}
							status.put("responseMessage", responseMessage.get("contactRemove"));
						} else {
							status.put("responseStatus", false);
							status.put("responseMessage", responseMessage.get("id.or.key/value.is.null.or.incorrect"));

						}
					}
					contactsDetails.setUpdatedDateTime(new Date());
					contactsRepository.save(contactsDetails);
				} else {
					status.put("responseStatus", false);
					status.put("responseMessage", responseMessage.get("id.or.key/value.is.null.or.incorrect"));
				}
			} else {
				status.put("responseStatus", false);
				status.put("responseMessage", responseMessage.get("id.or.key/value.is.null.or.incorrect"));
			}
		} catch (Exception e) {
			status.put("responseStatus", false);
			status.put("responseMessage", responseMessage.get("contact.update.failed"));
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public JSONObject deleteContacts(JSONObject contactsId) {
		return null;
	}

	@Override
	public JSONObject getContactsById(JSONObject contacts) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		List<Contacts> contactsList = null;
		List<JSONObject> contactss = new ArrayList<>();

		if ((contacts.get("userId") != null && !contacts.get("userId").toString().isEmpty())
				&& (!contacts.get("mobileNumber").toString().isEmpty() && (contacts.get("mobileNumber") != null))) {
			Integer contactsId = Integer.parseInt(contacts.get("userId").toString());
			try {
				contactsList = contactsRepository.getContactsById(contactsId, false,
						contacts.get("mobileNumber").toString());
				if (contactsList.size() > 0) {
					for (Contacts contactsDetails : contactsList) {
						JSONObject contactJson = new JSONObject();
						contactJson.put("contactId", contactsDetails.getContactId());
						contactJson.put("nickName", contactsDetails.getNickName());
						contactJson.put("mobileNumber", contactsDetails.getMobileNumber());
						if (contactsDetails.getCountry() != null) {
							contactJson.put("countryId", contactsDetails.getCountry().getCountryId());
							contactJson.put("countryIsdCode", contactsDetails.getCountry().getCountryIsdCode());
							contactJson.put("countryName", contactsDetails.getCountry().getCountryName());
						}
						contactJson.put("inviteeStatus", contactsDetails.getInviteeStatus());
						contactJson.put("contactBlocked", contactsDetails.getContactBlocked());
						contactJson.put("contactRemoved", contactsDetails.getContactRemoved());
						if (contactsDetails.getContactUser() != null) {
							Contacts contactUser = getContactsByCallUsersAndMobile(contactsId,
									contactsDetails.getMobileNumber(), contactsDetails.getContactUser().getUserId());
							if (contactUser != null) {
								if (contactUser.getContactBlocked() == true
										|| contactUser.getContactRemoved() == true) {
									contactJson.put("onlineStatus", User.OnlineStatus.OFFLINE);
								} else if (contactsDetails.getInviteeStatus().equals(Contacts.InviteeStatus.PENDING)
										|| contactsDetails.getInviteeStatus().equals(Contacts.InviteeStatus.REJECTED)) {
									contactJson.put("onlineStatus", User.OnlineStatus.OFFLINE);
								} else {
									contactJson.put("onlineStatus", contactsDetails.getContactUser().getOnlineStatus());
								}
								contactJson.put("profileStatus", contactsDetails.getContactUser().getProfileStatus());
								contactJson.put("contactUserId", contactsDetails.getContactUser().getUserId());
							}
						} else {
							contactJson.put("contactUserId", null);
							if (contactsDetails.getInviteeStatus().equals(Contacts.InviteeStatus.PENDING)
									|| contactsDetails.getInviteeStatus().equals(Contacts.InviteeStatus.REJECTED)) {
								contactJson.put("onlineStatus", User.OnlineStatus.OFFLINE);
							} else {
								contactJson.put("onlineStatus", User.OnlineStatus.OFFLINE);
							}

							contactJson.put("profileStatus", null);
						}
						// if (contactsDetails.getContactUser() != null) {
						// contactJson.put("contactUserId",
						// contactsDetails.getContactUser().getUserId());
						// if (contactsDetails.getContactBlocked() == true) {
						// contactJson.put("onlineStatus",
						// User.OnlineStatus.OFFLINE);
						// } else {
						// contactJson.put("onlineStatus",
						// contactsDetails.getContactUser().getOnlineStatus());
						// }
						// contactJson.put("profileStatus",
						// contactsDetails.getContactUser().getProfileStatus());
						// } else {
						//
						// }
						contactJson.put("callCount", contactsDetails.getCallCount());
						contactss.add(contactJson);
						status.put("contacts", contactss);
						status.put("responseMessage", responseMessage.get("contactList"));
					}
				} else {
					status.put("responseStatus", false);
					status.put("responseMessage", responseMessage.get("contact.list.is.empty"));
				}
				return status;

			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseStatus", false);
				status.put("responseMessage", responseMessage.get("contact.list.error"));
			}
		} else {
			status.put("responseStatus", false);
			status.put("responseMessage", responseMessage.get("id.or.key/value.is.null.or.incorrect"));
			return status;
		}
		return status;
	}

	@Override
	public Contacts getContacts(Integer contactsId) {
		return null;
	}

	@Override
	public Contacts checkContactToCallFromCaller(Integer userId, String mobileNumber) {
		List<Contacts> contactsList = null;
		try {
			contactsList = contactsRepository.checkContactToCallFromCaller(userId, false, mobileNumber);
			if (!contactsList.isEmpty()) {
				for (Contacts contactDetails : contactsList) {

					List<Contacts> contacts = new ArrayList<>();
					contacts.add(contactDetails);
					return contacts.get(0);
				}
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (contactsList != null || contactsList.size() > 0) {
			return contactsList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Contacts save(Contacts t) throws Exception {
		return contactsRepository.save(t);
	}

	@Override
	public Contacts update(Contacts t) throws Exception {
		return contactsRepository.save(t);
	}

	@Override
	public void delete(Contacts t) throws Exception {
		contactsRepository.delete(t);
	}

	@Override
	public void delete(Integer id) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public Contacts find(Integer id) throws Exception {
		return contactsRepository.findOne(id);
	}

	@Override
	public List<Contacts> findAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contacts getContactsByCallUsersAndMobile(Integer baseUserId, String mobileNumber, Integer callToUserId) {
		return contactsRepository.getContactsByCallUsersAndMobile(baseUserId, mobileNumber, callToUserId);
	}

	@Override
	public JSONObject removeContact(JSONObject contacts) throws Exception {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		if ((contacts.get("baseUserId") != null && !contacts.get("baseUserId").toString().isEmpty())
				&& (!contacts.get("mobileNumber").toString().isEmpty() && (contacts.get("mobileNumber") != null))
				&& (!contacts.get("contactMobileNumber").toString().isEmpty()
						&& (contacts.get("contactMobileNumber") != null))) {
			User user = userService.getUserByMobileNumber(contacts.get("contactMobileNumber").toString());
			Contacts contactUser = getContactsByCallUsersAndMobile(user.getUserId(),
					contacts.get("mobileNumber").toString(), (Integer) contacts.get("baseUserId"));
			if (contactUser != null) {
				delete(contactUser);
				status.put("responseMessage", responseMessage.get("contact.removed"));
			} else {
				status.put("responseStatus", false);
				status.put("responseMessage", responseMessage.get("contact.removed.error"));
				return status;
			}

		}

		return status;
	}
}
