package com.dd.auth.api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException {
        ApiResponse response = new ApiResponse(401, "Unauthorised");
        response.setMessage("Unauthorised");
        OutputStream out = httpServletResponse.getOutputStream();
        objectMapper.writeValue(out, response);
        out.flush();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse {

        private int status;
        private String message;
        private Object result;

        public ApiResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
