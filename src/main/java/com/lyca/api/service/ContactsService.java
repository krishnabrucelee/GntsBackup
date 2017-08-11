/**
 * 
 */
package com.lyca.api.service;

import org.json.simple.JSONObject;
import com.lyca.api.util.CRUDService;
import com.lyca.api.model.Contacts;

/**
 * @author Krishna
 *
 */
public interface ContactsService extends CRUDService<Contacts> {

	/**
	 * Create Contacts.
	 * 
	 * @param contacts
	 * @return contacts
	 */
	public JSONObject addContacts(JSONObject contacts);

	/**
	 * List Contacts.
	 * 
	 * @param contacts
	 * @return contacts
	 */
	public JSONObject listContacts();

	/**
	 * Update Contacts.
	 * 
	 * @param contacts
	 * @return contacts
	 */
	public JSONObject updateContacts(JSONObject contacts);

	/**
	 * Delete Contacts.
	 * 
	 * @param contacts
	 * @return contacts
	 */
	public JSONObject deleteContacts(JSONObject contactsId);

	/**
	 * @param contacts
	 * @return
	 */
	public JSONObject getContactsById(JSONObject contacts);

	public Contacts getContacts(Integer contactsId);

	public Contacts checkContactToCallFromCaller(Integer userId, String mobileNumber);
}
