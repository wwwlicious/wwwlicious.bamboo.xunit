package com.wwwlicious.xunit.impl;

import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.atlassian.bamboo.build.test.TestCollectionResultBuilder;
import com.atlassian.bamboo.build.test.TestReportCollector;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.resultsummary.tests.TestCaseResultErrorImpl;
import com.atlassian.bamboo.resultsummary.tests.TestState;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.math.DoubleMath;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Set;

public class xUnitTestReportCollector implements TestReportCollector {
    @NotNull
    public TestCollectionResult collect(@NotNull java.io.File file) throws Exception {
        final TestCollectionResultBuilder builder = new TestCollectionResultBuilder();
        final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        final Document doc = domFactory.newDocumentBuilder().parse(file);

        Collection<TestResults> successfulTestResults = Lists.newArrayList();
        Collection<TestResults> skippedTestResults = Lists.newArrayList();
        Collection<TestResults> failingTestResults = Lists.newArrayList();


        /*
        v1 <test name="TestClass.TestMethod" type="TestClass" method="TestMethod" result="Pass" time="0.0078892" />
        v2 <test name="TestClass.TestMethod" type="TestClass" method="TestMethod" time="0.029422" result="Pass" />
         */
        NodeList tests = doc.getElementsByTagName("test");
        for (int i = 0; i < tests.getLength(); i++) {
            try {
                final Element test = (Element) tests.item(i);
                TestResults testResult = getTestResult(test);
                switch (testResult.getState()) {
                    case FAILED:
                        failingTestResults.add(testResult);
                        break;
                    case SKIPPED:
                        skippedTestResults.add(testResult);
                        break;
                    case SUCCESS:
                        successfulTestResults.add(testResult);
                        break;
                }
            } catch (Exception e) {
                // log exception
            }
        }

        return builder
                .addSuccessfulTestResults(successfulTestResults)
                .addFailedTestResults(failingTestResults)
                .addSkippedTestResults(skippedTestResults)
                .build();
    }

    @NotNull
    public Set<String> getSupportedFileExtensions() {
        return Sets.newHashSet("xml"); // this will collect all *.result files
    }

    private TestResults getTestResult(Element testNode) {
        NamedNodeMap attributes = testNode.getAttributes();
        String suiteName = attributes.getNamedItem("type").getNodeValue();
        String testName = attributes.getNamedItem("name").getNodeValue();
        String durationString = attributes.getNamedItem("time").getNodeValue();
        String status = attributes.getNamedItem("result").getNodeValue();

        long durationInMilliseconds = DoubleMath.roundToLong((Double.parseDouble(durationString) * 1000), RoundingMode.DOWN);

        TestResults testResults = new TestResults(suiteName, testName, durationInMilliseconds);

        if ("Pass".equalsIgnoreCase(status)) {
            testResults.setState(TestState.SUCCESS);
        } else if ("Skip".equalsIgnoreCase(status)) {
            testResults.setState(TestState.SKIPPED);
        } else if ("Fail".equalsIgnoreCase(status)) {
            testResults.setState(TestState.FAILED);
            xunitError xunitError = new xunitError(testNode);
            testResults.addError(new TestCaseResultErrorImpl(xunitError.toString()));
        }

        return testResults;
    }
}