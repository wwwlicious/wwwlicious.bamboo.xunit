package com.wwwlicious.xunit.impl;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.task.TaskTestResultsSupport;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.sal.api.message.I18nResolver;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class xUnitParserConfigurator extends AbstractTaskConfigurator implements TaskTestResultsSupport {

    private I18nResolver i18nResolver;

    public boolean taskProducesTestResults(@NotNull TaskDefinition taskDefinition) {
        return true;
    }

    @NotNull
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition) {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        config.put(xUnitParserTask.TEST_FILE_PATTERN, params.getString(xUnitParserTask.TEST_FILE_PATTERN));
        config.put(xUnitParserTask.COLLECT_OUTDATED_FILES, params.getString(xUnitParserTask.COLLECT_OUTDATED_FILES));
        return config;
    }

    public void populateContextForEdit(@NotNull Map<String, Object> map, @NotNull TaskDefinition taskDefinition) {
        super.populateContextForEdit(map, taskDefinition);
        map.put(xUnitParserTask.TEST_FILE_PATTERN, taskDefinition.getConfiguration().get(xUnitParserTask.TEST_FILE_PATTERN));
        map.put(xUnitParserTask.COLLECT_OUTDATED_FILES, taskDefinition.getConfiguration().get(xUnitParserTask.COLLECT_OUTDATED_FILES));
    }

    public void populateContextForCreate(@NotNull Map<String, Object> map) {
        super.populateContextForCreate(map);
        map.put(xUnitParserTask.TEST_FILE_PATTERN, "artifacts/**/*.xml");
        map.put(xUnitParserTask.COLLECT_OUTDATED_FILES, false);
    }

    public void validate(@NotNull ActionParametersMap params, @NotNull ErrorCollection errorCollection) {
        super.validate(params, errorCollection);

        final String directory = params.getString(xUnitParserTask.TEST_FILE_PATTERN);
        if (StringUtils.isEmpty(directory))
        {
            errorCollection.addError(xUnitParserTask.TEST_FILE_PATTERN, i18nResolver.getText(xUnitParserTask.TEST_FILE_PATTERN + ".error"));
        }
    }

    public void setI18nResolver(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }
}