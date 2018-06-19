package model.facade.server;

import debugger.Debugger;
import model.definitions.PlayerState;
import model.definitions.ResourceHand;
import model.facade.AI.AICommands;
import model.overhead.Model;
import model.player.Player;
import model.resources.DevelopmentCard;
import params.AcceptTradeParams;
import params.DiscardCardsParams;
import params.RollNumberParams;
import params.SendChatParams;
import shared.definitions.DevCardType;
import shared.definitions.ResourceType;

/**
 * Created by MTAYS on 9/21/2016.
 */
public class ServerGeneralCommandFacade {
    private static ServerGeneralCommandFacade instance;
    public static ServerGeneralCommandFacade getInstance(){
        if(instance==null)instance=new ServerGeneralCommandFacade();
        return instance;
    }
    private ServerGeneralCommandFacade(){
        //nothing
    }
    //For all trade requests, the acceptor is GIVING AWAY the positive values of the ResourceHand
    /**
     * @param params
     *        -params of the operation
     *        @param model the model upon which the operation is to be performed
     * @pre two valid player numbers
     * @pre a valid resource hand
     * @post will return boolean declaring if the accepting player can make the trade
     */
    public static boolean canAcceptTrade(AcceptTradeParams params, Model model){
        if(model==null)return false;
        if(model.getPeripherals().getActiveTradeOffer()==null)return false;
        Player acceptorP=model.getPlayerByIndex(model.getPeripherals().getActiveTradeOffer().getReceiver().getIndex());//params.acceptor);
        //Player offerorP=model.getPlayerByIndex(params.offeror);
        Player offerorP=model.getPlayerByIndex(model.getPeripherals().getActiveTradeOffer().getOfferer().getIndex());
        if(model.getPeripherals().getActiveTradeOffer().getOfferer()!=offerorP||
                model.getPeripherals().getActiveTradeOffer().getReceiver()!=acceptorP)
            return false;
        return offerorP.getResourceAggregation().canTransferResourcesTo(acceptorP.getResourceAggregation(),model.getTradeOffer().getOffer());
        //return acceptorP.getResourceAggregation().canTransferResourcesTo(offerorP.getResourceAggregation(),model.getTradeOffer().getOffer());
    }

    /**
     * @param params
     * 		- parameters of the operation
     * @param model the model upon which the operation is to be performed
     * @pre a domestic trad has been offered
     * @pre To accept the offered trade, you have the required resources
     * @post If you accepted, you and the player who offered swap the specified resources
     * @post If you declined no resources are exchanged
     * @post The trade offer is removed
     * @return true if trade accepted, false otherwise
     */
    public static boolean acceptTrade(AcceptTradeParams params,Model model){//This player has been offered a trade and attempts to accept. Return true if he is capable.
        if(model==null)return false;
        if(params.willAccept&&!canAcceptTrade(params,model)){
            model.getPeripherals().setActiveTradeOffer(null);
            model.incrementVersion();
            return false;
        }
        if(model.getPeripherals().getActiveTradeOffer()==null)return false;
        Player acceptorP = model.getPlayerByIndex(model.getPeripherals().getActiveTradeOffer().getReceiver().getIndex());//model.getPlayerByIndex(params.acceptor);
        Player offerorP = model.getPlayerByIndex(model.getPeripherals().getActiveTradeOffer().getOfferer().getIndex());
        if(acceptorP==null||offerorP==null){
            if(acceptorP==null)Debugger.LogMessage("Acceptor was null");
            if(offerorP==null)Debugger.LogMessage("Offerer was null");
            return false;
        }
        if(params.willAccept) {
            ResourceHand offer = model.getTradeOffer().getOffer();
            if(offer==null){
                Debugger.LogMessage("Offer was null");
                return false;
            }
            if(offerorP.getResourceAggregation().transferResourcesTo(acceptorP.getResourceAggregation(),offer)){
            }
        }
        model.setTradeOffer(null);
        model.incrementVersion();
        if(model.getPeripherals()!=null){
            if(params.willAccept)model.getPeripherals().logMessage(acceptorP,acceptorP.getNickname()+" has accepted the trade");
            else model.getPeripherals().logMessage(acceptorP,acceptorP.getNickname()+" has rejected the trade");
        }
        return true;
    }

    /**
     * @param params
     *      -parameters of the cards discarded
     * @param model the model upon which the operation is to be performed
     * @pre 8 or more cards in hand, status is discarding, have the cards referenced
     * @post will declare whether discarding is required
     */
    public static boolean canDiscardCards(DiscardCardsParams params,Model model){
        if(model==null)return false;
        Player discarder=model.getPlayerByIndex(params.playerIndex);
        if(
                model.getPeripherals().getCurrentState()!=PlayerState.Discarding||
                discarder.sumOfResourceCards()<=7||
                !discarder.getResourceAggregation().canTransferResourcesTo(model.getResourceAggregation(), params.discardedCards)
                )return false;
        return true;
    }
    /**
     * @param params
     *      -parameters of discard cards
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre status of client model is "Discarding"
     * @pre player has more than 7 cards
     * @pre the cards being chosen to discard are held by the player
     * @post the specified resources will be removed
     * @post the last on to discard will change the client model to "Robbing"
     */
    public static boolean discardCards(DiscardCardsParams params,Model model){
        if(model==null)return false;
        Player discarder=model.getPlayerByIndex(params.playerIndex);
        if(!canDiscardCards(params,model))return false;
        if(!discarder.getResourceAggregation().transferResourcesTo(model.getResourceAggregation(), params.discardedCards)){
            return false;
        }
        discarder.setHasDiscarded(true);
        model.getPeripherals().setCurrentState(PlayerState.Robbing);
        for(Player player:model.getPlayers()){
            if(!player.getHasDiscarded()&&player.getInternetID()>-10&&player.sumOfResourceCards()>7){
                model.getPeripherals().setCurrentState(PlayerState.Discarding);
                break;
            }
        }
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(discarder,discarder.getNickname()+" has discarded");
        if(model.getPlayerByIndex(model.getPeripherals().getTurnTracker().getActiveID()).getInternetID()<-10){
            AICommands.AIRob(model.getPlayerByIndex(model.getPeripherals().getTurnTracker().getActiveID()),model,params.gameId);
        }

        model.incrementVersion();
        return true;
    }




    /**
     * @param params
     * 		- params of the roll
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre valid player
     * @post if the player's turn and hasn't rolled, will be able to roll
     */
    public static boolean canRoll(RollNumberParams params,Model model){
        if(model==null)return false;
        Player roller = model.getPlayerByIndex(params.playerIndex);
        if(roller==null) {
            return false;
        }
        if(model.getPeripherals().getCurrentState()!= PlayerState.Rolling||
                params.playerIndex!=model.getPeripherals().getTurnTracker().getActiveID())
            return false;
        return true;
    }
    /**
     * @param params
     * 		- parameters of the roll that was executed
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre it is the player's turn
     * @pre the client model's status is rolling
     * @post the client's model is now in "Discarding", "Robbing", or "Playing"
     */
    public static boolean roll(RollNumberParams params,Model model){
        if(model==null)return false;
        if(!canRoll(params,model))return false;
        int rollResult=params.number;
        Player player=model.getPlayerByIndex(params.playerIndex);
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(player,player.getNickname()+" has rolled a "+Integer.toString(rollResult));
        if(rollResult==7){
            model.getPeripherals().setCurrentState(PlayerState.Robbing);//If someone needs to discard it will be changed
            for(Player playerTemp:model.getPlayers()){
                if(playerTemp.sumOfResourceCards()>7){
                    if(playerTemp.getInternetID()<-10){
                        if(AICommands.AIDiscard(playerTemp,model,params.gameId)) {
                            if (model.getPeripherals() != null)
                                model.getPeripherals().logMessage(playerTemp, playerTemp.getNickname() + " has discarded");
                        }
                    }
                    else{
                        model.getPeripherals().setCurrentState(PlayerState.Discarding);
                    }
                }
            }
        }
        else{
            model.getGameBoard().distributeResources(rollResult,model);
            model.getPeripherals().setCurrentState(PlayerState.Playing);
        }
        if(model.getPeripherals().getCurrentState()==PlayerState.Robbing&&player.getInternetID()<-10){//AI CODE
            AICommands.AIRob(player,model,params.gameId);
           return true;
        }
        model.incrementVersion();
        return true;
    }

    public static boolean sendChat(SendChatParams params,Model model) {
        if(model==null||model.getPeripherals()==null)return false;
        if(true)cheat(params,model);
        model.getPeripherals().getChatLog().addMessage(model.getPlayerByIndex(params.playerIndex),params.content);
        model.incrementVersion();
        return true;
    }
    private static void cheat(SendChatParams params,Model model){
        if(model==null)return;
        if(params==null)return;
        if(params.content.charAt(0)=='-'){//Chat message is potentially a cheat!
            String message=params.content;
            if(message.length()>5){//Must contain more than -cwmt
                String temp = message.substring(1,5);
                if(temp.equals("cwmt")){//Confirm this is a legit cheat
                    message=message.substring(5);
                    while(message.length()>1&&message.charAt(0)==' ')message=message.substring(1);
                    if(message.length()>2){
                        temp=message.substring(0,2);
                        if(temp.equals("gr")) {//Give Resource cheat
                            message = message.substring(2);
                            giveResourceCheat(params.playerIndex,message,model);
                        }
                        else if(temp.equals("dr")&&message.length()>3){//"dar" resource cheat (give to other player)
                            if(Character.isDigit(message.charAt(2))){
                                int extract=0;
                                try{
                                    temp=message.substring(2,3);
                                    message=message.substring(3);
                                    extract=Integer.parseInt(temp);
                                    giveResourceCheat(extract,message,model);
                                }catch (Exception e){
                                    Debugger.LogMessage("error");
                                    return;
                                }
                            }
                        }
                        else if(temp.equals("gd")){
                            giveDevelopmentCheat(params.playerIndex,message.substring(2),model);
                        }
                        else if(temp.equals("tt")){
                            testSomethingCheat(params.playerIndex,message.substring(2),model);
                        }
                        else return;
                        Player player=model.getPlayerByIndex(params.playerIndex);
                        if(player==null)return;
                        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(player,player.getNickname()+" has cheated!");
                    }
                }
            }
        }

    }
    private static void giveDevelopmentCheat(int playerIndex, String message, Model model){
        if(model==null)return;
        Player player=model.getPlayerByIndex(playerIndex);
        if(player==null)return;
        message=message.replaceAll("\\s+","");
        DevelopmentCard developmentCard;
        switch (message){
            case("so"):
                developmentCard=new DevelopmentCard(DevCardType.SOLDIER);
                break;
            case "yp":
                developmentCard=new DevelopmentCard(DevCardType.YEAR_OF_PLENTY);
                break;
            case "mo":
                developmentCard=new DevelopmentCard(DevCardType.MONOPOLY);
                break;
            case "rb":
                developmentCard=new DevelopmentCard(DevCardType.ROAD_BUILD);
                break;
            case "vp":
                developmentCard=new DevelopmentCard(DevCardType.MONUMENT);
                break;
            default:
                Debugger.LogMessage("Not recognized as a devCard "+message);
                return;
        }
        player.addDevelopmentCard(developmentCard);
    }
    private static void giveResourceCheat(int playerIndex, String message,Model model){
        int i=0;
        StringBuilder numberCruncher=new StringBuilder();
        ResourceHand result=new ResourceHand(0,0,0,0,0);
        while(i< ResourceType.numberOfTypes()&&message.length()>0){
            if(message.charAt(0)==' '){
                if(numberCruncher.length()>0){
                    try{
                        int extract=Integer.parseInt(numberCruncher.toString());
                        result.setResource(i,extract);
                        numberCruncher=new StringBuilder();
                        ++i;
                    }catch (Exception e){
                        return;
                    }
                }
            }
            else{
                if(Character.isDigit(message.charAt(0))){
                    numberCruncher.append(message.charAt(0));
                }
                else{
                    return;
                }
            }
            if(numberCruncher.length()>0&&i<ResourceType.numberOfTypes()){
                int extract=Integer.parseInt(numberCruncher.toString());
                result.setResource(i,extract);
            }
            message=message.substring(1);
        }
        Player player=model.getPlayerByIndex(playerIndex);
        if(player==null)return;
        player.getResourceAggregation().addResources(result);
    }
    private static void testSomethingCheat(int playerIndex, String message, Model model){
        Debugger.LogMessage("testSomethingCheat has been activated.");
        switch (message){
            default:Debugger.LogMessage("TestSomethingCheat does not recognize input");
        }

        Debugger.LogMessage("testSomethingCheat has finished");
    }
}
