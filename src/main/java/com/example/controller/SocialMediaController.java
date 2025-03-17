package com.example.controller;


import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.UnauthorizedValidationException;
import com.example.exception.ConflictException;
import com.example.exception.ValidationException;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Annotating this class as a @RestController means that @ResponseBody annotations are not required.
 */
@RestController
public class SocialMediaController {

    @Autowired
    private final AccountService accountService;
    @Autowired
    private final MessageService messageService;

    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    /**
     * As a user, I should be able to create a new Account on the endpoint POST /register. The body will contain a
     * representation of a JSON Account, but will not contain an accountId.
     * <p>
     * If successful, the response body should contain a JSON of the Account, including its accountId. The response
     * status should be 200 OK, which is the default.
     * <p>
     * If the registration is not successful due to a duplicate username, the response status should be 409. (Conflict)
     * If the registration is not successful for some other reason, the response status should be 400. (Client error)
     *
     * @param account account object marshalled from @RequestBody
     * @return the created account or an empty body
     * @see ConflictException
     * @see ValidationException
     */
    @PostMapping("/register")
    public ResponseEntity<Account> registerNewAccount(@RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(accountService.registerNewAccount(account));
    }

    /**
     * As a user, I should be able to verify my login on the endpoint POST /login. The request body will contain a JSON
     * representation of an Account.
     * <p>
     * If successful, the response body should contain a JSON of the account in the response body, including its
     * accountId. The response status should be 200 OK, which is the default.
     * <p>
     * If the login is not successful, the response status should be 401. (Unauthorized)
     *
     * @param account account object marshalled from @RequestBody
     * @return the logged in account or an empty body
     * @see UnauthorizedValidationException
     */
    @PostMapping("/login")
    public ResponseEntity<Account> loginAccount(@RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(accountService.loginAccount(account));
    }

    /**
     * As a user, I should be able to submit a new post on the endpoint POST /messages. The request body will contain a
     * JSON representation of a message but will not contain a messageId.
     * <p>
     * If successful, the response body should contain a JSON of the message, including its messageId. The response
     * status should be 200, which is the default.
     * <p>
     * If the creation of the message is not successful, the response status should be 400. (Client error)
     *
     * @param message message object marshalled from @RequestBody
     * @return the created message or an empty body
     * @see ValidationException
     */
    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(messageService.createMessage(message));

    }

    /**
     * As a user, I should be able to submit a GET request on the endpoint GET /messages.
     * <p>
     * The response body should contain a JSON representation of a list containing all messages retrieved from the
     * database.
     * <p>
     * It is expected for the list to simply be empty if there are no messages.
     * <p>
     * The response status should always be 200, which is the default.
     *
     * @return a possibly empty list of all messages
     */
    @GetMapping("/messages")
    public List<Message> getMessages() {
        return messageService.getMessages();
    }

    /**
     * As a user, I should be able to submit a GET request on the endpoint GET /messages/{messageId}.
     * <p>
     * The response body should contain a JSON representation of the message identified by the messageId.
     * <p>
     * It is expected for the response body to simply be empty if there is no such message.
     * <p>
     * The response status should always be 200, which is the default.
     *
     * @param messageId integer messageId determined by the {messageId} @PathVariable
     * @return the json representation of the message identified by messageId or null
     */
    @GetMapping("/messages/{messageId}")
    public Message getMessageById(@PathVariable int messageId) {
        return messageService.getMessageById(messageId).orElse(null);
    }

    /**
     * As a User, I should be able to submit a DELETE request on the endpoint DELETE /messages/{messageId}.
     * <p>
     * If the message existed, the response body should contain the number of rows updated (1).
     * <p>
     * If the message did not exist the response body should be empty.
     * <p>
     * The response status should always be 200, which is the default.
     *
     * @param messageId integer messageId determined by the {messageId} @PathVariable
     * @return the number of rows affected or null
     */
    @DeleteMapping("/messages/{messageId}")
    public Integer deleteMessageById(@PathVariable int messageId) {
        return messageService.deleteMessageById(messageId).orElse(null);
    }

    /**
     * As a user, I should be able to submit a PATCH request on the endpoint PATCH /messages/{messageId}.
     * <p>
     * The request body should contain a new messageText value to replace the message identified by messageId. The
     * request body can not be guaranteed to contain any other information.
     * <p>
     * If the update is successful, the response body should contain the number of rows updated (1), and the response
     * status should be 200.
     * <p>
     * If the update of the message is not successful for any reason, the response status should be 400. (Client error)
     *
     * @param messageId integer messageId determined by the {messageId} @PathVariable
     * @param message   message object marshalled from @RequestBody
     * @return either a ResponseEntity with an OK status and a body consisting of the number of rows affected by this
     * request or a ResponseEntity with a BAD_REQUEST status and no body.
     * @see ValidationException
     */
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessageById(@PathVariable int messageId, @RequestBody Message message) {
        Integer rowsUpdated = messageService.updateMessageById(messageId, message);
        return ResponseEntity.status(HttpStatus.OK).body(rowsUpdated);
    }

    /**
     * As a user, I should be able to submit a GET request on the endpoint GET /accounts/{accountId}/messages.
     * <p>
     * The response body should contain a JSON representation of a list containing all messages posted by a particular
     * user, which is retrieved from the database.
     * <p>
     * It is expected for the list to simply be empty if there are no messages.
     * <p>
     * The response status should always be 200, which is the default.
     *
     * @param accountId integer accountId determined by the {accountId} @PathVariable
     * @return a list of all the messages for a given account or an empty list if no such account exists
     */
    @GetMapping("/accounts/{accountId}/messages")
    public List<Message> getAccountMessages(@PathVariable int accountId) {
        return messageService.getAccountMessages(accountId);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> handleConflictException() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(UnauthorizedValidationException.class)
    public ResponseEntity<String> handleUnauthorizedValidationException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .build();
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
