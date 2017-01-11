package model.facade.shared;

import client.communication.LogEntry;
import client.data.PlayerInfo;
import client.data.RobPlayerInfo;
import model.definitions.EnumConverter;
import model.definitions.PlayerState;
import model.definitions.ResourceHand;
import model.facade.server.ServerGeneralCommandFacade;
import model.gameboard.Hex;
import model.gameboard.NodePoint;
import model.gameboard.Port;
import model.overhead.Model;
import model.peripherals.MessageLog;
import model.peripherals.TradeOffer;
import model.peripherals.TurnTracker;
import model.player.Player;
import model.resources.ResourcePile;
import model.structures.Colony;
import model.structures.Road;
import params.DiscardCardsParams;
import shared.definitions.*;
import shared.locations.HexLocation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by MTAYS on 10/6/2016.
 */
public class GuiModelFacade {

    public static TurnTracker getTurnTracker() {
        if(ModelReferenceFacade.getModel()==null)return null;
        if(ModelReferenceFacade.getModel().getPeripherals()==null)return null;
        if(ModelReferenceFacade.getModel().getPeripherals().getTurnTracker()==null)return null;
        return ModelReferenceFacade.getModel().getPeripherals().getTurnTracker();
    }
    public static int getCurrentActivePlayerIndex(){
        if(ModelReferenceFacade.getModel()==null)return -1;
        if(ModelReferenceFacade.getModel().getPeripherals()==null)return -1;
        if(ModelReferenceFacade.getModel().getPeripherals().getTurnTracker()==null)return -1;
        return ModelReferenceFacade.getModel().getPeripherals().getTurnTracker().getActiveID();
    }

    public static Player getLocalPlayer() {
        if(ModelReferenceFacade.getModel()==null)return null;
        if(ModelReferenceFacade.getModel().getPlayer()==null)return null;
        return ModelReferenceFacade.getPlayer(ModelReferenceFacade.getLocalPlayerIndex());
        //return ModelReferenceFacade.getModel().getPlayer();//This command is buggy...
    }
    public static CatanColor getLocalPlayerColor(){
        if(ModelReferenceFacade.getModel()==null)return null;
        if(ModelReferenceFacade.getLocalPlayerIndex()<0)return null;
        if(ModelReferenceFacade.getPlayer(ModelReferenceFacade.getLocalPlayerIndex())==null)return null;
        return ModelReferenceFacade.getPlayer(ModelReferenceFacade.getLocalPlayerIndex()).getColor();
        //if(ModelReferenceFacade.getModel().getPlayer()==null)return null;
        //return ModelReferenceFacade.getModel().getPlayer().getColor();
    }

    public static PlayerState getCurrentState(){
        if(ModelReferenceFacade.getModel()==null)return null;
        if(ModelReferenceFacade.getModel().getPeripherals()==null)return null;
        return ModelReferenceFacade.getModel().getPeripherals().getCurrentState();
    }
    public static void setCurrentState(PlayerState state){
        if(ModelReferenceFacade.getModel()==null)return;
        if(ModelReferenceFacade.getModel().getPeripherals()==null)return;
        ModelReferenceFacade.getModel().getPeripherals().setCurrentState(state);
    }

    public static List<Hex> getHexes() {
        if(ModelReferenceFacade.getModel()==null)return null;
        if(ModelReferenceFacade.getModel().getGameBoard().getHexes()==null)return null;
        return ModelReferenceFacade.getModel().getGameBoard().getHexes();
    }

    public static List<Port> getPorts() {
        if(ModelReferenceFacade.getModel()==null)return null;
        if(ModelReferenceFacade.getModel().getGameBoard().getPorts()==null)return null;
        return ModelReferenceFacade.getModel().getGameBoard().getPorts();
    }

    public static List<NodePoint> getNodes() {
        if(ModelReferenceFacade.getModel()==null)return null;
        if(ModelReferenceFacade.getModel().getGameBoard().getNodePoints()==null)return null;
        return ModelReferenceFacade.getModel().getGameBoard().getNodePoints();
    }
    public static List<Colony> getSettlements(){
        return getColonies(true);
    }
    public static List<Colony> getCities(){
        return getColonies(false);
    }
    public static List<Road> getRoads(){
        if(ModelReferenceFacade.getModel()==null)return null;
        if(ModelReferenceFacade.getModel().getPlayers()==null)return null;
        List<Player> players=ModelReferenceFacade.getModel().getPlayers();
        List<Road> result=new LinkedList<>();
        for(int i=0;i<players.size();++i){
            Player player=players.get(i);
            for(int j=0;j<player.getOwnedRoad().size();++j){
                result.add(player.getOwnedRoad().get(j));
            }
        }
        return result;
    }
    public static HexLocation getRobberLocation(){
        if(ModelReferenceFacade.getModel()==null)return null;
        if(ModelReferenceFacade.getModel().getPeripherals()==null)return null;
        if(ModelReferenceFacade.getModel().getPeripherals().getRobber()==null)return null;
        return ModelReferenceFacade.getModel().getPeripherals().getRobber().getHex().getHexLocation();
    }
    public static RobPlayerInfo[] getAllRobbablePlayersInfo(HexLocation location){
        List<Player> robberables=getAllRobbablePlayers(location);
        RobPlayerInfo[] result=new RobPlayerInfo[robberables.size()];
        for(int i=0;i<robberables.size();++i){
            RobPlayerInfo info=new RobPlayerInfo();
            Player player=robberables.get(i);
            info.setPlayerIndex(player.getIndex());
            info.setId(player.getInternetID());
            info.setColor(player.getColor());
            info.setName(player.getNickname());
            info.setNumCards(player.sumOfResourceCards());
            result[i]=info;
        }
        return result;
    }
    public static boolean isLocalPlayerTurn(){
        return ModelReferenceFacade.getLocalPlayerIndex()==GuiModelFacade.getCurrentActivePlayerIndex();
    }
    public static List<Player> getAllRobbablePlayers(HexLocation location){
        return getAllRobbablePlayers(location,ModelReferenceFacade.getModel(),ModelReferenceFacade.getLocalPlayerIndex());
    }
    public static List<Player> getAllRobbablePlayers(HexLocation location, Model model, int localIndex){
        //Ought to be used in conjunction with player.getSumOfResourceCards() to get resources, and player.getNickname for name
        //Assumes localPlayer is calling it, and thus is unrobbable
        List<Player> result=new LinkedList<>();
        Hex hex=model.getGameBoard().getHexAt(location);
        for(int i=0;i<hex.getNodePoints().length;++i){
            NodePoint nodePoint=hex.getNodePoint(i);
            if(nodePoint!=null){
                Colony colony=nodePoint.getColony();
                if(colony!=null){
                    if(colony.getOwningPlayer().getIndex()!=localIndex){
                        Player player=colony.getOwningPlayer();
                        if(player.sumOfResourceCards()>0){
                            boolean setCheck=false;
                            for(int j=0;j<result.size();++j){
                                if(result.get(j).getIndex()==player.getIndex()){
                                    setCheck=true;
                                    break;
                                }
                            }
                            if(!setCheck)result.add(player);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static Map<DevCardType,Integer> getSumsOfPlayableDevCards(int playerIndex){//Does not include new dev cards
        Map<DevCardType,Integer> result = new HashMap<>();
        Integer[] sums= new Integer[5];
        for(int i=0;i<5;++i)sums[i]=0;
        Player player=ModelReferenceFacade.getPlayer(playerIndex);
        if(player!=null){
            for(int i=0;i<player.getDevelopmentCards().size();++i){
                if(player.getDevelopmentCards().get(i)!=null){
                    ++sums[EnumConverter.DevCardType(player.getDevelopmentCards().get(i).getType())];
                }
            }
        }
        for(int i=0;i<5;++i){
            result.put(EnumConverter.DevCardType(i),sums[i]);
        }
        return result;
    }
    public static Map<DevCardType,Integer> getSumsOfAllDevCards(int playerIndex){
        Map<DevCardType,Integer> result=new HashMap<>();
        Integer[] sums= new Integer[5];
        for(int i=0;i<5;++i)sums[i]=0;
        Player player=ModelReferenceFacade.getPlayer(playerIndex);
        if(player!=null){
            for(int i=0;i<player.getDevelopmentCards().size();++i){
                if(player.getDevelopmentCards().get(i)!=null){
                    ++sums[EnumConverter.DevCardType(player.getDevelopmentCards().get(i).getType())];
                }
            }
            for(int i=0;i<player.getNewDevelopmentCards().size();++i){
                if(player.getNewDevelopmentCards().get(i)!=null){
                    ++sums[EnumConverter.DevCardType(player.getNewDevelopmentCards().get(i).getType())];
                }
            }
        }
        for(int i=0;i<5;++i){
            result.put(EnumConverter.DevCardType(i),sums[i]);
        }
        return result;
    }
    public static boolean anyPlayerNeedsToDiscard(){
        List<Player> players=ModelReferenceFacade.getModel().getPlayers();
        for(int i=0;i<players.size();++i){
            DiscardCardsParams params=new DiscardCardsParams();
            params.playerIndex=players.get(i).getIndex();
            params.discardedCards=new ResourceHand(0,0,0,0,0);
            if(ServerGeneralCommandFacade.canDiscardCards(params,ModelReferenceFacade.getModel()))return true;
        }
        return false;
    }
    public static PlayerInfo getLocalPlayerInfo(){
        Player localPlayer=getLocalPlayer();
        if(localPlayer != null) return localPlayer.generatePlayerInfo();
        else return null;
    }
    public static List<PlayerInfo> getAllPlayerInfo(){
        if(ModelReferenceFacade.getModel() == null) return null;
        List<PlayerInfo> result = new LinkedList<>();
        List<Player> players=ModelReferenceFacade.getModel().getPlayers();
        for(int i=0;i<players.size();++i){
            result.add(players.get(i).generatePlayerInfo());
        }
        return result;
    }
    public static ResourceHand getLocalPlayerResourceHand(){
        if(ModelReferenceFacade.getPlayer(ModelReferenceFacade.getLocalPlayerIndex())==null)return null;
        ResourceHand result=new ResourceHand(ModelReferenceFacade.getPlayer(ModelReferenceFacade.getLocalPlayerIndex()).getResourceAggregation());
        return result;
    }
    public static ResourcePile getLocalPlayerResourcePile(){
        return ModelReferenceFacade.getPlayer(ModelReferenceFacade.getLocalPlayerIndex()).getResourceAggregation();
    }
    public static TradeOffer getTradeOffer(){
        if(ModelReferenceFacade.getModel()==null)return null;
        if(ModelReferenceFacade.getModel().getPeripherals()==null)return null;
        return ModelReferenceFacade.getModel().getTradeOffer();
    }
    public static int getPlayerBestMaritimeRatio(int playerIndex,ResourceType type){
        return getPlayerBestMaritimeRatio(playerIndex,type,ModelReferenceFacade.getModel());
    }
    public static int getPlayerBestMaritimeRatio(int playerIndex,ResourceType type,Model model){
        int result=4;
        PortType ideal = EnumConverter.PortType(type);
        Player player=model.getPlayerByIndex(playerIndex);
        if(player==null)return 99;//AKA NO CAN DO
        for(int i=0;i<player.getOwnedColonies().size();++i){
            Port port=player.getOwnedColonies().get(i).getNodePoint().getPort();
            if(port!=null){
                if(port.getType()==ideal)return 2;
                if(result>3&&port.getType()== PortType.THREE)result=3;
            }
        }
        return result;
    }
    public static List<LogEntry> getAllChatAsLogEntries(){
        if(ModelReferenceFacade.getModel()==null||ModelReferenceFacade.getModel().getPeripherals()==null)return null;
        return decomposeMessageLogToLogEntries(ModelReferenceFacade.getModel().getPeripherals().getChatLog());
    }
    public static List<LogEntry> getAllSystemLogAsLogEntries(){
        if(ModelReferenceFacade.getModel()==null||ModelReferenceFacade.getModel().getPeripherals()==null)return null;
        return decomposeMessageLogToLogEntries(ModelReferenceFacade.getModel().getPeripherals().getSystemLog());
    }
    private static List<LogEntry> decomposeMessageLogToLogEntries(MessageLog messageLog){
        List result = new LinkedList();
        if(messageLog!=null){
            for(int i=0;i<messageLog.getLength();++i){
                CatanColor color=CatanColor.WHITE;
                String source=messageLog.getSource(i);
                Player player=ModelReferenceFacade.getModel().getPlayerByNickname(source);
                if(player!=null&&player.getColor()!=null)color=player.getColor();
                LogEntry temp=new LogEntry(color,messageLog.getMessage(i));
                result.add(temp);
            }
        }
        return result;
    }
    public static boolean isSetupRound(){
        PlayerState state=getCurrentState();
        if(state==PlayerState.FirstRound||state==PlayerState.SecondRound)return true;
        return false;
    }
    private static List<Colony> getColonies(boolean settlement){
        if(ModelReferenceFacade.getModel()==null)return null;
        if(ModelReferenceFacade.getModel().getPlayers()==null)return null;
        List<Player> players=ModelReferenceFacade.getModel().getPlayers();
        List<Colony> result=new LinkedList<>();
        for(int i=0;i<players.size();++i){
            Player player=players.get(i);
            for(int j=0;j<player.getOwnedColonies().size();++j){
                Colony colony=player.getOwnedColonies().get(j);
                if(settlement&&colony.getPieceType()== PieceType.SETTLEMENT)result.add(colony);
                else if(!settlement&&colony.getPieceType()==PieceType.CITY)result.add(colony);
            }
        }
        return result;
    }
}
