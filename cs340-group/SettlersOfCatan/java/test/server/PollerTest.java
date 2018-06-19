package server;

import junit.framework.TestCase;

/**
 * Created by tsmit on 9/28/2016.
 */
public class PollerTest extends TestCase {

    private static IServer server = new MockServerProxy();

    public void testPoller() throws Exception {
        Poller mockPoller = new Poller(server);
        mockPoller.setTimer(Poller.PollType.LIST);
        Poller realPoller = new Poller();
        realPoller.setTimer(Poller.PollType.MODEL);
    }
}
