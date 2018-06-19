package model.facade.server;

import junit.framework.TestCase;
import model.facade.shared.ModelReferenceFacade;
import model.overhead.Model;

/**
 * Created by tsmit on 9/28/2016.
 */
public class ServerGeneralCommandFacadeTest extends TestCase {

    ServerGeneralCommandFacade serverGeneralCommandFacade;
    ModelReferenceFacade modelReferenceFacade;
    Model model;

    public void setUp() throws Exception {
        super.setUp();
        model = new Model();
        model.initializeModelDefault();
        serverGeneralCommandFacade = ServerGeneralCommandFacade.getInstance();
        modelReferenceFacade = ModelReferenceFacade.getInstance();
        modelReferenceFacade.setModel(model);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCanAcceptTrade(){
    }

    public void testAcceptTrade(){
    }

    public void testCanDiscardCards(){
    }

    public void testDiscardCards(){
    }

    public void testCanRoll(){
    }

    public void testRoll(){
    }

    public void testSendChat() {
    }
}
