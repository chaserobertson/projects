package model.peripherals;

import model.definitions.ResourceHand;
import model.player.Player;

import java.io.Serializable;

/**
 * Created by MTAYS on 9/24/2016.
 */
public class TradeOffer implements Serializable {
    private Player offerer;
    private Player receiver;
    private ResourceHand offer;

    public TradeOffer(Player offerer, Player receiver, ResourceHand offer) {
        this.offerer = offerer;
        this.receiver = receiver;
        this.offer = offer;
    }

    public Player getOfferer() {return offerer;}
    public void setOfferer(Player offerer) {this.offerer = offerer;}
    public Player getReceiver() {return receiver;}
    public void setReceiver(Player receiver) {this.receiver = receiver;}
    public ResourceHand getOffer() {return offer;}
    public void setOffer(ResourceHand offer) {this.offer = offer;}
    public boolean equals(TradeOffer tradeOffer){
        if(tradeOffer==null)return false;
        if(offerer.getIndex()!=tradeOffer.getOfferer().getIndex())return false;
        if(receiver.getIndex()!=tradeOffer.getReceiver().getIndex())return false;
        if(!offer.equals(tradeOffer.getOffer()))return false;
        return true;
    }
}
