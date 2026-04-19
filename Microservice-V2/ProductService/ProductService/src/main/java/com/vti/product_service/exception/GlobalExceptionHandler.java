package com.vti.product_service.exception;

import com.vti.product_service.common.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var listErrors = e.getBindingResult().getFieldErrors();
        List<String> message = new ArrayList<>();

        for (var error : listErrors) {
            message.add(error.getField() + ": " + error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(null, String.join(", ", message)));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<BaseResponse<Object>> handleApplicationException(ApplicationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(null, e.getMessage()));
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<BaseResponse<Object>> handleUnAuthorizedException(UnAuthorizedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new BaseResponse<>(null, e.getMessage() != null ? e.getMessage() : "Access Denied"));
    }

    @ExceptionHandler(UnAuthenException.class)
    public ResponseEntity<BaseResponse<Object>> handleUnAuthenException(UnAuthenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new BaseResponse<>(null, e.getMessage() != null ? e.getMessage() : "Unauthorized"));
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<BaseResponse<Object>> handleGeneralException(Exception e) {
        // Log lỗi tại đây để debug: e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(null, "Xảy ra lỗi, vui lòng thử lại" + e.getMessage()));
    }
}
