package model.facade.AI;

import debugger.Debugger;
import model.definitions.EnumConverter;
import model.definitions.PlayerState;
import model.definitions.ResourceHand;
import model.definitions.ResourceTypeQuantityPair;
import model.facade.server.ServerGeneralCommandFacade;
import model.facade.server.ServerPlayingCommandFacade;
import model.facade.shared.GuiModelFacade;
import model.gameboard.GameBoard;
import model.gameboard.Hex;
import model.gameboard.NodePoint;
import model.overhead.Model;
import model.player.Player;
import model.structures.Colony;
import model.structures.Road;
import params.*;
import shared.definitions.CatanColor;
import shared.definitions.PieceType;
import shared.definitions.ResourceType;
import shared.locations.EdgeLocation;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Created by MTAYS on 11/19/2016.
 */
public class AIInterface {
    public static AIInterface getInstance(){
        return new AIInterface();
    }
    public Player factoryAI(int index, CatanColor color){
        String name="";
        switch (index){
            case 0:name="John, Humanity's last hope";
                break;
            case 1:name="T-800 Arnold";
                break;
            case 2:name="T-1000 Robert";
                break;
            case 3:name="T-X Kristanna";
                break;
            default:name="Skynet";
        }
        return new Player(index,name,color,AITypes.miniSkynet.toInt());
    }
    public void AITurn(Player AI,Model model,int gameID){
        if(model==null||AI==null||AI.getInternetID()>-10){
            return;
        }
        PlayerState state=model.getPeripherals().getCurrentState();
        if(state==PlayerState.FirstRound||state==PlayerState.SecondRound){
            AISetupRound(AI,model,gameID);
        }
        else if(state==PlayerState.Rolling) {
            AIRolling(AI,model,gameID);
        }
        state=model.getPeripherals().getCurrentState();
        if(state==PlayerState.Playing){//YES THIS IS NOT ELSE IF! Rolling leads into this, but some things don't lead into it from rolling
            AIPlayingRound(AI,model,gameID);
        }
    }
    public void AISetupRound(Player AI,Model model,int gameID){
        try {
            BuildSettlementParams params = new BuildSettlementParams();
            params.setupRound = true;
            params.gameId = gameID;
            params.playerIndex = AI.getIndex();
            params.free = true;
            NodePoint nodePointStore = null;
            try {
                nodePointStore = calculateOptimalNodePoint(AI, true, false, model);
                params.vertexLocation=nodePointStore.getNormalizedVertexLocation();
            }catch (Exception e){
                Debugger.LogMessage("AIInterface:AISetupRound:Exception caught calculating Optimal NodePoint");
            }
            if (nodePointStore==null)Debugger.LogMessage("AIInterface:AISetupRound:nodePointStore is null!");
            if(nodePointStore!=null&&ServerPlayingCommandFacade.canBuildSettlement(params,model)){
                params.generateCommand().execute();
            }
            else Debugger.LogMessage("AIInterface:AISetupRound:PlaceSettlement failed");
            if(nodePointStore!=null){
                EdgeLocation optimalEdge=calculateOptimalEdgeLocation(AI,true,model);
                if(optimalEdge!=null){
                    BuildRoadParams params2=new BuildRoadParams(AI.getIndex(),optimalEdge,true,true);
                    params2.gameId= gameID;
                    if(!ServerPlayingCommandFacade.canPlaceRoad(params2,model)){
                        Debugger.LogMessage("AIInterface:AISetupRound:road building failed");
                    }
                    else params2.generateCommand().execute();
                }
                else{
                    Debugger.LogMessage("AIInterface:AISetupRound:OptimalEdge returned null");
                }
            }
        }catch (Exception e){
            Debugger.LogMessage("ServerPlayingCommandFacade:AITurn:Some Exception got thrown during AI setupround");
        }
    }
    public void AIRolling(Player AI,Model model,int gameID){
        try {
            RollNumberParams params = new RollNumberParams(AI.getIndex());
            params.gameId=gameID;
            if (!ServerGeneralCommandFacade.canRoll(params, model)) {
                Debugger.LogMessage("ServerPlayingCOmmandFacade:AiTurn:Ai has failed to roll");
            }
            else params.generateCommand().execute();
            //model.getPeripherals().setCurrentState(PlayerState.Playing);
        } catch (Exception e){
            Debugger.LogMessage("ServerPlayingCommandFacade:AiTurn:Some exception got thrown during AI \"rolling\" turn");
        }
    }
    public void AIPlayingRound(Player AI,Model model,int gameID){
        if(AI==null||model==null||AI.getInternetID()>-10)return;
        boolean loop=true;
        while (loop){
            loop=false;
            if(AITryBuildSettlement(AI,model,gameID))loop=true;
            if(AITryBuildCity(AI,model,gameID))loop=true;
            if(AITryBuildRoad(AI,model,gameID))loop=true;
            AITryBuyDevCard(AI,model,gameID);
            if(AITryUseDevCard(AI,model,gameID))loop=true;
        }
    }
    public boolean AIDiscard(Player AI, Model model,int gameID){
        if(AI==null||AI.getInternetID()>-10||model==null)return false;
        if(AI.sumOfResourceCards()<=7)return false;
        int toDiscard=AI.sumOfResourceCards()/2;
        ResourceHand toDiscardHand=new ResourceHand(0,0,0,0,0);
        while(toDiscard>0){
            ResourceType toTransfer=AI.getResourceAggregation().chooseRandomResource();
            AI.getResourceAggregation().transferResourcesTo(model.getResourceAggregation(),
                    new ResourceTypeQuantityPair(toTransfer,1));
            --toDiscard;
            toDiscardHand.addResource(toTransfer,1);
        }
        model.getResourceAggregation().transferResourcesTo(AI.getResourceAggregation(),toDiscardHand);
        DiscardCardsParams params=new DiscardCardsParams(AI.getIndex(),toDiscardHand);
        params.generateCommand().execute();
        return true;
    }
    public void AIRob(Player AI, Model model,int gameID){
        if(AI==null||model==null||AI.getInternetID()>-10||model.getGameBoard()==null||model.getGameBoard().getHexes()==null
                ||model.getPeripherals()==null||model.getPeripherals().getRobber()==null)return;
        RobPlayerParams params=new RobPlayerParams();params.location=null;
        Random random = new Random();
        int mid=random.nextInt(model.getGameBoard().getHexes().size());
        for(int i=mid;i<model.getGameBoard().getHexes().size();++i){
            Hex hex=model.getGameBoard().getHexes().get(i);
            if(hex!=null&&(model.getPeripherals().getRobber().getHex()==null||
                    model.getPeripherals().getRobber().getHex().getHexLocation()!=hex.getHexLocation())){
                params.location=hex.getHexLocation();
                break;
            }
        }
        if(params.location==null){
            for(int i=0;i<mid;++i){
                Hex hex=model.getGameBoard().getHexes().get(i);
                if(hex!=null&&(model.getPeripherals().getRobber().getHex()==null||
                        model.getPeripherals().getRobber().getHex().getHexLocation()!=hex.getHexLocation())){
                    params.location=hex.getHexLocation();
                    break;
                }
            }
        }
        params.playerIndex=AI.getIndex();
        List<Player> robbables= GuiModelFacade.getAllRobbablePlayers(params.location,model,AI.getIndex());
        if(robbables.size()>0)params.victimIndex=robbables.get(0).getIndex();
        else params.victimIndex=-1;
        params.gameId=gameID;
        if(!ServerPlayingCommandFacade.canRobPlayer(params,model)){
            Debugger.LogMessage("AICommands:Error trying to Rob players");
            Debugger.LogMessage(params.toString());
        }
        else params.generateCommand().execute();
        model.getPeripherals().setCurrentState(PlayerState.Playing);
        //if(model.getPeripherals()!=null)model.getPeripherals().logMessage(AI,AI.getNickname()+" has opted not to move the robber with his AI cheat powers");
        for(Player player:model.getPlayers()){
            if(player.getInternetID()>-10&&player.getHasDiscarded()){
                AICommands.AIEndTurnLoop(model,gameID);
                break;
            }
        }
    }
    public void AIReceiveTradeOffer(Player AI, Model model,int gameID){
        ResourceHand offer=model.getPeripherals().getActiveTradeOffer().getOffer();
        AcceptTradeParams params=new AcceptTradeParams();
        params.willAccept=true;
        params.playerIndex=AI.getIndex();
        params.acceptor=AI.getIndex();
        params.offeror=model.getPeripherals().getActiveTradeOffer().getOfferer().getIndex();
        String offererName=model.getPeripherals().getActiveTradeOffer().getOfferer().getNickname();

        params.willAccept=offer.sum()>2;
        params.gameId=gameID;
        SendChatParams chatParams=new SendChatParams(AI.getIndex(),"");
        chatParams.gameId=gameID;
        try{
            if(!params.willAccept||!ServerGeneralCommandFacade.canAcceptTrade(params,model)){
                chatParams.content="I do not barter with measly humans";
            }
            else{
                chatParams.content="I am pleased with your humility "+offererName;
            }
        }catch (Exception e){
            Debugger.LogMessage("AIInterface:AiReceiveTradeOffer:Exception trying to accept trade");
        }
        params.generateCommand().execute();
        chatParams.generateCommand().execute();
    }
    protected EdgeLocation calculateOptimalEdgeLocation(Player AI, boolean setUp, Model model){
        if(AI==null||AI.getInternetID()>-10)return null;
        NodePoint node1=null;
        NodePoint node2=null;
        int best=-1;
        List<ResourceType> desired=calculateDesired(AI);
        if(setUp){
            for (Colony colony: AI.getOwnedColonies()){
                if(colony.getNodePoint().getRoads().size()<=0){
                    for(NodePoint nodePoint:colony.getNodePoint().getNeighbors()){
                        int temp=calculateNodeRoadValue(AI,nodePoint,model,3,colony.getNodePoint(),desired);
                        if(temp>best){
                            node1=colony.getNodePoint();
                            node2=nodePoint;
                            best=temp;
                        }
                    }
                }
            }
        }
        else{
            for(Road road:AI.getOwnedRoad()){
                for(NodePoint nodePoint:road.getNode1().getNeighbors()){
                    if(!road.getNode1().sharesRoadWith(nodePoint)) {
                        int temp = calculateNodeRoadValue(AI, nodePoint, model, 3, road.getNode1(), desired);
                        if (temp > best) {
                            node1 = road.getNode1();
                            node2 = nodePoint;
                            best = temp;
                        }
                    }
                }
                for(NodePoint nodePoint:road.getNode2().getNeighbors()){
                    if(!road.getNode2().sharesRoadWith(nodePoint)) {
                        int temp = calculateNodeRoadValue(AI, nodePoint, model, 4, road.getNode2(), desired);
                        if (temp > best) {
                            node1 = road.getNode2();
                            node2 = nodePoint;
                            best = temp;
                        }
                    }
                }
            }
        }
        if(node1!=null&&node2!=null){
            //We found something!
            return NodePoint.determineConnectingEdge(node1,node2);//Note, this could be null if something went wrong.
        }
        return null;
    }
    protected int calculateNodeRoadValue(Player AI, NodePoint node, Model model, int iterationsLeft, NodePoint previous, List<ResourceType> desired){//-1 indicates not useful
        //When iteratiionsLeft==0 don't look beyond this node.
        if(AI==null||AI.getInternetID()>-10||node==null)return -1;//desired can be null, previous can be null
        int best=calculateNodePointValue(AI,node,true,false,desired);//Yes, it claims to be setup. This is because it doesn't require linked roads
        if(best!=-1)best+=(iterationsLeft*2);
        if(iterationsLeft>0){
            if(node.getColony()!=null&&node.getColony().getOwningPlayer().getIndex()!=AI.getIndex())return -1;//Don't look past this settlement
            //Try to connect to the other nodes...
            for(NodePoint nodePoint:node.getNeighbors()){
                if(nodePoint!=previous&&!node.sharesRoadWith(nodePoint)){
                    int temp=calculateNodeRoadValue(AI,nodePoint,model,iterationsLeft-1,node,desired);
                    if(temp>best)best=temp;
                }
            }
        }
        return best;
    }
    protected NodePoint calculateOptimalNodePoint(Player AI, boolean setUp, boolean city, Model model){
        NodePoint result=null;
        List<ResourceType> desired=calculateDesired(AI);
        int best=0;
        for(NodePoint nodePoint:model.getGameBoard().getNodePoints()){
            int temp=calculateNodePointValue(AI,nodePoint,setUp,city,desired);
            if(temp>best){
                best=temp;
                result=nodePoint;
            }
        }
        return result;
    }
    protected int calculateNodePointValue(Player AI, NodePoint nodePoint, boolean setUp, boolean city, List<ResourceType> desired) {
        //Returns -1 if nodePoint is not viable
        //Greater value returns higher values
        if(nodePoint.getColony()!=null&&!city){
            return -1;
        }
        else if(city&&(nodePoint.getColony()==null||nodePoint.getColony().getPieceType()== PieceType.CITY))return -1;
        if(city&&nodePoint.getColony().getOwningPlayer().getIndex()!=AI.getIndex())return -1;
        if(!city){
            for(NodePoint nodePoint1:nodePoint.getNeighbors()){
                if(nodePoint1.getColony()!=null)return -1;
            }
        }
        if(!setUp&&!city){
            boolean found=false;
            for(Road road:nodePoint.getRoads()){
                if(road.getOwningPlayer().getIndex()==AI.getIndex()){
                    found=true;
                    break;
                }
            }
            if(!found)return -1;
        }
        //Now that we have determined that nodePoint can be built on, we need to calculate its value
        int result=0;
        for(Hex hex:nodePoint.getHexes()){
            int chitvalue=hex.getChitValue();
            if(chitvalue<7)result+=chitvalue;
            else if(chitvalue>7)result+=(13-chitvalue);
            //else result+=0;//desert and ocean give no bonus
            if(desired!=null&&hex.getResourceType()!=null){
                for(ResourceType resourceType:desired){
                    if(hex.getResourceType()==resourceType){
                        result+=2;//+2 points if its a resource you need!
                    }
                }
            }
        }
        //if(!city&&nodePoint.getPort()!=null)result+=2;//Not that Ai knows how to use ports...
        return result;
    }
    protected List<ResourceType> calculateDesired(Player AI){
        List<ResourceType> desired=new LinkedList<>();
        if(AI==null||AI.getInternetID()>-10)return desired;
        ResourceHand sums=new ResourceHand(0,0,0,0,0);
        for(Colony colony:AI.getOwnedColonies()){
            List<ResourceTypeQuantityPair> colonyQuants=colony.getResources();
            for(ResourceTypeQuantityPair quantityPair:colonyQuants){
                sums.addResource(quantityPair.type,quantityPair.quantity);
            }
        }
        reduce:
        while(true){
            for(int quant:sums.getResources()){
                if(quant<=0)break reduce;
            }
            for(int i=0;i<ResourceType.numberOfTypes();++i){
                sums.getResources()[i]=sums.getResources()[i]-1;
            }
        }
        for(int i=0;i<ResourceType.numberOfTypes();++i){
            if(sums.getResource(i)==0){
                desired.add(EnumConverter.ResourceType(sums.getResource(i)));
            }
        }
        return desired;
    }

    protected boolean AITryBuildSettlement(Player AI, Model model,int gameID){
        BuildSettlementParams params=new BuildSettlementParams(null,false,false);
        params.playerIndex=AI.getIndex();
        if(ServerPlayingCommandFacade.canBuildSettlement(params,model)){
            NodePoint optimalSpot=calculateOptimalNodePoint(AI,false,false,model);
            if(optimalSpot!=null){
                params = new BuildSettlementParams();
                params.setupRound = true;
                params.gameId = gameID;
                params.playerIndex = AI.getIndex();
                params.free = true;
                params.vertexLocation=optimalSpot.getNormalizedVertexLocation();
                if(!ServerPlayingCommandFacade.canPlaceSettlement(params,model)){
                    Debugger.LogMessage("AIInterface:AITryBuildSettlement:failed in trying to place settlement");
                    Debugger.LogMessage(params.toString());
                }
                else {
                    params.generateCommand().execute();
                    return true;
                }
            }
        }
        return false;
    }
    protected boolean AITryBuildCity(Player AI, Model model,int gameID){
        BuildCityParams params=new BuildCityParams(AI.getIndex(),null);
        params.playerIndex=AI.getIndex();
        if(ServerPlayingCommandFacade.canBuildCity(params,model)){
            NodePoint optimalSpot=calculateOptimalNodePoint(AI,false,true,model);
            if(optimalSpot!=null){
                params = new BuildCityParams();
                params.gameId = gameID;
                params.playerIndex = AI.getIndex();
                params.vertexLocation=optimalSpot.getNormalizedVertexLocation();
                if(!ServerPlayingCommandFacade.canPlaceCity(params,model)){
                    Debugger.LogMessage("AIInterface:AITryBuildCity:failed in trying to place city");
                    Debugger.LogMessage(params.toString());
                }
                else {
                    params.generateCommand().execute();
                    return true;
                }
            }
        }
        return false;
    }
    protected boolean AITryBuildRoad(Player AI, Model model,int gameID){
        BuildRoadParams params=new BuildRoadParams(AI.getIndex(),null,false,false);
        if(ServerPlayingCommandFacade.canBuildARoad(params,model)){
            EdgeLocation optimalEdge=calculateOptimalEdgeLocation(AI,false,model);
            if(optimalEdge!=null) {
                params = new BuildRoadParams();
                params.gameId = gameID;
                params.playerIndex = AI.getIndex();
                params.free = false;
                params.setupRound = false;
                params.roadLocation =optimalEdge;
                if(!ServerPlayingCommandFacade.canPlaceRoad(params,model)){
                    Debugger.LogMessage("AIInterface:AiTryBuildRoad:Operation failed");
                    Debugger.LogMessage(params.toString());
                }
                else {
                    params.generateCommand().execute();
                    return true;
                }
            }
        }
        return false;
    }
    protected void AITryBuyDevCard(Player AI, Model model,int gameID){

    }
    protected boolean AITryUseDevCard(Player AI, Model model,int gameID){
        return false;
    }

    protected boolean canSecureResources(Player AI, Model model, ResourceHand desired){
        if(AI==null||AI.getInternetID()>-10||model==null||desired==null)return false;
        if(AI.getResourceAggregation().canTransferResourcesTo(model.getResourceAggregation(),desired))return true;
        ResourceHand maritimeRatios=new ResourceHand(0,0,0,0,0);
        for(int i=0;i<ResourceType.numberOfTypes();++i){
            maritimeRatios.setResource(i,GuiModelFacade.getPlayerBestMaritimeRatio(AI.getIndex(),EnumConverter.ResourceType(i),model));
        }
        return false;
    }
    protected boolean tryToSecureResources(Player AI, Model model, ResourceHand desired,int gameID){
        if(AI==null||AI.getInternetID()>-10||model==null||desired==null)return false;
        if(AI.getResourceAggregation().canTransferResourcesTo(model.getResourceAggregation(),desired))return true;
        if(canSecureResources(AI,model,desired)){
            //Now has to calculate Resources to trade
        }

        return false;
    }
}

