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

/**
 * @author Krishna
 *
 */
@Entity
@Table(name = "tbl_calllog")
public class CallLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	/** call id. */
	@OneToOne(targetEntity = CallDetails.class)
	@JoinColumn(name = "call_id", referencedColumnName = "id")
	private CallDetails callDetails;

	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	@Column(name = "call_status")
	private CallStatus callStatus;

	@Column(name = "in_call_time")
	private Date inCallTime;

	@Column(name = "out_call_time")
	private Date outCallTime;

	@Column(name = "created_date")
	private Date createdDateTime;

	@Column(name = "updated_date")
	private Date updatedDateTime;

	public enum CallStatus {
		CALLING, ATTENDED, CALLEND, REJECTED, MISSEDCALL, DISCONNECTED, GROUPCALL
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the callDetails
	 */
	public CallDetails getCallDetails() {
		return callDetails;
	}

	/**
	 * @param callDetails
	 *            the callDetails to set
	 */
	public void setCallDetails(CallDetails callDetails) {
		this.callDetails = callDetails;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the inCallTime
	 */
	public Date getInCallTime() {
		return inCallTime;
	}

	/**
	 * @param inCallTime
	 *            the inCallTime to set
	 */
	public void setInCallTime(Date inCallTime) {
		this.inCallTime = inCallTime;
	}

	/**
	 * @return the outCallTime
	 */
	public Date getOutCallTime() {
		return outCallTime;
	}

	/**
	 * @param outCallTime
	 *            the outCallTime to set
	 */
	public void setOutCallTime(Date outCallTime) {
		this.outCallTime = outCallTime;
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
	 * @return the callStatus
	 */
	public CallStatus getCallStatus() {
		return callStatus;
	}

	/**
	 * @param callStatus
	 *            the callStatus to set
	 */
	public void setCallStatus(CallStatus callStatus) {
		this.callStatus = callStatus;
	}

}
