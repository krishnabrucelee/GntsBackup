/**
 * 
 */
package com.lyca.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lyca.api.model.CallDetails;

/**
 * @author Krishna
 *
 */
@Repository
public interface CallDetailsRepository extends JpaRepository<CallDetails, Integer> {

}
