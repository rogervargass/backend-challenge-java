package com.picpaysimplified.services;

import com.picpaysimplified.domain.user.User;
import com.picpaysimplified.domain.user.UserType;
import com.picpaysimplified.dtos.UserDTO;
import com.picpaysimplified.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public User createUser(UserDTO request) {
        User newUser = new User(request);
        this.saveUser(newUser);
        return newUser;
    }

    private void validateSenderTransaction(User sender) throws Exception {
        if(sender.getUserType() == UserType.MERCHANT) {
            throw new Exception("Usuário do tipo Lojista não está autorizado a realizar transação");
        }
    }

    private void validateSenderAmount(User sender, BigDecimal amount) throws Exception {
        if(sender.getBalance().compareTo(amount) < 0) {
            throw new Exception("Saldo insuficiente");
        }
    }

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        validateSenderTransaction(sender);
        validateSenderAmount(sender, amount);
    }

    public void updateBalance(Long senderId, Long receiverId, BigDecimal amount) throws Exception {
        User sender = this.findUserById(senderId);
        User receiver = this.findUserById(receiverId);

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        this.saveUser(sender);
        this.saveUser(receiver);
    }

    public List<User> getAllUsers() {
        return this.repository.findAll();
    }

    public User findUserById(Long userId) throws Exception {
        return this.repository.findUserById(userId).orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public void saveUser(User user) {
        this.repository.save(user);
    }
}
