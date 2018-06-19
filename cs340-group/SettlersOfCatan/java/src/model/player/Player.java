package model.player;

import java.io.Serializable;
import java.util.*;

import client.data.PlayerInfo;
import debugger.Debugger;
import model.definitions.EnumConverter;
import model.facade.shared.ModelReferenceFacade;
import model.resources.DevelopmentCard;
import model.resources.CardBank;
import model.resources.ResourcePile;
import model.structures.Colony;
import model.structures.Road;
import shared.definitions.*;
import shared.locations.EdgeDirection;

public class Player implements Serializable{
	/**
	 * Description: Exception in Victory Points
	 */
	public class VictoryPointsException extends Exception {
    		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

			public VictoryPointsException(String message) {
        		super(message);
    		}
	}
	/**
	 * Description: index of the player, associated with turn order
	 */
	private int index;
	/**
	 * Description: visible name of player
	 */
	private String nickname;
	/**
	 * Description: color of player
	 */
	private CatanColor color;
	/**
	 * Description: Victory point aggregate
	 */
	private int victoryPoints;
	/**
	 * Description: all urban structures of the player
	 */
	private List<Colony> ownedColonies;
	/**
	 * Description: all roads of the player
	 */
	private List<Road> ownedRoads;
	/**
	 * Description: resources of the player
	 */
	//private ResourcePile resourceAggregation;
	/**
	 * Description: all developmentCards in the old player hand
	 */
	//private List<DevelopmentCard> developmentCards;
	private CardBank personalBank;
	/**
	 * Description: all development cards in the new player hand
	 */
	private List<DevelopmentCard> newDevelopmentCards;
	/**
	 * Description: current amount of army cards played
	 */
	private int currentArmySize;
	/**
	 * Description: whether the player has played a dev card this turn
	 */
	private boolean hasPlayedDev;
	private boolean hasDiscarded;
	private int remainingRoads;
	private int remainingSettlements;
	private int remainingCities;
	private int playedMonuments;
	private int internetID;
	/**
	 * Description: amount of roads possible per player in traditional game
	 */
	private static final int maxRoads=15;
	/**
	 * Description: amount of settlements possible per player in traditional game
	 */
	private static final int maxSettlements=5;
	/**
	 * Description: amount of cities possible per player in traditional game
	 */
	private static final int maxCities=4;

	public int getIndex(){return index;}
	public void setIndex(int id){this.index =id;}
	public String getNickname(){return nickname;}
	public void setNickname(String nickname){this.nickname=nickname;}
	public CatanColor getColor(){return color;}
	public void setColor(CatanColor color){this.color=color;}
	public int getVictoryPoints(){return victoryPoints;}
	public void setVictoryPoints(int victoryPoints){this.victoryPoints=victoryPoints;}
	public List<Colony> getOwnedColonies(){return ownedColonies;}
	public void setOwnedColonies(List<Colony> colonyList){this.ownedColonies = colonyList;}
	public List<Road> getOwnedRoad(){return ownedRoads;}
	public void setOwnedRoad(List<Road> roadList){this.ownedRoads=roadList;}
	public CardBank getPersonalBank(){return personalBank;}
	public void setPersonalBank(CardBank bank){this.personalBank=bank;}
	public ResourcePile getResourceAggregation(){return personalBank.getResourcePile();}
	public void setResourceAggregation(ResourcePile resourceAggregation){personalBank.setResourcePile(resourceAggregation);}
	public List<DevelopmentCard> getDevelopmentCards(){return personalBank.getDevelopmentCards();}
	public void setDevelopmentCards(List<DevelopmentCard> developmentCards){personalBank.setDevelopmentCards(developmentCards);}
	public List<DevelopmentCard> getNewDevelopmentCards(){return newDevelopmentCards;}
	public void setNewDevelopmentCards(List<DevelopmentCard> developmentCards){newDevelopmentCards=developmentCards;}
	public boolean getHasPlayedDev(){return hasPlayedDev;}
	public void setHasPlayedDev(boolean state){hasPlayedDev=state;}
	public boolean getHasDiscarded(){return hasDiscarded;}
	public void setHasDiscarded(boolean state){hasDiscarded=state;}
	public void setRemainingRoads(int roads){this.remainingRoads=roads;}
	public void setRemainingSettlements(int settlements){this.remainingSettlements=settlements;}
	public void setRemainingCities(int cities){this.remainingCities=cities;}
	public int getPlayedMonuments(){return playedMonuments;}
	public void addMonument(){++playedMonuments;}
	public void setPlayedMonuments(int monuments){playedMonuments=monuments;}
	public int getInternetID(){return internetID;}
	public void setInternetID(int id){internetID=id;}

	/**
	 * @param index
	 * 		-the index of the player
	 * @param nickname
	 * 		-the nickname of the player;
	 * @param color
	 * 		-the color of the player
	 * @pre color is a valid CatanColor
	 * @post a new player object is formed with the specified parameters. It has no resources, no development cards, and is in a waiting state
	 */
	public Player(int index, String nickname, CatanColor color){
		this.index = index;
		this.nickname=nickname;
		this.color=color;
		victoryPoints=0;
		ownedColonies =new ArrayList();
		ownedRoads=new ArrayList();
		personalBank=new CardBank();
		newDevelopmentCards=new ArrayList<>();
		hasPlayedDev=false;
		currentArmySize=0;
		remainingRoads=maxRoads;
		remainingCities=maxCities;
		remainingSettlements=maxSettlements;
		playedMonuments=0;
		internetID=-1;
	}
	public Player(int index,String nickname,CatanColor color, int internetID){
		this(index,nickname,color);
		this.internetID=internetID;
	}
	public Player(int soldiers, int victoryPoints, int monuments, boolean hasPlayedDev,
				  boolean hasDiscarded, int index, String name, String color,int internetID,
				  int remainingCities,int remainingRoads,int remainingSettlements){
		ownedColonies =new ArrayList();
		ownedRoads=new ArrayList();
		newDevelopmentCards=new ArrayList<>();
		personalBank=new CardBank();
		initializePlayerFromData(soldiers,victoryPoints,monuments,hasPlayedDev,hasDiscarded,index,name,color,internetID,remainingCities,remainingRoads,remainingSettlements);
	}
	/**
	 * @param change
	 * 		-the change in value of player's victory points
	 * @pre the change does not bring the player's points below 0
	 * @post player's victoryPoints are modified appropriately
	 */
	public void modifyVictoryPoints(int change)throws Exception{//return true if points total 10 or more. I don't reccomend relying on this.
		victoryPoints+=change;
		if(victoryPoints<0)throw new VictoryPointsException(Integer.toString(victoryPoints));
	}

	//DevelopmentCard functions
	/**
	 * @param developmentCard
	 * 		-a developmentCard of the same type of the one to be removed from old card list
	 * @pre developmentCard is not null
	 * @post if the player has the card it is remove
	 * @return true if succesfully removed, false otherwise
	 */
	public boolean removeDevelopmentCard(DevelopmentCard developmentCard){
		List<DevelopmentCard> developmentCards=personalBank.getDevelopmentCards();
		for(int i=0;i<developmentCards.size();++i){
			if(developmentCards.get(i).getType()==developmentCard.getType()){
				developmentCards.remove(i);
				return true;
			}	
		}
		return false;
	}
	/**
	 * @param index
	 * 		-the index of the card to be removed from the player's old card list
	 * @pre index is in bounds
	 * @post card is removed
	 */
	public void removeDevelopmentCard(int index)throws ArrayIndexOutOfBoundsException{
		List<DevelopmentCard> developmentCards = personalBank.getDevelopmentCards();
		if(index>=developmentCards.size()||index<0)throw new ArrayIndexOutOfBoundsException();
		developmentCards.remove(index);
	}
	/**
	 * @param developmentCard
	 * 		-developmentcard to be added to the player's old card list
	 * @pre developmentCard is not null
	 * @post developmentCard is added to the old card list
	 */
	public void addDevelopmentCard(DevelopmentCard developmentCard){
		personalBank.getDevelopmentCards().add(developmentCard);
	}
	/**
	 * @param developmentCard
	 * 		-developmentCard to be added to the Player's new card list
	 * @pre developmentCard is not Null
	 * @post developmentCard is added to the new Card list
	 */
	public void addNewDevelopmentCard(DevelopmentCard developmentCard){
		newDevelopmentCards.add(developmentCard);
	}
	/**
	 * @pre none
	 * @post all cards in new DevelopmentCard's list are added to the old Card list and removed from new
	 */
	public void transferNewDevelopmentIntoOld(){
		for(int i=0;i<newDevelopmentCards.size();++i){
			personalBank.getDevelopmentCards().add(newDevelopmentCards.get(i));
		}
		newDevelopmentCards=new ArrayList<>();
	}
	public boolean hasDevCard(DevCardType type){
        for(int i=0;i<personalBank.getDevelopmentCards().size();++i){
            if(personalBank.getDevelopmentCards().get(i).getType()==type) return true;
        }
        return false;
    }
    public boolean removeDevelopmentCard(DevCardType type){
        int i=0;
        for(;i<personalBank.getDevelopmentCards().size();++i){
            if(personalBank.getDevelopmentCards().get(i).getType()==type){
                try{
                    removeDevelopmentCard(i);
                    return true;
                }catch(Exception e){
                    Debugger.LogMessage("Player:removeDevlopmentCard:tried to remove out of bounds index");
                }
            }
        }
        return false;
    }


	//ResourceCardFunctions
	/**
	 * @pre ResourcePile and DevelopmentCards are not null
	 * @return the sume of the total number of resources and developmentcards owned
	 */
	public int sumOfAllCards(){
		return personalBank.getResourcePile().sumOfResources()+personalBank.getDevelopmentCards().size();
	}
	public int sumOfDevCards(){
		return personalBank.getDevelopmentCards().size();
	}
	/**
	 * @pre resourceAggregation not null
	 * @post returned sum
	 * @return total number of resources of the player
	 */
	public int sumOfResourceCards(){
		return personalBank.getResourcePile().sumOfResources();
	}

	//Structure functions
	/**
	 * @pre ownedRoads is not null
	 * @post total number of roads available to be distributed
	 * @return total number of roads that remain to be distributed
	 */
	public int getRemainingRoads(){return remainingRoads;}
	/**
	 * @pre urbanCount is not null is not null
	 * @post total number of settlements available to be distributed
	 * @return total number of settlements that remain to be distributed
	 */
	public int getRemainingSettlements(){
		return remainingSettlements;
	}
	public int getOwnedSettlementsCount(){
		int result=0;
		for(int i=0;i<ownedColonies.size();++i)if(ownedColonies.get(i).getPieceType()==PieceType.SETTLEMENT)++result;
		return result;
	}
	/**
	 * @pre urbanCount is not null
	 * @post total number of cities available to be distributed
	 * @return total number of cities that remain to be distributed
	 */
	public int getRemainingCities(){
		return remainingCities;
	}
	public void addColony(Colony colony){
		if(colony.getPieceType()==PieceType.SETTLEMENT){
			remainingSettlements--;
		}
		if(colony.getPieceType()==PieceType.CITY){
			remainingSettlements++;
			remainingCities--;
		}
		ownedColonies.add(colony);
		try{
			modifyVictoryPoints(1);
		} catch (Exception e){
			Debugger.LogMessage("Victory points went negative");
		}
	}
	public void addRoad(Road road){
		remainingRoads--;
		ownedRoads.add(road);
	}

	/**
	 * @pre none
	 * @post length of the player's longest road
	 * @return length of the longest road
	 */
	public int getMaxRoadLength(){
		int result=0;
		for(int i=0;i<ownedRoads.size();++i){
			int temp=ownedRoads.get(i).maxLengthFromHere(null);
			if(temp>result)result=temp;
		}
		return result;
	}
	/**
	 * @pre none
	 * @post total size of army
	 * @return total armySize
	 */
	private int urbanCount(PieceType type){
		int result=0;
		for(Colony colony : ownedColonies){
			if(colony.getPieceType()==type)++result;
		}
		return result;
	}

	//Army Functions
	public int getArmySize(){
		return currentArmySize;
	}
	public void addArmy(){currentArmySize++;}
	public void setArmySize(int armySize){currentArmySize=armySize;}
	public PlayerInfo generatePlayerInfo(){
		PlayerInfo result=new PlayerInfo();
		result.setColor(color);
		result.setName(nickname);
		result.setId(internetID);
		result.setPlayerIndex(index);
		return result;
	}

	public void initializePlayerFromData(int soldiers, int victoryPoints, int monuments, boolean hasPlayedDev,
										 boolean hasDiscarded, int index, String name, String color,int internetID,
										 int remainingCities,int remainingRoads,int remainingSettlements){
		currentArmySize=soldiers;
		this.victoryPoints=victoryPoints;
		this.hasPlayedDev=hasPlayedDev;
		this.hasDiscarded=hasDiscarded;
		this.index =index;
		nickname=name;
		this.color= EnumConverter.CatanColor(color);
		this.internetID=internetID;
		this.playedMonuments=monuments;
		this.remainingCities=remainingCities;
		this.remainingRoads=remainingRoads;
		this.remainingSettlements=remainingSettlements;
		//Still need to initialize resources, Old Dev Cards, New Dev Cards. Those should be done locally in another function
	}
	public void initializeDevDeckFromInts(boolean oldDeck,int yearOfPlenty,int monopoly,int soldier, int roadBuilding, int monument){
		//To initialize to oldDeck, set oldDeck true. To new Deck, set it false
		List<DevelopmentCard> activeList;
		if(oldDeck)activeList=personalBank.getDevelopmentCards();
		else activeList=newDevelopmentCards;
		activeList=new LinkedList<>();
		for(int i=0;i<yearOfPlenty;++i)activeList.add(new DevelopmentCard(DevCardType.YEAR_OF_PLENTY));
		for(int i=0;i<monopoly;++i)activeList.add(new DevelopmentCard(DevCardType.MONOPOLY));
		for(int i=0;i<soldier;++i)activeList.add(new DevelopmentCard(DevCardType.SOLDIER));
		for(int i=0;i<roadBuilding;++i)activeList.add(new DevelopmentCard(DevCardType.ROAD_BUILD));
		for(int i=0;i<monument;++i)activeList.add(new DevelopmentCard(DevCardType.MONUMENT));
	}
	public void initializeBankFromData(int brick, int ore, int sheep, int wheat, int wood){
		personalBank.setResourcePile(new ResourcePile(wood,brick,sheep,wheat,ore));
	}
	/**
	 * @pre none
	 * @post A string in JSON format containing Player's crucuial information
	 * @return String in JSON format
	 */
	public String serialize() {
		//TODO:implement
		return "";
	}

	public void initializeDefaultPlayer(int internetID,int index,String name,CatanColor color){
		personalBank.setResourcePile(new ResourcePile(0,0,0,0,0));
		personalBank.setDevelopmentCards(new ArrayList<>());
		newDevelopmentCards=new ArrayList<>();
		ownedRoads=new ArrayList<>();
		ownedColonies=new ArrayList<>();
		remainingRoads=15;
		remainingCities=4;
		remainingSettlements=5;
		currentArmySize=0;
		victoryPoints=0;
		playedMonuments=0;
		hasDiscarded=false;
		hasPlayedDev=false;
		this.internetID=internetID;
		this.index=index;
		this.nickname=name;
		this.color=color;
	}

	public boolean equals(Player player){
		if (player==null)return false;
		if(victoryPoints!=player.getVictoryPoints())return false;
		if(index!=player.getIndex())return false;
		if(!nickname.equals(player.getNickname()))return false;
		if(!color.equals(player.getColor()))return false;
		if(!personalBank.equals(player.getPersonalBank()))return false;
		if(currentArmySize!=player.getArmySize())return false;
		if(hasPlayedDev!=player.getHasPlayedDev())return false;
		if(hasDiscarded!=player.getHasDiscarded())return false;
		if(remainingRoads!=player.getRemainingRoads())return false;
		if(remainingSettlements!=player.getRemainingSettlements())return false;
		if(remainingCities!=player.getRemainingCities())return false;
		if(playedMonuments!=player.getPlayedMonuments())return false;
		if(internetID!=player.getInternetID())return false;
		if(ownedColonies.size()!=player.ownedColonies.size())return false;
		if(ownedRoads.size()!=player.ownedRoads.size())return false;
		if(newDevelopmentCards.size()!=player.getNewDevelopmentCards().size())return false;
		int[] devCounts=new int[5];
		for(int i=0;i<newDevelopmentCards.size();++i){
			devCounts[EnumConverter.DevCardType(newDevelopmentCards.get(i).getType())]++;
		}
		int[] devCounts2=new int[5];
		for(int i=0;i<player.getNewDevelopmentCards().size();++i){
			devCounts2[EnumConverter.DevCardType(player.newDevelopmentCards.get(i).getType())]++;
		}
		for(int i=0;i<5;++i){
			if(devCounts[i]!=devCounts2[i])return false;
		}
		return true;
	}
}
