package model.facade.server;

import junit.framework.TestCase;
import model.facade.shared.ModelReferenceFacade;
import model.overhead.Model;

/**
 * Created by tsmit on 9/28/2016.
 */
public class ServerDevCardFacadeTest extends TestCase {

    ServerDevCardFacade serverDevCardFacade;
    ModelReferenceFacade modelReferenceFacade;
    Model model;

    public void setUp() throws Exception {
        super.setUp();
        model = new Model();
        model.initializeModelDefault();
        serverDevCardFacade = ServerDevCardFacade.getInstance();
        modelReferenceFacade = ModelReferenceFacade.getInstance();
        modelReferenceFacade.setModel(model);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCanPlayDevCard(){
        //I'm abducting this for my own Equals test
        Model testModel1=new Model();
        testModel1.initializeModelDefault();
        Model testModel2=new Model();
        testModel2.initializeModelDefault();
        //assertTrue(testModel1.equals(testModel2));
    }

    public void testCanUseSoldier(){

    }

    public void testUseSoldier(){

    }

    public void testCanUseYearOfPlenty(){

    }

    public void testUseYearOfPlenty(){

    }

    public void testCanUseRoadBuilding(){

    }

    public void testUseRoadBuilding(){

    }

    public void testCanUseMonopoly(){

    }

    public void testUseMonopoly(){

    }

    public void testCanUseMonument(){

    }

    public void testUseMonument(){

    }
}
