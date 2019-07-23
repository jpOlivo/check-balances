package com.nu.dto;

import java.io.Serializable;
import java.util.UUID;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@SuppressWarnings("serial")
@ApiModel(description = "Representation of a committed transaction")
public class ResponseTransactionDTO implements Serializable {

	@ApiModelProperty(value = "Committed transaction identifier")
	private UUID uuid;

	public ResponseTransactionDTO() {
	}

	public ResponseTransactionDTO(UUID uuid) {
		super();
		this.uuid = uuid;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

}
