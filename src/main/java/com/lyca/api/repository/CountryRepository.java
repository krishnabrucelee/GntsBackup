/**
 * 
 */
package com.lyca.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lyca.api.model.Country;

/**
 * @author Krishna
 *
 */
public interface CountryRepository extends JpaRepository<Country, Integer> {

}
