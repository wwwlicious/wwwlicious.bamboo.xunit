package com.wwwlicious.xunit.impl;

import org.w3c.dom.Node;

/**
 * v1 and v2 (has cdata tags)
 * <failure exception-type="">
 * <message></message>
 * <stack-trace></stack-trace>
 * </failure>
 */
public class xunitError {
    private static final String UNKNOWN_ERROR_MISSING_FAILURE_NODE = "Unknown error, missing Failure node in results";
    private String errorMessage = "Unknown error";
    private String errorStackTrace = "";

    xunitError(Node test) {
        if (test != null && test.hasChildNodes()) {
            Node failure = test.getFirstChild();
            if (failure.getNodeName().equalsIgnoreCase("Failure")) {
                errorMessage = failure.getFirstChild().getNodeValue();
                errorStackTrace = failure.getLastChild().getNodeValue();
            } else {
                errorMessage = UNKNOWN_ERROR_MISSING_FAILURE_NODE;
            }
        } else {
            errorMessage = UNKNOWN_ERROR_MISSING_FAILURE_NODE;
        }
    }

    @Override
    public String toString() {
        return "message:/t" + errorMessage + "/r/n" + "stacktrace:/t" + errorStackTrace;
    }
}
