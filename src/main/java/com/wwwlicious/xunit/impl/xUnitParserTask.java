package com.wwwlicious.xunit.impl;

import com.atlassian.bamboo.build.logger.interceptors.ErrorMemorisingInterceptor;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.v2.build.CurrentResult;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Scanned
public class xUnitParserTask implements TaskType {

    static final String TEST_FILE_PATTERN = "xUnitResults.FilePattern";
    static final String COLLECT_OUTDATED_FILES = "xUnitResults.PickupOutdatedFiles";
    private static final Logger log = Logger.getLogger(xUnitParserTask.class);
    @ComponentImport
    private final TestCollationService testCollationService;

    public xUnitParserTask(TestCollationService testCollationService) {
        this.testCollationService = testCollationService;
    }

    @NotNull
    @Override
    public TaskResult execute(@NotNull TaskContext taskContext) throws TaskException {
        CurrentResult currentResult = taskContext.getCommonContext().getCurrentResult();
        ErrorMemorisingInterceptor errorLines = ErrorMemorisingInterceptor.newInterceptor();
        taskContext.getBuildLogger().getInterceptorStack().add(errorLines);

        try {
            this.testCollationService.collateTestResults(taskContext,
                    taskContext.getConfigurationMap().get(TEST_FILE_PATTERN),
                    new xUnitTestReportCollector(),
                    taskContext.getConfigurationMap().getAsBoolean(COLLECT_OUTDATED_FILES));
            return TaskResultBuilder.newBuilder(taskContext).checkTestFailures().build();
        } catch (Exception e) {
            throw new TaskException("Failed to execute task: " + e.getLocalizedMessage());
        } finally {
            currentResult.addBuildErrors(errorLines.getErrorStringList());
        }
    }
}
