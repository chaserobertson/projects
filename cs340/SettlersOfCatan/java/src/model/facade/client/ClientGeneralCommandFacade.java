package model.facade.client;

import model.definitions.ResourceHand;
import model.facade.server.ServerGeneralCommandFacade;
import model.facade.shared.ModelReferenceFacade;
import model.facade.shared.SerializeFacade;
import model.overhead.Model;
import model.serialization.Deserializer;
import server.HTTPServerProxy;
import server.IServer;
import params.AcceptTradeParams;
import params.DiscardCardsParams;
import params.RollNumberParams;
import params.SendChatParams;

/**
 * Created by MTAYS on 9/21/2016.
 */
public class ClientGeneralCommandFacade {
    private static ClientGeneralCommandFacade instance;
    public static ClientGeneralCommandFacade getInstance(){
        if(instance==null)instance=new ClientGeneralCommandFacade();
        return instance;
    }
    private ClientGeneralCommandFacade(){
        //nothing
    }

    private static IServer server = HTTPServerProxy.getInstance();

    //For all trade requests, the acceptor is GIVING AWAY the positive values of the ResourceHand
    /**
     * @param acceptor
     * 		- the player who determines to accept the trade
     * @param offeror
     * 		- the player offering the trade
     * @pre two valid player numbers
     * @pre a valid resource hand
     * @post will return boolean declaring if the accepting player can make the trade
     */
    public static boolean canAcceptTrade(int acceptor, int offeror){
        AcceptTradeParams params = new AcceptTradeParams();
        params.acceptor=acceptor;
        params.offeror=offeror;
        return ServerGeneralCommandFacade.canAcceptTrade(params,ModelReferenceFacade.getModel());
    }

    /**
     * @param acceptor
     * 		- the player who determines to accept the trade
     * @param offeror
     * 		- the player offering the trade
     * @param trade
     * 		- the trade being offered
     * @pre a domestic trad has been offered
     * @pre To accept the offered trade, you have the required resources
     * @post If you accepted, you and the player who offered swap the specified resources
     * @post If you declined no resources are exchanged
     * @post The trade offer is removed
     * @return true if trade accepted, false otherwise
     */
    public static boolean acceptTrade(int acceptor, int offeror, ResourceHand trade ,boolean tradeAccepted){//This player has been offered a trade and attempts to accept. Return true if he is capable.
        if(!canAcceptTrade(acceptor,offeror)) return false;
        AcceptTradeParams params = new AcceptTradeParams();
        params.acceptor = acceptor;
        params.offeror = offeror;
        params.willAccept = tradeAccepted;
        String result = server.acceptTrade(params);
        SerializeFacade.buildModelFromJSON(result);
        return true;
    }

    /**
     * @param discarderID
     * 		- player discarding cards
     * @param discards
     * 		- cards being discarded
     * @pre valid inputs
     * @post will declare whether discarding is required
     */
    public static boolean canDiscardCards(int discarderID,ResourceHand discards){
        DiscardCardsParams params = new DiscardCardsParams();
        params.playerIndex=discarderID;
        params.discardedCards=discards;
        return ServerGeneralCommandFacade.canDiscardCards(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param discarderID
     * 		- player discarding cards
     * @param discards
     * 		- cards being discarded
     * @pre status of client model is "Discarding"
     * @pre player has more than 7 cards
     * @pre the cards being chosen to discard are held by the player
     * @post the specified resources will be removed
     * @post the last on to discard will change the client model to "Robbing"
     */
    public static boolean discardCards(int discarderID,ResourceHand discards){
        if(!canDiscardCards(discarderID,discards))return false;
        DiscardCardsParams params = new DiscardCardsParams();
        params.playerIndex=discarderID;
        params.discardedCards=discards;
        String result = server.discardCards(params);
        SerializeFacade.buildModelFromJSON(result);
        return true;
    }




    /**
     * @param rollerID
     * 		- player rolling
     * @pre valid player
     * @post if the player's turn and hasn't rolled, will be able to roll
     */
    public static boolean canRoll(int rollerID){
        RollNumberParams params = new RollNumberParams();
        params.playerIndex = rollerID;
        return ServerGeneralCommandFacade.canRoll(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param rollerID
     * 		- player rolling
     * @pre it is the player's turn
     * @pre the client model's status is rolling
     * @post the client's model is now in "Discarding", "Robbing", or "Playing"
     */
    public static boolean roll(int rollerID){
        if(!canRoll(rollerID))return false;
        RollNumberParams params = new RollNumberParams();
        params.playerIndex = rollerID;
        String result = server.rollNumber(params);
        SerializeFacade.buildModelFromJSON(result);
        return true;
    }

    public static boolean sendChat(int playerID,String chat) {
        SendChatParams params=new SendChatParams();
        params.playerIndex=playerID;
        params.content=chat;
        String newModelJson=server.sendChat(params);
        Model model= Deserializer.deserialize(newModelJson);
        ModelReferenceFacade.getInstance().setmodel(model);
        return true;
    }
}
