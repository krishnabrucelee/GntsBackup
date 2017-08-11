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
@Table(name = "tbl_calldetails")
public class CallDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	/** Caller id or user id */
	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "caller_id", referencedColumnName = "id")
	private User callerId;
	
	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "call_to_id", referencedColumnName = "id")
	private User callTo;
	
	@Column(name = "call_status")
	private CallStatus callStatus;
	
	@Column(name = "call_time")
	private Date callTime;
	
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
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the callerId
	 */
	public User getCallerId() {
		return callerId;
	}

	/**
	 * @param callerId the callerId to set
	 */
	public void setCallerId(User callerId) {
		this.callerId = callerId;
	}

	/**
	 * @return the callTo
	 */
	public User getCallTo() {
		return callTo;
	}

	/**
	 * @param callTo the callTo to set
	 */
	public void setCallTo(User callTo) {
		this.callTo = callTo;
	}
	/**
	 * @return the callStatus
	 */
	public CallStatus getCallStatus() {
		return callStatus;
	}

	/**
	 * @param callStatus the callStatus to set
	 */
	public void setCallStatus(CallStatus callStatus) {
		this.callStatus = callStatus;
	}

	/**
	 * @return the callTime
	 */
	public Date getCallTime() {
		return callTime;
	}

	/**
	 * @param callTime the callTime to set
	 */
	public void setCallTime(Date callTime) {
		this.callTime = callTime;
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
