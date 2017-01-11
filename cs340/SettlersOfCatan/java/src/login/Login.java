package login;

import params.Credentials;
import persistance.interfaces.IPersistanceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Will on 11/7/2016.
 */
public class Login {
    private static Login instance;
    public static Login getInstance(){
        if (instance==null){
            instance=new Login();
        }
        return instance;
    }
    private Login(){
        serverCredentialsList=new ArrayList<>();
    }

    private List<ServerCredentials> serverCredentialsList = new ArrayList<>();

    /**
     * @param name
     * @param password
     * @return boolean: whether Player successfully logged in
     */
    public boolean LoginPlayer(String name, String password){
        for(ServerCredentials player: serverCredentialsList){
            if(player.getUsername().equals(name)){
                if(player.getPassword().equals(password)){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * @param name
     * @param password
     * @return boolean: whether Player was successfully registered or not
     */
    public boolean RegisterPlayer(String name, String password){
        boolean sameUsername = false;
        for(ServerCredentials player: serverCredentialsList){
            if(player.getUsername().equals(name)){
                sameUsername = true;
            }
        }
        if(!sameUsername){
            int nextID = 0;
            if(serverCredentialsList.size() > 0){
                nextID = serverCredentialsList.get(serverCredentialsList.size()-1).getId() + 1;
            }
            serverCredentialsList.add(new ServerCredentials(nextID, name, password));
            return true;
        }
        else{
            return false;
        }
    }

    public ServerCredentials getPlayer(int id){
        for(ServerCredentials player: serverCredentialsList){
            if(player.getId() == id){
                return player;
            }
        }
        return null;
    }

    public int getPlayerId(String name) {
        for(ServerCredentials player: serverCredentialsList){
            if(player.getUsername().equals(name)){
                return player.getId();
            }
        }
        return -1;
    }
    public String toString(){
        StringBuilder result = new StringBuilder();
        for(ServerCredentials credentials:serverCredentialsList){
            result.append(credentials.toString());
        }
        return result.toString();
    }
    public void loadFromPersistance(IPersistanceProvider persistanceProvider){
        if(persistanceProvider==null)return;
        List<Credentials> credentialsList=persistanceProvider.retrieveUsers();
        if(credentialsList!=null) {
            for (Credentials credentials : credentialsList) {
                if(credentials!=null)RegisterPlayer(credentials.username, credentials.password);
            }
        }
    }
}
