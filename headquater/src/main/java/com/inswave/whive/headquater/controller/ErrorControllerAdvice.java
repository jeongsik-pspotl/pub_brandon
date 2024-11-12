package com.inswave.whive.headquater.controller;

import com.inswave.whive.common.error.WHiveBaseException;
import com.inswave.whive.common.error.WHiveConflictException;
import com.inswave.whive.common.error.WHiveInvliadRequestException;
import com.inswave.whive.common.error.WHiveNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ErrorControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WHiveInvliadRequestException.class)
    @ResponseBody String handleBadRequest(WHiveInvliadRequestException ex) {
        log.debug("ErrorControllerAdvice.handleBadRequest()", ex);
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(WHiveConflictException.class)
    @ResponseBody String handleConflict(WHiveConflictException ex) {
        log.debug("ErrorControllerAdvice.handleConflict()", ex);
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(WHiveNotFoundException.class)
    @ResponseBody String handleNotFound(WHiveNotFoundException ex) {
        log.debug("ErrorControllerAdvice.handleNotFound()", ex);
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(WHiveBaseException.class)
    @ResponseBody String handleInternalServerError(Exception ex) {
        log.debug("ErrorControllerAdvice.handleInternalServerError()", ex);
        return ex.getMessage();
    }
}