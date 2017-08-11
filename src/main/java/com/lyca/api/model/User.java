/**
 * 
 */
package com.lyca.api.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * @author Krishna
 *
 */
@Entity
@Table(name = "tbl_userdetails")
public class User implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer userId;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "mobile_number")
	private String mobileNumber;
	
	@Column(name = "stb_user")
	private Boolean stbUser;
	
	@Column(name = "lyca_subscriber_id")
	private String lycaSubscriberId;
	
	@Column(name = "password")
	private String password;
	
	@JsonUnwrapped
    @OneToOne(targetEntity = Country.class)
    @JoinColumn(name = "country_id", referencedColumnName = "id")
	private Country country;
	
	@Column(name = "profile_pic_url")
	private String profilePicUrl;

	@Column(name = "profile_status")
	private String profileStatus;
	
	@Column(name = "online_status")
	private OnlineStatus onlineStatus;
	
	@Column(name = "otp")
	private String otp;
	
	@Column(name = "otp_status")
	private Boolean otpStatus;
	
	@Column(name = "created_date")
	private Date createdDateTime;
	
	@Column(name = "updated_date")
	private Date updatedDateTime;
	
	public enum OnlineStatus {
		AVAILABLE, OFFLINE, DONOTDISTURB
	}
	
	/**
	 * Get the userId of Users.
	 *
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * Set the userId of Users.
	 *
	 * @param userId the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the stbUser
	 */
	public Boolean getStbUser() {
		return stbUser;
	}

	/**
	 * @param stbUser the stbUser to set
	 */
	public void setStbUser(Boolean stbUser) {
		this.stbUser = stbUser;
	}

	/**
	 * @return the lycaSubscriberId
	 */
	public String getLycaSubscriberId() {
		return lycaSubscriberId;
	}

	/**
	 * @param lycaSubscriberId the lycaSubscriberId to set
	 */
	public void setLycaSubscriberId(String lycaSubscriberId) {
		this.lycaSubscriberId = lycaSubscriberId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the countryId
	 */
	@JsonIgnore
	public Country getCountry() {
		return country;
	}

	/**
	 * @param countryId the countryId to set
	 */
	@JsonGetter
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * @return the profilePicUrl
	 */
	public String getProfilePicUrl() {
		return profilePicUrl;
	}

	/**
	 * @param profilePicUrl the profilePicUrl to set
	 */
	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}

	/**
	 * @return the profileStatus
	 */
	public String getProfileStatus() {
		return profileStatus;
	}

	/**
	 * @param profileStatus the profileStatus to set
	 */
	public void setProfileStatus(String profileStatus) {
		this.profileStatus = profileStatus;
	}

	/**
	 * @return the onlineStatus
	 */
	public OnlineStatus getOnlineStatus() {
		return onlineStatus;
	}

	/**
	 * @param onlineStatus the onlineStatus to set
	 */
	public void setOnlineStatus(OnlineStatus onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	/**
	 * @return the otp
	 */
	public String getOtp() {
		return otp;
	}

	/**
	 * @param otp the otp to set
	 */
	public void setOtp(String otp) {
		this.otp = otp;
	}

	/**
	 * @return the createdDateTime
	 */
	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	/**
	 * @param createdDateTime the createdDateTime to set
	 */
	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	/**
	 * @return the updatedDateTime
	 */
	public Date getUpdatedDateTime() {
		return updatedDateTime;
	}

	/**
	 * @param updatedDateTime the updatedDateTime to set
	 */
	public void setUpdatedDateTime(Date updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	/**
	 * @return the otpStatus
	 */
	public Boolean getOtpStatus() {
		return otpStatus;
	}

	/**
	 * @param otpStatus the otpStatus to set
	 */
	public void setOtpStatus(Boolean otpStatus) {
		this.otpStatus = otpStatus;
	}

}
