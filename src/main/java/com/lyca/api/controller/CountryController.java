package com.lyca.api.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.lyca.api.service.CountryService;

/**
 * 
 * @author Krishna
 *
 */
@RestController
@RequestMapping("/country")
public class CountryController {

	/**
	 * Country Service.
	 */
	@Autowired
	private CountryService countryService;

	/**
	 * Create Country.
	 * 
	 * @param country
	 * @return country
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody JSONObject addCountry(@RequestBody JSONObject country) {
		return countryService.addCountry(country);
	}

	/**
	 * Update Country.
	 * 
	 * @param country
	 * @return country
	 */
	@RequestMapping(value = "/update")
	public @ResponseBody JSONObject updateCountry(@RequestBody JSONObject country) {
		return countryService.updateCountry(country);
	}

	/**
	 * Delete country.
	 * 
	 * @param country
	 * @return country
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public @ResponseBody JSONObject deleteCountry(@RequestBody JSONObject countryId) {
		return countryService.deleteCountry(countryId);
	}

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public @ResponseBody JSONObject getCountryById(@RequestBody JSONObject country) {
		System.out.println(country);
		if (country.get("key") != null && country.get("key").equals("listAll")) {
			return countryService.listCountry();
		} else {
			return countryService.getCountryById(country);
		}
	}
}
