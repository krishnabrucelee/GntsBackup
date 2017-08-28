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
import com.lyca.api.model.Invities;
import com.lyca.api.model.Invities.InviteeStatus;
import com.lyca.api.repository.InvitieRepository;
import com.lyca.api.service.ContactsService;
import com.lyca.api.service.InvitieService;

/**
 * @author Krishna
 *
 */
@Service
@SuppressWarnings("unchecked")
public class InvitieServiceImpl implements InvitieService {

	@Autowired
	private InvitieRepository invitieRepository;

	@Autowired
	private ContactsService contactsService;

	@Autowired
	@Qualifier("responseMessage")
	private Properties responseMessage;

	@Override
	public JSONObject addInvitie(JSONObject invitie) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject listInvitie() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject updateInvitie(JSONObject invitie) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		if ((invitie.get("invitieId") != null && !invitie.get("invitieId").toString().isEmpty())
				&& (invitie.get("inviteeStatus") != null && !invitie.get("inviteeStatus").toString().isEmpty())
				&& (invitie.get("baseUserId") != null && !invitie.get("baseUserId").toString().isEmpty())
				&& (invitie.get("mobileNumber") != null && !invitie.get("mobileNumber").toString().isEmpty())
				&& (invitie.get("contactMobileNumber") != null && !invitie.get("contactMobileNumber").toString().isEmpty())
				&& (invitie.get("nickName") != null && !invitie.get("nickName").toString().isEmpty())
				&& (invitie.get("countryId") != null && !invitie.get("countryId").toString().isEmpty())) {

			try {
				Invities invitieDetails = invitieRepository.findOne((Integer) invitie.get("invitieId"));
				invitieDetails.setUpdatedDateTime(new Date());
				if (invitie.get("invitationCode") != null) {
					invitieDetails.setInvitationCode(invitie.get("invitationCode").toString());
				}

				if (invitie.get("inviteeStatus").toString().equals(Invities.InviteeStatus.ACCEPTED.toString())) {
					
					JSONObject contactStatus = contactsService.addContacts(invitie);
					if (contactStatus.get("responseStatus").equals(false)) {
						status.put("responseMessage", contactStatus.get("responseMessage"));
						status.put("responseStatus", contactStatus.get("responseStatus"));
						return status;
					} else {
						invitieDetails.setInviteeStatus(Invities.InviteeStatus.ACCEPTED);
						invitieRepository.save(invitieDetails);
					}
				}

				if (invitie.get("inviteeStatus").toString().equals(Invities.InviteeStatus.REJECTED.toString())) {
					JSONObject contact = contactsService.removeContact(invitie);
					if (contact.get("responseStatus").equals(false)) {
						status.put("responseMessage", contact.get("responseMessage"));
						status.put("responseStatus", contact.get("responseStatus"));
						return status;
					} 
				}

				if (invitie.get("inviteeStatus").toString().equals(Invities.InviteeStatus.PENDING.toString())) {
					invitieDetails.setInviteeStatus(Invities.InviteeStatus.PENDING);
					invitieRepository.save(invitieDetails);
				}
				status.put("responseMessage", responseMessage.get("invities.list.updated"));
			} catch (Exception e) {
				status.put("status", false);
				status.put("reason", "Error happend");
				status.put("originalErrorMsg", e.getMessage());
				e.printStackTrace();
			}
		} else {
			status.put("responseStatus", false);
			status.put("responseMessage", responseMessage.get("please.provide.the.valid.details"));
			return status;
		}
		return status;
	}

	@Override
	public JSONObject deleteInvitie(JSONObject invitieId) {
		return null;
	}

	@Override
	public JSONObject getInvitieById(JSONObject invitie) {
		return null;
	}

	@Override
	public Invities getInvitie(Integer invitieId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getInvitieByBaseUser(Integer userId) {
		JSONObject status = new JSONObject();
		status.put("status", true);
		List<Invities> invitieList = null;
		try {
			invitieList = invitieRepository.getInvitieByBaseUser(userId);
			if (invitieList.size() == 1) {
				for (Invities invitieDetails : invitieList) {

					List<Invities> invities = new ArrayList<>();
					invities.add(invitieDetails);
					status.put("Invities", invities.get(0));
				}
			}
			return status;
		} catch (Exception e) {
			e.printStackTrace();
			status.put("result", false);
		}
		return status;
	}

	@Override
	public JSONObject getUnAcceptedInvitie() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Invities addInvitie(Invities invitieDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Invities getInvitiesByMobileNumber(String inviteeMobileNumber) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		List<Invities> invitieList = null;
		if (!inviteeMobileNumber.isEmpty()) {

			try {
				invitieList = invitieRepository.getInvitiesByMobileNumber(inviteeMobileNumber);
				if (!invitieList.isEmpty()) {
					for (Invities invitieDetails : invitieList) {

						List<Invities> invities = new ArrayList<>();
						invities.add(invitieDetails);
						return invities.get(0);
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
		return invitieList.get(0);
	}

	@Override
	public JSONObject getInvitieByBaseUserList(JSONObject invitie) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		List<Invities> invitieList = null;
		Integer invitieId = Integer.parseInt(invitie.get("baseUserId").toString());
		try {
			invitieList = invitieRepository.getInvitieByBaseUser(invitieId);
			if (invitieList.isEmpty()) {
				status.put("Invities", invitieList);
				status.put("responseMessage", responseMessage.get("my.Invities.list"));
			}
			System.out.println(" Inside Rest DAO Bus Status=" + status);
			return status;
		} catch (Exception e) {
			e.printStackTrace();
			status.put("result", false);
		}
		return status;
	}

	@Override
	public JSONObject getMyInvitieListByMobileNumber(JSONObject invitie) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		List<Invities> invitieList = null;
		if (invitie.get("mobileNumber") != null && !invitie.get("mobileNumber").toString().isEmpty()) {

			try {
				invitieList = invitieRepository.getMyInvitieListByMobileNumber(invitie.get("mobileNumber").toString(),
						Invities.InviteeStatus.PENDING);
				if (!invitieList.isEmpty()) {
					status.put("responseStatus", true);
					status.put("responseMessage", responseMessage.get("my.Invities.list"));
					status.put("Invities list", invitieList);
					return status;
				} else {
					status.put("responseStatus", false);
					status.put("responseMessage", responseMessage.get("invities.list.Empty"));
					return status;
				}
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseStatus", false);
				status.put("Error", e.getMessage());
			}
		} else {
			status.put("responseMessage", responseMessage.get("mobile.number.cannot.be.blank"));
		}
		return status;
	}

	@Override
	public Invities updateInvitie(Invities invitieDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Invities save(Invities t) throws Exception {
		// TODO Auto-generated method stub
		return invitieRepository.save(t);
	}

	@Override
	public Invities update(Invities t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Invities t) throws Exception {
		invitieRepository.delete(t);
	}
	
	@Override
	public void delete(Integer id) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Invities find(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Invities> findAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getMyInvitieListByMobileNumberCount(JSONObject invitie) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		List<Invities> invitieList = new ArrayList<>();
		if (invitie.get("mobileNumber") != null && !invitie.get("mobileNumber").toString().isEmpty()) {

			try {
				invitieList = invitieRepository.getMyInvitieListByMobileNumber(invitie.get("mobileNumber").toString(),
						Invities.InviteeStatus.PENDING);
				if (!invitieList.isEmpty()) {
					return invitieList.size();
				} 
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseStatus", false);
				status.put("Error", e.getMessage());
			}
		} else {
			status.put("responseMessage", responseMessage.get("mobile.number.cannot.be.blank"));
		}
		return invitieList.size();
	}

	@Override
	public Invities getInvitieTwoAndFroAndNotRejected(Integer userId, String mobileNumber, InviteeStatus rejected) {
		JSONObject status = new JSONObject();
		status.put("responseStatus", true);
		Invities invitieList = new Invities();
		if (userId != null && mobileNumber != null) {

			try {
				return invitieList = invitieRepository.getInvitieTwoAndFroAndNotRejected(userId, mobileNumber);
			} catch (Exception e) {
				e.printStackTrace();
				status.put("responseStatus", false);
				status.put("Error", e.getMessage());
			}
		} else {
			status.put("responseStatus", false);
			status.put("responseMessage", responseMessage.get("mobile.number.cannot.be.blank"));
		}
		return invitieList;
	}

}
