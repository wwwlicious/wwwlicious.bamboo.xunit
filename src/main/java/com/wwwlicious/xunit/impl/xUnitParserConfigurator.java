package com.wwwlicious.xunit.impl;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.task.TaskTestResultsSupport;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class xUnitParserConfigurator extends AbstractTaskConfigurator implements TaskTestResultsSupport {

    public boolean taskProducesTestResults(@NotNull TaskDefinition taskDefinition) {
        return true;
    }

    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        config.put(xunitParserTask.TEST_FILE_PATTERN, params.getString(xunitParserTask.TEST_FILE_PATTERN));
        return config;
    }

    public void populateContextForEdit(@NotNull Map<String, Object> map, @NotNull TaskDefinition taskDefinition) {
        super.populateContextForEdit(map, taskDefinition);
        map.put(xunitParserTask.TEST_FILE_PATTERN, taskDefinition.getConfiguration().get(xunitParserTask.TEST_FILE_PATTERN));
    }

    public void populateContextForCreate(@NotNull Map<String, Object> map) {
        super.populateContextForCreate(map);
        map.put(xunitParserTask.TEST_FILE_PATTERN, "artifacts/**/*.xml");
    }

    public void validate(@NotNull ActionParametersMap params, @NotNull ErrorCollection errorCollection) {
        super.validate(params, errorCollection);

        final String directory = params.getString(xunitParserTask.TEST_FILE_PATTERN);
        if (StringUtils.isEmpty(directory))
        {
            errorCollection.addError(xunitParserTask.TEST_FILE_PATTERN, getI18nBean().getText("xunit.resultFilePattern.error"));
        }
    }
}