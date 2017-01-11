package model.peripherals;

import model.definitions.EnumConverter;
import model.definitions.PlayerState;
import model.definitions.ResourceHand;
import model.facade.shared.ModelReferenceFacade;
import model.gameboard.Hex;
import model.overhead.Model;
import model.player.Player;
import model.resources.CardBank;
import model.resources.DevelopmentCard;
import model.resources.ResourcePile;
import shared.definitions.DevCardType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by MTAYS on 9/23/2016.
 */
public class Peripherals implements Serializable {
    private TurnTracker turnTracker;
    private CardBank gameBank = new CardBank();
    private Robber robber;
    private LargestArmy largestArmy;
    private LongestRoad longestRoad;
    private MessageLog chatLog;
    private MessageLog systemLog;
    private int radius;
    private int winner;
    private TradeOffer activeTradeOffer;

    private static final int RESOURCECARDCOUNT=19;


    public Peripherals(){
        initializePeripherals();
    }

    public TurnTracker getTurnTracker() {return turnTracker;}
    public void setTurnTracker(TurnTracker turnTracker) {this.turnTracker = turnTracker;}
    public CardBank getGameBank() {return gameBank;}
    public void setGameBank(CardBank gameBank) {this.gameBank = gameBank;}
    public Robber getRobber() {return robber;}
    public void setRobber(Robber robber) {this.robber = robber;}
    public LargestArmy getLargestArmy() {return largestArmy;}
    public void setLargestArmy(LargestArmy largestArmy) {this.largestArmy = largestArmy;}
    public LongestRoad getLongestRoad() {return longestRoad;}
    public void setLongestRoad(LongestRoad longestRoad) {this.longestRoad = longestRoad;}
    public MessageLog getChatLog() {return chatLog;}
    public void setChatLog(MessageLog chatLog) {this.chatLog = chatLog;}
    public MessageLog getSystemLog() {return systemLog;}
    public void setSystemLog(MessageLog systemLog) {this.systemLog = systemLog;}
    public int getRadius(){return radius;}
    public void setRadius(int radius){this.radius=radius;}
    public int getWinner(){return winner;}
    public void setWinner(int winner){this.winner=winner;}
    public TradeOffer getActiveTradeOffer(){return activeTradeOffer;}
    public void setActiveTradeOffer(TradeOffer tradeOffer){this.activeTradeOffer=tradeOffer;}
    public PlayerState getCurrentState(){return turnTracker.getCurrentState();}
    public void setCurrentState(PlayerState state){turnTracker.setCurrentState(state);}

    public void logMessage(Player player,String message){
        if(systemLog==null)return;
        systemLog.addMessage(player,message);
    }
    public void sendChat(Player player,String message){
        if(chatLog==null)return;
        chatLog.addMessage(player,message);
    }
    public DevelopmentCard drawDevCard(){
        List<model.resources.DevelopmentCard> deck=gameBank.getDevelopmentCards();
        if(deck.size()<=0)return null;
        DevelopmentCard result = deck.get(0);
        deck.remove(0);
        return result;
    }
    public void moveRobber(Hex hex){
        robber.moveTo(hex);
    }
    public void shuffleDevelopmentCardDeck(){
        Collections.shuffle(gameBank.getDevelopmentCards(),new Random());
    }
    public void initializePeripherals(){
        turnTracker=new TurnTracker();
        initializeDevelopmentCardDeck();
        initializeResourceCardDeck();
        robber=new Robber();
        largestArmy=new LargestArmy();
        longestRoad=new LongestRoad();
        chatLog=new MessageLog();
        systemLog=new MessageLog();
        radius =3;
        winner=-1;
        activeTradeOffer=null;
    }
    public ResourcePile getResourcePile(){return gameBank.getResourcePile();}

    private void initializeDevelopmentCardDeck(){
        gameBank.setDevelopmentCards(new ArrayList<>());
        for(DevCardType devCard : DEVCARDDECK){
            gameBank.getDevelopmentCards().add(new DevelopmentCard(devCard));
        }
        shuffleDevelopmentCardDeck();
    }
    private void initializeResourceCardDeck(){
        gameBank.setResourcePile(new ResourcePile(RESOURCECARDCOUNT,RESOURCECARDCOUNT,RESOURCECARDCOUNT,RESOURCECARDCOUNT,RESOURCECARDCOUNT));
    }

    public String serialize(){
        /*
        Of note: Turntracker should be opened here, and then pull own Longest Road/Army and shove the rest to TurnTracker
        */
        return "";
    }
    public void initializeTurnTrackerData(int currentActiveID,String status, int longestRoad,int largestArmy, Model model){
        turnTracker=new TurnTracker();
        turnTracker.setActivePlayers(model.getPlayerCount());
        turnTracker.setActiveID(currentActiveID);
        turnTracker.setCurrentState(EnumConverter.PlayerState(status));
        if(longestRoad>-1)this.longestRoad.setPlayer(model.getPlayerByIndex(longestRoad));//Should access it internally... Could force Model to be passed to class
        else this.longestRoad.setPlayer(null);
        if(largestArmy>-1)this.largestArmy.setPlayer(model.getPlayerByIndex(largestArmy));
        else this.largestArmy.setPlayer(null);
    }
    public void initializeMiscData(int winnerID){//At the moment this is all I can think of
        winner=winnerID;
    }
    public void initializeTradeOfferData(int senderID, int receiverID, int brick, int ore, int sheep, int wheat, int wood){
        if(senderID>=0&&receiverID>=0){
            activeTradeOffer=new TradeOffer(ModelReferenceFacade.getModel().getPlayerByIndex(senderID),
                    ModelReferenceFacade.getModel().getPlayerByIndex(receiverID),
                    new ResourceHand(brick,ore,sheep,wheat,wood));
        }
    }
    public void initializeTradeOfferData(int senderID, int receiverID, ResourcePile resourcePile){
        if(senderID>=0&&receiverID>=0){
            activeTradeOffer=new TradeOffer(ModelReferenceFacade.getModel().getPlayerByIndex(senderID),
                    ModelReferenceFacade.getModel().getPlayerByIndex(receiverID),
                    new ResourceHand(resourcePile));
        }
    }
    public void initializeBankData(int brick,int ore,int sheep,int wheat,int wood){
        gameBank.setResourcePile(new ResourcePile(wood,brick,sheep,wheat,ore));
    }
    public void initializeBankData(ResourcePile resourcePile){
        gameBank.setResourcePile(resourcePile);
    }
    //For chat logs, either just add the messages in one at a time, or call clear and do so.
    public boolean equals(Peripherals peripherals){
        if(peripherals==null)return false;
        if(turnTracker!=null){
            if(!turnTracker.equals(peripherals.getTurnTracker()))return false;
        }
        else if(peripherals.getTurnTracker()!=null)return false;
        if(robber!=null){
            if(!robber.equals(peripherals.getRobber()))return false;
        }
        else if(peripherals.getRobber()!=null)return false;
        if(gameBank!=null){
            if(!gameBank.equals(peripherals.getGameBank()))return false;
        }
        else if(peripherals.getGameBank()!=null)return false;
        if(largestArmy!=null){
            if(!largestArmy.equals(peripherals.getLargestArmy()))return false;
        }
        else if(peripherals.getLargestArmy()!=null)return false;
        if(longestRoad!=null){
            if(!longestRoad.equals(peripherals.getLongestRoad()))return false;
        }
        else if(peripherals.getLongestRoad()!=null)return false;
        if(chatLog!=null){
            if(!chatLog.equals(peripherals.getChatLog()))return false;
        }
        else if(peripherals.getChatLog()!=null)return false;
        if(systemLog!=null){
            if(!systemLog.equals(peripherals.getSystemLog()))return false;
        }
        else if(peripherals.getSystemLog()!=null)return false;
        if(activeTradeOffer!=null){
            if(!activeTradeOffer.equals(peripherals.getActiveTradeOffer()))return false;
        }
        else if(peripherals.getActiveTradeOffer()!=null)return false;
        if(radius!=peripherals.getRadius())return false;
        if(winner!=peripherals.getWinner())return false;
        return true;
    }

    private static final DevCardType[] DEVCARDDECK={
            DevCardType.SOLDIER,//14
            DevCardType.SOLDIER,
            DevCardType.SOLDIER,
            DevCardType.SOLDIER,
            DevCardType.SOLDIER,
            DevCardType.SOLDIER,
            DevCardType.SOLDIER,
            DevCardType.SOLDIER,
            DevCardType.SOLDIER,
            DevCardType.SOLDIER,
            DevCardType.SOLDIER,
            DevCardType.SOLDIER,
            DevCardType.SOLDIER,
            DevCardType.SOLDIER,
            DevCardType.YEAR_OF_PLENTY,//2
            DevCardType.YEAR_OF_PLENTY,
            DevCardType.MONOPOLY,//2
            DevCardType.MONOPOLY,
            DevCardType.ROAD_BUILD,//2
            DevCardType.ROAD_BUILD,
            DevCardType.MONUMENT,//5
            DevCardType.MONUMENT,
            DevCardType.MONUMENT,
            DevCardType.MONUMENT,
            DevCardType.MONUMENT
    };
}
