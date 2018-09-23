package com.test.friendmanagement.controller;

import com.test.friendmanagement.dto.FriendResponse;
import com.test.friendmanagement.exception.RelationshipException;
import com.test.friendmanagement.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Locale;

@ControllerAdvice
public class ExceptionHandlerController {

    private MessageSource messageSource;

    @Autowired
    public ExceptionHandlerController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(UserException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FriendResponse userException(UserException userException) {
        return FriendResponse.builder().errorMessage(
                messageSource.getMessage(userException.getMessage(), null, Locale.getDefault()))
                .success(false)
                .build();
    }

    @ExceptionHandler(RelationshipException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FriendResponse relationshipException(RelationshipException relationshipException) {
        return FriendResponse.builder().errorMessage(
                messageSource.getMessage(relationshipException.getMessage(), null, Locale.getDefault()))
                .success(false)
                .build();
    }
}
