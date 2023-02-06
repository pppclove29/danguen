package com.example.danguen.config.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException{

    public static final String message = "User not found";
}
