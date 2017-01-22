package com.wwwlicious.xunit.impl;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.task.*;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.jetbrains.annotations.NotNull;

@Scanned
public class xunitParserTask implements TaskType {
    public static final java.lang.String TEST_FILE_PATTERN = "xUnitResultsFilePattern";

    @ComponentImport
    private final TestCollationService testCollationService;

    public xunitParserTask(@NotNull final TestCollationService testCollationService) {
        this.testCollationService = testCollationService;
    }

    @NotNull
    public TaskResult execute(@NotNull TaskContext taskContext) throws TaskException {
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        TaskResultBuilder taskResultBuilder = TaskResultBuilder.newBuilder(taskContext);

        final String testFilePattern = taskContext.getConfigurationMap().get(TEST_FILE_PATTERN);

        buildLogger.addBuildLogEntry("Parsing xunit test results");
        testCollationService.collateTestResults(taskContext, testFilePattern, new xUnitTestReportCollector());
        buildLogger.addBuildLogEntry("Finished parsing xunit test results");

        return taskResultBuilder.checkTestFailures().build();
    }
}
