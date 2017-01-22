package ut.com.wwwlicious.xunit;

import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.wwwlicious.xunit.impl.xUnitTestReportCollector;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class xUnitTestReportCollectorTest {
    @Test
    public void collectV1() throws Exception {
        File file = new File("src\\test\\resources\\Xunit-v1-failures.xml");
        xUnitTestReportCollector xUnitTestReportCollector = new xUnitTestReportCollector();
        TestCollectionResult collect = xUnitTestReportCollector.collect(file);
        assertEquals(4, collect.getFailedTestResults().size());
        assertEquals(24, collect.getSkippedTestResults().size());
        assertEquals(4585, collect.getSuccessfulTestResults().size());
    }

    @Test
    public void collectV2() throws Exception {
        File file = new File("src\\test\\resources\\Xunit-v2-failures.xml");
        xUnitTestReportCollector xUnitTestReportCollector = new xUnitTestReportCollector();
        TestCollectionResult collect = xUnitTestReportCollector.collect(file);
        assertEquals(3, collect.getFailedTestResults().size());
        assertEquals(24, collect.getSkippedTestResults().size());
        assertEquals(4586, collect.getSuccessfulTestResults().size());
    }
}