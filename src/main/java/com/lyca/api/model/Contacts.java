/**
 * 
 */
package com.lyca.api.model;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author Krishna
 *
 */
@Entity
@Table(name = "tbl_contacts")
public class Contacts {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer contactId;

	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "base_user", referencedColumnName = "id")
	private User baseUser;

	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "contact_user", referencedColumnName = "id")
	private User contactUser;

	@OneToOne(targetEntity = Country.class)
	@JoinColumn(name = "country_id", referencedColumnName = "id")
	private Country country;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Invities> invities;

	@Column(name = "invite_status")
	private InviteeStatus inviteeStatus;

	@Column(name = "mobile_number")
	private String mobileNumber;

	@Column(name = "contact_blocked")
	private Boolean contactBlocked;

	@Column(name = "contact_removed")
	private Boolean contactRemoved;

	@Column(name = "nick_name")
	private String nickName;

	@Column(name = "call_count")
	private Integer callCount;

	@Column(name = "created_date")
	private Date createdDateTime;

	@Column(name = "updated_date")
	private Date updatedDateTime;

	public enum InviteeStatus {
		PENDING, ACCEPTED, REJECTED
	}

	/**
	 * @return the createdDateTime
	 */
	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	/**
	 * @param createdDateTime
	 *            the createdDateTime to set
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
	 * @param updatedDateTime
	 *            the updatedDateTime to set
	 */
	public void setUpdatedDateTime(Date updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	/**
	 * @return the contactId
	 */
	public Integer getContactId() {
		return contactId;
	}

	/**
	 * @param contactId
	 *            the contactId to set
	 */
	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	/**
	 * @return the baseUser
	 */
	public User getBaseUser() {
		return baseUser;
	}

	/**
	 * @param baseUser
	 *            the baseUser to set
	 */
	public void setBaseUser(User baseUser) {
		this.baseUser = baseUser;
	}

	/**
	 * @return the contactUser
	 */
	public User getContactUser() {
		return contactUser;
	}

	/**
	 * @param contactUser
	 *            the contactUser to set
	 */
	public void setContactUser(User contactUser) {
		this.contactUser = contactUser;
	}

	/**
	 * @return the invities
	 */
	public List<Invities> getInvities() {
		return invities;
	}

	/**
	 * @param invities
	 *            the invities to set
	 */
	public void setInvities(List<Invities> invities) {
		this.invities = invities;
	}

	/**
	 * @return the contactBlocked
	 */
	public Boolean getContactBlocked() {
		return contactBlocked;
	}

	/**
	 * @param contactBlocked
	 *            the contactBlocked to set
	 */
	public void setContactBlocked(Boolean contactBlocked) {
		this.contactBlocked = contactBlocked;
	}

	/**
	 * @return the contactRemoved
	 */
	public Boolean getContactRemoved() {
		return contactRemoved;
	}

	/**
	 * @param contactRemoved
	 *            the contactRemoved to set
	 */
	public void setContactRemoved(Boolean contactRemoved) {
		this.contactRemoved = contactRemoved;
	}

	/**
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * @param nickName
	 *            the nickName to set
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	/**
	 * @return the callCount
	 */
	public Integer getCallCount() {
		return callCount;
	}

	/**
	 * @param callCount
	 *            the callCount to set
	 */
	public void setCallCount(Integer callCount) {
		this.callCount = callCount;
	}

	/**
	 * @return the country
	 */
	public Country getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber
	 *            the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the inviteeStatus
	 */
	public InviteeStatus getInviteeStatus() {
		return inviteeStatus;
	}

	/**
	 * @param inviteeStatus
	 *            the inviteeStatus to set
	 */
	public void setInviteeStatus(InviteeStatus inviteeStatus) {
		this.inviteeStatus = inviteeStatus;
	}

}
