package model.facade.server;

import junit.framework.TestCase;
import model.definitions.Constants;
import model.definitions.ResourceHand;
import model.definitions.ResourceTypeQuantityPair;
import model.facade.shared.ModelReferenceFacade;
import model.gameboard.NodePoint;
import model.gameboard.Port;
import model.overhead.Model;
import model.peripherals.TradeOffer;
import model.player.Player;
import model.resources.ResourcePile;
import model.serialization.Deserializer;
import model.structures.Road;
import model.structures.Settlement;
import params.*;
import shared.definitions.PortType;
import shared.definitions.ResourceType;
import shared.locations.*;
import model.definitions.PlayerState;

/**
 * Created by tsmit on 9/28/2016.
 */
public class ServerPlayingCommandFacadeTest extends TestCase {

    ServerPlayingCommandFacade serverPlayingCommandFacade;
    ModelReferenceFacade modelReferenceFacade;
    Model model;

    public void setUp() throws Exception {
        super.setUp();
        model = new Model();
        model.initializeModelDefault();
        serverPlayingCommandFacade = ServerPlayingCommandFacade.getInstance();
        modelReferenceFacade = ModelReferenceFacade.getInstance();
        modelReferenceFacade.setModel(model);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCanBuildARoad(){

    }

    public void testCanPlaceRoad(){
        BuildRoadParams params = new BuildRoadParams();
        params.setupRound=false;
        params.playerIndex=0;
        params.free=false;
        params.roadLocation=new EdgeLocation(new HexLocation(0,0), EdgeDirection.North);
        model= Deserializer.deserialize(Constants.initJSONModelTest);
        assertFalse(ServerPlayingCommandFacade.canPlaceRoad(params,model));//Cant build a road
        assertFalse(ServerPlayingCommandFacade.canBuildARoad(params,model));//No resources
        params.free=true;
        assertTrue(ServerPlayingCommandFacade.canBuildARoad(params,model));
        model.getPeripherals().setCurrentState(PlayerState.Playing);
        assertTrue(ServerPlayingCommandFacade.canBuildARoad(params,model));
        assertFalse(ServerPlayingCommandFacade.canPlaceRoad(params,model));
        params.setupRound=true;
        assertTrue(ServerPlayingCommandFacade.canBuildARoad(params,model));
        assertFalse(ServerPlayingCommandFacade.canPlaceRoad(params,model));
        NodePoint centerNorthEast = model.getNodeAt(new VertexLocation(new HexLocation(0,0), VertexDirection.NorthEast));
        centerNorthEast.setColony(new Settlement(centerNorthEast,model.getPlayerByIndex(0),model.getPlayerByIndex(0).getColor()));
        assertTrue(ServerPlayingCommandFacade.canPlaceRoad(params,model));
        centerNorthEast.setColony(new Settlement(centerNorthEast,model.getPlayerByIndex(1),model.getPlayerByIndex(1).getColor()));
        assertFalse(ServerPlayingCommandFacade.canPlaceRoad(params,model));
        NodePoint centerNorthWest=model.getNodeAt(new VertexLocation(new HexLocation(0,0),VertexDirection.NorthWest));
        Road tempRoad = new Road(model.getPlayerByIndex(0),centerNorthEast,centerNorthWest,params.roadLocation);
        centerNorthEast.addRoad(tempRoad);
        centerNorthWest.addRoad(tempRoad);
        centerNorthEast.setColony(new Settlement(centerNorthEast,model.getPlayerByIndex(0),model.getPlayerByIndex(0).getColor()));
        assertFalse(ServerPlayingCommandFacade.canPlaceRoad(params,model));
        params.roadLocation=new EdgeLocation(new HexLocation(0,0),EdgeDirection.NorthWest);
        assertFalse(ServerPlayingCommandFacade.canPlaceRoad(params,model));
        params.setupRound=false;
        assertTrue(ServerPlayingCommandFacade.canPlaceRoad(params,model));
        params.free=false;
        assertFalse(ServerPlayingCommandFacade.canBuildARoad(params,model));
        assertFalse(ServerPlayingCommandFacade.canPlaceRoad(params,model));
        model.getPlayerByIndex(0).getResourceAggregation().addResources(
                new ResourceTypeQuantityPair(ResourceType.WOOD,1),new ResourceTypeQuantityPair(ResourceType.BRICK,1)
        );
        assertTrue(ServerPlayingCommandFacade.canBuildARoad(params,model));
        assertTrue(ServerPlayingCommandFacade.canPlaceRoad(params,model));
        model.getPlayerByIndex(0).getResourceAggregation().setQuantityResourceType(ResourceType.WOOD,0);
        assertFalse(ServerPlayingCommandFacade.canBuildARoad(params,model));
        assertFalse(ServerPlayingCommandFacade.canPlaceRoad(params,model));
        model.getPlayerByIndex(0).getResourceAggregation().setQuantityResourceType(ResourceType.WOOD,1);
        model.getPlayerByIndex(0).setRemainingRoads(0);
        assertFalse(ServerPlayingCommandFacade.canBuildARoad(params,model));
        assertFalse(ServerPlayingCommandFacade.canPlaceRoad(params,model));
        //To wrap up.
        model.initializeModelDefault();

    }

    public void testPlaceRoad(){
    }

    public void testCanPlaceSettlement(){
        model.getPeripherals().setCurrentState(PlayerState.Playing);
        NodePoint centerNorthEast = model.getNodeAt(new VertexLocation(new HexLocation(0,0),VertexDirection.NorthEast));
        NodePoint centerNorthWest = model.getNodeAt(new VertexLocation(new HexLocation(0,0),VertexDirection.NorthWest));
        Player player0 = model.getPlayerByIndex(0);
        model.getPeripherals().setCurrentState(PlayerState.Playing);
        //Settlement testSettlement=new Settlement (centerNorthEast,player0,player0.getColor());
        BuildSettlementParams params=new BuildSettlementParams();
        params.setupRound=false;
        params.playerIndex=0;
        params.free=false;
        params.vertexLocation=new VertexLocation(new HexLocation(0,0),VertexDirection.NorthEast);
        assertFalse(ServerPlayingCommandFacade.canBuildSettlement(params,ModelReferenceFacade.getModel()));
        assertFalse(ServerPlayingCommandFacade.canPlaceSettlement(params,ModelReferenceFacade.getModel()));
        params.setupRound=true;
        assertTrue(ServerPlayingCommandFacade.canBuildSettlement(params,ModelReferenceFacade.getModel()));
        assertTrue(ServerPlayingCommandFacade.canPlaceSettlement(params,ModelReferenceFacade.getModel()));
        params.setupRound=false;
        params.free=true;
        assertTrue(ServerPlayingCommandFacade.canBuildSettlement(params,ModelReferenceFacade.getModel()));
        assertFalse(ServerPlayingCommandFacade.canPlaceSettlement(params,ModelReferenceFacade.getModel()));
        Road tempRoad=new Road(model.getPlayerByIndex(1),centerNorthEast,centerNorthWest,new EdgeLocation(new HexLocation(0,0),EdgeDirection.North));
        centerNorthEast.addRoad(tempRoad);
        centerNorthWest.addRoad(tempRoad);
        assertFalse(ServerPlayingCommandFacade.canPlaceSettlement(params,ModelReferenceFacade.getModel()));
        tempRoad.setOwningPlayer(player0);
        assertTrue(ServerPlayingCommandFacade.canPlaceSettlement(params,ModelReferenceFacade.getModel()));
        params.free=false;
        assertFalse(ServerPlayingCommandFacade.canBuildSettlement(params,ModelReferenceFacade.getModel()));
        assertFalse(ServerPlayingCommandFacade.canPlaceSettlement(params,ModelReferenceFacade.getModel()));

    }

    public void testCanBuildSettlement(){
    }

    public void testPlaceSettlement(){
    }

    public void testCanPlaceCity(){//3 ore 2 wheat, local owned settlement
        Model model = Deserializer.deserialize(Constants.initJSONModelTest);
        BuildCityParams params = new BuildCityParams();
        params.playerIndex=0;
        params.vertexLocation=new VertexLocation(new HexLocation(0,0),VertexDirection.NorthEast);
        Player player = model.getPlayerByIndex(0);
        player.setResourceAggregation(new ResourcePile(0,0,0,2,3));
        Settlement localSettlement=new Settlement(model.getNodeAt(params.vertexLocation),player,player.getColor());
        player.addColony(localSettlement);
        assertTrue(ServerPlayingCommandFacade.canBuildCity(params,model));//perfect Situation
        assertTrue(ServerPlayingCommandFacade.canPlaceCity(params,model));//As above
        player.setRemainingCities(0);
        assertFalse(ServerPlayingCommandFacade.canBuildCity(params,model));//No cities avaialble
        assertFalse(ServerPlayingCommandFacade.canPlaceCity(params,model));
        player.setRemainingCities(1);
        assertTrue(ServerPlayingCommandFacade.canPlaceCity(params,model));
        params.vertexLocation=new VertexLocation(new HexLocation(0,0),VertexDirection.NorthWest);
        assertTrue(ServerPlayingCommandFacade.canBuildCity(params,model));//no settlement under it, but plenty of resources
        assertFalse(ServerPlayingCommandFacade.canPlaceCity(params,model));
        params.vertexLocation=new VertexLocation(new HexLocation(0,0),VertexDirection.NorthEast);
        localSettlement.setOwningPlayer(model.getPlayerByIndex(1));
        assertTrue(ServerPlayingCommandFacade.canBuildCity(params,model));//Plenty of resources
        assertFalse(ServerPlayingCommandFacade.canPlaceCity(params,model));//Not his settlement
        localSettlement.setOwningPlayer(player);
        player.setResourceAggregation(new ResourcePile(99,99,99,99,2));
        assertFalse(ServerPlayingCommandFacade.canBuildCity(params,model));//Not enough ore
        assertFalse(ServerPlayingCommandFacade.canPlaceCity(params,model));
        player.setResourceAggregation(new ResourcePile(99,99,99,1,99));
        assertFalse(ServerPlayingCommandFacade.canBuildCity(params,model));//Not enough wheat
        assertFalse(ServerPlayingCommandFacade.canPlaceCity(params,model));
        player.setResourceAggregation(new ResourcePile(99,99,99,2,3));
        assertTrue(ServerPlayingCommandFacade.canBuildCity(params,model));//All is well again
        assertTrue(ServerPlayingCommandFacade.canPlaceCity(params,model));


    }

    public void testCanBuildCity(){
    }

    public void testBuildCity(){
    }

    public void testCanOfferTrade(){
        Model model = Deserializer.deserialize(Constants.initJSONModelTest);
        OfferTradeParams params=new OfferTradeParams();
        params.playerIndex=0;
        params.receiver=1;
        params.offer=new ResourceHand(0,0,0,0,0);
        Player player0=model.getPlayerByIndex(0);
        Player player1=model.getPlayerByIndex(1);
        assertTrue(ServerPlayingCommandFacade.canOfferTrade(params,model));
        model.getPeripherals().setCurrentState(PlayerState.Discarding);
        assertFalse(ServerPlayingCommandFacade.canOfferTrade(params,model));//Not in playing phase
        model.getPeripherals().setCurrentState(PlayerState.Playing);
        model.getPeripherals().setActiveTradeOffer(new TradeOffer(player0,player1,params.offer));
        assertFalse(ServerPlayingCommandFacade.canOfferTrade(params,model));//Already a tradeOffer
        model.setTradeOffer(null);
        player0.setResourceAggregation(new ResourcePile(3,3,3,0,0));
        player1.setResourceAggregation(new ResourcePile(0,0,0,3,3));
        params.offer=new ResourceHand(3,3,3,-3,-3);
        assertTrue(ServerPlayingCommandFacade.canOfferTrade(params,model));//all is well
        params.offer=new ResourceHand(3,3,3,3,0);
        assertFalse(ServerPlayingCommandFacade.canOfferTrade(params,model));//offerer does not have resources
        params.offer=new ResourceHand(3,3,-3,-3,-3);
        assertTrue(ServerPlayingCommandFacade.canOfferTrade(params,model));//Even though receiver can not accept, its okay
        params.offer.setResource(0,4);
        assertFalse(ServerPlayingCommandFacade.canOfferTrade(params,model));//Offerer has insufficient of the first resource

    }


    public void testOfferTrade(){
    }

    public void testCanMaritimeTrade(){
        Model model =Deserializer.deserialize(Constants.initJSONModelTest);
        MaritimeTradeParams params=new MaritimeTradeParams();
        params.ratio=4;
        params.playerIndex=0;
        params.inputResource=ResourceType.WOOD;
        params.outputResource=ResourceType.BRICK;
        Player player0=model.getPlayerByIndex(0);
        player0.getResourceAggregation().setQuantityResourceType(ResourceType.WOOD,4);
        assertTrue(ServerPlayingCommandFacade.canMaritimeTrade(params,model));
        model.getPeripherals().setCurrentState(PlayerState.Discarding);
        assertFalse(ServerPlayingCommandFacade.canMaritimeTrade(params,model));
        model.getPeripherals().setCurrentState(PlayerState.Playing);
        params.ratio=3;
        assertFalse(ServerPlayingCommandFacade.canMaritimeTrade(params,model));
        params.ratio=4;
        params.inputResource=ResourceType.BRICK;
        assertFalse(ServerPlayingCommandFacade.canMaritimeTrade(params,model));
        params.inputResource=ResourceType.WOOD;
        player0.getResourceAggregation().setQuantityResourceType(ResourceType.WOOD,3);
        assertFalse(ServerPlayingCommandFacade.canMaritimeTrade(params,model));
        //I'm going to generate a port in the middle of the world. Shhhh...
        Settlement localSettlement=new Settlement(model.getNodeAt(new VertexLocation(new HexLocation(0,0),VertexDirection.NorthEast)),player0,player0.getColor());
        player0.addColony(localSettlement);
        Port port=new Port(PortType.THREE);
        localSettlement.getNodePoint().setPort(port);
        params.ratio=3;
        assertTrue(ServerPlayingCommandFacade.canMaritimeTrade(params,model));
        player0.getResourceAggregation().setQuantityResourceType(ResourceType.WOOD,2);
        assertFalse(ServerPlayingCommandFacade.canMaritimeTrade(params,model));
        params.ratio=2;
        assertFalse(ServerPlayingCommandFacade.canMaritimeTrade(params,model));
        port.setType(PortType.WOOD);
        assertTrue(ServerPlayingCommandFacade.canMaritimeTrade(params,model));//Failed
        port.setType(PortType.BRICK);
        assertFalse(ServerPlayingCommandFacade.canMaritimeTrade(params,model));
        port.setType(PortType.WOOD);
        player0.setResourceAggregation(new ResourcePile(1,99,99,99,99));
        assertFalse(ServerPlayingCommandFacade.canMaritimeTrade(params,model));
    }

    public void testMaritimeTrade(){
    }

    public void testCanRobPlayer(){
        Model model=Deserializer.deserialize(Constants.initJSONModelTest);
        RobPlayerParams params=new RobPlayerParams();
        params.location=new HexLocation(0,0);
        params.playerIndex=0;
        params.victimIndex=1;
        Player player0=model.getPlayerByIndex(0);
        Player player1=model.getPlayerByIndex(1);
        Settlement localSettlement=new Settlement(model.getNodeAt(new VertexLocation(new HexLocation(0,0),VertexDirection.NorthEast)),player1,player1.getColor());
        player1.addColony(localSettlement);
        player1.setResourceAggregation(new ResourcePile(1,1,1,1,1));
        for (int i=0;model.getPeripherals().getRobber().getHex()==null||model.getPeripherals().getRobber().getHex().getHexLocation().equals(new HexLocation(0,0));++i){
            model.getPeripherals().getRobber().setHex(model.getGameBoard().getHexes().get(i));
        }//This is a complex way of assigning the robber to some hex not 0,0
        assertTrue(ServerPlayingCommandFacade.canRobPlayer(params,model));//All is well. All is well.
        params.victimIndex=-1;
    }

    public void testRobPlayer(){
    }

    public void testCanEndTurn(){
    }

    public void testEndTurn(){
    }

    public void testCanBuyDevCard(){
    }

    public void testBuyDevCard(){
    }
}
