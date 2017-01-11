package model.facade.client;

import junit.framework.TestCase;
import model.definitions.EnumConverter;
import model.definitions.PlayerState;
import model.definitions.ResourceHand;
import model.facade.shared.ModelReferenceFacade;
import model.gameboard.GameBoard;
import model.gameboard.Hex;
import model.gameboard.NodePoint;
import model.overhead.Model;
import model.player.Player;
import model.structures.Road;
import model.structures.Settlement;
import shared.definitions.HexType;
import shared.definitions.ResourceType;
import shared.locations.*;

/**
 * Created by tsmit on 9/28/2016.
 */
public class ClientPlayingCommandFacadeTest extends TestCase {

    ClientPlayingCommandFacade clientPlayingCommandFacade = ClientPlayingCommandFacade.getInstance();
    ModelReferenceFacade modelReferenceFacade;
    Model model;

    public void setUp() throws Exception {
        super.setUp();
        model = new Model();
        model.initializeModelDefault();
        clientPlayingCommandFacade = ClientPlayingCommandFacade.getInstance();
        modelReferenceFacade = ModelReferenceFacade.getInstance();
        modelReferenceFacade.setModel(model);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCanBuildARoad(){
        Player player = model.getPlayerByIndex(0);
        Boolean free = true;
        Boolean notFree = false;
        model.setActiveID(0);
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        assertTrue(clientPlayingCommandFacade.canBuildARoad(0,free)); //valid
        assertFalse(clientPlayingCommandFacade.canBuildARoad(0,notFree)); //does not have required resources
        player.getResourceAggregation().addResource(ResourceType.BRICK,1);
        player.getResourceAggregation().addResource(ResourceType.WOOD,1);
        assertTrue(clientPlayingCommandFacade.canBuildARoad(0,notFree)); //valid
    }

    public void testCanPlaceRoad(){
        Player player = model.getPlayerByIndex(0);
        Boolean free = true;
        Boolean notFree = false;
        Boolean setupRound = true;
        Boolean notSetupRound = false;
        HexLocation hexLocation = new HexLocation(0,0);
        EdgeLocation edge = new EdgeLocation(hexLocation, EdgeDirection.North);
        EdgeLocation base = new EdgeLocation(hexLocation, EdgeDirection.NorthWest);
        VertexDirection[] vertexDirections = EnumConverter.EdgeDirectionToVertexDirection(base.getDir());
        VertexLocation vLoc1=new VertexLocation(base.getHexLoc(),vertexDirections[0]);
        VertexLocation vLoc2=new VertexLocation(base.getHexLoc(),vertexDirections[1]);

        VertexDirection[] vertexDirections2 = EnumConverter.EdgeDirectionToVertexDirection(edge.getDir());
        VertexLocation vLoc3=new VertexLocation(edge.getHexLoc(),vertexDirections2[0]);
        VertexLocation vLoc4=new VertexLocation(edge.getHexLoc(),vertexDirections2[1]);
        NodePoint node1= ModelReferenceFacade.getModel().getNodeAt(vLoc1);//NorthWest
        NodePoint node2=ModelReferenceFacade.getModel().getNodeAt(vLoc2);
        NodePoint node3= ModelReferenceFacade.getModel().getNodeAt(vLoc3);//North
        NodePoint node4=ModelReferenceFacade.getModel().getNodeAt(vLoc4);
        //Two of the above should be the same... too lazy to figure it out.
        //Matthew- I don't know how this was passing earlier, but you weren't assigning roads right.
        //So I reworked the tests
        Road roadBase = new Road(player,node1,node2,base);
        Road road = new Road(player,node3,node4,edge);
        //BEFORE BEING ADDED TO NODES THESE ROADS ARE UNDETECTABLE
        node1.addRoad(roadBase);
        node2.addRoad(roadBase);
        model.getPeripherals().setCurrentState(PlayerState.Playing);
        GameBoard gameBoard=model.getGameBoard();
        assertTrue(clientPlayingCommandFacade.canPlaceRoad(0,free,edge,notSetupRound)); //valid
        assertFalse(clientPlayingCommandFacade.canPlaceRoad(0,notFree,edge,notSetupRound)); //does not have resources
        player.getResourceAggregation().addResource(ResourceType.BRICK,1);
        player.getResourceAggregation().addResource(ResourceType.WOOD,1);
        assertTrue(clientPlayingCommandFacade.canPlaceRoad(0,notFree,edge,notSetupRound)); //valid
        assertFalse(clientPlayingCommandFacade.canPlaceRoad(0,notFree,base,notSetupRound));//Should try to place over old road
        assertFalse(clientPlayingCommandFacade.canPlaceRoad(0,notFree,new EdgeLocation(hexLocation,EdgeDirection.South),notSetupRound));
    }

    public void testPlaceRoad(){
    }

    public void testCanPlaceSettlement(){
        Player player = model.getPlayerByIndex(0);
        Boolean free = true;
        Boolean notFree = false;
        Boolean setupRound = true;
        Boolean notSetupRound = false;
        model.setActiveID(0);
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        HexLocation hexLocation = new HexLocation(0,0);
        VertexLocation vertexLocation = new VertexLocation(hexLocation,VertexDirection.NorthWest);
        EdgeLocation base = new EdgeLocation(hexLocation, EdgeDirection.NorthWest);
        VertexDirection[] vertexDirections = EnumConverter.EdgeDirectionToVertexDirection(base.getDir());
        VertexLocation vLoc1=new VertexLocation(base.getHexLoc(),vertexDirections[0]);
        VertexLocation vLoc2=new VertexLocation(base.getHexLoc(),vertexDirections[1]);
        NodePoint node1= ModelReferenceFacade.getModel().getNodeAt(vLoc1);
        NodePoint node2=ModelReferenceFacade.getModel().getNodeAt(vLoc2);
        Road road = new Road(player,node1,node2,base);
        assertTrue(clientPlayingCommandFacade.canPlaceSettlement(0,free,vertexLocation,setupRound)); //valid
        assertFalse(clientPlayingCommandFacade.canPlaceSettlement(0,notFree,vertexLocation,notSetupRound)); //no resources
        player.getResourceAggregation().addResource(ResourceType.BRICK,1);
        player.getResourceAggregation().addResource(ResourceType.WOOD,1);
        player.getResourceAggregation().addResource(ResourceType.WHEAT,1);
        player.getResourceAggregation().addResource(ResourceType.SHEEP,1);
        assertTrue(clientPlayingCommandFacade.canPlaceSettlement(0,notFree,vertexLocation,setupRound)); //valid
        assertFalse(clientPlayingCommandFacade.canPlaceSettlement(0,notFree,vertexLocation,notSetupRound)); //needs connecting road
        model.getGameBoard().getNodePointAt(vertexLocation).addRoad(road);
        assertTrue(clientPlayingCommandFacade.canPlaceSettlement(0,notFree,vertexLocation,notSetupRound)); //valid
    }

    public void testCanBuildSettlement(){
        Player player = model.getPlayerByIndex(0);
        Boolean free = true;
        Boolean notFree = false;
        model.setActiveID(0);
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        assertTrue(clientPlayingCommandFacade.canBuildSettlement(0,free)); //valid
        assertFalse(clientPlayingCommandFacade.canBuildSettlement(0,notFree)); //does not have required resources
        player.getResourceAggregation().addResource(ResourceType.BRICK,1);
        player.getResourceAggregation().addResource(ResourceType.WOOD,1);
        player.getResourceAggregation().addResource(ResourceType.WHEAT,1);
        player.getResourceAggregation().addResource(ResourceType.SHEEP,1);
        assertTrue(clientPlayingCommandFacade.canBuildSettlement(0,notFree)); //valid
    }

    public void testPlaceSettlement(){
    }

    public void testCanPlaceCity(){
        Player player = model.getPlayerByIndex(0);
        Boolean free = true;
        Boolean notFree = false;
        model.setActiveID(0);
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        HexLocation hexLocation = new HexLocation(0,0);
        VertexLocation vertexLocation = new VertexLocation(hexLocation,VertexDirection.NorthWest);
        EdgeLocation base = new EdgeLocation(hexLocation, EdgeDirection.NorthWest);
        VertexDirection[] vertexDirections = EnumConverter.EdgeDirectionToVertexDirection(base.getDir());
        VertexLocation vLoc1=new VertexLocation(base.getHexLoc(),vertexDirections[0]);
        VertexLocation vLoc2=new VertexLocation(base.getHexLoc(),vertexDirections[1]);
        NodePoint node1= ModelReferenceFacade.getModel().getNodeAt(vLoc1);
        NodePoint node2=ModelReferenceFacade.getModel().getNodeAt(vLoc2);
        Road road = new Road(player,node1,node2,base);
        assertFalse(clientPlayingCommandFacade.canPlaceCity(0,vertexLocation)); //does not have required resources
        player.getResourceAggregation().addResource(ResourceType.ORE,3);
        player.getResourceAggregation().addResource(ResourceType.WHEAT,2);
        assertFalse(clientPlayingCommandFacade.canPlaceCity(0,vertexLocation)); //does not have a road there
        model.getGameBoard().getNodePointAt(vertexLocation).addRoad(road);
        assertFalse(clientPlayingCommandFacade.canPlaceCity(0,vertexLocation)); //no settlement to build on top of
        Settlement settlement = new Settlement(model.getGameBoard().getNodePointAt(vertexLocation),player,player.getColor());
        model.getGameBoard().getNodePointAt(vertexLocation).setColony(settlement);
        assertTrue(clientPlayingCommandFacade.canPlaceCity(0,vertexLocation)); //valid
    }

    public void testCanBuildCity(){
        Player player = model.getPlayerByIndex(0);
        model.setActiveID(0);
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        assertFalse(clientPlayingCommandFacade.canBuildCity(0)); //does not have required resources
        player.getResourceAggregation().addResource(ResourceType.ORE,3);
        player.getResourceAggregation().addResource(ResourceType.WHEAT,2);
        assertTrue(clientPlayingCommandFacade.canBuildCity(0)); //valid
    }

    public void testBuildCity(){
    }

    public void testCanOfferTrade(){
        Player offerer = model.getPlayerByIndex(0);
        Player acceptor = model.getPlayerByIndex(1);
        model.setActiveID(0);
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        ResourceHand resourceHand = new ResourceHand(1,-1,0,0,0); // as seen by the acceptor
        assertFalse(clientPlayingCommandFacade.canOfferTrade(0,1,resourceHand)); //no resources
        offerer.getResourceAggregation().addResource(ResourceType.WOOD,1);
        acceptor.getResourceAggregation().addResource(ResourceType.BRICK,1);
        assertTrue(clientPlayingCommandFacade.canOfferTrade(0,1,resourceHand)); //valid
    }

    public void testOfferTrade(){
    }

    public void testCanMaritimeTrade(){
        Player player = model.getPlayerByIndex(0);
        model.setActiveID(0);
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        HexLocation hexLocation = new HexLocation(0,2);
        VertexLocation vertexLocation = new VertexLocation(hexLocation,VertexDirection.SouthEast);
        //0,3, SouthEast
        //assertTrue(model.getGameBoard().getNodePoints().size()==54);
        assertFalse(clientPlayingCommandFacade.canMaritimeTrade(0,3,ResourceType.BRICK,ResourceType.ORE)); //no resources
        player.getResourceAggregation().addResource(ResourceType.BRICK,3);
        assertFalse(clientPlayingCommandFacade.canMaritimeTrade(0,3,ResourceType.BRICK,ResourceType.ORE)); //no settlement

        NodePoint nodeTest=model.getGameBoard().getNodePointAt(vertexLocation);
        vertexLocation=vertexLocation.getNormalizedLocation();

        Settlement settlement = new Settlement(model.getGameBoard().getNodePointAt(vertexLocation),player,player.getColor());
        player.addColony(settlement);
        model.getGameBoard().getNodePointAt(vertexLocation).setColony(settlement);
        assertTrue(clientPlayingCommandFacade.canMaritimeTrade(0,3,ResourceType.BRICK,ResourceType.ORE)); //valid
    }

    public void testMaritimeTrade(){
    }

    public void testCanRobPlayer(){
        Player attacker = model.getPlayerByIndex(0);
        Player victim = model.getPlayerByIndex(1);
        model.setActiveID(0);
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        HexLocation hexLocation = new HexLocation(0,0);
        HexLocation hexLocBase = new HexLocation(0,3);
        Hex hexBase = new Hex(hexLocBase);
        model.getPeripherals().getRobber().moveTo(hexBase);
        assertFalse(clientPlayingCommandFacade.canRobPlayer(0,1,hexLocation)); //no resources
        victim.getResourceAggregation().addResource(ResourceType.BRICK,1);
        assertTrue(clientPlayingCommandFacade.canRobPlayer(0,1,hexLocation)); //This is the Desert!
        model.getGameBoard().getHexAt(hexLocation).setHexType(HexType.BRICK);
        assertTrue(clientPlayingCommandFacade.canRobPlayer(0,1,hexLocation));//Now its Not.

    }

    public void testRobPlayer(){
    }

    public void testCanEndTurn(){
        model.setActiveID(0);
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        assertTrue(clientPlayingCommandFacade.canEndTurn(0)); //valid
    }

    public void testEndTurn(){
    }

    public void testCanBuyDevCard(){
        Player player = model.getPlayerByIndex(0);
        model.setActiveID(0);
        model.getPeripherals().getTurnTracker().setCurrentState(PlayerState.Playing);
        assertFalse(clientPlayingCommandFacade.canBuyDevCard(0)); //does not have required resources
        player.getResourceAggregation().addResource(ResourceType.ORE,1);
        player.getResourceAggregation().addResource(ResourceType.WHEAT,1);
        player.getResourceAggregation().addResource(ResourceType.SHEEP,1);
        assertTrue(clientPlayingCommandFacade.canBuyDevCard(0)); //valid
    }

    public void testBuyDevCard(){
    }

}
