package com.nu.service;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.nu.domain.OperationType;
import com.nu.domain.Transaction;
import com.nu.domain.UserAccount;
import com.nu.dto.TransactionDTO;
import com.nu.repository.UserAccountRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class UserAccountServiceTest {
	@MockBean
	private UserAccountRepository repository;

	@Autowired
	private UserAccountService sut;

	@Test
	public void givenUserAccountExisting_thenReturnsBalance() {
		// Given
		String username = "juan";

		UserAccount userAccount = new UserAccount();
		userAccount.setUsername(username);
		userAccount.setBalance(BigDecimal.valueOf(100));
		userAccount.setVersion(Long.valueOf(0));

		Optional<UserAccount> userAccountOptional = Optional.of(userAccount);
		doReturn(userAccountOptional).when(repository).findById(username);

		// When
		UserAccount result = sut.getUserAccount(username);

		// Then
		assertThat(result, is(not(nullValue())));
		assertThat(result.getUsername(), is(username));
		assertThat(result.getBalance(), is(BigDecimal.valueOf(100)));
		assertThat(result.getVersion(), is(Long.valueOf(0)));

		ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
		verify(repository).findById(usernameCaptor.capture());
		assertThat(usernameCaptor.getValue(), is(username));

	}
	
	
	@Test
	public void givenUserAccountExistingAndDate_thenReturnsBalance() {
				// Given
				String username = "juan";

				UserAccount userAccount = new UserAccount();
				userAccount.setUsername(username);
				userAccount.setBalance(BigDecimal.valueOf(800));
				userAccount.setVersion(Long.valueOf(0));
				
				List<Transaction> transactions = new ArrayList<Transaction>();
				
				Transaction t = new Transaction();
				t.setAmount(BigDecimal.valueOf(400));
				t.setOperationType(OperationType.IN);
				t.setTime(LocalDateTime.of(2019, Month.JUNE, 28, 19, 30, 40));
				transactions.add(t);
				
				t = new Transaction();
				t.setAmount(BigDecimal.valueOf(500));
				t.setOperationType(OperationType.IN);
				t.setTime(LocalDateTime.of(2019, Month.JUNE, 29, 19, 30, 40));
				transactions.add(t);
				
				t = new Transaction();
				t.setAmount(BigDecimal.valueOf(100));
				t.setOperationType(OperationType.OUT);
				t.setTime(LocalDateTime.of(2019, Month.JUNE, 29, 19, 30, 40));
				transactions.add(t);
				
				t = new Transaction();
				t.setAmount(BigDecimal.valueOf(200));
				t.setOperationType(OperationType.OUT);
				t.setTime(LocalDateTime.of(2019, Month.JUNE, 30, 19, 30, 40));
				transactions.add(t);
				
				userAccount.setTransactions(transactions);
				
				Optional<UserAccount> userAccountOptional = Optional.of(userAccount);
				doReturn(userAccountOptional).when(repository).findById(username);

				// When
				BigDecimal result = sut.getBalanceUserAccountByDate(username, LocalDate.of(2019, Month.JUNE, 29));

				// Then
				assertThat(result, is(not(nullValue())));
				assertThat(result, is(BigDecimal.valueOf(800)));

				ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
				verify(repository).findById(usernameCaptor.capture());
				assertThat(usernameCaptor.getValue(), is(username));
	}
	
	@Test
	public void givenUserAccountExisting_whenCheckin_thenUpdateBalance() {
		// Given
		String username = "juan";
		
		UserAccount userAccount = new UserAccount();
		userAccount.setUsername(username);
		userAccount.setBalance(BigDecimal.valueOf(100));
		userAccount.setVersion(Long.valueOf(0));
		userAccount.setTransactions(new ArrayList<Transaction>());
		
		Optional<UserAccount> userAccountOptional = Optional.of(userAccount);
		doReturn(userAccountOptional).when(repository).findById(username);

		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setAmount(BigDecimal.valueOf(20));
		transactionDTO.setOperationType(OperationType.IN);

		// When
		sut.updateUserAccount(username, transactionDTO);

		// Then
		ArgumentCaptor<UserAccount> userAccountCaptor = ArgumentCaptor.forClass(UserAccount.class);
		verify(repository).save(userAccountCaptor.capture());
		UserAccount ua = userAccountCaptor.getValue();
        assertThat(ua, is(not(nullValue())));
        assertThat(ua.getUsername(), is(username));
        assertThat(ua.getBalance(), is(BigDecimal.valueOf(120)));
        assertThat(ua.getVersion(), is(Long.valueOf(0)));
        assertThat(ua.getTransactions(), hasSize(1));
	}

}
