/**
 * 
 */
package com.lyca.api.service;

import org.json.simple.JSONObject;
import com.lyca.api.model.Invities;
import com.lyca.api.util.CRUDService;

/**
 * @author Krishna
 *
 */
public interface InvitieService extends CRUDService<Invities> {

	/**
	 * Create Invitie.
	 * 
	 * @param invitie
	 * @return invitie
	 */
	public JSONObject addInvitie(JSONObject invitie);

	/**
	 * List Invitie.
	 * 
	 * @param invitie
	 * @return invitie
	 */
	public JSONObject listInvitie();

	/**
	 * Update Invitie.
	 * 
	 * @param invitie
	 * @return invitie
	 */
	public JSONObject updateInvitie(JSONObject invitie);

	/**
	 * Delete Invitie.
	 * 
	 * @param invitie
	 * @return invitie
	 */
	public JSONObject deleteInvitie(JSONObject invitieId);

	/**
	 * @param invitie
	 * @return
	 */
	public JSONObject getInvitieById(JSONObject invitie);

	public Invities getInvitie(Integer invitieId);

	public JSONObject getInvitieByBaseUser(Integer userId);

	public JSONObject getUnAcceptedInvitie();

	public Invities addInvitie(Invities invitieDetails);
	
	public Invities getInvitiesByMobileNumber(String mobileNumber);

	public JSONObject getInvitieByBaseUserList(JSONObject invitie);

	public JSONObject getMyInvitieListByMobileNumber(JSONObject invitie);

	public Invities updateInvitie(Invities invitieDetails);
}
