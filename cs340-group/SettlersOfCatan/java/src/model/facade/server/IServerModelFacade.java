package model.facade.server;

import client.data.GameInfo;
import client.data.PlayerInfo;
import model.overhead.Model;
import params.AddAIRequest;

/**
 * Created by MTAYS on 12/6/2016.
 */
public interface IServerModelFacade extends IServerModelFacadeStripped{
    //LocalAccess Commands

    void setGameName(String gameName);


    Model getLocalModel();

    void setLocalModel(Model model);

    //ModelAccess commands
    //To get all player info call GuiModelFacade.getAllPlayerInfo
    int getModelVersion();

    //ExecutableCommands
    //PlayerInfo does not require, and will not check, for color. It will only check the minimal provided, which I do not yet know
    //Checks for size.
    boolean canAddPlayer(PlayerInfo playerInfo);

    boolean addPlayer(PlayerInfo playerInfo);

    GameInfo getGameInfo();

    //Checks if player is registered to the game in session.
    boolean canRejoin(PlayerInfo playerInfo)//These should be called when a player trys to log in, might reject them.
    ;

    boolean rejoin(PlayerInfo playerInfo);

    //Not certain if this function should be here, or elsewhere. Depends on if logged in players are stored locally
    boolean canStart();

    void incrementModelVersion();

    void addAI(AddAIRequest params);

}
