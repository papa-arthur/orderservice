package com.alpha.orderservice.exception;

import graphql.GraphQLError;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    @GraphQlExceptionHandler({UserNotFoundException.class, ProductNotFoundException.class})
    public GraphQLError handle(RuntimeException exception) {
        return GraphQLError.newError().errorType(ErrorType.NOT_FOUND)
                .message(exception.getMessage()).build();
    }

}
