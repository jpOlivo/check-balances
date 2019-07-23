package com.nu.controller;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.nu.domain.OperationType;
import com.nu.dto.ResponseTransactionDTO;
import com.nu.dto.TransactionDTO;
import com.nu.dto.UserAccountDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserAccountControllerIT {
	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void contextLoads() {
	}

	@Test
	public void givenUserAccountExisting_thenReturnsBalance() {
		// Given
		String username = "juan";

		// When
		ResponseEntity<UserAccountDTO> response = restTemplate.getForEntity("/api/accounts/{name}/balance",
				UserAccountDTO.class, username);

		// Then
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		UserAccountDTO result = response.getBody();
		assertThat(result.getName(), is(username));
		assertThat(result.getBalance(), is(closeTo(new BigDecimal(0), new BigDecimal(1))));
	}

	@Test
	public void givenUserAccountNonExisting_thenReturnsNotFound() {
		// Given
		String username = "foo";

		// When
		ResponseEntity<UserAccountDTO> response = restTemplate.getForEntity("/api/accounts/{name}/balance",
				UserAccountDTO.class, username);

		// Then
		assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
	}

	@Test
	public void givenUserAccountExisting_whenCheckin_thenUpdateBalance() {
		// Given
		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setAmount(BigDecimal.valueOf(20));
		transactionDTO.setOperationType(OperationType.IN);

		// When
		String username = "juan";

		Map<String, String> params = new HashMap<String, String>();
		params.put("name", username);

		HttpHeaders headers = new HttpHeaders();
		headers.set("If-Match", "\"0\"");

		HttpEntity<TransactionDTO> requestEntity = new HttpEntity<TransactionDTO>(transactionDTO, headers);
		ResponseEntity<ResponseTransactionDTO> response = restTemplate.exchange("/api/accounts/{name}/transactions",
				HttpMethod.PUT, requestEntity, ResponseTransactionDTO.class, params);

		// Then
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody().getUuid().toString(), not(isEmptyOrNullString()));

		ResponseEntity<UserAccountDTO> r = restTemplate.getForEntity("/api/accounts/{name}/balance",
				UserAccountDTO.class, username);

		assertThat(r.getStatusCode(), is(HttpStatus.OK));
		UserAccountDTO result = r.getBody();
		assertThat(result.getName(), is(username));
		assertThat(result.getBalance(), is(closeTo(new BigDecimal(20), new BigDecimal(21))));
	}

	@Test
	public void givenUserAccountExisting_whenCheckinAndResourceOutdated_thenReturnsPreconditionFailed() {
		// Given
		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setAmount(BigDecimal.valueOf(20));
		transactionDTO.setOperationType(OperationType.IN);

		// When
		String username = "juan";

		Map<String, String> params = new HashMap<String, String>();
		params.put("name", username);

		HttpHeaders headers = new HttpHeaders();
		headers.set("If-Match", "\"1\"");

		HttpEntity<TransactionDTO> requestEntity = new HttpEntity<TransactionDTO>(transactionDTO, headers);
		ResponseEntity<ResponseTransactionDTO> response = restTemplate.exchange("/api/accounts/{name}/transactions",
				HttpMethod.PUT, requestEntity, ResponseTransactionDTO.class, params);

		// Then
		assertThat(response.getStatusCode(), is(HttpStatus.PRECONDITION_FAILED));

		ResponseEntity<UserAccountDTO> r = restTemplate.getForEntity("/api/accounts/{name}/balance",
				UserAccountDTO.class, username);

		assertThat(r.getStatusCode(), is(HttpStatus.OK));
		UserAccountDTO result = r.getBody();
		assertThat(result.getName(), is(username));
		assertThat(result.getBalance(), is(closeTo(new BigDecimal(0), new BigDecimal(1))));
	}

	@Test
	public void givenUserAccountExisting_whenCheckinAndResourceVersionNotProvided_thenReturnsBadRequest() {
		// Given
		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setAmount(BigDecimal.valueOf(20));
		transactionDTO.setOperationType(OperationType.IN);

		// When
		String username = "juan";

		Map<String, String> params = new HashMap<String, String>();
		params.put("name", username);

		HttpEntity<TransactionDTO> requestEntity = new HttpEntity<TransactionDTO>(transactionDTO);
		ResponseEntity<ResponseTransactionDTO> response = restTemplate.exchange("/api/accounts/{name}/transactions",
				HttpMethod.PUT, requestEntity, ResponseTransactionDTO.class, params);

		// Then
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));

		ResponseEntity<UserAccountDTO> r = restTemplate.getForEntity("/api/accounts/{name}/balance",
				UserAccountDTO.class, username);

		UserAccountDTO result = r.getBody();
		assertThat(result.getName(), is(username));
		assertThat(result.getBalance(), is(closeTo(new BigDecimal(0), new BigDecimal(1))));

	}

	@Test
	public void givenUserAccountExistingWithInsufficientBalance_whenCheckout_thenReturnsConflict() {
		// Given
		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setAmount(BigDecimal.valueOf(20));
		transactionDTO.setOperationType(OperationType.OUT);

		// When
		String username = "juan";

		Map<String, String> params = new HashMap<String, String>();
		params.put("name", username);

		HttpHeaders headers = new HttpHeaders();
		headers.set("If-Match", "\"0\"");

		HttpEntity<TransactionDTO> requestEntity = new HttpEntity<TransactionDTO>(transactionDTO, headers);
		ResponseEntity<ResponseTransactionDTO> response = restTemplate.exchange("/api/accounts/{name}/transactions",
				HttpMethod.PUT, requestEntity, ResponseTransactionDTO.class, params);

		// Then
		assertThat(response.getStatusCode(), is(HttpStatus.CONFLICT));

		ResponseEntity<UserAccountDTO> r = restTemplate.getForEntity("/api/accounts/{name}/balance",
				UserAccountDTO.class, username);

		assertThat(r.getStatusCode(), is(HttpStatus.OK));
		UserAccountDTO result = r.getBody();
		assertThat(result.getName(), is(username));
		assertThat(result.getBalance(), is(closeTo(new BigDecimal(0), new BigDecimal(1))));
	}

}
