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
import com.lyca.api.service.InvitieService;

/**
 * @author Krishna
 *
 */
@RestController
@RequestMapping("/invitie")
public class InvitieController {

	/**
	 * Invitie Service.
	 */
	@Autowired
	private InvitieService invitieService;

	/**
	 * Create Invitie.
	 * 
	 * @param invitie
	 * @return invitie
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody JSONObject addInvitie(@RequestBody JSONObject invitie) {
		return invitieService.addInvitie(invitie);
	}

	/**
	 * Update Invitie.
	 * 
	 * @param invitie
	 * @return invitie
	 */
	@RequestMapping(value = "/update")
	public @ResponseBody JSONObject updateInvitie(@RequestBody JSONObject invitie) {
		return invitieService.updateInvitie(invitie);
	}

	/**
	 * Delete invitie.
	 * 
	 * @param invitie
	 * @return invitie
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public @ResponseBody JSONObject deleteInvitie(@RequestBody JSONObject invitieId) {
		return invitieService.deleteInvitie(invitieId);
		
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public @ResponseBody JSONObject getInvitieById(@RequestBody JSONObject invitie) {
		System.out.println(invitie);
		if (invitie.get("key") != null && invitie.get("key").equals("listAll")) {
			return invitieService.listInvitie();
		} else {
			return invitieService.getInvitieById(invitie);
		}
	} 
	
	@RequestMapping(value = "/getInvitie", method = RequestMethod.POST)
	public @ResponseBody JSONObject getInvitieByBaseUserList(@RequestBody JSONObject invitie) {
		System.out.println(invitie);
			return invitieService.getInvitieByBaseUserList(invitie);
	}
	
	@RequestMapping(value = "/getMyInvitieList", method = RequestMethod.POST)
	public @ResponseBody JSONObject getMyInvitieListByMobileNumber(@RequestBody JSONObject invitie) {
		System.out.println(invitie);
			return invitieService.getMyInvitieListByMobileNumber(invitie);
	}
}
