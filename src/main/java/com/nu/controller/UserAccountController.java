package com.nu.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.nu.domain.UserAccount;
import com.nu.dto.ResponseTransactionDTO;
import com.nu.dto.TransactionDTO;
import com.nu.dto.UserAccountDTO;
import com.nu.exception.IfMatchErrorException;
import com.nu.exception.IfMatchNotFoundException;
import com.nu.service.UserAccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@Api("/api/accounts")
@RestController
public class UserAccountController {

	@Autowired
	private UserAccountService userAccountService;

	@ApiOperation(value = "Register a new monetary transaction", response = ResponseTransactionDTO.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Success", response = ResponseTransactionDTO.class),
					@ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 409, message = "Conflict"),
					@ApiResponse(code = 412, message = "Precondition Failed"),
					@ApiResponse(code = 404, message = "Not found") })
	@PutMapping("/api/accounts/{name}/transactions")
	public ResponseEntity<ResponseTransactionDTO> registerTransaction(@ApiIgnore  WebRequest request,
			@ApiParam(required = true, name = "name", value = "Name of the user who owns the account") @PathVariable String name,
			@ApiParam(required = true, name = "transaction", value = "Money transaction") @RequestBody TransactionDTO transactionDTO) {
		
		UserAccount userAccount = userAccountService.getUserAccount(name);

		String ifMatchValue = request.getHeader("If-Match");
		if (isEmpty(ifMatchValue)) throw new IfMatchNotFoundException();
		if (!ifMatchValue.equals("\"" + userAccount.getVersion() + "\"")) throw new IfMatchErrorException();

		userAccount = userAccountService.updateUserAccount(name, transactionDTO);

		return ResponseEntity.ok().eTag("\"" + userAccount.getVersion() + "\"")
				.body(new ResponseTransactionDTO(UUID.randomUUID()));

	}

	private boolean isEmpty(String value) {
		return !StringUtils.hasText(value);
	}

	@ApiResponses({ @ApiResponse(code = 200, message = "Success", response = UserAccountDTO.class) })
	@ApiOperation(value = "Return balance of an user account", response = UserAccountDTO.class)
	@GetMapping("/api/accounts/{name}/balance")
	public ResponseEntity<UserAccountDTO> getBalance(
			@ApiParam(required = true, name = "name", value = "Name of the user who owns the account") @PathVariable String name) {

		UserAccount userAccount = userAccountService.getUserAccount(name);
		return ResponseEntity.ok().eTag("\"" + userAccount.getVersion() + "\"").body(new UserAccountDTO(userAccount));
	}
}
