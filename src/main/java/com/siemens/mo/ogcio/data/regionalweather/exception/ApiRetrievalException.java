package com.siemens.mo.ogcio.data.regionalweather.exception;


import org.springframework.http.HttpStatus;

public class ApiRetrievalException extends Exception {

    private HttpStatus httpStatus;

    public ApiRetrievalException(HttpStatus httpStatus) {
        super( "HttpStatus: " + httpStatus);
        this.httpStatus = httpStatus;
    }
}
