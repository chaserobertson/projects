package model.facade.client;

import model.facade.server.ServerDevCardFacade;
import model.facade.shared.ModelReferenceFacade;
import model.facade.shared.SerializeFacade;
import model.serialization.Deserializer;
import server.HTTPServerProxy;
import server.IServer;
import shared.definitions.ResourceType;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import params.*;

/**
 * Created by MTAYS on 9/21/2016.
 */
public class ClientDevCardFacade {
    private static ClientDevCardFacade instance;
    public static ClientDevCardFacade getInstance(){
        if(instance==null)instance=new ClientDevCardFacade();
        return instance;
    }
    private ClientDevCardFacade(){
        //nothing
    }

    private static IServer server = HTTPServerProxy.getInstance();

    //DevCardCommands
    //General Preconditions: your turn, in Playing State, card exists in Old Dev Card hand, have not already played a non-monument card this turn


    //Soldier
    /**
     * @param player
     *  	- player to play the card
     * @param victim
     *  	- player to be robbed
     * @param robberMove
     * 		- hex to move robber to
     * @pre
     * @post
     */
    public static boolean canUseSoldier(int player, int victim, HexLocation robberMove){
        SoldierParams params=new SoldierParams();
        params.playerIndex=player;
        params.victimIndex=victim;
        params.location=robberMove;
        return ServerDevCardFacade.canUseSoldier(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param player
     *  	- player to play the card
     * @param victim
     *  	- player to be robbed
     * @param robberMove
     * 		- hex to move robber to
     * @pre The robber is not being kept in the same location
     * @pre If a player is being robbed (i.e., victimIndex != ­1), the player being robbed has resource cards
     * @post The robber is in the new location
     * @post The player being robbed (if any) gave you one of his resource cards (randomly selected)
     * @post If applicable, "largest army" has been awarded to the player who has played the most soldier cards
     * @post You are not allowed to play other development cards during this turn (except for monument cards, which may still be played)
     */
    public static boolean useSoldier(int player, int victim, HexLocation robberMove){
        if(!canUseSoldier(player,victim,robberMove))return false;
        SoldierParams params=new SoldierParams();
        params.playerIndex=player;
        params.victimIndex=victim;
        params.location=robberMove;
        String result = server.playCardSoldier(params);
        ModelReferenceFacade.getInstance().setmodel(Deserializer.deserialize(result));
        return true;
    }




    //YearOfPlenty
    /**
     * @param player
     * 		- player to play the card
     * @param resource1
     * 		- first resource requested
     * @param resource2
     * 		- second resource requested
     * @pre
     * @post
     */
    public static boolean canUseYearOfPlenty(int player, ResourceType resource1, ResourceType resource2){
        YearOfPlentyParams params=new YearOfPlentyParams();
        params.playerIndex=player;
        params.resource1=resource1;
        params.resource2=resource2;
        return ServerDevCardFacade.canUseYearOfPlenty(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param player
     * 		- player to play the card
     * @param resource1
     * 		- first resource requested
     * @param resource2
     * 		- second resource requested
     * @pre The two specified resources are in the bank
     * @post You gained the two specified resources
     */
    public static boolean useYearOfPlenty(int player, ResourceType resource1, ResourceType resource2){
        if(!canUseYearOfPlenty(player,resource1,resource2))return false;
        YearOfPlentyParams params=new YearOfPlentyParams();
        params.playerIndex = player;
        params.resource1 = resource1;
        params.resource2 = resource2;
        String result = server.playCardYearOfPlenty(params);
        SerializeFacade.buildModelFromJSON(result);
        return false;
    }




    //RoadBuilding
    /**
     * @param player
     * 		- player to play the card
     * @param road1
     * 		- first road requested
     * @param road2
     * 		- second road requested
     * @pre
     * @post
     */
    public static boolean canUseRoadBuilding(int player, EdgeLocation road1, EdgeLocation road2){
        RoadBuildingParams params = new RoadBuildingParams();
        params.playerIndex=player;
        params.spot1=road1;
        params.spot2=road2;
        return ServerDevCardFacade.canUseRoadBuilding(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param player
     * 		- player to play the card
     * @param road1
     * 		- first road requested
     * @param road2
     * 		- second road requested
     * @pre The first road location (spot1) is connected to one of your roads.
     * @pre The second road location (spot2) is connected to one of your roads or to the first road location (spot1)
     * @pre Neither road location is on water
     * @pre You have at least two unused roads
     * @post You have two fewer unused roads
     * @post Two new roads appear on the map at the specified locations
     * @post If applicable, "longest road" has been awarded to the player with the longest road
     */
    public static boolean useRoadBuilding(int player,EdgeLocation road1, EdgeLocation road2){
        //removed the if so that it would, it was always returning false.
        //if(!canUseRoadBuilding(player,road1,road2))return false;
        RoadBuildingParams params = new RoadBuildingParams();
        params.playerIndex=player;
        params.spot1=road1;
        params.spot2=road2;
        String result = server.playCardRoadBuilding(params);
        SerializeFacade.buildModelFromJSON(result);
        return false;
    }





    //Monopoly
    /**
     * @param player
     * 		- player to play the card
     * @pre
     * @post
     */
    public static boolean canUseMonopoly(int player,ResourceType resource){
        MonopolyParams params=new MonopolyParams();
        params.playerIndex=player;
        params.resource=resource;
        return ServerDevCardFacade.canUseMonopoly(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param player
     * 		- player to play the card
     * @pre none
     * @post All of the other players have given you all of their resource cards of the specified type
     */
    public static boolean useMonopoly(int player, ResourceType resource){
        if(!canUseMonopoly(player,resource))return false;
        MonopolyParams params=new MonopolyParams();
        params.playerIndex=player;
        params.resource=resource;
        String result = server.playCardMonopoly(params);
        SerializeFacade.buildModelFromJSON(result);
        return false;
    }



    //Monument
    /**
     * @param player
     * 		- player to play the card
     * @pre
     * @post
     */
    public static boolean canUseMonument(int player){
        MonumentParams params = new MonumentParams();
        params.playerIndex = player;
        return ServerDevCardFacade.canUseMonument(params,ModelReferenceFacade.getModel());
    }
    /**
     * @param player
     * 		- player to play the card
     * @pre You have enough monument cards to win the game
     * @post You gained a victory point
     */
    public static boolean useMonument(int player){
        if(!canUseMonument(player))return false;
        MonumentParams params = new MonumentParams();
        params.playerIndex = player;
        String result = server.playCardMonument(params);
        SerializeFacade.buildModelFromJSON(result);
        return false;
    }
}
