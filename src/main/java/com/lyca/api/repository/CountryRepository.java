/**
 * 
 */
package com.lyca.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lyca.api.model.Country;
import com.lyca.api.model.User;

/**
 * @author Krishna
 *
 */
public interface CountryRepository extends JpaRepository<Country, Integer> {

	@Query("select country from Country country where country.countryIsoCode = :countryIsoCode")
	Country findByIsoCode(@Param("countryIsoCode") String countryIsoCode);
}
