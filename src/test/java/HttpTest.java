import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import me.homework.server.WebServer;
import me.homework.server.apps.FileServingApp;
import org.junit.*;

/**
 * Creates a new webserver with thread pooling and tests the requests.
 * 
 * Created by Mihail on 10/24/2015.
 * Modified by Javier on 12/05/2019.
 */
public class HttpTest {

    private Thread server;
    private int port;

    @Before
    public void setUp() {
        port = 52051;
        server = new Thread(new WebServer(port, 4, new FileServingApp("web/")));
        server.start();
    }

    @After
    public void tearDown() {
        server.interrupt();
    }

    @Test
    public void testGetRequests() throws Exception {
        final WebClient webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

        try {
            Page unexpectedResult = webClient.getPage("http://127.0.0.1:" 
                + port);
            Assert.assertEquals(403, 
                unexpectedResult.getWebResponse().getStatusCode());

            Page successfulResult = webClient.getPage("http://127.0.0.1:" 
                + port + "/index.html");
            Assert.assertEquals(200, 
                successfulResult.getWebResponse().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}