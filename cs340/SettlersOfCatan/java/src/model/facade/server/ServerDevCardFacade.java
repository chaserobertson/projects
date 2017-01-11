package model.facade.server;

import debugger.Debugger;
import model.definitions.PlayerState;
import model.definitions.ResourceTypeQuantityPair;
import model.overhead.Model;
import model.player.Player;
import params.*;
import shared.definitions.DevCardType;

import java.util.List;

/**
 * Created by MTAYS on 9/21/2016.
 */
public class ServerDevCardFacade {
    private static ServerDevCardFacade instance;
    public static ServerDevCardFacade getInstance(){
        if(instance==null)instance=new ServerDevCardFacade();
        return instance;
    }
    private ServerDevCardFacade(){
        //nothing
    }

    //DevCardCommands
    //General Preconditions: your turn, in Playing State, card exists in Old Dev Card hand, have not already played a non-monument card this turn
    /**
     *
     * @param params
     *
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    private static boolean canPlayDevCard(MoveParam params,Model model){
        if(model==null)return false;
        Player player = model.getPlayerByIndex(params.playerIndex);
        if(model.getPeripherals().getCurrentState()!= PlayerState.Playing||
                params.playerIndex!= model.getActiveID()||
                player.getHasPlayedDev()
                )return false;
        return true;
    }
    public static boolean canUseSomeDevCard(int playerIndex,Model model){
        if(model==null)return false;
        Player player = model.getPlayerByIndex(playerIndex);
        if(player==null)return false;
        MoveParam param=new MonumentParams();
        param.playerIndex=playerIndex;
        if(!canPlayDevCard(param,model)){
            return false;
        }
        if(player.sumOfDevCards()<=0){
            return false;
        }
        return true;
    }



    //Soldier
    /**
     * @param params
     *  	- parameters of command
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre have the card, and can play a dev card
     * @post Card has been used
     */
    public static boolean canUseSoldier(SoldierParams params,Model model){
        if(model==null)return false;
        if(!canPlayDevCard(params,model))return false;
        Player player = model.getPlayerByIndex(params.playerIndex);
        return player.hasDevCard(DevCardType.SOLDIER);
    }
    /**
     * @pre The robber is not being kept in the same location
     * @pre If a player is being robbed (i.e., victimIndex != ­1), the player being robbed has resource cards
     * @post The robber is in the new location
     * @post The player being robbed (if any) gave you one of his resource cards (randomly selected)
     * @post If applicable, "largest army" has been awarded to the player who has played the most soldier cards
     * @post You are not allowed to play other development cards during this turn (except for monument cards, which may still be played)
     */
    public static boolean useSoldier(SoldierParams params,Model model){
        if(model==null)return false;
        if(!canUseSoldier(params,model))return false;
        RobPlayerParams robParams=new RobPlayerParams();
        robParams.playerIndex=params.playerIndex;
        robParams.victimIndex=params.victimIndex;
        robParams.location=params.location;
        if(!ServerPlayingCommandFacade.canRobPlayer(robParams,model))return false;
        Player player = model.getPlayerByIndex(params.playerIndex);
        player.setHasPlayedDev(true);
        player.addArmy();
        player.removeDevelopmentCard(DevCardType.SOLDIER);

        model.recalculateLargestArmy();
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(player,player.getNickname()+" has played a soldier card");
        ServerPlayingCommandFacade.robPlayer(robParams,model);
        model.incrementVersion();
        return true;
    }




    //YearOfPlenty
    /**
     * @param params
     *      - parameters of the card
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre Have the card and can use dev card
     * @post card has been used
     */
    public static boolean canUseYearOfPlenty(YearOfPlentyParams params,Model model){
        if(model==null)return false;
        if(!canPlayDevCard(params,model))return false;
        Player player = model.getPlayerByIndex(params.playerIndex);
        if(!player.hasDevCard(DevCardType.YEAR_OF_PLENTY))return false;
        //Can year of Plenty pull two of the same?
        ResourceTypeQuantityPair res1=new ResourceTypeQuantityPair(params.resource1,1);
        ResourceTypeQuantityPair res2=new ResourceTypeQuantityPair(params.resource2,1);
        if(res1.type==res2.type)res1.quantity=2;//This is because its only canTransfer methods
        if(!model.getResourceAggregation().canTransferResourcesTo(player.getResourceAggregation(),res1))return false;
        if(!model.getResourceAggregation().canTransferResourcesTo(player.getResourceAggregation(),res2))return false;
        return true;
    }
    /**
     * @param params
     *      -parameters of Year of Plenty Card
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre The two specified resources are in the bank
     * @post You gained the two specified resources
     */
    public static boolean useYearOfPlenty(YearOfPlentyParams params,Model model){
        if(model==null)return false;
        if(!canUseYearOfPlenty(params,model))return false;
        ResourceTypeQuantityPair res1=new ResourceTypeQuantityPair(params.resource1,1);
        ResourceTypeQuantityPair res2=new ResourceTypeQuantityPair(params.resource2,1);
        Player player = model.getPlayerByIndex(params.playerIndex);
        player.setHasPlayedDev(true);
        if(!model.getResourceAggregation().transferResourcesTo(player.getResourceAggregation(),res1))return false;
        if(!model.getResourceAggregation().transferResourcesTo(player.getResourceAggregation(),res2))return false;
        player.removeDevelopmentCard(DevCardType.YEAR_OF_PLENTY);
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(player,player.getNickname()+" has used a year of plenty card");
        model.incrementVersion();
        return true;
    }




    //RoadBuilding
    /**
     * @param params
     *      -The parameters of the card
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canUseRoadBuilding(RoadBuildingParams params,Model model){
        if(model==null)return false;
        if(!canPlayDevCard(params,model))return false;
        Player player = model.getPlayerByIndex(params.playerIndex);
        if(!player.hasDevCard(DevCardType.ROAD_BUILD))return false;
        if(player.getRemainingRoads()<=0)return false;
        BuildRoadParams buildRoadParams1=new BuildRoadParams();
        buildRoadParams1.free=true;
        buildRoadParams1.playerIndex=params.playerIndex;
        buildRoadParams1.roadLocation=params.spot1;
        buildRoadParams1.setupRound=false;
        if(!ServerPlayingCommandFacade.canPlaceRoad(buildRoadParams1,model))return false;
        if(params.spot2!=null) {
            ServerPlayingCommandFacade.placeRoad(buildRoadParams1,model);//Theoretically functional, not used in client
            BuildRoadParams buildRoadParams2 = new BuildRoadParams();
            buildRoadParams2.free = true;
            buildRoadParams2.playerIndex = params.playerIndex;
            buildRoadParams2.roadLocation = params.spot2;
            buildRoadParams2.setupRound = false;
            if (!ServerPlayingCommandFacade.canPlaceRoad(buildRoadParams2,model)) {
                ServerPlayingCommandFacade.undoPlaceRoad(buildRoadParams1,model);//Theoretically functional, not used in client
                return false;
            }
            ServerPlayingCommandFacade.undoPlaceRoad(buildRoadParams1,model);//Theoretically functional, not used in client
        }
        return true;
    }
    /**
     *@param params
     *      -the parameters of the action
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre The first road location (spot1) is connected to one of your roads.
     * @pre The second road location (spot2) is connected to one of your roads or to the first road location (spot1)
     * @pre Neither road location is on water
     * @pre You have at least two unused roads
     * @post You have two fewer unused roads
     * @post Two new roads appear on the map at the specified locations
     * @post If applicable, "longest road" has been awarded to the player with the longest road
     */
    public static boolean useRoadBuilding(RoadBuildingParams params,Model model){
        if(model==null)return false;
        if(!canUseRoadBuilding(params,model))return false;
        Player player = model.getPlayerByIndex(params.playerIndex);
        BuildRoadParams buildRoadParams1=new BuildRoadParams();
        buildRoadParams1.free=true;
        buildRoadParams1.playerIndex=params.playerIndex;
        buildRoadParams1.roadLocation=params.spot1;
        buildRoadParams1.setupRound=false;
        BuildRoadParams buildRoadParams2=new BuildRoadParams();
        buildRoadParams2.free=true;
        buildRoadParams2.playerIndex=params.playerIndex;
        buildRoadParams2.roadLocation=params.spot2;
        buildRoadParams2.setupRound=false;
        if(!ServerPlayingCommandFacade.placeRoad(buildRoadParams1,model)){
            Debugger.LogMessage("ServerDevCardFacade:RoadBuilding:PlaceRoad1 has failed");
        }
        if(!ServerPlayingCommandFacade.placeRoad(buildRoadParams2,model)){
            Debugger.LogMessage("ServerDevCardFacade:RoadBuilding:PlaceRoad2 has failed");
        }
        player.removeDevelopmentCard(DevCardType.ROAD_BUILD);
        player.setHasPlayedDev(true);
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(player,player.getNickname()+" has used a road building card");
        return true;
    }





    //Monopoly
    /**
     *@param params
     *      -the parameters of the action
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canUseMonopoly(MonopolyParams params,Model model){
        if(model==null)return false;
        if(!canPlayDevCard(params,model))return false;
        Player player=model.getPlayerByIndex(params.playerIndex);
        if(!player.hasDevCard(DevCardType.MONOPOLY))return false;
        return true;
    }
    /**
     * @pre none
     * @post All of the other players have given you all of their resource cards of the specified type
     */
    public static boolean useMonopoly(MonopolyParams params,Model model){
        if(model==null)return false;
        if(!canUseMonopoly(params,model))return false;
        Player player = model.getPlayerByIndex(params.playerIndex);
        List<Player> players=model.getPlayers();
        for(int i=0;i<players.size();++i){
            players.get(i).getResourceAggregation().transferResourcesTo(player.getResourceAggregation(),
                    new ResourceTypeQuantityPair(params.resource,players.get(i).getResourceAggregation().getQuantityResourceType(params.resource)));
        }//Yes, technically he gives himself resources. Deal with it.
        player.removeDevelopmentCard(DevCardType.MONOPOLY);
        player.setHasPlayedDev(true);
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(player,player.getNickname()+" has used a monopoly card");
        model.incrementVersion();
        return true;
    }



    //Monument
    /**
     *@param params
     *      -the parameters of the action
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canUseMonument(MonumentParams params,Model model){
        if(model==null)return false;
        if(!canPlayDevCard(params,model))return false;
        Player player = model.getPlayerByIndex(params.playerIndex);
        if(!player.hasDevCard(DevCardType.MONUMENT))return false;
        int countOfMonuments=0;
        for(int i=0;i<player.getDevelopmentCards().size();++i){
            if(player.getDevelopmentCards().get(i).getType()==DevCardType.MONUMENT)++countOfMonuments;
        }
        if(player.getVictoryPoints()+countOfMonuments<10)return false;
        return true;
    }
    /**
     *@param params
     *      -the parameters of the action
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre You have enough monument cards to win the game
     * @post You gained a victory point
     */
    public static boolean useMonument(MonumentParams params,Model model){
        if(model==null)return false;
        if(!canUseMonument(params,model))return false;
        Player player = model.getPlayerByIndex(params.playerIndex);
        try{
            player.modifyVictoryPoints(1);
            player.removeDevelopmentCard(DevCardType.MONUMENT);
        }catch(Exception e){
            return false;
        }
        player.addMonument();
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(player,player.getNickname()+" has used a monument card");
        model.incrementVersion();
        return true;
    }
}
