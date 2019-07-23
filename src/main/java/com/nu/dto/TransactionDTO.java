package com.nu.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.nu.domain.OperationType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@SuppressWarnings("serial")
@ApiModel(description = "Representation of a money transaction")
public class TransactionDTO implements Serializable {

	@ApiModelProperty(value = "The amount of transaction")
	private BigDecimal amount;

	@ApiModelProperty(value = "The operation type", allowableValues = "IN, OUT")
	private OperationType operationType;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

}
