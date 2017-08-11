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
import com.lyca.api.service.ContactsService;

/**
 * @author Krishna
 *
 */
@RestController
@RequestMapping("/contacts")
public class ContactsController {

	/**
	 * Contacts Service.
	 */
	@Autowired
	private ContactsService contactsService;

	/**
	 * Create Contacts.
	 * 
	 * @param contacts
	 * @return contacts
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody JSONObject addContacts(@RequestBody JSONObject contacts) {
		return contactsService.addContacts(contacts);
	}

	/**
	 * Update Contacts.
	 * 
	 * @param contacts
	 * @return contacts
	 */
	@RequestMapping(value = "/update")
	public @ResponseBody JSONObject updateContacts(@RequestBody JSONObject contacts) {
		return contactsService.updateContacts(contacts);
	}

	/**
	 * Delete contacts.
	 * 
	 * @param contacts
	 * @return contacts
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public @ResponseBody JSONObject deleteContacts(@RequestBody JSONObject contactsId) {
		return contactsService.deleteContacts(contactsId);
	}

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public @ResponseBody JSONObject getContactsById(@RequestBody JSONObject contacts) {
		System.out.println(contacts);
		if (contacts.get("key") != null && contacts.get("key").equals("listAll")) {
			return contactsService.listContacts();
		} else {
			return contactsService.getContactsById(contacts);
		}
	}
}
