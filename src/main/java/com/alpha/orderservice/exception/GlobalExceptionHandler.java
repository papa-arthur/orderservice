package com.alpha.orderservice.exception;

import graphql.GraphQLError;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @GraphQlExceptionHandler(UserNotFoundException.class)
    public GraphQLError handle(UserNotFoundException ex) {
        return GraphQLError.newError().errorType(ErrorType.NOT_FOUND).message(ex.getMessage()).build();
    }

}
