package ut.com.wwwlicious.xunit;

import com.wwwlicious.xunit.impl.xunitError;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class xunitErrorTest {
    private static org.w3c.dom.Document loadXML(String xml) {
        DocumentBuilderFactory fctr = DocumentBuilderFactory.newInstance();
        DocumentBuilder bldr;
        try {
            bldr = fctr.newDocumentBuilder();
            InputSource insrc = new InputSource(new StringReader(xml));
            return bldr.parse(insrc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void xunitErrorReturnsDefaultForNull() {
        xunitError error = new xunitError(null);
        assertEquals("message:\tUnknown error, missing Failure node in results\r\nstacktrace:\t", error.toString());
    }

    @Test
    public void xunitErrorSetsMessageCorrectly() {
        org.w3c.dom.Document document = loadXML("<test><failure><message>testMsg</message><stack-trace>test stack</stack-trace></failure></test>");
        xunitError error = new xunitError((Element) document.getElementsByTagName("test").item(0));
        assertEquals("message:\ttestMsg\r\nstacktrace:\ttest stack", error.toString());
    }

    @Test
    public void xunitErrorSetsMessageCorrectlyWithCData() {
        org.w3c.dom.Document document = loadXML("<test><failure><message><![CDATA[testMsg]]></message><stack-trace><![CDATA[test stack]]></stack-trace></failure></test>");
        xunitError error = new xunitError((Element) document.getElementsByTagName("test").item(0));
        assertEquals("message:\ttestMsg\r\nstacktrace:\ttest stack", error.toString());
    }
}