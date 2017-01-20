package ut.com.wwwlicious.xunit;

import com.wwwlicious.xunit.api.MyPluginComponent;
import com.wwwlicious.xunit.impl.MyPluginComponentImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest {
    @Test
    public void testMyName() {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent", component.getName());
    }
}