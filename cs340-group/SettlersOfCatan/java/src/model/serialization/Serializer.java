package model.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import debugger.Debugger;
import model.definitions.EnumConverter;
import model.gameboard.GameBoard;
import model.gameboard.Hex;
import model.gameboard.Port;
import model.overhead.Model;
import model.peripherals.MessageLog;
import model.peripherals.Peripherals;
import model.peripherals.TradeOffer;
import model.player.Player;
import model.resources.DevelopmentCard;
import model.resources.ResourcePile;
import model.structures.Colony;
import model.structures.Road;
import shared.definitions.PieceType;
import shared.definitions.PortType;
import shared.definitions.ResourceType;
import shared.locations.HexLocation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chase on 9/21/16.
 */
public class Serializer implements Serializable {
    private static Model model;
    /**
     * @pre model exists
     *
     * @post model is unmodified, translated to JSON string
     *
     * @param modelToSerialize
     * @return JSON serialized model
     */
    public static String serialize(Model modelToSerialize) {
        model=modelToSerialize;
        JsonObject result = new JsonObject();
        if(model==null){
            Debugger.LogMessage("Model was blank");
            return result.toString();
        }
        result.add("bank",serializeBank());
        result.add("chat",serializeChat());
        result.add("log",serializeLog());
        result.add("map",serializeMap());
        result.add("players",serializePlayers());
        result.add("tradeOffer",serializeTradeOffer());
        result.add("turnTracker",serializeTurnTracker());
        result.addProperty("version", model.getVersion());
        result.addProperty("winner",model.getPeripherals().getWinner());
        return result.toString();
    }
    private static JsonObject serializeBank(){
        if(model.getPeripherals()==null||model.getPeripherals().getResourcePile()==null){
            Debugger.LogMessage("Serializer:Peripherals or Bank was null");
            return new JsonObject();
        }
        return serializeResourcePile(model.getPeripherals().getResourcePile());
    }
    private static JsonObject serializeChat(){return serializeMessageLog(model.getPeripherals().getChatLog());}
    private static JsonObject serializeLog(){
        return serializeMessageLog(model.getPeripherals().getSystemLog());
    }
    private static JsonObject serializeMap(){
        JsonObject result=new JsonObject();
        result.addProperty("radius",model.getPeripherals().getRadius());
        result.add("hexes",serializeHexes());
        result.add("ports",serializePorts());
        result.add("roads",serializeRoads());
        result.add("settlements",serializeSettlements());
        result.add("cities",serializeCities());
        result.add("robber",serializeRobber());
        return result;
    }
    private static JsonArray serializePlayers(){
        return serializePlayersArray();
    }
    private static JsonObject serializeTradeOffer(){
        TradeOffer tradeOffer= model.getTradeOffer();
        JsonObject result = new JsonObject();
        if(tradeOffer==null||tradeOffer.getOfferer()==null||tradeOffer.getReceiver()==null)return result;
        else result.addProperty("sender",tradeOffer.getOfferer().getIndex());
        result.addProperty("receiver",tradeOffer.getReceiver().getIndex());
        result.add("offer",serializeResourcePile(new ResourcePile(tradeOffer.getOffer())));
        return result;
    }
    private static JsonObject serializeTurnTracker(){
        JsonObject result = new JsonObject();
        try {

            Peripherals peripherals = model.getPeripherals();
            result.addProperty("currentTurn", peripherals.getTurnTracker().getActiveID());
            result.addProperty("status", EnumConverter.PlayerState(peripherals.getTurnTracker().getCurrentState()));
            if (peripherals.getLongestRoad().getPlayer() == null) result.addProperty("longestRoad", -1);
            else result.addProperty("longestRoad", peripherals.getLongestRoad().getPlayer().getIndex());
            if (peripherals.getLargestArmy().getPlayer() == null) result.addProperty("largestArmy", -1);
            else result.addProperty("largestArmy", peripherals.getLargestArmy().getPlayer().getIndex());
        }
        catch (Exception e){
            Debugger.LogMessage("Error serializingTurnTracker");
        }
        return result;
    }

    private static JsonObject serializeResourcePile(ResourcePile resourcePile){
        JsonObject result=new JsonObject();
        if(resourcePile!=null) {
            result.addProperty("brick", resourcePile.getQuantityResourceType(ResourceType.BRICK));
            result.addProperty("ore", resourcePile.getQuantityResourceType(ResourceType.ORE));
            result.addProperty("wheat", resourcePile.getQuantityResourceType(ResourceType.WHEAT));
            result.addProperty("sheep", resourcePile.getQuantityResourceType(ResourceType.SHEEP));
            result.addProperty("wood", resourcePile.getQuantityResourceType(ResourceType.WOOD));

        }
        return result;
    }
    private static JsonObject serializeMessageLog(MessageLog messageLog){
        JsonObject result=new JsonObject();
        JsonArray lines=new JsonArray();
        for(int i=0;i<messageLog.getLength();++i){
            JsonObject line=new JsonObject();
            line.addProperty("source",messageLog.getSource(i));
            line.addProperty("message",messageLog.getMessage(i));
            lines.add(line);
        }
        //if(lines.size()==0)return null; Shouldn't have to do this.
        result.add("lines",lines);
        return result;
    }
    private static JsonArray serializeHexes(){
        JsonArray result=new JsonArray();
        GameBoard gameBoard=model.getGameBoard();
        for(int i=0;i<gameBoard.getHexes().size();++i){
            JsonObject jsonHex=new JsonObject();
            Hex hex=gameBoard.getHexes().get(i);
            jsonHex.add("location", serializeLocation(hex.getHexLocation()));
            jsonHex.addProperty("resource",EnumConverter.HexType(hex.getHexType()));//Might need to check for desert notation
            jsonHex.addProperty("number",hex.getChitValue());
            result.add(jsonHex);
        }
        return result;
    }
    private static JsonArray serializePorts(){
        JsonArray result=new JsonArray();
        GameBoard gameBoard=model.getGameBoard();
        for(int i=0;i<gameBoard.getPorts().size();++i){
            JsonObject jsonPort=new JsonObject();
            Port port=gameBoard.getPorts().get(i);
            jsonPort.add("location",serializeLocation(port.getSeaHex()));
            jsonPort.addProperty("direction",EnumConverter.EdgeDirectionSH(port.getEdgeDirection()));
            if(port.getType()== PortType.THREE){
                jsonPort.addProperty("ratio",3);
            }
            else{
                jsonPort.addProperty("ratio",2);
                jsonPort.addProperty("resource",EnumConverter.PortType(port.getType()));//probably can put this outside the else
            }
            result.add(jsonPort);
        }
        return result;
    }
    private static JsonArray serializeRoads(){
        JsonArray result=new JsonArray();
        List<Player> players = model.getPlayers();
        for(int i=0;i<players.size();++i){
            Player player=players.get(i);
            for(int j=0;j<player.getOwnedRoad().size();++j){
                Road road=player.getOwnedRoad().get(j);
                JsonObject jsonRoad=new JsonObject();
                jsonRoad.addProperty("owner",player.getIndex());
                JsonObject location= serializeLocation(road.getLocation().getHexLoc());
                location.addProperty("direction",EnumConverter.EdgeDirection(road.getLocation().getDir()));//This may or may not be right. Might need EdgeDirectionSH
                jsonRoad.add("location",location);
                result.add(jsonRoad);
            }
        }
        return result;
    }
    private static JsonArray serializeSettlements(){
        JsonArray result=new JsonArray();
        List<Player> players = model.getPlayers();
        for(int i=0;i<players.size();++i){
            Player player=players.get(i);
            for(int j=0;j<player.getOwnedColonies().size();++j) {
                Colony colony=player.getOwnedColonies().get(j);
                if(colony.getPieceType()== PieceType.SETTLEMENT){
                    JsonObject jsonSettlement=new JsonObject();
                    jsonSettlement.addProperty("owner",player.getIndex());
                    JsonObject location=serializeLocation(colony.getNodePoint().getNormalizedVertexLocation().getHexLoc());
                    location.addProperty("direction",EnumConverter.VertexDirectionToString(colony.getNodePoint().getNormalizedVertexLocation().getDir()));
                    jsonSettlement.add("location",location);
                    result.add(jsonSettlement);
                }
            }
        }
        return result;
    }
    private static JsonArray serializeCities(){
        JsonArray result=new JsonArray();
        List<Player> players = model.getPlayers();
        for(int i=0;i<players.size();++i){
            Player player=players.get(i);
            for(int j=0;j<player.getOwnedColonies().size();++j) {
                Colony colony=player.getOwnedColonies().get(j);
                if(colony.getPieceType()== PieceType.CITY){
                    JsonObject jsonSettlement=new JsonObject();
                    jsonSettlement.addProperty("owner",player.getIndex());
                    JsonObject location=serializeLocation(colony.getNodePoint().getNormalizedVertexLocation().getHexLoc());
                    location.addProperty("direction",EnumConverter.VertexDirectionToString(colony.getNodePoint().getNormalizedVertexLocation().getDir()));
                    jsonSettlement.add("location",location);
                    result.add(jsonSettlement);
                }
            }
        }
        return result;
    }
    private static JsonObject serializeRobber(){
        return serializeLocation(model.getPeripherals().getRobber().getHex().getHexLocation());
    }
    private static JsonArray serializePlayersArray(){
        JsonArray result=new JsonArray();
        for(int i=0;i<model.getPlayers().size();++i){
            result.add(serializePlayer(model.getPlayers().get(i)));
        }
        return result;
    }
    private static JsonObject serializeLocation(HexLocation hexLocation){
        JsonObject result=new JsonObject();
        result.addProperty("x",hexLocation.getX());
        result.addProperty("y",hexLocation.getY());
        return result;
    }
    private static JsonObject serializeDevCards(List<DevelopmentCard> developmentCards){
        JsonObject result=new JsonObject();
        int monopoly=0,monument=0,roadBuilding=0,soldier=0,yearOfPlenty=0;
        for(int i=0;i<developmentCards.size();++i){
            switch (developmentCards.get(i).getType()){
                case MONOPOLY:++monopoly;
                    break;
                case MONUMENT:++monument;
                    break;
                case ROAD_BUILD:++roadBuilding;
                    break;
                case SOLDIER:++soldier;
                    break;
                case YEAR_OF_PLENTY:++yearOfPlenty;
                    break;
                default:
                    Debugger.LogMessage("Serializer:serializeDevCards:processed unidentified DevCard");
            }
        }
        result.addProperty("monopoly",monopoly);
        result.addProperty("monument",monument);
        result.addProperty("roadBuilding",roadBuilding);
        result.addProperty("soldier",soldier);
        result.addProperty("yearOfPlenty",yearOfPlenty);
        return result;
    }
    private static JsonObject serializePlayer(Player player){
        JsonObject result=new JsonObject();
        result.addProperty("cities",player.getRemainingCities());
        result.addProperty("color",EnumConverter.CatanColor(player.getColor()));
        result.addProperty("discarded",player.getHasDiscarded());
        result.addProperty("monuments",player.getPlayedMonuments());
        result.addProperty("name",player.getNickname());
        result.add("newDevCards",serializeDevCards(player.getNewDevelopmentCards()));
        result.add("oldDevCards",serializeDevCards(player.getDevelopmentCards()));
        result.addProperty("playerIndex",player.getIndex());
        result.addProperty("playedDevCard",player.getHasPlayedDev());
        result.addProperty("playerID",player.getInternetID());
        result.add("resources",serializeResourcePile(player.getResourceAggregation()));
        result.addProperty("roads",player.getRemainingRoads());
        result.addProperty("settlements",player.getRemainingSettlements());
        result.addProperty("soldiers",player.getArmySize());
        result.addProperty("victoryPoints",player.getVictoryPoints());
        return result;
    }
}
