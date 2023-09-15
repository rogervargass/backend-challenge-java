package com.picpaysimplified.dtos;

import com.picpaysimplified.domain.user.UserType;

import java.math.BigDecimal;

public record UserDTO(String firstName, String lastName, BigDecimal balance, String document, String email, String password, UserType userType) {
}
