/**
 * 
 */
package com.lyca.api.service.impl;

import java.util.List;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lyca.api.model.Country;
import com.lyca.api.repository.CountryRepository;
import com.lyca.api.service.CountryService;

/**
 * @author Krishna
 *
 */
@Service
@SuppressWarnings("unchecked")
public class CountryServiceImpl implements CountryService {

	@Autowired
	private CountryRepository countryRepository;

	@Override
	public Country save(Country t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Country update(Country t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
 
	@Override
	public void delete(Country t) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Integer id) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Country find(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return countryRepository.findOne(id);
	}

	@Override
	public List<Country> findAll() throws Exception {
		// TODO Auto-generated method stub
		return countryRepository.findAll();
	}

	@Override
	public JSONObject addCountry(JSONObject country) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject listCountry() {
		System.out.println("Inside Dao1Country");
		JSONObject status = new JSONObject();
		status.put("status", true);
		List<Country> countryList = null;
		try {
			countryList = countryRepository.findAll();
			status.put("Country", countryList);
			status.put("result", true);
		} catch (Exception e) {
			e.printStackTrace();
			status.put("result", false);
		}
		return status;
	}

	@Override
	public JSONObject updateCountry(JSONObject country) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject deleteCountry(JSONObject countryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getCountryById(JSONObject country) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Country getCountry(Integer countryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Country findByIsoCode(String countryIsoCode) {
		return countryRepository.findByIsoCode(countryIsoCode);
	}

}
