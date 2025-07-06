package com.hsu.pyeoning.global.controller;

import com.hsu.pyeoning.global.response.CustomApiResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<CustomApiResponse<?>> handleError(HttpServletRequest request) {
        // RequestDispatcher? : 클라이언트로부터 요청을 받고 이를 다른 리소스(서블릿, html, jsp)로 넘겨주는 역할을 하는 인터페이스
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if(statusCode == 400) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new CustomApiResponse<>(HttpStatus.BAD_REQUEST.value(), null, "잘못된 요청입니다,"));
            }
            else if(statusCode == 403) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new CustomApiResponse<>(HttpStatus.FORBIDDEN.value(), null, "접근이 금지되었습니다."));
            }
            else if(statusCode == 404) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new CustomApiResponse<>(HttpStatus.NOT_FOUND.value(), null, "요청 경로를 찾을 수 없습니다."));
            }
            else if(statusCode == 405) {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                        .body(new CustomApiResponse<>(HttpStatus.METHOD_NOT_ALLOWED.value(), null, "허용되지 않는 메소드입니다."));
            }
            else if(statusCode == 500) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new CustomApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "서버 에러가 발생하였습니다."));
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "알 수 없는 에러"));
    }
}

