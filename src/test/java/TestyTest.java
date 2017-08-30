import com.ndustrialio.testy.Testy;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by jmhunt on 8/28/17.
 */
public class TestyTest
{
    Testy testy;
    @Before
    public void setup() throws Exception
    {
        testy = new Testy("example.yaml");
    }

    @Test
    public void runTest() throws Exception
    {
        testy.init();
    }

    @After
    public void cleanup() throws Exception
    {
        testy.shutdown();
    }


}
