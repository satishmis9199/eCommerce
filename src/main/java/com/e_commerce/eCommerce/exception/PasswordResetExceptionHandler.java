package com.e_commerce.eCommerce.exception;


import com.e_commerce.eCommerce.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;


@RestControllerAdvice
public class PasswordResetExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleExpired(TokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body(new ApiResponse<>(false, ex.getMessage()));
    }

    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsed(TokenAlreadyUsedException ex) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body(new ApiResponse<>(false, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage()));
    }
    @ExceptionHandler(vendorNotFoundException.class)

    public ResponseEntity<ApiResponse<Void>> handleNotFound(vendorNotFoundException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage()));
    }
    @ExceptionHandler(ProductAlreadyExist.class)
    public ResponseEntity<ApiResponse<?>> handleAlreadyProductInBrand(ProductAlreadyExist exist){
        exist.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ApiResponse<>(
                                false,
                                exist.getMessage()
                        )
                );
    }
    @ExceptionHandler(BrandDoesNotExist.class)
    public ResponseEntity<ApiResponse<?>> brandALreadyExist(BrandDoesNotExist exist){
        exist.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new ApiResponse<>(
                                false,
                                exist.getMessage()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(
                        false,
                        "Something went wrong. Please try again later."
                ));
    }
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> orderNotFound(OrderNotFoundException exist){
        exist.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ApiResponse<>(
                                false,
                                exist.getMessage()
                        )
                );
    }
    @ExceptionHandler(TenantNoFoundException.class)
    public ResponseEntity<ApiResponse<?>> orderNotFound(TenantNoFoundException exist){
        exist.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ApiResponse<>(
                                false,
                                exist.getMessage()
                        )
                );
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> productNotFound(ProductNotFoundException exist){
        exist.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ApiResponse<>(
                                false,
                                exist.getMessage()
                        )
                );
    }
    @ExceptionHandler(InvaidPriceException.class)
    public ResponseEntity<ApiResponse<?>> invalidPrice(InvaidPriceException exist){
        exist.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ApiResponse<>(
                                false,
                                exist.getMessage()
                        )
                );
    }
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> categoryNotFond(CategoryNotFoundException exist){
        exist.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ApiResponse<>(
                                false,
                                exist.getMessage()
                        )
                );
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> Unautorized(UnauthorizedException exist){
        exist.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ApiResponse<>(
                                false,
                                exist.getMessage()
                        )
                );
    }
    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ApiResponse<?>> invalidState(InvalidStateException exist){
        exist.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ApiResponse<>(
                                false,
                                exist.getMessage()
                        )
                );
    }






}
