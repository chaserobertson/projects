package model.peripherals;

import debugger.Debugger;
import model.definitions.PlayerState;

import java.io.Serializable;

/**
 * Created by MTAYS on 9/19/2016.
 */
public class TurnTracker implements Serializable {
    private PlayerState currentState;
    /**
     * Description: current active ID
     */
    private int activeID;
    /**
     * Description: amount of players in game
     */
    private int activePlayers;
    private boolean onlyOnce=true;
    public TurnTracker(){this(0,0);}
    public TurnTracker(int activeID,int activePlayers){
        this.activeID=activeID;
        this.activePlayers=activePlayers;
        currentState=PlayerState.FirstRound;
        onlyOnce=true;
    }
    public int getActiveID(){return activeID;}
    public void setActiveID(int activeID){this.activeID=activeID;}
    public int getActivePlayers(){return activePlayers;}
    public void setActivePlayers(int activePlayers){this.activePlayers=activePlayers;}
    public PlayerState getCurrentState(){return currentState;}
    public void setCurrentState(PlayerState playerState){this.currentState=playerState;}
    /**
     * @pre none
     * @post active ID is cycled forward, wrapping to 0
     */
    public void advanceActiveID(){
        if(currentState!=PlayerState.SecondRound) {
            ++activeID;
            if(currentState!=PlayerState.FirstRound)currentState=PlayerState.Rolling;
            if (activeID >= activePlayers) {
                if (currentState == PlayerState.FirstRound&&onlyOnce) {
                    onlyOnce=false;
                    currentState = PlayerState.SecondRound;
                    activeID = activePlayers - 1;
                } else {
                    activeID = 0;
                }
            }
        }
        else{
            --activeID;
            if(activeID<0){
                currentState=PlayerState.Rolling;
                activeID=0;
            }
        }
    }
    /**
     * @pre none
     * @post turn number is advanced by one
     */
    public String serialize(){
        //TODO:implement
        return "";
    }
    public boolean equals(TurnTracker turnTracker){
        if(turnTracker==null)return false;
        if(currentState==null&&turnTracker.getCurrentState()!=null)return false;
        if(!currentState.equals(turnTracker.getCurrentState())){return false;}
        if(activeID!=turnTracker.activeID)return false;
        if(activePlayers!=turnTracker.getActivePlayers())return false;
        return true;
    }
}
