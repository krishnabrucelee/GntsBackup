/**
 * 
 */
package com.lyca.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.lyca.api.model.Contacts;

/**
 * @author Krishna
 *
 */
public interface ContactsRepository extends JpaRepository<Contacts, Integer> {
	
	@Query("select contact from Contacts contact where contact.baseUser.userId = :userId and contact.contactRemoved = :contactRemoved and contact.mobileNumber =:mobileNumber ")
	List<Contacts> checkContactToCallFromCaller(@Param("userId") Integer userId, @Param("contactRemoved") Boolean contactRemoved, @Param("mobileNumber") String mobileNumber);

	@Query("select contact from Contacts contact where contact.baseUser.userId = :userId and contact.contactRemoved = :contactRemoved and NOT contact.mobileNumber =:mobileNumber ORDER BY contact.nickName ASC ")
	List<Contacts> getContactsById(@Param("userId") Integer userId, @Param("contactRemoved") Boolean contactRemoved, @Param("mobileNumber") String mobileNumber);

	@Query("select contact from Contacts contact where contact.mobileNumber = :mobileNumber and contact.baseUser.userId = :userId ")
	List<Contacts> getDuplicateContactByBaseUserId(@Param("mobileNumber") String mobileNumber, @Param("userId") Integer userId);

	@Query("select contact from Contacts contact where contact.mobileNumber = :mobileNumber and contact.baseUser.userId = :baseUserId and contact.contactUser.userId = :callToUserId")
	Contacts getContactsByCallUsersAndMobile(@Param("baseUserId") Integer baseUserId, @Param("mobileNumber") String mobileNumber, @Param("callToUserId") Integer callToUserId);

}
