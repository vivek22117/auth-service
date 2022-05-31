package com.dd.auth.api.cognito;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private String username;
    private String password;
    private List<UserAttributes> userAttributes = new ArrayList<>();
    private List<CustomAttributes> customAttributes = new ArrayList<>();
}
