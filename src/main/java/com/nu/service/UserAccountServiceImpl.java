package com.nu.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.nu.domain.OperationType;
import com.nu.domain.Transaction;
import com.nu.domain.UserAccount;
import com.nu.dto.TransactionDTO;
import com.nu.exception.ResourceLockingFailureException;
import com.nu.exception.TransactionNotAllowedException;
import com.nu.exception.UserAccountNotFoundException;
import com.nu.repository.UserAccountRepository;

@Service
public class UserAccountServiceImpl implements UserAccountService {

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Override
	public UserAccount getUserAccount(String username) {
		return userAccountRepository.findById(username).orElseThrow(() -> new UserAccountNotFoundException(username));
	}

	@Override
	public UserAccount updateUserAccount(String username, TransactionDTO transactionDTO) {
		UserAccount userAccount = getUserAccount(username);
		BigDecimal currentBalance = userAccount.getBalance();

		validateBalance(transactionDTO, userAccount);

		Transaction transaction = new Transaction();
		transaction.setAmount(transactionDTO.getAmount());
		transaction.setOperationType(transactionDTO.getOperationType());
		transaction.setTime(LocalDateTime.now());

		userAccount.getTransactions().add(transaction);

		switch (transactionDTO.getOperationType()) {
		case IN:
			userAccount.setBalance(currentBalance.add(transactionDTO.getAmount()));
			break;

		case OUT:
			userAccount.setBalance(currentBalance.subtract(transactionDTO.getAmount()));
			break;

		default:
			throw new TransactionNotAllowedException("Operation type not allowed");
		}

		try {
			return userAccountRepository.save(userAccount);
		} catch (OptimisticLockingFailureException e) {
			throw new ResourceLockingFailureException(userAccount.getVersion());
		}
	}

	private void validateBalance(TransactionDTO transactionDTO, UserAccount userAccount) {

		if (OperationType.OUT.equals(transactionDTO.getOperationType())
				&& transactionDTO.getAmount().compareTo(userAccount.getBalance()) > 0) {
			throw new TransactionNotAllowedException("There are not enough funds to carry out this operation.");
		}
	}

	@Override
	public BigDecimal getBalanceUserAccountByDate(String username, LocalDate date) {
		Optional<UserAccount> userAccountOptional = userAccountRepository.findById(username);
		BigDecimal balance = BigDecimal.valueOf(0);

		if (userAccountOptional.isPresent()) {
			Predicate<Transaction> isBefore = (t) -> t.getTime().toLocalDate().isBefore(date);
			Predicate<Transaction> isEqual = (t) -> t.getTime().toLocalDate().isEqual(date);

			List<Transaction> transactions = userAccountOptional.get().getTransactions().stream()
					.filter(isBefore.or(isEqual)).collect(Collectors.toList());

			Function<Transaction, BigDecimal> action = (Transaction t) -> {
				if (OperationType.IN.equals(t.getOperationType())) {
					return t.getAmount();
				} else {
					return t.getAmount().negate();
				}
			};

			balance = transactions.stream().map(action).reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b));
		}
		return balance;
	}

}
