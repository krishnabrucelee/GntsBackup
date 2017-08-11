package com.lyca.api.service;

import org.json.simple.JSONObject;
import com.lyca.api.util.CRUDService;
import com.lyca.api.model.Country;

/**
 * @author Krishna
 *
 */
public interface CountryService extends CRUDService<Country> {

	/**
	 * Create Country.
	 * 
	 * @param country
	 * @return country
	 */
	public JSONObject addCountry(JSONObject country);

	/**
	 * List Country.
	 * 
	 * @param country
	 * @return country
	 */
	public JSONObject listCountry();

	/**
	 * Update Country.
	 * 
	 * @param country
	 * @return country
	 */
	public JSONObject updateCountry(JSONObject country);

	/**
	 * Delete Country.
	 * 
	 * @param country
	 * @return country
	 */
	public JSONObject deleteCountry(JSONObject countryId);

	/**
	 * @param country
	 * @return
	 */
	public JSONObject getCountryById(JSONObject country);

	public Country getCountry(Integer countryId);
}
