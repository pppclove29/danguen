package com.example.danguen.domain.user.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public static final String message = "User not found";
}
