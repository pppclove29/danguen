package com.example.danguen.domain.comment.exception;

public class AlreadyDeletedCommentException extends RuntimeException{
    private static final long serialVersionUID = 1L;
	public static final String message = "Article is already deleted";
}
