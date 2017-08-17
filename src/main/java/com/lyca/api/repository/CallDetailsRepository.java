/**
 * 
 */
package com.lyca.api.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.lyca.api.model.CallDetails;

/**
 * @author Krishna
 *
 */
@Repository
public interface CallDetailsRepository extends JpaRepository<CallDetails, Integer> {

//	@Query("select callDetails from CallDetails callDetails where callDetails.callTo.userId =:userId and not callDetails.callStatus = :callStatus "
//			+ "ORDER BY callDetails.id ")
//	List<CallDetails> getFavlist(com.lyca.api.model.CallLog.CallStatus groupcall, Integer userId,
//			PageRequest pageRequest);

}
