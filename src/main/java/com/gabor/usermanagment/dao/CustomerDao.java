package com.gabor.usermanagment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gabor.usermanagment.beans.Customer;

@Repository
public interface CustomerDao extends JpaRepository<Customer, Long>{
	
	
	
	
	
}
