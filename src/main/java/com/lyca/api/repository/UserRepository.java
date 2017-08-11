package com.lyca.api.repository;

import com.lyca.api.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author Krishna
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select user from User user where user.mobileNumber = :mobileNumber")
	User getUserByMobileNumber(@Param("mobileNumber") String mobileNumber);
    
    @Query("select user from User user where user.lycaSubscriberId = :lycaSubscriberId")
    List<User> getUserBySubscriberId(@Param("lycaSubscriberId") String lycaSubscriberId);

    @Query("select user from User user where user.id = :userId")
	List<User> getUserById(@Param("userId") Integer userId);

    @Query("select user from User user where user.mobileNumber = :mobileNumber and user.otp = :otp")
	List<User> otpVerification(@Param("mobileNumber") String mobileNumber, @Param("otp") String otp);

}
