package com.hsu.pyeoning.global.exception;

import com.hsu.pyeoning.global.response.CustomApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);

    // @Valid 유효성 검사 실패 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        // 로그 추가
        logger.error("MethodArgumentNotValidException 발생: {}", errorMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.createFailWithout(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    // 메서드 파라미터가 직접 제약 조건을 위반할 시
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomApiResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        // 로그 추가
        logger.error("ConstraintViolationException 발생: {}", errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.createFailWithout(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    // Unauthorized 예외의 경우
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CustomApiResponse<?>> handleUnauthorizedException(UnauthorizedException e) {
        logger.warn("UnauthorizedException 발생: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CustomApiResponse.createFailWithout(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    // 그 외의 모든 예외의 경우
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomApiResponse<?>> handleException(Exception exception) {
        String errorMessage = "오류가 발생하였습니다. " +
                "1. 토큰을 삽입했는지 확인해 주세요. " +
                "2. 토큰의 유효기간을 확인해 주세요 (새로 발급하여 시도해보세요). " +
                "문제가 해결되지 않는다면, 관리자에게 문의해 주세요.";

        // 전체 예외 로그 출력
        logger.error("Exception 발생: {}", exception.getMessage(), exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CustomApiResponse.createFailWithout(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage));
    }
}
