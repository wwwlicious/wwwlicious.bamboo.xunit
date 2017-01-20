package com.wwwlicious.xunit.impl;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.task.*;
import org.jetbrains.annotations.NotNull;

public class xunitParserTask implements TaskType {
    private final TestCollationService testCollationService;

    public xunitParserTask(TestCollationService testCollationService) {
        this.testCollationService = testCollationService;
    }

    @NotNull
    public TaskResult execute(@NotNull TaskContext taskContext) throws TaskException {
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        TaskResultBuilder taskResultBuilder = TaskResultBuilder.newBuilder(taskContext);

        final String testFilePattern = taskContext.getConfigurationMap().get("testPattern");

        buildLogger.addBuildLogEntry("Parsing xunit test results");
        testCollationService.collateTestResults(taskContext, testFilePattern, new xUnitTestReportCollector());
        buildLogger.addBuildLogEntry("Finished Parsing xunit test results");

        return taskResultBuilder.checkTestFailures().build();
    }
}
