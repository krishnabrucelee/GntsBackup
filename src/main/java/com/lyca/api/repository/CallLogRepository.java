/**
 * 
 */
package com.lyca.api.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lyca.api.model.CallDetails;
import com.lyca.api.model.CallLog;
import com.lyca.api.model.CallLog.CallStatus;

/**
 * @author Krishna
 *
 */
@Repository
public interface CallLogRepository extends JpaRepository<CallLog, Integer> {

	@Query("select callLog from CallLog callLog where callLog.callDetails.id = :callDetailsId and callLog.outCallTime IS NULL")
	List<CallLog> getOnCallLiveUsers(@Param("callDetailsId") Integer callDetailsId);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("update CallLog callLog set callLog.callStatus = :callStatus where callLog.callDetails.id = :callId")
	void updateCallStatus(@Param("callId") Integer callId, @Param("callStatus") CallStatus callStatus);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("update CallLog callLog set callLog.callStatus = :callStatus, callLog.updatedDateTime = :updatedDateTime where callLog.callDetails.id = :callId")
	void updateCallStatusWithDateTime(@Param("callId") Integer callId, @Param("callStatus") CallStatus callStatus, @Param("updatedDateTime") Date date);

	@Query("select callLog from CallLog callLog where callLog.callDetails.id = :callDetailsId AND callLog.user.userId =:callToId")
	List<CallLog> getCallLogByCallIdAndUserId(@Param("callDetailsId") Integer callDetailsId, @Param("callToId") Integer callToId);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("update CallLog callLog set callLog.callStatus = :callStatus, callLog.outCallTime = :outCallTime, callLog.updatedDateTime = :updatedDateTime where callLog.callDetails.id = :callDetailsId")
	void updateCallLogInitiater(@Param("callDetailsId") Integer callDetailsId, @Param("callStatus") CallStatus callStatus, @Param("outCallTime") Date outCallTime, @Param("updatedDateTime") Date updatedDateTime);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("update CallLog callLog set callLog.callStatus = :callStatus, callLog.outCallTime = :outCallTime, callLog.updatedDateTime = :updatedDateTime where callLog.user.userId = :callerId and callLog.callDetails.id = :callDetailsId")
	void updateActiveCall(@Param("callerId") Integer callerId, @Param("callDetailsId") Integer callDetailsId, @Param("callStatus") CallStatus callStatus, @Param("outCallTime") Date outCallTime, @Param("updatedDateTime") Date updatedDateTime);

	@Query("select callLog from CallLog callLog where callLog.user.userId =:userId ORDER BY callLog.id DESC")
	List<CallLog> getUserFromCallLog(@Param("userId") Integer userId, Pageable pageable);
	
	@Query("select callLog from CallLog callLog where callLog.callDetails.id =:callDetailsId and not callLog.user.userId =:userId and not callLog.callStatus = :callStatus ORDER BY callLog.id DESC")
	List<CallLog> getCallLogFav(@Param("callStatus") CallLog.CallStatus callStatus, @Param("callDetailsId") Integer callDetailsId, @Param("userId") Integer userId, Pageable pageable);

	@Query("select callLog from CallLog callLog where callLog.user.userId =:userId and not callLog.callStatus = :callStatus ORDER BY callLog.id DESC")
	List<CallLog> getCallDetailsIdByUserId(@Param("callStatus") CallLog.CallStatus callStatus, @Param("userId") Integer userId, Pageable pageable);
	
//	@Query("select distinct callLog from CallLog callLog LEFT JOIN callLog.callDetails callDetails "
//			+ "where callLog.user.userId =:userId and callDetails.id = callLog.callDetails.id and callLog.callStatus <>:callStatus GROUP BY callLog.id DESC")
//	List<CallLog> getCallDetailsIdByUserId(@Param("callStatus") CallLog.CallStatus callStatus, @Param("userId") Integer userId, Pageable pageable);
	
	@Query("SELECT call.callerId.userId, call.callTo.userId, call.callStatus, "
			+ "user.firstName, user.lastName, user.mobileNumber, user.profileStatus, user.onlineStatus, country.countryId, "
			+ "country.countryIsdCode, country.countryName, count(call.id), contact.nickName " + "FROM CallDetails call , "
					+ "Contacts contact "
			+ "LEFT JOIN call.callTo user "
			+ "LEFT JOIN call.callTo.country country "
			+ "WHERE (call.callerId.userId = :userId AND NOT call.callStatus = :callStatus) and "
			+ "contact.contactUser.userId = call.callTo.userId and (contact.baseUser.userId = :userId and contact.contactBlocked = false) "
			+ "GROUP BY call.callerId.userId, call.callTo.userId ORDER BY count(call.id) DESC ")
	List<Object[]> getFavList(@Param("callStatus") CallDetails.CallStatus callStatus, @Param("userId") Integer userId, Pageable pageable);

	@Query("select callLog from CallLog callLog where user.userId = :callerId and outCallTime IS NULL")
	List<CallLog> checkActiveCallFromOutTime(@Param("callerId") Integer callerId);

	@Query("select callLog from CallLog callLog where callLog.callDetails.id = :callDetailsId")
	List<CallLog> getOnCallLiveUsersWithOutTimeNOTNull(@Param("callDetailsId") Integer callDetailsId);
}