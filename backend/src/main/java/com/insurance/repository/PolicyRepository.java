package com.insurance.repository;

import com.insurance.model.Policy;
import com.insurance.model.PolicyStatus;
import com.insurance.model.PolicyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    
    Page<Policy> findByStatus(PolicyStatus status, Pageable pageable);
    
    Page<Policy> findByType(PolicyType type, Pageable pageable);
    
    Page<Policy> findByHolderNameContainingIgnoreCase(String holderName, Pageable pageable);
    
    Page<Policy> findByStatusAndType(PolicyStatus status, PolicyType type, Pageable pageable);
    
    long countByStatus(PolicyStatus status);
    
    long countByType(PolicyType type);
    
    @Query("SELECT COUNT(p) FROM Policy p WHERE p.policyNumber LIKE :prefix%")
    long countByPolicyNumberPrefix(String prefix);
}
