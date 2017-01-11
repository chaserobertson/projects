package model.serialization;

import client.data.GameInfo;
import client.data.PlayerInfo;
import com.google.gson.*;
import debugger.Debugger;
import model.definitions.EnumConverter;
import model.gameboard.*;
import model.overhead.Model;
import model.peripherals.*;
import model.player.Player;
import model.resources.DevelopmentCard;
import model.resources.ResourcePile;
import shared.definitions.*;

import java.io.Serializable;
import java.util.*;

/**
 * Created by chase on 9/21/16.
 */
public class Deserializer implements Serializable{

    /**
     * @pre model exists
     *
     * @post model is deserialized from JSON string
     *
     * @param JSONmodel
     * @return deserialized replacement model
     */
    public static Model deserialize(String JSONmodel) {
        try {
            Model model = new Model();

            Gson gsonBuilder = new GsonBuilder().create();

            JsonObject jsonObj = gsonBuilder.fromJson(JSONmodel, JsonObject.class);
            JsonObject jsonMap = jsonObj.getAsJsonObject("map");
            JsonArray jsonPlayers = jsonObj.getAsJsonArray("players");

            if (jsonPlayers != null) model = setPlayers(model, jsonPlayers);
            model = setGameBoard(model, jsonMap);
            model = setPeripherals(model, jsonObj);


            return model;
        }catch (Exception e){return null;}
    }
    public static int extractVersionNumber(String JSONmodel){
        int result=-1;
        Gson gsonBuilder = new GsonBuilder().create();

        JsonObject jsonObj = gsonBuilder.fromJson(JSONmodel, JsonObject.class);
        try{
            result = jsonObj.get("version").getAsInt();
        }
        catch (Exception e){
            Debugger.LogMessage("Deserializer:ExtractVersionNumber:failed to extract. Either algorithm wrong, or JSON wrong");
        }
        return result;
    }

    private static Model setGameBoard(Model model, JsonObject jsonMap){

        JsonArray jsonHexes = jsonMap.getAsJsonArray("hexes");
        List<Hex> boardHexes = new ArrayList<>();

        //start MatthewRevision
        //Process all Hexes
        for(int i=0;i<jsonHexes.size();++i){
            JsonObject jsonHex = jsonHexes.get(i).getAsJsonObject();
            String resourceType;
            try{
                resourceType=jsonHex.get("resource").getAsString();
            }
            catch (Exception e){
                resourceType="";
            }
            JsonObject jsonLocation = jsonHex.getAsJsonObject("location");
            int x=jsonLocation.get("x").getAsInt();
            int y=jsonLocation.get("y").getAsInt();
            int chitValue=7;
            try{
                chitValue=jsonHex.get("number").getAsInt();
            }catch(Exception e){
                chitValue=7;
            }
            boardHexes.add(model.getGameBoard().generateHexFromData(x,y,resourceType,chitValue));
        }
        try {
            model.getGameBoard().initializeFromHexes(boardHexes);
        }catch(Exception e){
            Debugger.LogMessage("Deserializer:setGameBoard:error initializeingFromHexes");
        }

        //Process all ports
        JsonArray jsonPortsT = jsonMap.getAsJsonArray("ports");
        for(int i=0;i<jsonPortsT.size();++i){
            JsonObject jsonPort=jsonPortsT.get(i).getAsJsonObject();
            String resource = "";
            if(jsonPort.get("resource")!=null){
                resource=jsonPort.get("resource").getAsString();
            }
            String direction=jsonPort.get("direction").getAsString();
            int ratio=jsonPort.get("ratio").getAsInt();
            JsonObject jsonPosition=jsonPort.get("location").getAsJsonObject();
            int posx=jsonPosition.get("x").getAsInt();
            int posy=jsonPosition.get("y").getAsInt();
            try {
                model.initializePort(resource, posx, posy, direction, ratio);
            }catch (Exception e){
                Debugger.LogMessage("Deserializer:setGameBoard:error initializing Port");
            }
        }

        //process all settlements
        JsonArray jsonSettlementsT = jsonMap.getAsJsonArray("settlements");
        for(int i=0;i<jsonSettlementsT.size();++i){
            JsonObject jsonSettlement=jsonSettlementsT.get(i).getAsJsonObject();
            int owner=jsonSettlement.get("owner").getAsInt();
            JsonObject jsonLocation=jsonSettlement.get("location").getAsJsonObject();
            int xpos=jsonLocation.get("x").getAsInt();
            int ypos=jsonLocation.get("y").getAsInt();
            String direction = jsonLocation.get("direction").getAsString();
            try{
                model.initializeColony(owner,xpos,ypos,direction,true);
            }catch(Exception e){
                Debugger.LogMessage("Deserializer:setGameBoard:Error initializing Settlement");
            }
        }

        //process all cities
        JsonArray jsonCitiesT = jsonMap.getAsJsonArray("cities");
        for(int i=0;i<jsonCitiesT.size();++i){
            JsonObject jsonCity=jsonCitiesT.get(i).getAsJsonObject();
            int owner=jsonCity.get("owner").getAsInt();
            JsonObject jsonLocation=jsonCity.get("location").getAsJsonObject();
            int xpos=jsonLocation.get("x").getAsInt();
            int ypos=jsonLocation.get("y").getAsInt();
            String direction = jsonLocation.get("direction").getAsString();
            try{
                model.initializeColony(owner,xpos,ypos,direction,false);
            }catch(Exception e){
                Debugger.LogMessage("Deserializer:setGameBoard:Error initializing City");
            }
        }

        //process all roads
        JsonArray jsonRoadsT=jsonMap.getAsJsonArray("roads");
        for(int i=0;i<jsonRoadsT.size();++i){
            JsonObject jsonRoad=jsonRoadsT.get(i).getAsJsonObject();
            int owner=jsonRoad.get("owner").getAsInt();
            JsonObject jsonLocation=jsonRoad.get("location").getAsJsonObject();
            int xpos=jsonLocation.get("x").getAsInt();
            int ypos=jsonLocation.get("y").getAsInt();
            String direction = jsonLocation.get("direction").getAsString();
            try{
                model.initializeRoad(owner,xpos,ypos,direction);
            }catch(Exception e){
                Debugger.LogMessage("Deserializer:setGameBoard:Error initializing Road");
            }
        }

        //process other data in Maps JSONobject
        int radius=jsonMap.get("radius").getAsInt();
        JsonObject robberPos=jsonMap.get("robber").getAsJsonObject();
        int robberx=robberPos.get("x").getAsInt();
        int robbery=robberPos.get("y").getAsInt();

        model.initializeMiscMapElements(radius,robberx,robbery);
        return model;
    }

    private static Model setPeripherals(Model model, JsonObject jsonObj){
        //TurnTracker
        JsonObject jsonTracker = jsonObj.getAsJsonObject("turnTracker");
        int currentActiveID=jsonTracker.get("currentTurn").getAsInt();
        String status = jsonTracker.get("status").getAsString();
        int longestRoad=jsonTracker.get("longestRoad").getAsInt();
        int largestArmy=jsonTracker.get("largestArmy").getAsInt();
        model.getPeripherals().initializeTurnTrackerData(currentActiveID,status,longestRoad,largestArmy,model);

        //tradeOffer
        try {
            JsonObject jsonTradeOffer = jsonObj.getAsJsonObject("tradeOffer");
            int senderID = jsonTradeOffer.get("sender").getAsInt();
            int receiverID = jsonTradeOffer.get("receiver").getAsInt();
            ResourcePile resourcePile = getResources(jsonTradeOffer.get("offer").getAsJsonObject());
            model.getPeripherals().initializeTradeOfferData(senderID, receiverID,resourcePile);
        }catch(Exception e) {
            //Debugger.LogMessage("Deserializer:setPeripherals:Non critical error establishing tradeOffer-might not exist");
        }

        //Bank
        ResourcePile resourcePile=getResources(jsonObj.getAsJsonObject("bank"));
        model.getPeripherals().initializeBankData(resourcePile);

        //Winner
        model.getPeripherals().initializeMiscData(jsonObj.get("winner").getAsInt());

        //Chat
        try {
            model.getPeripherals().setChatLog(getMessageLog(jsonObj.getAsJsonObject("chat")));
        }catch(Exception e){
            Debugger.LogMessage("Deserializer:setPeripherals:non critical exception in messageLog-might not exist");
        }
        try {
            model.getPeripherals().setSystemLog(getMessageLog(jsonObj.getAsJsonObject("log")));
        }catch(Exception e){
            Debugger.LogMessage("Deserializer:setPeripherals:non critical exception in systemLog-might not exist");
        }

        //Version
        int version = jsonObj.get("version").getAsInt();
        model.setVersion(version);

        return model;
    }

    private static Model setPlayers(Model model, JsonArray jsonPlayers){
        for(int i=0;i<jsonPlayers.size();++i){//Copy pasted and revised--Matthew
            JsonElement jsonElement = jsonPlayers.get(i);
            if(jsonElement.getClass() == JsonObject.class){
                JsonObject jsonPlayer = jsonElement.getAsJsonObject();

                int soldiers=jsonPlayer.get("soldiers").getAsInt();
                int victoryPoints=jsonPlayer.get("victoryPoints").getAsInt();
                int monuments = jsonPlayer.get("monuments").getAsInt();

                boolean hasPlayedDev=jsonPlayer.get("playedDevCard").getAsBoolean();
                boolean hasDiscarded=jsonPlayer.get("discarded").getAsBoolean();

                int index=jsonPlayer.get("playerIndex").getAsInt();
                String name = jsonPlayer.get("name").getAsString();
                String color = jsonPlayer.get("color").getAsString();
                int internetID = jsonPlayer.get("playerID").getAsInt();

                int remainingCities=jsonPlayer.get("cities").getAsInt();
                int remainingRoads=jsonPlayer.get("roads").getAsInt();
                int remainingSettlements=jsonPlayer.get("settlements").getAsInt();

                Player player = new Player(soldiers,victoryPoints,monuments,hasPlayedDev,hasDiscarded,index,name,color,internetID,remainingCities,remainingRoads,remainingSettlements);

                JsonObject jsonResources = jsonPlayer.getAsJsonObject("resources");
                player.setResourceAggregation(getResources(jsonResources));

                JsonObject jsonOldDevCards = jsonPlayer.getAsJsonObject("oldDevCards");
                JsonObject jsonNewDevCards = jsonPlayer.getAsJsonObject("newDevCards");
                player.setNewDevelopmentCards(getDevCards(jsonNewDevCards));
                player.setDevelopmentCards(getDevCards(jsonOldDevCards));
                model.addPlayer(player);
            }
        }
        return model;
    }

    private static ResourcePile getResources(JsonObject jsonResourceBank){
        int woodCount = jsonResourceBank.get("wood").getAsInt();
        int brickCount = jsonResourceBank.get("brick").getAsInt();
        int oreCount = jsonResourceBank.get("ore").getAsInt();
        int sheepCount = jsonResourceBank.get("sheep").getAsInt();
        int wheatCount = jsonResourceBank.get("wheat").getAsInt();
        ResourcePile rp = new ResourcePile(woodCount, brickCount, sheepCount, wheatCount, oreCount);
        return rp;
    }

    private static List<DevelopmentCard> getDevCards(JsonObject jsonDevBank){
        List<DevelopmentCard> devCards = new ArrayList<>();
        int yearOfPlenty = jsonDevBank.get("yearOfPlenty").getAsInt();
        for(int i = 0; i < yearOfPlenty; i++){
            devCards.add(new DevelopmentCard(DevCardType.YEAR_OF_PLENTY));
        }
        int monopoly = jsonDevBank.get("monopoly").getAsInt();
        for(int i = 0; i < monopoly; i++){
            devCards.add(new DevelopmentCard(DevCardType.MONOPOLY));
        }
        int soldier = jsonDevBank.get("soldier").getAsInt();
        for(int i = 0; i < soldier; i++){
            devCards.add(new DevelopmentCard(DevCardType.SOLDIER));
        }
        int roadBuilding = jsonDevBank.get("roadBuilding").getAsInt();
        for(int i = 0; i < roadBuilding; i++){
            devCards.add(new DevelopmentCard(DevCardType.ROAD_BUILD));
        }
        int monument = jsonDevBank.get("monument").getAsInt();
        for(int i = 0; i < monument; i++){
            devCards.add(new DevelopmentCard(DevCardType.MONUMENT));
        }
        return devCards;
    }

    private static void putLinesInMessageLog(MessageLog log, JsonArray jsonMessages){
        for(int i=0;i<jsonMessages.size();++i){
            JsonObject jsonLines=jsonMessages.get(i).getAsJsonObject();
            String message=jsonLines.get("message").getAsString();
            String source = jsonLines.get("source").getAsString();
            log.addMessage(source,message);
        }
    }
    private static MessageLog getMessageLog(JsonObject jsonMessageLog){
        MessageLog log=new MessageLog();
        JsonArray jsonMessages=jsonMessageLog.getAsJsonArray("lines");
        for(int i=0;i<jsonMessages.size();++i){
            JsonObject jsonLines=jsonMessages.get(i).getAsJsonObject();
            String message=jsonLines.get("message").getAsString();
            String source = jsonLines.get("source").getAsString();
            log.addMessage(source,message);
        }
        return log;
    }

    public static GameInfo deserializeGameInfo(String sGameInfo) {
        Gson gson = new GsonBuilder().create();
        JsonObject gameObject = gson.fromJson(sGameInfo, JsonObject.class);

        GameInfo gameInfo = new GameInfo();
        gameInfo.setTitle(gameObject.get("title").getAsString());
        gameInfo.setId(gameObject.get("id").getAsInt());
        JsonArray playerArray = gameObject.getAsJsonArray("players");
        for(int j = 0; j < playerArray.size(); j++) {
            JsonObject playerObject = playerArray.get(j).getAsJsonObject();
            if(!playerObject.has("name")) continue;
            PlayerInfo playerInfo = new PlayerInfo();
            JsonElement playerColor = playerObject.get("color");
            if(playerColor != null) playerInfo.setColor(EnumConverter.CatanColor(playerColor.getAsString()));
            JsonElement playerName = playerObject.get("name");
            if(playerName != null) playerInfo.setName(playerName.getAsString());
            JsonElement playerId = playerObject.get("id");
            if(playerId != null) playerInfo.setId(playerId.getAsInt());
            gameInfo.addPlayer(playerInfo);
        }
        return gameInfo;
    }

    public static List<GameInfo> deserializeGameInfoList(String sGameList) {
        List<GameInfo> gameInfos = new ArrayList<>();
        Gson gsonBuilder = new GsonBuilder().create();
        if(sGameList==null||sGameList.equals("")||sGameList.length()<=2||sGameList.equals("unsuccessful")){
            return gameInfos;
        }
        JsonArray gameArray = gsonBuilder.fromJson(sGameList, JsonArray.class);

        for(int i = 0; i < gameArray.size(); i++) {
            JsonObject gameObject = gameArray.get(i).getAsJsonObject();
            gameInfos.add(deserializeGameInfo(gameObject.toString()));
        }
        return gameInfos;
    }
}
