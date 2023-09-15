package com.picpaysimplified.services;

import com.picpaysimplified.domain.transaction.Transaction;
import com.picpaysimplified.domain.user.User;
import com.picpaysimplified.dtos.TransactionDTO;
import com.picpaysimplified.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private RestTemplate restTemplate;

    public void createTransaction(TransactionDTO transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        this.userService.validateTransaction(sender, transaction.value());

        boolean isAuthorized = this.isAuthorizedTransaction(sender, transaction.value());
        if(!isAuthorized) {
            throw new Exception("Transação não autorizada");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setAmount(transaction.value());
        newTransaction.setTimestamp(LocalDateTime.now());

        this.userService.updateBalance(sender.getId(), receiver.getId(), newTransaction.getAmount());

        this.repository.save(newTransaction);
    }

    public boolean isAuthorizedTransaction(User sender, BigDecimal value) throws Exception {
        ResponseEntity<Map> authorizationResponse = this.restTemplate.getForEntity("https://run.mocky.io/v3/8fafdd68-a090-496f-8c9a-3442cf30dae6", Map.class);

        if(authorizationResponse.getStatusCode() == HttpStatus.OK) {
            String message = (String) authorizationResponse.getBody().get("message");
            return "Autorizado".equals(message);
        } else return false;
    }
}
