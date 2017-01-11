package model.facade.client;

import model.definitions.PlayerState;
import model.definitions.ResourceHand;
import model.facade.server.ServerDevCardFacade;
import model.facade.server.ServerPlayingCommandFacade;
import model.facade.shared.ModelReferenceFacade;
import model.facade.shared.SerializeFacade;
import model.gameboard.Hex;
import server.HTTPServerProxy;
import server.IServer;
import params.*;
import shared.definitions.ResourceType;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import shared.locations.VertexLocation;

/**
 * Created by MTAYS on 9/21/2016.
 */
public class ClientPlayingCommandFacade {
    private static ClientPlayingCommandFacade instance;
    public static ClientPlayingCommandFacade getInstance(){
        if(instance==null)instance=new ClientPlayingCommandFacade();
        return instance;
    }
    private ClientPlayingCommandFacade(){
        //nothing
    }

    private static IServer server = HTTPServerProxy.getInstance();

    /**
     * @param placerID
     * 		- player placing piece
     * @param forFree
     * 		- check to see if resources should be paid
     * @pre
     * @post
     */
    public static boolean canBuildARoad(int placerID,boolean forFree){
        //I'm making this state deductive
        BuildRoadParams params = new BuildRoadParams();
        params.playerIndex=placerID;
        params.free=forFree;
        params.setupRound=false;
        if(ModelReferenceFacade.getModel()!=null&&ModelReferenceFacade.getModel().getPeripherals()!=null) {
            PlayerState gameState = ModelReferenceFacade.getModel().getPeripherals().getCurrentState();
            if(gameState==PlayerState.FirstRound||gameState==PlayerState.SecondRound)params.setupRound=true;
        }
        return ServerPlayingCommandFacade.canBuildARoad(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param placerID
     * 		- player placing piece
     * @param forFree
     * 		- check to see if resources should be paid
     * @param edge
     * 		- edge to be played on
     * @param setupRound
     * 		- indicates if the current placing is occurring during the setup
     * @pre
     * @post
     */
    public static boolean canPlaceRoad(int placerID,boolean forFree, EdgeLocation edge, boolean setupRound){
        BuildRoadParams params = new BuildRoadParams();
        params.playerIndex=placerID;
        params.setupRound=setupRound;
        params.free=forFree;
        params.roadLocation=edge;
        return ServerPlayingCommandFacade.canPlaceRoad(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param placerID
     * 		- player placing piece
     * @param forFree
     * 		- check to see if resources should be paid
     * @param edge
     * 		- edge to be played on
     * @param setupRound
     * 		- indicates if the current placing is occurring during the setup
     * @pre The road location is open
     * @pre The road location is connected to another road owned by the player
     * @pre The road location is not on water
     * @pre You have the required resources (1 wood, 1 brick; 1 road)
     * @pre Setup round: Must be placed by settlement owned by the player with no adjacent road
     * @post the required resources are spent
     * @post the road is on the map at the given location
     * @post give new "longest road" if applicable
     */
    public static boolean placeRoad(int placerID, boolean forFree, EdgeLocation edge, boolean setupRound){
        if(!canPlaceRoad(placerID,forFree,edge,setupRound)) return false;
        BuildRoadParams params = new BuildRoadParams();
        params.playerIndex=placerID;
        params.setupRound=setupRound;
        params.free=forFree;
        params.roadLocation=edge;
        String result = server.buildRoad(params);
        SerializeFacade.buildModelFromJSON(result);
       return true;
    }





    //Build settlement
    public static boolean canPlaceSettlement(int placer, boolean forFree, VertexLocation location, boolean setupRound){
        BuildSettlementParams params=new BuildSettlementParams();
        params.playerIndex=placer;
        params.free=forFree;
        params.vertexLocation=location;
        params.setupRound=setupRound;
        return ServerPlayingCommandFacade.canPlaceSettlement(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param placerID
     * 		- player placing piece
     * @param forFree
     * 		- check to see if resources should be paid
     * @pre
     * @post
     */
    public static boolean canBuildSettlement(int placerID, boolean forFree){
        BuildSettlementParams params=new BuildSettlementParams();
        params.setupRound=false;
        if(ModelReferenceFacade.getModel()!=null&&ModelReferenceFacade.getModel().getPeripherals()!=null) {
            PlayerState gameState = ModelReferenceFacade.getModel().getPeripherals().getCurrentState();
            if(gameState==PlayerState.FirstRound||gameState==PlayerState.SecondRound)params.setupRound=true;
        }
        params.playerIndex=placerID;
        params.free=forFree;
        return ServerPlayingCommandFacade.canBuildSettlement(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param placer
     * 		- player placing piece
     * @param forFree
     * 		- check to see if resources should be paid
     * @param location
     * 		- location to play piece
     * @param setupRound
     * 		- indicates if the current placing is occurring during the setup
     * @pre The settlement location is open
     * @pre The settlement location is not on water
     * @pre The settlement location is connected to one of your roads except during setup
     * @pre You have the required resources (1 wood, 1 brick, 1 wheat, 1 sheep; 1 settlement)
     * @pre The settlement cannot be placed adjacent to another settlement
     * @post You lost the resources required to build a settlement (1 wood, 1 brick, 1 wheat, 1 sheep; 1 settlement)
     * @post The settlement is on the map at the specified location
     */
    public static boolean placeSettlement(int placer, boolean forFree, VertexLocation location, boolean setupRound){
        if(!canPlaceSettlement(placer,forFree,location,setupRound))return false;
        BuildSettlementParams params=new BuildSettlementParams();
        params.playerIndex=placer;
        params.free=forFree;
        params.vertexLocation=location;
        params.setupRound=setupRound;
        String result =server.buildSettlement(params);
        SerializeFacade.buildModelFromJSON(result);
        return true;
    }



    //Build City111111111111111
    /**
     * @param placer
     * 		- player placing piece
     * @param location
     * 		- location to play piece
     * @pre
     * @post
     */
    public static boolean canPlaceCity(int placer, VertexLocation location){
        BuildCityParams params= new BuildCityParams();
        params.playerIndex=placer;
        params.vertexLocation=location;
        return ServerPlayingCommandFacade.canPlaceCity(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param placerID
     * 		- player placing piece
     * @pre
     * @post
     */
    public static boolean canBuildCity(int placerID){
        BuildCityParams params= new BuildCityParams();
        params.playerIndex=placerID;
        return ServerPlayingCommandFacade.canBuildCity(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param placer
     * 		- player placing piece
     * @param location
     * 		- location to play piece
     * @pre The city location is where you currently have a settlement
     * @pre You have the required resources (2 wheat, 3 ore; 1 city)
     * @post You lost the resources required to build a city (2 wheat, 3 ore; 1 city)
     * @post The city is on the map at the specified location
     * @post You got a settlement back

     */
    public static boolean buildCity(int placer, VertexLocation location){
        if(!canPlaceCity(placer,location))return false;
        BuildCityParams params= new BuildCityParams();
        params.playerIndex=placer;
        params.vertexLocation=location;
        String result = server.buildCity(params);
        SerializeFacade.buildModelFromJSON(result);
        return true;
    }



    //offer trade
    /**
     * @param offerer
     * 		- player offering trade
     * @param receiver
     * 		- player accepting trade
     * @param offer
     * 		- the proposed trade
     * @pre
     * @post
     */
    public static boolean canOfferTrade(int offerer,int receiver, ResourceHand offer){
        OfferTradeParams params=new OfferTradeParams();
        params.playerIndex=offerer;
        params.receiver=receiver;
        params.offer=offer;
        return ServerPlayingCommandFacade.canOfferTrade(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param offerer
     * 		- player offering trade
     * @param receiver
     * 		- player accepting trade
     * @param offer
     * 		- the proposed trade
     * @pre You have the resources you are offering
     * @post The trade is offered to the other player (stored in the server model)
     */
    public static boolean offerTrade(int offerer,int receiver, ResourceHand offer){
        if(!canOfferTrade(offerer,receiver,offer))return false;
        OfferTradeParams params=new OfferTradeParams();
        params.playerIndex=offerer;
        params.receiver=receiver;
        params.offer=offer;
        String result = server.offerTrade(params);
        SerializeFacade.buildModelFromJSON(result);
        return true;
    }

    //Maritime trade
    /**
     * @param player
     * 		- player making the trade
     * @param ratio
     * 		- the exchange rate
     * @param typeIn
     * 		- required resource
     * @param typeOut
     * 		- given resource
     * @pre
     * @post
     */
    public static boolean canMaritimeTrade(int player,int ratio, ResourceType typeIn, ResourceType typeOut){
        MaritimeTradeParams params = new MaritimeTradeParams();
        params.playerIndex=player;
        params.inputResource=typeIn;
        params.outputResource=typeOut;
        params.ratio=ratio;
        return ServerPlayingCommandFacade.canMaritimeTrade(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param player
     * 		- player making the trade
     * @param ratio
     * 		- the exchange rate
     * @param typeIn
     * 		- required resource
     * @param typeOut
     * 		- given resource
     * @pre You have the resources you are giving
     * @pre For ratios less than 4, you have the correct port for the trade
     * @post The trade has been executed (the offered resources are in the bank, and the requested resource has been received)
     */
    public static boolean maritimeTrade(int player,int ratio, ResourceType typeIn, ResourceType typeOut){
        if(!canMaritimeTrade(player,ratio,typeIn,typeOut))return false;
        MaritimeTradeParams params = new MaritimeTradeParams();
        params.playerIndex=player;
        params.inputResource=typeIn;
        params.outputResource=typeOut;
        params.ratio=ratio;
        String result = server.maritimeTrade(params);
        SerializeFacade.buildModelFromJSON(result);
        return true;
    }



    //Move Robber
    public static boolean canMoveRobber(HexLocation hexLoc){
        return ServerPlayingCommandFacade.canMoveRobberHere(hexLoc,ModelReferenceFacade.getModel());
    }

    public static boolean moveRobber(HexLocation hexLoc) {
        if(!canMoveRobber(hexLoc))return false;
        Hex proposedRobberHex = ModelReferenceFacade.getModel().getGameBoard().getHexAt(hexLoc);
        ModelReferenceFacade.getModel().getPeripherals().moveRobber(proposedRobberHex);
        return true;
    }

    //Rob Player
    /**
     * @param robbing
     * 		- player robbing
     * @param robbed
     * 		- player being robbed
     * @param newSpot
     * 		- new hex location for the robber
     * @pre
     * @post
     */
    public static boolean canRobPlayer(int robbing, int robbed, HexLocation newSpot){
        RobPlayerParams params=new RobPlayerParams();
        params.playerIndex=robbing;
        params.location=newSpot;
        params.victimIndex=robbed;
        return ServerPlayingCommandFacade.canRobPlayer(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param robbing
     * 		- player robbing
     * @param robbed
     * 		- player being robbed
     * @param newSpot
     * 		- new hex location for the robber
     * @pre The robber is not being kept in the same location
     * @pre If a player is being robbed (i.e., victimIndex != ­1), the player being robbed has resource cards
     * @post The robber is in the new location
     * @post The player being robbed (if any) gave you one of his resource cards (randomly selected)
     */
    public static boolean robPlayer(int robbing, int robbed, HexLocation newSpot){
        //if(!canRobPlayer(robbing,robbed,newSpot))return false;
        RobPlayerParams params=new RobPlayerParams();
        params.playerIndex=robbing;
        params.location=newSpot;
        params.victimIndex=robbed;
        String result = server.robPlayer(params);
        SerializeFacade.buildModelFromJSON(result);
        return true;
    }

    //finishTurn
    /**
     * @param player
     * 		- player to end their turn
     * @pre
     * @post
     */
    public static boolean canEndTurn(int player){
        FinishTurnParams params = new FinishTurnParams();
        params.playerIndex=player;
        return ServerPlayingCommandFacade.canEndTurn(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param player
     * 		- player to end their turn
     * @pre none
     * @post The cards in your new dev card hand have been transferred to your old dev card hand
     * @post It is the next player’s turn
     */
    public static boolean endTurn(int player){
        if(!canEndTurn(player))return false;
        FinishTurnParams params = new FinishTurnParams();
        params.playerIndex=player;
        String result = server.finishTurn(params);
        SerializeFacade.buildModelFromJSON(result);
        return true;
    }




    //buyDevCard
    /**
     * @param player
     * 		- buying player
     * @pre
     * @post
     */
    public static boolean canBuyDevCard(int player){
        BuyDevCardParams params = new BuyDevCardParams();
        params.playerIndex=player;
        return ServerPlayingCommandFacade.canBuyDevCard(params,ModelReferenceFacade.getModel());
    }
    public static boolean canUseAnyDevCard(int player){
        return ServerDevCardFacade.canUseSomeDevCard(player,ModelReferenceFacade.getModel());
    }
    /**
     * @param player
     * 		- buying player
     * @pre You have the required resources (1 ore, 1 wheat, 1 sheep)
     * @pre There are dev cards left in the deck
     * @post You have a new card
     * If it is a monument card, it has been added to your old devcard hand
     * If it is a non­monument card, it has been added to your new devcard hand (unplayable this turn)
     */
    public static boolean buyDevCard(int player){
        if(!canBuyDevCard(player))return false;
        BuyDevCardParams params = new BuyDevCardParams();
        params.playerIndex=player;
        String result = server.buyDevCard(params);
        SerializeFacade.buildModelFromJSON(result);
        return true;
    }
}
