package model.facade.shared;

import client.data.GameInfo;
import client.data.PlayerInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import debugger.Debugger;
import model.definitions.EnumConverter;
import model.overhead.Model;
import model.serialization.Deserializer;
import model.serialization.Serializer;

import java.util.List;

/**
 * Created by MTAYS on 9/21/2016.
 */
public class SerializeFacade {

    /**
     * @param JSON
     * 		- the JSON from the server to be
     * @pre JSON is valid
     * @post A valid JSON will recreate the model in the client
     */
    public static void buildModelFromJSON(String JSON){
        if(JSON==null||JSON.equals("")){
            Debugger.LogMessage("SerializeFacade:BuildModelFromJSON:An empty or null string was entered");
            return;
        }
        Model model=Deserializer.deserialize(JSON);
        //Might be worth it to just delete this check and auto replace;
        if(model!=null)ModelReferenceFacade.getInstance().setmodel(model);
    }

    /**
     * @pre none
     * @post will create a valid JSON of the current model to pass to the server
     * @return JSON string
     */
    public static String convertModelToJSON(){
        if(ModelReferenceFacade.getModel()==null)return "";
        return Serializer.serialize(ModelReferenceFacade.getModel());
    }
    public static String convertModelToJSON(Model model){
        if(model==null)return "";
        return Serializer.serialize(model);
    }

    public static Model deserializeModelFromJSON(String JSON) {
        return Deserializer.deserialize(JSON);
    }
    public static String serializeGameInfoList(List<GameInfo> gameInfos){
        JsonArray result=new JsonArray();
        if(gameInfos==null){
            return result.toString();
        }
        for(int i=0;i<gameInfos.size();++i){
            result.add(serializeGameInfo(gameInfos.get(i)));
        }
        return result.toString();
    }
    public static JsonObject serializeGameInfo(GameInfo gameInfo){
        JsonObject jsonGameInfo=new JsonObject();
        if(gameInfo!=null){
            if(gameInfo.getTitle()==null)gameInfo.setTitle("");
            jsonGameInfo.addProperty("title",gameInfo.getTitle());
            jsonGameInfo.addProperty("id",gameInfo.getId());
            JsonArray jsonPlayersArray=new JsonArray();
            for(int j=0;j<gameInfo.getPlayers().size();++j){
                JsonObject jsonPlayer=new JsonObject();
                PlayerInfo playerInfo=gameInfo.getPlayers().get(j);
                if(playerInfo!=null){
                    jsonPlayer.addProperty("color", EnumConverter.CatanColor(playerInfo.getColor()));
                    jsonPlayer.addProperty("name",playerInfo.getName());
                    jsonPlayer.addProperty("id",playerInfo.getId());
                }
                jsonPlayersArray.add(jsonPlayer);
            }
            jsonGameInfo.add("players",jsonPlayersArray);
        }
        return jsonGameInfo;
    }
}
