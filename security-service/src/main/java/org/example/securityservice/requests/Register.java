package org.example.securityservice.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class Register {
    @Getter @Setter
    private String username;


    @Getter @Setter
    private String password;



}
