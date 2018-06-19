package model.facade.server;

import debugger.Debugger;
import model.definitions.EnumConverter;
import model.definitions.PlayerState;
import model.definitions.ResourceTypeQuantityPair;
import model.facade.AI.AICommands;
import model.gameboard.Hex;
import model.gameboard.NodePoint;
import model.gameboard.Port;
import model.overhead.Model;
import model.peripherals.Robber;
import model.peripherals.TradeOffer;
import model.player.Player;
import model.resources.DevelopmentCard;
import model.resources.ResourcePile;
import model.structures.Road;
import model.structures.Colony;
import model.structures.Settlement;
import params.*;
import shared.definitions.DevCardType;
import shared.definitions.PortType;
import shared.definitions.ResourceType;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import shared.locations.VertexDirection;
import shared.locations.VertexLocation;
import model.structures.City;

import java.util.List;

/**
 * Created by MTAYS on 9/21/2016.
 */
public class ServerPlayingCommandFacade {
    public static boolean AIAutoLoop=true;
    private static ServerPlayingCommandFacade instance;
    public static ServerPlayingCommandFacade getInstance(){
        if(instance==null)instance=new ServerPlayingCommandFacade();
        return instance;
    }
    private ServerPlayingCommandFacade(){
        //nothing
    }
    //playing commands
    //general preconditions: your turn, status is playing
    /**
     * @param params
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre none
     * @post will return whether it is the players turn or not
     */
    private static boolean canPlayingCommands(MoveParam params,Model model){
        if(model==null){
            Debugger.LogMessage("!CanPlayingCommands:Model null");
            return false;
        }
        PlayerState currentState=model.getPeripherals().getCurrentState();
        if((currentState!= PlayerState.Playing&&currentState!=PlayerState.FirstRound&&currentState!=PlayerState.SecondRound)||
                params.playerIndex!=model.getActiveID()||
                model.getPeripherals().getWinner()>=0
                ){
            return false;
        }
        return true;
    }

    /**
     * @param params
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canBuildARoad(BuildRoadParams params,Model model){
        if(model==null)return false;
        Player placer = model.getPlayerByIndex(params.playerIndex);
        if(placer==null)return false;
        if(
                !canPlayingCommands(params,model)||
                placer.getRemainingRoads()<1||
                        (
                        !(params.free||params.setupRound)&&
                        !placer.getResourceAggregation().canTransferResourcesTo(model.getResourceAggregation(),
                                new ResourceTypeQuantityPair(ResourceType.BRICK,1),
                                new ResourceTypeQuantityPair(ResourceType.WOOD,1))
                        )
                )return false;
        return true;
    }
    /**
     * @param placer
     * 		- player placing piece
     * @param edge
     * 		- edge to be played on
     * @param setupRound
     * 		- indicates if the current placing is occurring during the setup
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    private static boolean canPlaceOnEdgeLocation(Player placer, EdgeLocation edge,boolean setupRound,Model model){
        if(model==null)return false;
        VertexDirection[] vertexDirections = EnumConverter.EdgeDirectionToVertexDirection(edge.getDir());
        VertexLocation vLoc1=new VertexLocation(edge.getHexLoc(),vertexDirections[0]);
        VertexLocation vLoc2=new VertexLocation(edge.getHexLoc(),vertexDirections[1]);
        NodePoint node1= model.getNodeAt(vLoc1.getNormalizedLocation());
        NodePoint node2=model.getNodeAt(vLoc2.getNormalizedLocation());
        if(node1==null||node2==null){
            //Debugger.LogMessage("ServerPlayingCommandFacade:CanPlaceOnEdgeLocation:Node detected null");
            return false;
        }
        if(node1.sharesRoadWith(node2)){
            return false;
        }
        //Determined that the space is free.
        if(!setupRound) {
            if(!nodeConnectedForRoadCheck(node1,placer)&&!nodeConnectedForRoadCheck(node2,placer)){
                return false;
            }
        }
        //determined the player owns a road
        else{
            Colony temp1 = node1.getColony();
            Colony temp2=node2.getColony();
            boolean good = false;
            if(temp1!=null&&temp1.getOwningPlayer()==placer&&!node1.containsRoadOf(placer))good = true;
            if(temp2!=null&&temp2.getOwningPlayer()==placer&&!node2.containsRoadOf(placer))good=true;
            if(!good){
                //Debugger.LogMessage("ServerPlayingCommandFacade:CanPlaceOnEdgeLocation:Determined already contains road of placer, or no colony of placer");
                return false;
            }
        }
        //determined the player owns a connected city.
        return true;
    }
    private static boolean nodeConnectedForRoadCheck(NodePoint node,Player placer){
        if(node.getColony()!=null){
            if(node.getColony().getOwningPlayer().getIndex()!=placer.getIndex())return false;
        }
        else if(!node.containsRoadOf(placer))return false;
        return true;
    }
    /**
     * @param params
     *      -parameters of the road construction
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canPlaceRoad(BuildRoadParams params,Model model){
        if(model==null)return false;
        EdgeLocation edge=params.roadLocation;
        if (!canBuildARoad(params,model)){
            //Debugger.LogMessage("ServerPlayingCommandFacade:CanPlaceRoad:Cannot build a road");
            return false;
        }
        if(!canPlaceOnEdgeLocation(model.getPlayerByIndex(params.playerIndex),edge,params.setupRound,model)){
            //Debugger.LogMessage("ServerPlayingCommandFacade:CanPlaceRoad:Cannot place on edge location");
            return false;
        }
        return true;
    }
    /**
     * @param params
     * 		- parameters of the road place
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre The road location is open
     * @pre The road location is connected to another road owned by the player
     * @pre The road location is not on water
     * @pre You have the required resources (1 wood, 1 brick; 1 road)
     * @pre Playing round: Must be placed by settlement owned by the player with no adjacent road
     * @post the required resources are spent
     * @post the road is on the map at the given location
     * @post give new "longest road" if applicable
     */
    public static boolean placeRoad(BuildRoadParams params,Model model){
        if(model==null)return false;
        EdgeLocation edge=params.roadLocation;
        Player placer = model.getPlayerByIndex(params.playerIndex);
        if(!canPlaceRoad(params,model))return false;
        //Place Road: create road object with player,nodes,and location. Player needs to add road
        VertexDirection[] vertexDirections = EnumConverter.EdgeDirectionToVertexDirection(edge.getDir());
        VertexLocation vLoc1=new VertexLocation(edge.getHexLoc(),vertexDirections[0]);
        VertexLocation vLoc2=new VertexLocation(edge.getHexLoc(),vertexDirections[1]);
        NodePoint node1=model.getNodeAt(vLoc1);
        NodePoint node2=model.getNodeAt(vLoc2);
        if(node1==null||node2==null)return false;
        Road temp = new Road(placer,node1,node2,edge);
        placer.addRoad(temp);
        node1.addRoad(temp);
        node2.addRoad(temp);
        if(!params.free&&!params.setupRound) {
            placer.getResourceAggregation().transferResourcesTo(model.getResourceAggregation(),
                    new ResourceTypeQuantityPair(ResourceType.BRICK, 1),
                    new ResourceTypeQuantityPair(ResourceType.WOOD, 1));
        }
        model.recalculateLongestRoad();
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(placer,placer.getNickname()+" has placed a road");
        model.incrementVersion();
        return true;
    }
    public static void undoPlaceRoad(BuildRoadParams params,Model model){
        if(model==null||params==null)return;
        EdgeLocation edge=params.roadLocation;
        if(edge==null)return;
        Player placer = model.getPlayerByIndex(params.playerIndex);
        //Place Road: create road object with player,nodes,and location. Player needs to add road
        VertexDirection[] vertexDirections;
        try {
            vertexDirections = EnumConverter.EdgeDirectionToVertexDirection(edge.getDir());
        }catch (Exception e){
            Debugger.LogMessage("UndoPlaceRoad received bad vertex Direction");
            return;
        }
        VertexLocation vLoc1=new VertexLocation(edge.getHexLoc(),vertexDirections[0]);
        VertexLocation vLoc2=new VertexLocation(edge.getHexLoc(),vertexDirections[1]);
        NodePoint node1=model.getNodeAt(vLoc1);
        NodePoint node2=model.getNodeAt(vLoc2);
        if(node1==null||node2==null||placer==null)return;
        for(int i=0;i<placer.getOwnedRoad().size();++i){
            if(placer.getOwnedRoad().get(i).getLocation().equals(edge)){
                Road road=placer.getOwnedRoad().get(i);
                placer.getOwnedRoad().remove(i);
                for(int j=0;j<node1.getRoads().size();++j){
                    if(node1.getRoads().get(j).getLocation().equals(edge))node1.getRoads().remove(j);
                }
                for(int j=0;j<node2.getRoads().size();++j){
                    if(node2.getRoads().get(j).getLocation().equals(edge))node2.getRoads().remove(j);
                }
                placer.setRemainingRoads(placer.getRemainingRoads()+1);//Add road back
                return;
            }
        }
    }





    //Build settlement
    /**
    * @param params
    * @param model
    *      -the model upon which the operation is to be performed
    *
     */
    public static boolean canPlaceSettlement(BuildSettlementParams params,Model model){
        if(model==null)return false;
        if(!canBuildSettlement(params,model)){
            return false;
        }
        VertexLocation location = params.vertexLocation;
        if(model.getGameBoard().getNodePointAt(location)==null)return false;
        if(!model.getGameBoard().getNodePointAt(location).canBuildColonyHere(model.getPlayerByIndex(params.playerIndex),params.setupRound,false)) {
            return false;
        }
        return true;
    }
    /**
     * @param params
     *      -params of build settlement
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canBuildSettlement(BuildSettlementParams params,Model model){
        if(model==null)return false;
        Player placer = model.getPlayerByIndex(params.playerIndex);
        if(placer==null)return false;
        if(
                !canPlayingCommands(params,model)||
                        placer.getRemainingSettlements()<1||
                        (!(params.free||params.setupRound)&&!placer.getResourceAggregation().canTransferResourcesTo(
                                model.getResourceAggregation(),
                                new ResourceTypeQuantityPair(ResourceType.BRICK,1),
                                new ResourceTypeQuantityPair(ResourceType.WOOD,1),
                                new ResourceTypeQuantityPair(ResourceType.SHEEP,1),
                                new ResourceTypeQuantityPair(ResourceType.WHEAT,1))
                        )
                )return false;
        return true;
    }
    /**
     * @param params
     *      -parameters of build
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre The settlement location is open
     * @pre The settlement location is not on water
     * @pre The settlement location is connected to one of your roads except during setup
     * @pre You have the required resources (1 wood, 1 brick, 1 wheat, 1 sheep; 1 settlement)
     * @pre The settlement cannot be placed adjacent to another settlement
     * @post You lost the resources required to build a settlement (1 wood, 1 brick, 1 wheat, 1 sheep; 1 settlement)
     * @post The settlement is on the map at the specified location
     */
    public static boolean placeSettlement(BuildSettlementParams params,Model model){
        if(model==null)return false;
        if(!canPlaceSettlement(params,model))return false;
        Player placer = model.getPlayerByIndex(params.playerIndex);
        NodePoint position = model.getNodeAt(params.vertexLocation);
        Settlement newSettlement=new Settlement(position,placer,placer.getColor());
        placer.addColony(newSettlement);
        if(!params.free&&!params.setupRound) {
            placer.getResourceAggregation().transferResourcesTo(
                    model.getResourceAggregation(),
                    new ResourceTypeQuantityPair(ResourceType.BRICK, 1),
                    new ResourceTypeQuantityPair(ResourceType.WOOD, 1),
                    new ResourceTypeQuantityPair(ResourceType.SHEEP, 1),
                    new ResourceTypeQuantityPair(ResourceType.WHEAT, 1));
        }
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(placer,placer.getNickname()+" has placed a settlement");
        model.incrementVersion();
        return true;
    }



    //Build City
    /**
     * @param params
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canPlaceCity(BuildCityParams params,Model model){
        if(model==null)return false;
        if(!canBuildCity(params,model))return false;
        VertexLocation location = params.vertexLocation;
        if(model.getNodeAt(location)==null)return false;
        if(!model.getNodeAt(location).canBuildColonyHere(model.getPlayerByIndex(params.playerIndex),false,true))return false;
        return true;
    }
    /**
     * @param params
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canBuildCity(BuildCityParams params,Model model){
        if(model==null)return false;
        Player placer = model.getPlayerByIndex(params.playerIndex);
        if(placer==null)return false;
        if(
                !canPlayingCommands(params,model)||
                        placer.getRemainingCities()<1||
                        (!placer.getResourceAggregation().canTransferResourcesTo(model.getResourceAggregation(),
                                new ResourceTypeQuantityPair(ResourceType.ORE,3),
                                new ResourceTypeQuantityPair(ResourceType.WHEAT,2)))
                )return false;
        return true;
    }
    /**
     * @param params
     *      -parameters of buildCity
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre The city location is where you currently have a settlement
     * @pre You have the required resources (2 wheat, 3 ore; 1 city)
     * @post You lost the resources required to build a city (2 wheat, 3 ore; 1 city)
     * @post The city is on the map at the specified location
     * @post You got a settlement back

     */
    public static boolean buildCity(BuildCityParams params,Model model){
        if(model==null)return false;
        if(!canPlaceCity(params,model))return false;
        Player placer = model.getPlayerByIndex(params.playerIndex);
        NodePoint position = model.getNodeAt(params.vertexLocation);
        City newCity=new City(position,placer,placer.getColor());
        placer.addColony(newCity);
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(placer,placer.getNickname()+" has placed a city");
        if(placer.getResourceAggregation().transferResourcesTo(model.getResourceAggregation(),
                new ResourceTypeQuantityPair(ResourceType.ORE,3),
                new ResourceTypeQuantityPair(ResourceType.WHEAT,2))){
            model.incrementVersion();
            return true;
        }
        return false;
    }



    //offer trade
    /**
     * @param params
     *      -params of offer
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canOfferTrade(OfferTradeParams params,Model model){
        //only default of playing commands
        if(model==null)return false;
        if(!canPlayingCommands(params,model)||
                model.getPeripherals().getActiveTradeOffer()!=null||
                !model.getPlayerByIndex(params.playerIndex).getResourceAggregation().canTransferResourcesTo
                        (model.getResourceAggregation(),params.offer))return false;
        return true;
    }
    /**
     * @param params
     *      -params of offer
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre You have the resources you are offering
     * @post The trade is offered to the other player (stored in the server model)
     */
    public static boolean offerTrade(OfferTradeParams params,Model model){
        if(model==null)return false;
        if(!canOfferTrade(params,model))return false;
        Player offerer=model.getPlayerByIndex(params.playerIndex);
        Player receiver=model.getPlayerByIndex(params.receiver);
        model.getPeripherals().setActiveTradeOffer(new TradeOffer(offerer,receiver,params.offer));
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(offerer,offerer.getNickname()+" has offered a trade");
        if(receiver.getInternetID()<-10&&AIAutoLoop){
            AICommands.AIReceiveTradeOffer(receiver,model,params.gameId);
        }
        model.incrementVersion();
        return true;
    }

    //Maritime trade
    /**
     * @param params
     *      -params of maritimeTrade
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canMaritimeTrade(MaritimeTradeParams params,Model model){
        if(model==null)return false;
        if(!canPlayingCommands(params,model))return false;
        Player player = model.getPlayerByIndex(params.playerIndex);
        int quantity =params.ratio;
        ResourcePile bank = model.getResourceAggregation();
        ResourceTypeQuantityPair givePair = new ResourceTypeQuantityPair(params.inputResource,quantity);
        if(!player.getResourceAggregation().canTransferResourcesTo(bank,givePair))return false;
        if(!bank.canTransferResourcesTo(player.getResourceAggregation(),new ResourceTypeQuantityPair(params.outputResource,1)))return false;
        if(params.ratio==4)return true;
        PortType portType;
        if(params.ratio==3)portType=PortType.THREE;
        else portType=EnumConverter.PortType(params.inputResource);
        for(int i = 0; i<player.getOwnedColonies().size(); ++i){
            Port port=player.getOwnedColonies().get(i).getNodePoint().getPort();
            if(port!=null&&port.getType().equals(portType))return true;
        }
        return false;
    }
    /**
     * @param params
     *      - params of maritimeTrade
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre You have the resources you are giving
     * @pre For ratios less than 4, you have the correct port for the trade
     * @post The trade has been executed (the offered resources are in the bank, and the requested resource has been received)
     */
    public static boolean maritimeTrade(MaritimeTradeParams params,Model model){
        if(model==null)return false;
        if(!canMaritimeTrade(params,model))return false;
        ResourceTypeQuantityPair input = new ResourceTypeQuantityPair(params.inputResource,params.ratio);
        ResourceTypeQuantityPair output=new ResourceTypeQuantityPair(params.outputResource,1);
        Player trader=model.getPlayerByIndex(params.playerIndex);
        ResourcePile bank = model.getResourceAggregation();
        trader.getResourceAggregation().transferResourcesTo(bank,input);
        bank.transferResourcesTo(trader.getResourceAggregation(),output);
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(trader,trader.getNickname()+" has performed a maritime trade");
        model.incrementVersion();
        return true;
    }





    //Rob Player
    /**
     * @param params
     *      -parameters of rob player
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canRobPlayer(RobPlayerParams params,Model model){
        //Seems weird that we have to be in the playing phase and not in the "robbing" phase.
        if(model==null)return false;
        //if(!canPlayingCommands(params))return false;
        if(!canMoveRobberHere(params.location,model))return false;
        if(params.victimIndex>=0){
            Player robee=model.getPlayerByIndex(params.victimIndex);
            if(robee.sumOfResourceCards()<=0)return false;
        }
        return true;
    }
    public static boolean canMoveRobberHere(HexLocation location,Model model){
        if(model==null)return false;
        Hex destination = model.getGameBoard().getHexAt(location);
        Robber robber = model.getPeripherals().getRobber();
        if(destination==null) return false;
        if(robber==null)return false;
        if(robber.getHex()==null||robber.getHex().getHexLocation().equals(location))return false;
        //if(destination.getHexType()==HexType.DESERT||destination.getHexType()==null)return false;// old rule
        return true;
    }
    /**
     * @param params
     *      -params of rob player
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre The robber is not being kept in the same location
     * @pre If a player is being robbed (i.e., victimIndex != ­1), the player being robbed has resource cards
     * @post The robber is in the new location
     * @post The player being robbed (if any) gave you one of his resource cards (randomly selected)
     */
    public static boolean robPlayer(RobPlayerParams params,Model model){
        if(model==null)return false;
        if(!canRobPlayer(params,model))return false;
        Hex destination = model.getGameBoard().getHexAt(params.location);
        Robber robber = model.getPeripherals().getRobber();
        robber.moveTo(destination);
        Player player = model.getPlayerByIndex(params.playerIndex);
        if(player==null)return false;
        if(params.victimIndex>=0){
            Player robee= model.getPlayerByIndex(params.victimIndex);
            if(robee==null)return false;
            ResourceTypeQuantityPair steal=new ResourceTypeQuantityPair(robee.getResourceAggregation().chooseRandomResource(),1);
            robee.getResourceAggregation().transferResourcesTo(player.getResourceAggregation(),steal);
            if(model.getPeripherals()!=null)model.getPeripherals().logMessage(player,player.getNickname()+" has robbed "+robee.getNickname());
        }
        else if(model.getPeripherals()!=null)model.getPeripherals().logMessage(player,player.getNickname()+" was unable to rob anyone");
        model.getPeripherals().setCurrentState(PlayerState.Playing);
        model.incrementVersion();
        return  true;
    }




    //finishTurn
    /**
     * @param params
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canEndTurn(FinishTurnParams params,Model model){
        return canPlayingCommands(params,model);
    }
    /**
     * @param params
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre none
     * @post The cards in your new dev card hand have been transferred to your old dev card hand
     * @post It is the next player’s turn
     */
    public static boolean endTurn(FinishTurnParams params,Model model){
        if(model==null)return false;
        if(!canEndTurn(params,model))return false;
        PlayerState initialState=model.getPeripherals().getCurrentState();

        Player player = model.getPlayerByIndex(params.playerIndex);
        player.transferNewDevelopmentIntoOld();
        model.getPeripherals().getTurnTracker().advanceActiveID();//This also takes care of PlayerState
        List<Player> playerList = model.getPlayers();
        for (Player playerT:playerList) {
            playerT.setHasPlayedDev(false);
            playerT.setHasDiscarded(false);
        }
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(player,player.getNickname()+" has ended their turn");
        if(AIAutoLoop)AICommands.AIEndTurnLoop(model,params.gameId);
        //Distribute resources to players if its end of setup
        if(initialState==PlayerState.SecondRound&&model.getPeripherals().getTurnTracker().getCurrentState()!=PlayerState.SecondRound){
            for(Player playerReceiver:model.getPlayers()){
                if(playerReceiver.getOwnedColonies().size()>=2){
                    if(playerReceiver.getOwnedColonies().get(1)!=null){
                        playerReceiver.getResourceAggregation().addResources(playerReceiver.getOwnedColonies().get(1).getResources());
                    }
                }
            }
        }
        model.incrementVersion();
        return true;
    }
    public static boolean endTurn(FinishTurnParams params, Model model, boolean AIStart){//This is the end turn called by AI's
        if(model==null)return false;
        if(!canEndTurn(params,model))return false;
        PlayerState initialState=model.getPeripherals().getCurrentState();

        Player player = model.getPlayerByIndex(params.playerIndex);
        player.transferNewDevelopmentIntoOld();
        model.getPeripherals().getTurnTracker().advanceActiveID();//This also takes care of PlayerState
        List<Player> playerList = model.getPlayers();
        for (Player playerT:playerList) {
            playerT.setHasPlayedDev(false);
            playerT.setHasDiscarded(false);
        }
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(player,player.getNickname()+" has ended their turn");
        if(AIStart)AICommands.AIEndTurnLoop(model,params.gameId);
        //Distribute resources to players if its end of setup
        if(initialState==PlayerState.SecondRound&&model.getPeripherals().getTurnTracker().getCurrentState()!=PlayerState.SecondRound){
            for(Player playerReceiver:model.getPlayers()){
                if(playerReceiver.getOwnedColonies().size()>=2){
                    if(playerReceiver.getOwnedColonies().get(1)!=null){
                        playerReceiver.getResourceAggregation().addResources(playerReceiver.getOwnedColonies().get(1).getResources());
                    }
                }
            }
        }
        model.incrementVersion();
        return true;
    }





    //buyDevCard
    /**
     * @param params
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre
     * @post
     */
    public static boolean canBuyDevCard(BuyDevCardParams params,Model model){
        if(model==null)return false;
        Player player = model.getPlayerByIndex(params.playerIndex);
        if(!player.getResourceAggregation().canTransferResourcesTo(model.getResourceAggregation(),
                new ResourceTypeQuantityPair(ResourceType.SHEEP,1),
                new ResourceTypeQuantityPair(ResourceType.WHEAT,1),
                new ResourceTypeQuantityPair(ResourceType.ORE,1))
                ) return false;
        if(model.getDevelopmentCardDeck().size()<=0)return false;
        return true;
    }
    /**
     * @param params
     * @param model
     *      -the model upon which the operation is to be performed
     * @pre You have the required resources (1 ore, 1 wheat, 1 sheep)
     * @pre There are dev cards left in the deck
     * @post You have a new card
     * If it is a monument card, it has been added to your old devcard hand
     * If it is a non­monument card, it has been added to your new devcard hand (unplayable this turn)
     */
    public static boolean buyDevCard(BuyDevCardParams params,Model model){
        if(model==null)return false;
        if(!canBuyDevCard(params,model))return false;
        Player player = model.getPlayerByIndex(params.playerIndex);
        if(!player.getResourceAggregation().transferResourcesTo(model.getResourceAggregation(),
                new ResourceTypeQuantityPair(ResourceType.SHEEP,1),
                new ResourceTypeQuantityPair(ResourceType.WHEAT,1),
                new ResourceTypeQuantityPair(ResourceType.ORE,1))
                ) return false;
        DevelopmentCard result =model.drawDevelopmentCard();
        if(result.getType()== DevCardType.MONUMENT)player.addDevelopmentCard(result);
        else player.addNewDevelopmentCard(result);
        if(model.getPeripherals()!=null)model.getPeripherals().logMessage(player,player.getNickname()+" has bought a development card");
        model.incrementVersion();
        return true;
    }

}
