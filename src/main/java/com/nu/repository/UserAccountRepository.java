package com.nu.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nu.domain.UserAccount;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, String> {
	
}
