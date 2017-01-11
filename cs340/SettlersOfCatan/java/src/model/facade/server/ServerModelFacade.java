package model.facade.server;

import client.data.GameInfo;
import client.data.PlayerInfo;
import debugger.Debugger;
import model.definitions.EnumConverter;
import model.facade.AI.AITypes;
import model.facade.shared.SerializeFacade;
import model.overhead.Model;
import params.AddAIRequest;
import shared.definitions.CatanColor;

/**
 * Created by MTAYS on 10/25/2016.
 */
public class ServerModelFacade implements IServerModelFacade {//NO ONE SHOULD CREATE ONE OF THESE EXCEPT THE ServerModelListStorage
    private Model localModel;
    private String gameName;
    private int gameID;
    //Might be worth storing other game specific info here, such as who is currently there.
    public ServerModelFacade(String name,int gameID,Model model){
        gameName=name;
        this.gameID=gameID;
        localModel=model;
    }
    //LocalAccess Commands
    @Override
    public String getGameName(){return gameName;}
    @Override
    public void setGameName(String gameName){this.gameName=gameName;}
    @Override
    public int getGameID(){return gameID;}
    @Override
    public void setGameID(int gameID){this.gameID=gameID;}
    @Override
    public Model getLocalModel(){return localModel;}
    @Override
    public String getLocalModelString(){return localModel.toString();}
    @Override
    public void setLocalModel(Model model){localModel=model;}

    //ModelAccess commands
    //To get all player info call GuiModelFacade.getAllPlayerInfo
    @Override
    public int getModelVersion(){
        if(localModel==null)return -1;
        return localModel.getVersion();
    }


    //ExecutableCommands
    //PlayerInfo does not require, and will not check, for color. It will only check the minimal provided, which I do not yet know
    //Checks for size.
    @Override
    public boolean canAddPlayer(PlayerInfo playerInfo){
        return false;
    }
    @Override
    public boolean addPlayer(PlayerInfo playerInfo){
        if(localModel==null)return false;
        if(!canAddPlayer(playerInfo))return false;
        localModel.addPlayer(playerInfo.getPlayerIndex(),playerInfo.getName(),playerInfo.getColor(),playerInfo.getId());
        return true;
    }
    @Override
    public GameInfo getGameInfo(){
        GameInfo result = new GameInfo();
        result.setId(gameID);
        result.setTitle(gameName);
        for (PlayerInfo playerInfo:localModel.getPlayerInfos()) {
            result.addPlayer(playerInfo);
        }
        return result;
    }
    //Checks if player is registered to the game in session.
    @Override
    public boolean canRejoin(PlayerInfo playerInfo){
        return false;
    }//These should be called when a player trys to log in, might reject them.
    @Override
    public boolean rejoin(PlayerInfo playerInfo){
        return false;
    }
    //Not certain if this function should be here, or elsewhere. Depends on if logged in players are stored locally
    @Override
    public boolean canStart(){
        return false;
    }
    @Override
    public void incrementModelVersion(){
        if(localModel==null)return;
        localModel.incrementVersion();
    }
    @Override
    public void addAI(AddAIRequest params){
        try {
            if (localModel == null) return;
            if (localModel.getPlayers().size() < 4) {
                AITypes type = AITypes.AIType(params.AIType);
                if (type != null) {
                    localModel.addPlayer(type.getInterface().factoryAI(localModel.getPlayers().size(), generateUnusedColor()));
                }
                else{
                    Debugger.LogMessage(params.AIType);
                }
            }
        }catch (NullPointerException e){
            Debugger.LogMessage("Error Adding AI");
        }
    }

    @Override
    public ServerModelFacade generateServerModel(String serializedModel,int gameID,String gameName){
        return new ServerModelFacade(SerializeFacade.deserializeModelFromJSON(serializedModel),gameID,gameName);
    }
    private ServerModelFacade(Model model, int gameID, String gameName){
        localModel=model;
        this.gameID=gameID;
        this.gameName=gameName;
    }
    private CatanColor generateUnusedColor(){
        CatanColor result;
        int i=0;
        while((result= EnumConverter.CatanColor(i))!=null){
            ++i;
            boolean found=false;
            for(CatanColor color:localModel.getAllColors()){
                if(color.equals(result)){
                    found=true;
                    break;
                }
            }
            if(!found)return result;
        }
        if(result==null)result=CatanColor.BROWN;
        return result;
    }
}
