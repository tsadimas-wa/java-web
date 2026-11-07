package gr.uniwa.ice.webapp1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloServletTest {

    @Test
    void escapesHtml() {
        assertEquals("&lt;b&gt;", HelloServlet.escape("<b>"));
    }
}
