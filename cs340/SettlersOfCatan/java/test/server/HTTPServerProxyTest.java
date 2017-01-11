package server;

import junit.framework.TestCase;
import model.definitions.ResourceHand;
import params.*;
import shared.definitions.ResourceType;
import shared.locations.*;

/**
 * Created by tsmit on 9/28/2016.
 */
public class HTTPServerProxyTest extends TestCase {

    private static HTTPServerProxy httpServerProxy = HTTPServerProxy.getInstance();

    private final String testString = "unsuccessful";
    private Credentials credentials = new Credentials("dill","pickle");
    private String newGame = "{\"title\":\"test\",\"id\":3,\"players\":[{},{},{},{}]}";
    private CreateGameRequest createGameRequest = new CreateGameRequest(true, true, true, "test");
    private JoinGameRequest joinGameRequest = new JoinGameRequest(0,"blue");
    private AddAIRequest addAIRequest = new AddAIRequest();
    private Version version = new Version(1);
    private VertexLocation vertexLocation = new VertexLocation(new HexLocation(0,0), VertexDirection.East);
    private EdgeLocation edgeLocation1 = new EdgeLocation(new HexLocation(0,0),EdgeDirection.NorthEast);
    private EdgeLocation edgeLocation2 = new EdgeLocation(new HexLocation(0,0),EdgeDirection.NorthWest);
    private ResourceHand resourceHand = new ResourceHand(1,1,1,1,1);

    private SendChatParams sendChatParams = new SendChatParams(0,"string");
    private RollNumberParams rollNumberParams = new RollNumberParams(0);
    private RobPlayerParams robPlayerParams = new RobPlayerParams(0,-1,new HexLocation(0,0));
    private FinishTurnParams finishTurnParams = new FinishTurnParams(0);
    private BuyDevCardParams buyDevCardParams = new BuyDevCardParams(0);
    private YearOfPlentyParams yearOfPlentyParams = new YearOfPlentyParams(0, ResourceType.BRICK,ResourceType.ORE);
    private RoadBuildingParams roadBuildingParams = new RoadBuildingParams(0, edgeLocation1,edgeLocation2);
    private SoldierParams soldierParams = new SoldierParams(0,0,new HexLocation(0,0));
    private MonopolyParams monopolyParams = new MonopolyParams(0,ResourceType.BRICK);
    private MonumentParams monumentParams = new MonumentParams(0);
    private BuildRoadParams buildRoadParams = new BuildRoadParams(0,edgeLocation1,true,true);
    private BuildSettlementParams buildSettlementParams = new BuildSettlementParams(vertexLocation, true, true);
    private BuildCityParams buildCityParams = new BuildCityParams(0,vertexLocation);
    private OfferTradeParams offerTradeParams = new OfferTradeParams(0,resourceHand,1);
    private AcceptTradeParams acceptTradeParams = new AcceptTradeParams(1,0,false);
    private MaritimeTradeParams maritimeTradeParams = new MaritimeTradeParams(0,4,ResourceType.BRICK,ResourceType.BRICK);
    private DiscardCardsParams discardCardsParams = new DiscardCardsParams(0, resourceHand);

    /**
     * tests all non-trivial server requests against testString
     * testString is "unsuccessful"
     * server requests return unsuccessful when 400 response
     */
    public void testHttpServerProxy() {
        assertEquals(-1,httpServerProxy.getPlayerID());
        //test user/
        assertNotSame(testString, httpServerProxy.userRegister(credentials));
        assertNotSame(testString, httpServerProxy.userLogin(credentials));
        assertEquals(0,httpServerProxy.getPlayerID());
        //test games/
        assertNotSame(testString, httpServerProxy.gameList());
        assertNotSame(testString, httpServerProxy.gameCreate(createGameRequest));
        assertNotSame(testString, httpServerProxy.gameJoin(joinGameRequest));
        //test game/
        assertNotSame(testString, httpServerProxy.gameModel(version));
        assertNotSame(testString, httpServerProxy.gameAddAI(addAIRequest));
        httpServerProxy.gameAddAI(addAIRequest);
        httpServerProxy.gameAddAI(addAIRequest);
        assertNotSame(testString, httpServerProxy.gameListAI());
        //test moves/
        assertNotSame(testString, httpServerProxy.sendChat(sendChatParams));
        assertNotSame(testString, httpServerProxy.rollNumber(rollNumberParams));
        assertNotSame(testString, httpServerProxy.robPlayer(robPlayerParams));
        assertNotSame(testString, httpServerProxy.finishTurn(finishTurnParams));
        assertNotSame(testString, httpServerProxy.buyDevCard(buyDevCardParams));
        assertNotSame(testString, httpServerProxy.playCardYearOfPlenty(yearOfPlentyParams));
        assertNotSame(testString, httpServerProxy.playCardRoadBuilding(roadBuildingParams));
        assertNotSame(testString, httpServerProxy.playCardSoldier(soldierParams));
        assertNotSame(testString, httpServerProxy.playCardMonopoly(monopolyParams));
        assertNotSame(testString, httpServerProxy.playCardMonument(monumentParams));
        assertNotSame(testString, httpServerProxy.buildRoad(buildRoadParams));
        assertNotSame(testString, httpServerProxy.buildSettlement(buildSettlementParams));
        assertNotSame(testString, httpServerProxy.buildCity(buildCityParams));
        assertNotSame(testString, httpServerProxy.offerTrade(offerTradeParams));
        assertNotSame(testString, httpServerProxy.acceptTrade(acceptTradeParams));
        assertNotSame(testString, httpServerProxy.maritimeTrade(maritimeTradeParams));
        assertNotSame(testString, httpServerProxy.discardCards(discardCardsParams));
    }
}
