package com.wwwlicious.xunit.impl;

import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.resultsummary.tests.TestCaseResultErrorImpl;
import com.atlassian.bamboo.resultsummary.tests.TestState;
import com.google.common.collect.Lists;
import net.jodah.xsylum.XmlDocument;
import net.jodah.xsylum.XmlElement;
import net.jodah.xsylum.Xsylum;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;

/**
 * all credit to for this - https://bitbucket.org/arasureynn/bamboo-xunit-parser
 * better xml parsing and nicer decoupling
 */
public class xUnitParser {
    private static final Logger log = Logger.getLogger(xUnitParser.class);

    private List<TestResults> successfulTests = Lists.newArrayList();
    private List<TestResults> failingTests = Lists.newArrayList();
    private List<TestResults> skippedTests = Lists.newArrayList();

    public void parse(@NotNull InputStream inputStream) throws Exception {

        XmlDocument doc = Xsylum.documentFor(inputStream);

        List<XmlElement> nList = doc.getAll("test");

        for (XmlElement element : nList) {
            String className = element.attribute("type");
            String methodName = element.attribute("method");
            String duration = element.attribute("time");
            String status = element.attribute("result").toLowerCase();

            long durationInMilliseconds = (long) (Double.parseDouble(duration) * 1000);
            TestResults results = new TestResults(className, methodName, durationInMilliseconds);
            switch (status) {
                case "fail":
                    results.setState(TestState.FAILED);
                    XmlElement failureReport = element.get("failure");
                    if (failureReport != null) {
                        results.addError(new TestCaseResultErrorImpl(failureReport.value()));
                    }
                    failingTests.add(results);
                    break;
                case "pass":
                    results.setState(TestState.SUCCESS);
                    successfulTests.add(results);
                    break;
                case "skip":
                    results.setState(TestState.SKIPPED);
                    XmlElement skipReport = element.get("reason");
                    results.addError(new TestCaseResultErrorImpl(skipReport.value()));
                    skippedTests.add(results);
                    break;
            }
        }
    }

    @NotNull
    public List<TestResults> getSuccessfulTests() {
        return this.successfulTests;
    }

    @NotNull
    public List<TestResults> getFailedTests() {
        return this.failingTests;
    }

    @NotNull
    public List<TestResults> getSkippedTests() {
        return this.skippedTests;
    }
}
