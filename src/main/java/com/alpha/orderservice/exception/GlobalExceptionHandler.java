package com.alpha.orderservice.exception;

import graphql.GraphQLError;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    @GraphQlExceptionHandler({UserNotFoundException.class, ProductNotFoundException.class, OrderNotFoundException.class})
    public GraphQLError handleNotFoundExceptions(RuntimeException exception) {
        return GraphQLError.newError().errorType(ErrorType.NOT_FOUND)
                .message(exception.getMessage()).build();
    }
    @GraphQlExceptionHandler({InsufficientStockException.class, UserExistsException.class ,IllegalArgumentException.class})
    public GraphQLError handleBadRequest(RuntimeException exception) {
        return GraphQLError.newError().errorType(ErrorType.BAD_REQUEST)
                .message(exception.getMessage()).build();
    }
    @GraphQlExceptionHandler(AuthenticationException.class)
    public GraphQLError handleEntityExistsException (AuthenticationException exception) {
        return GraphQLError.newError().errorType(ErrorType.UNAUTHORIZED)
                .message(exception.getMessage()).build();
    }

}
