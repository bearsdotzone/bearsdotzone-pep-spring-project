package com.example.service;

import com.example.entity.Account;
import com.example.exception.UnauthorizedValidationException;
import com.example.exception.ConflictException;
import com.example.exception.ValidationException;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * The registration will be successful if and only if the username is not blank, the password is at least 4
     * characters long, and an Account with that username does not already exist.
     *
     * @param account the account to be created
     * @return the account with its accountId field populated
     * @throws ValidationException occurs when the username or password do not meet requirements
     * @throws ConflictException   occurs when a user with that username already exists in the database
     */
    public Account registerNewAccount(Account account) {
        if (account.getUsername().isEmpty())
            throw new ValidationException();
        if (account.getPassword().length() < 4)
            throw new ValidationException();
        if (accountRepository.findByUsername(account.getUsername()).isPresent())
            throw new ConflictException();

        return accountRepository.save(account);
    }

    /**
     * The login will be successful if and only if the username and password provided match a real account existing on
     * the database.
     *
     * @param account the account to be logged into without an accountId
     * @return the account with its accountId field populated
     * @throws UnauthorizedValidationException occurs when the username and password do not match an account in the
     *                                         database
     */
    public Account loginAccount(Account account) {
        return accountRepository.findByUsernameAndPassword(account.getUsername(), account.getPassword())
                                .orElseThrow(UnauthorizedValidationException::new);
    }
}
