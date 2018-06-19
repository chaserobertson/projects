package model.facade.client;

import junit.framework.TestCase;
import model.definitions.EnumConverter;
import model.definitions.PlayerState;
import model.facade.shared.ModelReferenceFacade;
import model.gameboard.NodePoint;
import model.overhead.Model;
import model.player.Player;
import model.resources.DevelopmentCard;
import model.structures.Road;
import shared.definitions.DevCardType;
import shared.definitions.ResourceType;
import shared.locations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tsmit on 9/28/2016.
 */
public class ClientDevCardFacadeTest extends TestCase {

    ClientDevCardFacade clientDevCardFacade;
    ModelReferenceFacade modelReferenceFacade;
    Model model;

    public void setUp() throws Exception {
        super.setUp();
        model = new Model();
        model.initializeModelDefault();
        clientDevCardFacade = ClientDevCardFacade.getInstance();
        modelReferenceFacade = ModelReferenceFacade.getInstance();
        modelReferenceFacade.setModel(model);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void runAll() throws Exception {
        testCanUseSoldier();
        testUseSoldier();
        testCanUseYearOfPlenty();
        testUseYearOfPlenty();
        testCanUseRoadBuilding();
        testUseRoadBuilding();
        testCanUseMonopoly();
        testUseMonopoly();
        testCanUseMonument();
        testUseMonument();
    }

    public void testCanUseSoldier() {
        Player player = model.getPlayerByIndex(0);
        HexLocation hexLocation = new HexLocation(0,0);
        DevelopmentCard soldierCard = new DevelopmentCard(DevCardType.SOLDIER);
        List<DevelopmentCard> developmentCards = new ArrayList<>();
        developmentCards.add(soldierCard);
        assertFalse(clientDevCardFacade.canUseSoldier(0,1,hexLocation)); //player doesn't have card
        player.addNewDevelopmentCard(soldierCard);
        assertFalse(clientDevCardFacade.canUseSoldier(0,1,hexLocation)); //player has card but in just bought deck
        player.setDevelopmentCards(developmentCards);
        model.setActiveID(3);
        assertFalse(clientDevCardFacade.canUseSoldier(0,1,hexLocation)); //is not player's turn
        model.setActiveID(0);
        model.getPeripherals().setCurrentState(PlayerState.Discarding);
        assertFalse(clientDevCardFacade.canUseSoldier(0,1,hexLocation)); //player is not playing
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        player.addDevelopmentCard(soldierCard);
        assertTrue(clientDevCardFacade.canUseSoldier(0,1,hexLocation)); //valid
    }

    public void testUseSoldier(){
        //test later
    }

    public void testCanUseYearOfPlenty(){
        Player player = model.getPlayerByIndex(0);
        DevelopmentCard yearOfPlentyCard = new DevelopmentCard(DevCardType.YEAR_OF_PLENTY);
        List<DevelopmentCard> developmentCards = new ArrayList<>();
        developmentCards.add(yearOfPlentyCard);
        assertFalse(clientDevCardFacade.canUseYearOfPlenty(0,ResourceType.BRICK,ResourceType.ORE)); //player doesn't have card
        player.addNewDevelopmentCard(yearOfPlentyCard);
        assertFalse(clientDevCardFacade.canUseYearOfPlenty(0,ResourceType.BRICK,ResourceType.ORE)); //player has card but in just bought deck
        player.setDevelopmentCards(developmentCards);
        model.getPeripherals().getTurnTracker().setActiveID(3);
        assertFalse(clientDevCardFacade.canUseYearOfPlenty(0,ResourceType.BRICK,ResourceType.ORE)); //is not player's turn
        model.setActiveID(0);
        model.getPeripherals().setCurrentState(PlayerState.Discarding);
        assertFalse(clientDevCardFacade.canUseYearOfPlenty(0,ResourceType.BRICK,ResourceType.ORE)); //player is not playing
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        assertTrue(clientDevCardFacade.canUseYearOfPlenty(0,ResourceType.BRICK,ResourceType.ORE)); //valid
    }

    public void testUseYearOfPlenty(){
        //test later
    }

    public void testCanUseRoadBuilding(){
        Player player = model.getPlayerByIndex(0);
        HexLocation hexLocation = new HexLocation(0,0);
        EdgeLocation edge1 = new EdgeLocation(hexLocation, EdgeDirection.North);
        EdgeLocation edge2 = new EdgeLocation(hexLocation, EdgeDirection.NorthEast);
        EdgeLocation base = new EdgeLocation(hexLocation, EdgeDirection.NorthWest);
        VertexDirection[] vertexDirections = EnumConverter.EdgeDirectionToVertexDirection(base.getDir());
        VertexLocation vLoc1=new VertexLocation(base.getHexLoc(),vertexDirections[0]);
        VertexLocation vLoc2=new VertexLocation(base.getHexLoc(),vertexDirections[1]);
        NodePoint node1= ModelReferenceFacade.getModel().getNodeAt(vLoc1);
        NodePoint node2=ModelReferenceFacade.getModel().getNodeAt(vLoc2);
        VertexDirection[] vertexDirections2 = EnumConverter.EdgeDirectionToVertexDirection(edge1.getDir());
        VertexLocation vLoc3=new VertexLocation(edge1.getHexLoc(),vertexDirections2[0]);
        VertexLocation vLoc4=new VertexLocation(edge1.getHexLoc(),vertexDirections2[1]);
        NodePoint node3= ModelReferenceFacade.getModel().getNodeAt(vLoc3);
        NodePoint node4=ModelReferenceFacade.getModel().getNodeAt(vLoc4);
        VertexDirection[] vertexDirections3 = EnumConverter.EdgeDirectionToVertexDirection(edge2.getDir());
        VertexLocation vLoc5=new VertexLocation(edge2.getHexLoc(),vertexDirections3[0]);
        VertexLocation vLoc6=new VertexLocation(edge2.getHexLoc(),vertexDirections3[1]);
        NodePoint node5= ModelReferenceFacade.getModel().getNodeAt(vLoc5);
        NodePoint node6=ModelReferenceFacade.getModel().getNodeAt(vLoc6);
        Road roadBase = new Road(player,node1,node2,base);
        Road road1 = new Road(player,node3,node4,edge1);
        Road road2 = new Road(player,node3,node4,edge2);
        DevelopmentCard roadBuildCard = new DevelopmentCard(DevCardType.ROAD_BUILD);
        List<DevelopmentCard> developmentCards = new ArrayList<>();
        developmentCards.add(roadBuildCard);
        model.getGameBoard().getNodePoints().get(20).addRoad(roadBase);
        model.getPeripherals().setCurrentState(PlayerState.Playing);
        assertFalse(clientDevCardFacade.canUseRoadBuilding(0,edge1,edge2)); //player doesn't have card
        player.addNewDevelopmentCard(roadBuildCard);
        assertFalse(clientDevCardFacade.canUseRoadBuilding(0,edge1,edge2)); //player has card but in just bought deck
        player.setDevelopmentCards(developmentCards);
        assertFalse(clientDevCardFacade.canUseRoadBuilding(0,edge1,edge2)); //is not player's turn
        model.setActiveID(0);
        assertFalse(clientDevCardFacade.canUseRoadBuilding(0,edge1,edge2)); //player is not playing
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        assertFalse(clientDevCardFacade.canUseRoadBuilding(0,edge1,edge2)); //no connection
        //player.addRoad(roadBase);
        //assertTrue(clientDevCardFacade.canUseRoadBuilding(0,edge1,edge2)); //valid
    }

    public void testUseRoadBuilding(){
        //test later
    }

    public void testCanUseMonopoly(){
        Player player = model.getPlayerByIndex(0);
        DevelopmentCard yearOfPlentyCard = new DevelopmentCard(DevCardType.MONOPOLY);
        List<DevelopmentCard> developmentCards = new ArrayList<>();
        developmentCards.add(yearOfPlentyCard);
        assertFalse(clientDevCardFacade.canUseMonopoly(0,ResourceType.BRICK)); //player doesn't have card
        player.addNewDevelopmentCard(yearOfPlentyCard);
        assertFalse(clientDevCardFacade.canUseMonopoly(0,ResourceType.BRICK)); //player has card but in just bought deck
        player.setDevelopmentCards(developmentCards);
        model.getPeripherals().getTurnTracker().setActiveID(3);
        assertFalse(clientDevCardFacade.canUseMonopoly(0,ResourceType.BRICK)); //is not player's turn
        model.setActiveID(0);
        model.getPeripherals().setCurrentState(PlayerState.Discarding);
        assertFalse(clientDevCardFacade.canUseMonopoly(0,ResourceType.BRICK)); //player is not playing
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        assertTrue(clientDevCardFacade.canUseMonopoly(0,ResourceType.BRICK)); //valid
    }

    public void testUseMonopoly(){
        //test later
    }

    public void testCanUseMonument(){
        Player player = model.getPlayerByIndex(0);
        DevelopmentCard monumentCard = new DevelopmentCard(DevCardType.MONUMENT);
        List<DevelopmentCard> developmentCards = new ArrayList<>();
        developmentCards.add(monumentCard);
        assertFalse(clientDevCardFacade.canUseMonument(0)); //player doesn't have card
        player.addDevelopmentCard(monumentCard);
        assertFalse(clientDevCardFacade.canUseMonument(0)); //player has card but in just bought deck
        player.setDevelopmentCards(developmentCards);
        assertFalse(clientDevCardFacade.canUseMonument(0)); //is not player's turn
        model.setActiveID(0);
        assertFalse(clientDevCardFacade.canUseMonument(0)); //player is not playing
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        assertFalse(clientDevCardFacade.canUseMonument(0)); //player doesn't have enough points to play
        player.setVictoryPoints(9);
        assertTrue(clientDevCardFacade.canUseMonument(0)); //valid

    }

    public void testUseMonument(){
        //test later
    }

}
