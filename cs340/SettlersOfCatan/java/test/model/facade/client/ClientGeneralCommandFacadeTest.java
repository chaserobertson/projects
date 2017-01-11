package model.facade.client;

import junit.framework.TestCase;
import model.definitions.PlayerState;
import model.definitions.ResourceHand;
import model.facade.shared.ModelReferenceFacade;
import model.overhead.Model;
import model.peripherals.TradeOffer;
import model.player.Player;
import shared.definitions.ResourceType;

/**
 * Created by tsmit on 9/28/2016.
 */
public class ClientGeneralCommandFacadeTest extends TestCase {

    ClientGeneralCommandFacade clientGeneralCommandFacade = ClientGeneralCommandFacade.getInstance();
    ModelReferenceFacade modelReferenceFacade;
    Model model;

    public void setUp() throws Exception {
        super.setUp();
        model = new Model();
        model.initializeModelDefault();
        clientGeneralCommandFacade = ClientGeneralCommandFacade.getInstance();
        modelReferenceFacade = ModelReferenceFacade.getInstance();
        modelReferenceFacade.setModel(model);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCanAcceptTrade(){
        Player acceptor = model.getPlayerByIndex(0);
        Player offerer = model.getPlayerByIndex(1);
        ResourceHand resourceHand = new ResourceHand(1,-1,0,0,0); // as seen by the offerer
        TradeOffer tradeOffer = new TradeOffer(offerer,acceptor,resourceHand);
        model.setTradeOffer(tradeOffer);
        assertFalse(clientGeneralCommandFacade.canAcceptTrade(0,1)); //do not have valid resources
        acceptor.getResourceAggregation().addResource(ResourceType.BRICK,1);
        offerer.getResourceAggregation().addResource(ResourceType.WOOD,1);
        assertTrue(clientGeneralCommandFacade.canAcceptTrade(0,1)); //valid
    }

    public void testAcceptTrade(){
    }

    public void testCanDiscardCards(){
        Player player = model.getPlayerByIndex(0);
        ResourceHand resourceHand = new ResourceHand(0,-4,0,0,0);
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Discarding);
        assertFalse(clientGeneralCommandFacade.canDiscardCards(0,resourceHand)); //do not have valid resources
        player.getResourceAggregation().addResource(ResourceType.BRICK,7);
        assertFalse(clientGeneralCommandFacade.canDiscardCards(0,resourceHand)); //still not enough
        player.getResourceAggregation().addResource(ResourceType.BRICK,1);
        assertTrue(clientGeneralCommandFacade.canDiscardCards(0,resourceHand)); //valid
    }

    public void testDiscardCards(){
    }

    public void testCanRoll(){
        Player player = model.getPlayerByIndex(0);
        model.setActiveID(0);
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Rolling);
        assertTrue(clientGeneralCommandFacade.canRoll(0)); //valid
    }

    public void testRoll(){
    }

    public void testSendChat() {
    }
}
