package com.wwwlicious.xunit.impl;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.atlassian.bamboo.build.test.TestCollectionResultBuilder;
import com.atlassian.bamboo.build.test.TestReportCollector;
import com.google.common.collect.Sets;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

@ThreadSafe
public class xUnitTestReportCollector implements TestReportCollector {

    private static BuildLogger log;

    public xUnitTestReportCollector(@NotNull BuildLogger logger) {
        log = logger;
    }

    @NotNull
    @Override
    public TestCollectionResult collect(@NotNull File file) throws Exception {
        log.addBuildLogEntry(String.format("File %s was passed to xUnit test report collector", file));
        FileInputStream stream = new FileInputStream(file);

        try {
            xUnitParser parser = new xUnitParser(log, file);
            parser.parse(stream);

            return new TestCollectionResultBuilder()
                    .addFailedTestResults(parser.getFailedTests())
                    .addSkippedTestResults(parser.getSkippedTests())
                    .addSuccessfulTestResults(parser.getSuccessfulTests())
                    .build();
        } catch (Exception e) {
            log.addErrorLogEntry("Error collecting test results: " + e.toString());
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return new TestCollectionResultBuilder().build();
    }

    @NotNull
    @Override
    public Set<String> getSupportedFileExtensions() {
        return Sets.newHashSet("xml");
    }
}