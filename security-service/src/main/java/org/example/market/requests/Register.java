package org.example.market.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class Register {
    @Getter @Setter
    @NotBlank
    private String username;

    @NotBlank
    @Getter @Setter
    private String password;



}
