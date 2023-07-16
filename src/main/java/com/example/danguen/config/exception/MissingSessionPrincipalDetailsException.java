package com.example.danguen.config.exception;

public class MissingSessionPrincipalDetailsException extends RuntimeException{
    private static final long serialVersionUID = 1L;
	public static final String message = "You are not in session";
}
