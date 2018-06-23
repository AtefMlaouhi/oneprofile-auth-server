package com.newlight77.oauth2.server.exception;

import com.newlight77.exception.ExceptionDetail;
import com.newlight77.exception.ForbiddenException;
import com.newlight77.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice public class GlobalExceptionHandler {

  @ResponseBody @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, MissingServletRequestParameterException.class})
  public Object handle400Exception(HttpServletRequest request, Exception ex) {
    return ExceptionDetail.builder().classname(ex.getClass().getName()).date(Instant.now().toString()).message(ex.getLocalizedMessage())
        .path(request.getRequestURI()).params(request.getQueryString()).build();
  }

  @ResponseBody @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) @ExceptionHandler(Exception.class)
  public Object handle500Exception(HttpServletRequest request, Exception ex) {
    return ExceptionDetail.builder().classname(ex.getClass().getName()).date(Instant.now().toString()).message(ex.getLocalizedMessage())
        .path(request.getRequestURI()).params(request.getQueryString()).build();
  }

  @ResponseBody @ResponseStatus(HttpStatus.NOT_FOUND) @ExceptionHandler(ResourceAccessException.class)
  public Object handle404Exception(HttpServletRequest request, Exception ex) {
    return ExceptionDetail.builder().classname(ex.getClass().getName()).date(Instant.now().toString()).message(ex.getLocalizedMessage())
        .path(request.getRequestURI()).params(request.getQueryString()).build();
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED) @ExceptionHandler(UnauthorizedException.class) @ResponseBody
  public Object handle401Exception(HttpServletRequest request, Exception ex) {
    return ExceptionDetail.builder().classname(ex.getClass().getName()).date(Instant.now().toString()).message(ex.getLocalizedMessage())
        .path(request.getRequestURI()).params(request.getQueryString()).build();
  }

  @ResponseStatus(HttpStatus.FORBIDDEN) @ExceptionHandler(ForbiddenException.class) @ResponseBody
  public Object handle403Exception(HttpServletRequest request, Exception ex) {
    return ExceptionDetail.builder().classname(ex.getClass().getName()).date(Instant.now().toString()).message(ex.getLocalizedMessage())
        .path(request.getRequestURI()).params(request.getQueryString()).build();
  }

}
