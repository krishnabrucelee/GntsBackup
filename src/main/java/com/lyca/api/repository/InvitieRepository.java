/**
 * 
 */
package com.lyca.api.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lyca.api.model.Invities;
import com.lyca.api.model.Invities.InviteeStatus;

/**
 * @author Krishna
 *
 */
@Repository
public interface InvitieRepository extends JpaRepository<Invities, Integer> {

	@Query("select invite from Invities invite where invite.inviteeMobileNumber = :inviteeMobileNumber ORDER BY invite.invitieId DESC ")
	List<Invities> getInvitiesByMobileNumber(@Param("inviteeMobileNumber") String inviteeMobileNumber);

	@Query("select invite from Invities invite where baseUser.userId = :userId ")
	List<Invities> getInvitieByBaseUser(@Param("userId") Integer userId);

	@Query("select invite from Invities invite where invite.inviteeMobileNumber = :inviteeMobileNumber and invite.inviteeStatus = :inviteeStatus ")
	List<Invities> getMyInvitieListByMobileNumber(@Param("inviteeMobileNumber") String inviteeMobileNumber, @Param("inviteeStatus") InviteeStatus inviteeStatus);

}
