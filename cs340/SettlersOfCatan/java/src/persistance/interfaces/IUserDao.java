package persistance.interfaces;

import params.Credentials;

import java.util.List;

/**
 * Created by MTAYS on 11/28/2016.
 */
public interface IUserDao {
    /**
     *
     * @param credentials the credentials of the user being registered
     * @pre credentials is not null, and is valid, and is not a duplicate of previous user
     * @post user is added to the database
     */
    public void registerUser(Credentials credentials);

    /**
     *
     * @return a list of all the credentials stored in the database
     * @pre none
     * @post list is populated with all the credentials in the database
     */
    public List<Credentials> retrieveUsers();

    /**
     * @pre none
     * @post datbase is cleared
     */
    public void resetUsers();
}
