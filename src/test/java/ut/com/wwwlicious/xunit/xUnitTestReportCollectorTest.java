package ut.com.wwwlicious.xunit;

import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.wwwlicious.xunit.impl.xUnitTestReportCollector;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class xUnitTestReportCollectorTest {
    @Test
    public void collect() throws Exception {
        File file = new File("src\\test\\resources\\Xunit-v1-failures.xml");
        xUnitTestReportCollector xUnitTestReportCollector = new xUnitTestReportCollector();
        TestCollectionResult collect = xUnitTestReportCollector.collect(file);
        assertEquals(4, collect.getFailedTestResults().size());
        assertEquals(24, collect.getSkippedTestResults().size());
        assertEquals(4585, collect.getSuccessfulTestResults().size());
    }
}