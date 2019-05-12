import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import me.homework.server.WebServer;
import me.homework.server.apps.FileServingApp;
import org.junit.*;

public class HttpTest {

    private Thread server;
    private int port;

    @Before
    public void setUp() {
        port = 52052;
        server = new Thread(new WebServer(port, 4, new FileServingApp("web/")));
        server.start();
    }

    @After
    public void tearDown() {
        server.interrupt();
    }

    @Test
    public void testGetRequests() {
        final WebClient webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

        try {
            Page unexpectedResult = webClient.getPage("http://127.0.0.1:" + port);
            Assert.assertEquals(403, unexpectedResult.getWebResponse().getStatusCode());

            Page successfulResult = webClient.getPage("http://127.0.0.1:" + port + "/index.html");
            Assert.assertEquals(200, successfulResult.getWebResponse().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}