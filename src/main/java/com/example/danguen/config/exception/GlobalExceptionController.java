package com.example.danguen.config.exception;

import com.example.danguen.domain.image.exception.ArticleNotFoundException;
import com.example.danguen.domain.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(MissingSessionPrincipalDetailsException.class)
    public ResponseEntity<?> handleSessionValueNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(MissingSessionPrincipalDetailsException.message);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(UserNotFoundException.message);
    }
    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<?> handleArticleNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ArticleNotFoundException.message);
    }
}
