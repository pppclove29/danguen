package com.example.danguen.domain.comment.entity;

public class AlreadyDeletedCommentException extends RuntimeException{
    public static final String message = "Article is already deleted";
}
