package persistance.MockPersistance;

import params.Credentials;
import persistance.interfaces.IUserDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MTAYS on 11/29/2016.
 */
public class MockUserDAO implements IUserDao {
    @Override
    public void registerUser(Credentials credentials) {

    }

    @Override
    public List<Credentials> retrieveUsers() {
        return new ArrayList<>();
    }

    @Override
    public void resetUsers() {

    }
}
