package model.peripherals;

import model.player.Player;

import java.io.Serializable;

/**
 * Created by MTAYS on 9/21/2016.
 */
class AwardToken implements Serializable{
    private Player player;
    public AwardToken(){player=null;}
    public Player getPlayer(){return player;}
    public void setPlayer(Player player){this.player=player;}
    /**
     * @param player
     *  	- player to play the card
     * @pre valid player
     * @pre a new player has a larger stat then the player currently holding the token
     * @post the old player loses the token and two victory points
     * @post the new player gains the token and two victory points
     */
    public boolean moveToPlayer(Player player)throws Exception{
        if(this.player!=null){
            if(this.player==player){
                return false;
            }
            this.player.modifyVictoryPoints(-2);
        }
        this.player=player;
        if(player!=null){
            player.modifyVictoryPoints(2);
        }
        return true;
    }
    public boolean equals(AwardToken awardToken){
        if(awardToken==null)return false;
        if(player!=null){
            if(awardToken.getPlayer()!=null)return false;
            if(player.getIndex()!=awardToken.getPlayer().getIndex())return false;
        }
        if(awardToken.player!=null)return false;
        return true;
    }
}
