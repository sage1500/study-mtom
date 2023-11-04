package com.example.rest.server;

import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.api.rest.server.model.ErrorResource;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	// queryパラメータで入力チェックに引っかかったときの対応
	@ExceptionHandler(ConstraintViolationException.class)
	@Nullable
	public ResponseEntity<Object> handleConstraintViolationException(Exception ex, WebRequest request) throws Exception {
		return handleExceptionInternal(ex, ex.getMessage(), null, HttpStatus.BAD_REQUEST, request);
	}

	// エラー時の応答形式をAPIで決めたものにするための実装
	@Override
	protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers, HttpStatusCode statusCode,
			WebRequest request) {
		log.info("ERROR: {}", body);
		
		String msg;
		if (body instanceof ProblemDetail detail) {
			msg = detail.getDetail();
		} else {
			msg = Objects.toString(body);
		}
		
		return new ResponseEntity<>(new ErrorResource(900, msg), headers, statusCode);
	}

	// 何の例外が発生したかの確認用
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {
		log.info("handleExceptionInternal: ex={}", ex.getClass().getSimpleName());
		return super.handleExceptionInternal(ex, body, headers, statusCode, request);
	}
	
	
}
