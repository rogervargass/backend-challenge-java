package com.picpaysimplified.services;

import com.picpaysimplified.domain.user.User;
import com.picpaysimplified.domain.user.UserType;
import com.picpaysimplified.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

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

    public User findUserById(Long userId) throws Exception {
        return this.repository.findUserById(userId).orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public void saveUser(User user) {
        this.repository.save(user);
    }
}
