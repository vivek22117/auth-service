package com.dd.auth.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String name;
    private String mobile;
    private String address;
    private String email;
    private String username;
    private String password;
}
