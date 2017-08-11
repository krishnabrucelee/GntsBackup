/**
 * 
 */
package com.lyca.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Krishna
 *
 */
@Entity
@Table(name = "tbl_country")
public class Country {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer countryId;
	
	@Column(name = "country_name")
	private String countryName;
	
	@Column(name = "country_iso_code")
	private String countryIsoCode;
	
	@Column(name = "country_isd_code")
	private String countryIsdCode;
	
	/**
	 * @return the countryId
	 */
	public Integer getCountryId() {
		return countryId;
	}

	/**
	 * @param countryId the countryId to set
	 */
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	/**
	 * @return the countryName
	 */
	public String getCountryName() {
		return countryName;
	}

	/**
	 * @param countryName the countryName to set
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	/**
	 * @return the countryIsoCode
	 */
	public String getCountryIsoCode() {
		return countryIsoCode;
	}

	/**
	 * @param countryIsoCode the countryIsoCode to set
	 */
	public void setCountryIsoCode(String countryIsoCode) {
		this.countryIsoCode = countryIsoCode;
	}

	/**
	 * @return the countryIsdCode
	 */
	public String getCountryIsdCode() {
		return countryIsdCode;
	}

	/**
	 * @param countryIsdCode the countryIsdCode to set
	 */
	public void setCountryIsdCode(String countryIsdCode) {
		this.countryIsdCode = countryIsdCode;
	}
}
