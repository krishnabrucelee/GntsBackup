/**
 * 
 */
package com.lyca.api.model;

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
@Table(name = "tbl_invites")
public class Invities {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer invitieId;

	@JsonUnwrapped
	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "base_user", referencedColumnName = "id")
	private User baseUser;

	@Column(name = "invitation_code")
	private String invitationCode;

	@Column(name = "invitee_mobile_number")
	private String inviteeMobileNumber;

	@Column(name = "invitee_status")
	private InviteeStatus inviteeStatus;

	@Column(name = "created_date")
	private Date createdDateTime;
	
	@Column(name = "updated_date")
	private Date updatedDateTime;
	
	public enum InviteeStatus {
		PENDING, ACCEPTED, REJECTED
	}
	
	/**
	 * @return the baseUser
	 */
	@JsonIgnore
	public User getBaseUser() {
		return baseUser;
	}

	/**
	 * @param baseUser
	 *            the baseUser to set
	 */
	@JsonGetter
	public void setBaseUser(User baseUser) {
		this.baseUser = baseUser;
	}

	/**
	 * @return the invitationCode
	 */
	public String getInvitationCode() {
		return invitationCode;
	}

	/**
	 * @param invitationCode
	 *            the invitationCode to set
	 */
	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	/**
	 * @return the inviteeMobileNumber
	 */
	public String getInviteeMobileNumber() {
		return inviteeMobileNumber;
	}

	/**
	 * @param inviteeMobileNumber
	 *            the inviteeMobileNumber to set
	 */
	public void setInviteeMobileNumber(String inviteeMobileNumber) {
		this.inviteeMobileNumber = inviteeMobileNumber;
	}

	/**
	 * @return the inviteId
	 */
	public Integer getInvitieId() {
		return invitieId;
	}

	/**
	 * @param inviteId the inviteId to set
	 */
	public void setInvitieId(Integer invitieId) {
		this.invitieId = invitieId;
	}

	/**
	 * @return the inviteeStatus
	 */
	public InviteeStatus getInviteeStatus() {
		return inviteeStatus;
	}

	/**
	 * @param inviteeStatus the inviteeStatus to set
	 */
	public void setInviteeStatus(InviteeStatus inviteeStatus) {
		this.inviteeStatus = inviteeStatus;
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

}
