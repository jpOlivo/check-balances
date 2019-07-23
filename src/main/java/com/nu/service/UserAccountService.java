package com.nu.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nu.domain.UserAccount;
import com.nu.dto.TransactionDTO;

public interface UserAccountService {
	
	UserAccount getUserAccount(String username);
	
	BigDecimal getBalanceUserAccountByDate(String username, LocalDate date);

	UserAccount updateUserAccount(String username, TransactionDTO transactionDTO);
	
	
}
