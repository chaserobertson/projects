package model.resources;

import model.definitions.EnumConverter;
import shared.definitions.DevCardType;

import java.io.Serializable;
import java.util.*;

/**
 * Created by MTAYS on 9/21/2016.
 */
public class CardBank implements Serializable {
    private ResourcePile resourcePile;
    private List<DevelopmentCard> developmentCards;

    public CardBank(){
        resourcePile=new ResourcePile();
        developmentCards=new ArrayList<>();
    }
    public CardBank(ResourcePile resourcePile, List<DevelopmentCard> developmentCards){
        this.resourcePile=resourcePile;
        this.developmentCards=developmentCards;
    }
    public CardBank(int wood, int brick, int sheep, int wheat, int ore, DevelopmentCard... developmentCards){
        initialize(wood,brick,sheep,wheat,ore,developmentCards);
    }
    public CardBank(int wood, int brick, int sheep, int wheat, int ore, DevCardType... devCardTypes){
        DevelopmentCard[] developmentCards=new DevelopmentCard[devCardTypes.length];
        for (int i=0;i<devCardTypes.length;++i) {
            developmentCards[i]=new DevelopmentCard(devCardTypes[i]);
        }
        initialize(wood,brick,sheep,wheat,ore,developmentCards);
    }
    private void initialize(int wood, int brick, int sheep, int wheat, int ore, DevelopmentCard... developmentCards){
        resourcePile=new ResourcePile(wood,brick,sheep,wheat,ore);
        this.developmentCards=new ArrayList<>(developmentCards.length);
        for (DevelopmentCard developmentCard: developmentCards) {
            this.developmentCards.add(developmentCard);
        }
    }
    public ResourcePile getResourcePile(){return resourcePile;}
    public void setResourcePile(ResourcePile resourcePile){this.resourcePile=resourcePile;}
    public List<DevelopmentCard> getDevelopmentCards(){return developmentCards;}
    public void setDevelopmentCards(List<DevelopmentCard> developmentCards){this.developmentCards=developmentCards;}
    /**
     * @pre none
     * @post creates a valid JSON string
     * @return valid JSON string
     */
    public String serialize(){
        //TODO:implement
        return "";
    }
    public boolean equals(CardBank cardBank){
        if(cardBank==null)return false;
        if(developmentCards.size()!=cardBank.getDevelopmentCards().size())return false;
        if(resourcePile!=null) {
            if (!resourcePile.equals(cardBank.getResourcePile())) return false;
        }
        else if(cardBank.getResourcePile()!=null)return false;
        int[] devCounts=new int[5];
        for(int i=0;i<developmentCards.size();++i){
            devCounts[EnumConverter.DevCardType(developmentCards.get(i).getType())]++;
        }
        int[] devCounts2=new int[5];
        for(int i=0;i<cardBank.getDevelopmentCards().size();++i){
            devCounts2[EnumConverter.DevCardType(cardBank.getDevelopmentCards().get(i).getType())]++;
        }
        for(int i=0;i<5;++i){
            if(devCounts[i]!=devCounts2[i])return false;
        }
        return true;
    }
}
