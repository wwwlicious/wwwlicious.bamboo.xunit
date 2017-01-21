package com.wwwlicious.xunit.impl;

import org.w3c.dom.Element;

/**
 * v1 and v2 (has cdata tags)
 * <test>
 * <failure exception-type="">
 * <message></message>
 * <stack-trace></stack-trace>
 * </failure>
 * </test>
 */
public class xunitError {
    private static final String UNKNOWN_ERROR_MISSING_FAILURE_NODE = "Unknown error, missing Failure node in results";
    private String errorMessage = "Unknown error";
    private String errorStackTrace = "";

    /**
     * @param test an xunit test xml element
     */
    public xunitError(Element test) {
        if (test != null && test.hasChildNodes()) {
            Element failure = (Element) test.getElementsByTagName("failure").item(0);
            errorMessage = failure.getElementsByTagName("message").item(0).getTextContent();
            errorStackTrace = failure.getElementsByTagName("stack-trace").item(0).getTextContent();
        } else {
            errorMessage = UNKNOWN_ERROR_MISSING_FAILURE_NODE;
        }
    }

    @Override
    public String toString() {
        return "message:\t" + errorMessage + "\r\n" + "stacktrace:\t" + errorStackTrace;
    }
}
