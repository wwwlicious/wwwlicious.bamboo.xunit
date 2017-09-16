package com.wwwlicious.xunit.impl;

public class XunitTestCaseResult {

    private String exceptionType;
    private String message;
    private String stackTrace;

    public String getResult() {
        return String.join(System.lineSeparator(), exceptionType, message, stackTrace);
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
