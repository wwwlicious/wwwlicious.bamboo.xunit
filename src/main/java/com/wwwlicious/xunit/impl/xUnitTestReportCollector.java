package com.wwwlicious.xunit.impl;

import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.atlassian.bamboo.build.test.TestCollectionResultBuilder;
import com.atlassian.bamboo.build.test.TestReportCollector;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.resultsummary.tests.TestCaseResultErrorImpl;
import com.atlassian.bamboo.resultsummary.tests.TestState;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Collection;
import java.util.Set;

public class xUnitTestReportCollector implements TestReportCollector {
    @NotNull
    public TestCollectionResult collect(@NotNull java.io.File file) throws Exception {
        TestCollectionResultBuilder builder = new TestCollectionResultBuilder();

        Collection<TestResults> successfulTestResults = Lists.newArrayList();
        Collection<TestResults> skippedTestResults = Lists.newArrayList();
        Collection<TestResults> failingTestResults = Lists.newArrayList();

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        Document doc = domFactory.newDocumentBuilder().parse(file);

        /*
        v1 <test name="Catalogue.Shop.Service.Tests.AlertNotification.AlertNotificationServiceTests.Ctor_Throws_IfRequestGeneratorNull" type="Catalogue.Shop.Service.Tests.AlertNotification.AlertNotificationServiceTests" method="Ctor_Throws_IfRequestGeneratorNull" result="Pass" time="0.0078892" />
        v2 <test name="Catalogue.Shop.Service.Tests.ProductServiceTests.CanInitializeProductService" type="Catalogue.Shop.Service.Tests.ProductServiceTests" method="CanInitializeProductService" time="0.029422" result="Pass" />
         */
        NodeList tests = doc.getElementsByTagName("test");
        for (int i = 0; i < tests.getLength(); i++) {
            Node test = tests.item(i);

            NamedNodeMap attributes = test.getAttributes();
            String suiteName = attributes.getNamedItem("type").getNodeValue();
            String testName = attributes.getNamedItem("name").getNodeValue();
            String durationString = attributes.getNamedItem("time").getNodeValue();
            String status = attributes.getNamedItem("result").getNodeValue();

            Long durationInMilliseconds = (Long.parseLong(durationString) * 1000);

            TestResults testResults = new TestResults(suiteName, testName, durationInMilliseconds);

            if ("Pass".equalsIgnoreCase(status)) {
                testResults.setState(TestState.SUCCESS);
                successfulTestResults.add(testResults);
            } else if ("Ignore".equalsIgnoreCase(status)) {
                testResults.setState(TestState.SKIPPED);
                skippedTestResults.add(testResults);
            } else if ("Fail".equalsIgnoreCase(status)) {
                testResults.setState(TestState.FAILED);
                xunitError xunitError = new xunitError(test);
                testResults.addError(new TestCaseResultErrorImpl(xunitError.toString()));
                failingTestResults.add(testResults);
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
        return Sets.newHashSet("result"); // this will collect all *.result files
    }
}