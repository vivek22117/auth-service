package com.dd.auth.api.cognito;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String username;
    private String userStatus;
    private Date userCreateDate;
    private Date lastModifiedDate;
    private String userScope;
    private String email;
}
