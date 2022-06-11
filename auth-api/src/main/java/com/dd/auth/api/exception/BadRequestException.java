package com.dd.auth.api.exception;

import com.sun.corba.se.spi.ior.ObjectId;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

}
