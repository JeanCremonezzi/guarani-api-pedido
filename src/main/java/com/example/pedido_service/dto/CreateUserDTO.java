package com.example.pedido_service.dto;

import com.example.pedido_service.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Set<UserRole> roles;
}
