package persistance.FlatPersistance;

import params.Credentials;
import persistance.interfaces.IUserDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static persistance.FlatPersistance.FlatPersistanceProvider.fileEnding;

/**
 * Created by tsmit on 12/5/2016.
 */
public class FlatUserDao implements IUserDao {
    private String dirUsers=null;
    private final String fileUsersStart="users";
    public FlatUserDao(){
        try {
            File dirMain = new File("flatPersistenceV01");
            if(!dirMain.exists()&&!dirMain.mkdirs())throw new Exception();
            File dirUsers = new File(dirMain.getPath() + "/users");
            this.dirUsers=dirUsers.getPath();
            if((!dirUsers.exists()&&!dirUsers.mkdirs())) throw new Exception();
        }catch (Exception e){
            System.out.print("FlatUserDao:Error creating files.");
            e.printStackTrace();
        }
    }
    @Override
    public void registerUser(Credentials credentials) {
        List<Credentials> preExistant=retrieveUsers();
        preExistant.add(credentials);
        FlatPersistanceProvider.writeObjectToFile(dirUsers,fileUsersStart+fileEnding,preExistant);
    }

    @Override
    public List<Credentials> retrieveUsers() {
        List<Credentials> result=new ArrayList<>();
        Object object=FlatPersistanceProvider.readObjectFromFile(dirUsers,fileUsersStart+fileEnding);
        if(object!=null&&object instanceof List){
            List preExistantCreds=(List)object;
            if(preExistantCreds.size()>0&&preExistantCreds.get(0) instanceof Credentials){
                result=preExistantCreds;
            }
        }
        return result;
    }

    @Override
    public void resetUsers() {
        FlatPersistanceProvider.deleteAllInFolder(dirUsers);
    }
}
