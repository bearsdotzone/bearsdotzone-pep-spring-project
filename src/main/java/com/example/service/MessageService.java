package com.example.service;

import com.example.entity.Message;
import com.example.exception.ValidationException;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final MessageRepository messageRepository;

    public MessageService(AccountRepository accountRepository, MessageRepository messageRepository) {
        this.accountRepository = accountRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * The creation of the message will be successful if and only if the messageText is not blank, is not over 255
     * characters, and postedBy refers to a real, existing user.
     *
     * @param message the message to be posted
     * @return the message with its messageId populated
     * @throws ValidationException occurs when the message text is invalid or the postedBy id is invalid
     */
    public Message createMessage(Message message) {
        if (!isValidMessage(message))
            throw new ValidationException();
        if (accountRepository.findById(message.getPostedBy()).isEmpty())
            throw new ValidationException();
        return messageRepository.save(message);
    }

    public List<Message> getMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(int messageId) {
        return messageRepository.findById(messageId);
    }

    /**
     * If the message existed, the response should contain the number of rows updated (1).
     * <p>
     * If the message did not exist, the response should be empty.
     *
     * @param messageId the message to be deleted
     * @return the number of rows updated or empty if no rows were updated
     */
    public Optional<Integer> deleteMessageById(int messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return Optional.of(1);
        }
        return Optional.empty();
    }

    /**
     * The update of a message should be successful if and only if the message id already exists and the new messageText
     * is not blank and is not over 255 characters.
     *
     * @param messageId the message to be updated
     * @param message   the updated contents of the message
     * @return the number of rows updated or empty is no rows were updated
     * @throws ValidationException occurs if the messageId does not exist of the message text is invalid
     */
    public Integer updateMessageById(int messageId, Message message) {
        if (isValidMessage(message) && messageRepository.existsById(messageId)) {
            Message oldMessage = messageRepository.getById(messageId);
            oldMessage.setMessageText(message.getMessageText());
            messageRepository.save(oldMessage);
            return 1;
        }
        throw new ValidationException();
    }

    public List<Message> getAccountMessages(int accountId) {
        return messageRepository.findAllByPostedBy(accountId);
    }

    // "if the messageText is not blank and is not over 255 characters"
    private static boolean isValidMessage(Message message) {
        return !message.getMessageText().isEmpty() && message.getMessageText().length() < 256;
    }
}
