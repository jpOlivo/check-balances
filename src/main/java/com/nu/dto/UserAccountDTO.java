package com.nu.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.nu.domain.UserAccount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@SuppressWarnings("serial")
@ApiModel(description = "Representation of a user account")
public class UserAccountDTO implements Serializable {

	@ApiModelProperty(value = "The name of the user who owns the account")
	private String name;

	@ApiModelProperty(value = "The balance on the account")
	private BigDecimal balance;

	public UserAccountDTO() {
	}

	public UserAccountDTO(UserAccount userAccount) {
		super();
		this.name = userAccount.getUsername();
		this.balance = userAccount.getBalance();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
