package model.facade.server;

import java.io.Serializable;

/**
 * Created by MTAYS on 12/7/2016.
 */
public interface IServerModelFacadeStripped extends Serializable {
    int getGameID();
    void setGameID(int gameID);
    String getGameName();
    String getLocalModelString();
    IServerModelFacadeStripped generateServerModel(String serializedModel,int gameID,String gameName);
}
