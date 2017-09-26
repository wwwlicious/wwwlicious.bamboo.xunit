package com.wwwlicious.xunit.impl;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.resultsummary.tests.TestCaseResultErrorImpl;
import com.atlassian.bamboo.resultsummary.tests.TestState;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class xUnitParser {

    private final BuildLogger log;
    private final File file;

    public xUnitParser(BuildLogger logger, File file) {
        log = logger;
        this.file = file;
    }

    private List<TestResults> successfulTests = Lists.newArrayList();
    private List<TestResults> failingTests = Lists.newArrayList();
    private List<TestResults> skippedTests = Lists.newArrayList();

    public void parse(@NotNull InputStream inputStream) throws Exception {

        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.IS_COALESCING, true);
        XMLEventReader xmlEventReader = factory.createXMLEventReader(inputStream);

        try {
            TestResults testCase = new TestResults("Undefined", "Undefined", 0L);
            XunitTestCaseResult testError = new XunitTestCaseResult();

            while (xmlEventReader.hasNext()) {
                try {
                    XMLEvent xmlEvent = xmlEventReader.nextEvent();

                    // check for a root node of 'assemblies', otherwise skip file
                    if (xmlEvent.isStartDocument()) {
                        String rootNodeName = "";
                        if (xmlEvent.isStartElement()) {
                            rootNodeName = xmlEvent.asStartElement().getName().getLocalPart();
                        } else if (xmlEventReader.peek().isStartElement()) {
                            xmlEvent = xmlEventReader.nextEvent();
                            rootNodeName = xmlEvent.asStartElement().getName().getLocalPart();
                        }
                        if (!rootNodeName.equals("assemblies")) {
                            log.addBuildLogEntry("skipping non-xunit xml file:" + file.getAbsolutePath());
                            return;
                        }
                        xmlEvent = xmlEventReader.nextEvent();
                    }

                    // is an xunit result file.
                    if (xmlEvent.isStartElement()) {
                        StartElement startElement = xmlEvent.asStartElement();
                        if (startElement.getName().getLocalPart().equals("test")) {
                            // Get the test attributes from each test element
                            final String className = startElement.getAttributeByName(new QName("type")).getValue();
                            final String name = startElement.getAttributeByName(new QName("name")).getValue();
                            final String methodName = name.length() > className.length() ? name.substring(className.length() + 1) : startElement.getAttributeByName(new QName("method")).getValue();
                            final String duration = startElement.getAttributeByName(new QName("time")).getValue();
                            final String status = startElement.getAttributeByName(new QName("result")).getValue().toLowerCase();
                            final long durationInMilliseconds = (long) (Double.parseDouble(duration) * 1000);
                            testCase = new TestResults(className, methodName, durationInMilliseconds);
                            switch (status) {
                                case "fail":
                                    testCase.setState(TestState.FAILED);
                                    break;
                                case "pass":
                                    testCase.setState(TestState.SUCCESS);
                                    break;
                                case "skip":
                                    testCase.setState(TestState.SKIPPED);
                                    break;
                            }
                        }
                        // set the other variables from xml elements
                        else if (startElement.getName().getLocalPart().equals("failure")) {
                            testError.setExceptionType(startElement.getAttributeByName(new QName("exception-type")).getValue());
                        } else if (startElement.getName().getLocalPart().equals("message")) {
                            xmlEvent = xmlEventReader.nextEvent();
                            testError.setMessage(xmlEvent.asCharacters().getData());
                        } else if (startElement.getName().getLocalPart().equals("stack-trace")) {
                            xmlEvent = xmlEventReader.nextEvent();
                            testError.setStackTrace(xmlEvent.asCharacters().getData());
                        } else if (startElement.getName().getLocalPart().equals("reason")) {
                            // v1 has message child which gets picked up above
                            // v2 has reason element text
                            if (xmlEventReader.peek().isCharacters()) {
                                xmlEvent = xmlEventReader.nextEvent();
                                testError.setMessage(xmlEvent.asCharacters().getData());
                            }
                        }
                    }

                    // if test end element is reached, add test object to correct list
                    if (xmlEvent.isEndElement()) {
                        EndElement endElement = xmlEvent.asEndElement();
                        if (endElement.getName().getLocalPart().equals("test")) {
                            switch (testCase.getState()) {
                                case FAILED:
                                    testCase.addError(new TestCaseResultErrorImpl(testError.getResult()));
                                    failingTests.add(testCase);
                                    break;
                                case SKIPPED:
                                    testCase.addError(new TestCaseResultErrorImpl(testError.getMessage()));
                                    //testCase.setSystemOut(testError.getMessage());
                                    skippedTests.add(testCase);
                                    break;
                                case SUCCESS:
                                    successfulTests.add(testCase);
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.addErrorLogEntry(e.toString());
                }
            }
        } finally {
            xmlEventReader.close();
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