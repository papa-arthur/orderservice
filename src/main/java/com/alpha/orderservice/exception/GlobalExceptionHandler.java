package com.alpha.orderservice.exception;

import graphql.GraphQLError;
import jakarta.persistence.EntityExistsException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
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
//    @GraphQlExceptionHandler(UserExistsException.class)
//    public GraphQLError handleEntityExistsException (UserExistsException exception) {
//        return GraphQLError.newError().errorType(ErrorType.BAD_REQUEST)
//                .message(exception.getMessage()).build();
//    }

}
