package ut.com.wwwlicious.xunit;

import com.atlassian.bamboo.build.logger.NullBuildLogger;
import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.resultsummary.tests.TestCaseResultError;
import com.wwwlicious.xunit.impl.xUnitTestReportCollector;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class xUnitTestReportCollectorTest {
    @Test
    public void collectV1() throws Exception {
        File file = new File("src\\test\\resources\\Xunit-v1-failures.xml");
        xUnitTestReportCollector xUnitTestReportCollector = new xUnitTestReportCollector(new NullBuildLogger());
        TestCollectionResult collect = xUnitTestReportCollector.collect(file);
        assertEquals(4, collect.getFailedTestResults().size());


        TestResults testResults = collect.getFailedTestResults().get(0);
//        CheckErrorResult(testResults);
        assertEquals("SendRawTextNotification_Throws_IfIdentifierNullOrWhitespace(identifier: \" \")", testResults.getActualMethodName());
        assertEquals(24, collect.getSkippedTestResults().size());
        assertEquals(4585, collect.getSuccessfulTestResults().size());
    }

    @Test
    public void collectNonXUnitResultFile() throws Exception {
        File file = new File("src\\test\\resources\\atlassian-plugin.xml");
        xUnitTestReportCollector xUnitTestReportCollector = new xUnitTestReportCollector(new NullBuildLogger());
        TestCollectionResult collect = xUnitTestReportCollector.collect(file);
        assertEquals(0, collect.getFailedTestResults().size());
        assertEquals(0, collect.getSkippedTestResults().size());
        assertEquals(0, collect.getSuccessfulTestResults().size());
    }

    private void CheckErrorResult(TestResults testResults) {
        assertEquals("Catalogue.Shop.Service.Tests.AlertNotification.AlertNotificationServiceTests", testResults.getClassName());
        assertEquals(5L, testResults.getDurationMs());
        assertTrue(testResults.hasErrors());
        List<TestCaseResultError> errors = testResults.getErrors();
        assertEquals(1, errors.size());
        TestCaseResultError actual = errors.iterator().next();
//        assertEquals  ("Xunit.Sdk.XunitException\n" +
//                "Not using the full FakeHttpContext class like punchout test rig but haven't ironed out the test to work with MVCContrib one\n" +
//                "   at FluentAssertions.Execution.XUnit2TestFramework.Throw(String message) in C:\\projects\\fluentassertions-vf06b\\Src\\FluentAssertions.Net45\\Execution\\XUnit2TestFramework.cs:line 35\n" +
//                "   at FluentAssertions.Execution.TestFrameworkProvider.Throw(String message) in C:\\projects\\fluentassertions-vf06b\\Src\\FluentAssertions.Net40\\Execution\\TestFrameworkProvider.cs:line 43\n" +
//                "   at FluentAssertions.Execution.CollectingAssertionStrategy.ThrowIfAny(IDictionary`2 context) in C:\\projects\\fluentassertions-vf06b\\Src\\Core\\Execution\\CollectingAssertionStrategy.cs:line 52\n" +
//                "   at FluentAssertions.Execution.AssertionScope.Dispose() in C:\\projects\\fluentassertions-vf06b\\Src\\Core\\Execution\\AssertionScope.cs:line 254\n" +
//                "   at FluentAssertions.Specialized.ExceptionAssertions`1.ExceptionMessageAssertion.Execute(IEnumerable`1 messages, String expectation, String because, Object[] becauseArgs) in C:\\projects\\fluentassertions-vf06b\\Src\\Core\\Specialized\\ExceptionAssertions.cs:line 273\n" +
//                "   at FluentAssertions.Specialized.ExceptionAssertions`1.WithMessage(String expectedMessage, String because, Object[] becauseArgs) in C:\\projects\\fluentassertions-vf06b\\Src\\Core\\Specialized\\ExceptionAssertions.cs:line 88\n" +
//                "   at Catalogue.Shop.Service.Tests.AlertNotification.AlertNotificationServiceTests.SendRawTextNotification_Throws_IfIdentifierNullOrWhitespace(String identifier) in C:\\development\\repo\\catalogue.shop-release-1.1.9\\src\\Catalogue.Shop.Service.Tests\\AlertNotification\\AlertNotificationServiceTests.cs:line 80", actual.getResult());
    }

    @Test
    public void collectV2() throws Exception {
        File file = new File("src\\test\\resources\\Xunit-v2-failures.xml");
        xUnitTestReportCollector xUnitTestReportCollector = new xUnitTestReportCollector(new NullBuildLogger());
        TestCollectionResult collect = xUnitTestReportCollector.collect(file);
        assertEquals(3, collect.getFailedTestResults().size());
        TestResults testResults = collect.getFailedTestResults().get(0);
//        CheckErrorResult(testResults);
        assertEquals("SendRawTextNotification_Throws_IfIdentifierNullOrWhitespace(identifier: null)", testResults.getActualMethodName());
        assertEquals(24, collect.getSkippedTestResults().size());
        assertEquals(4586, collect.getSuccessfulTestResults().size());
    }
}