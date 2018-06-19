package login;

/**
 * Created by tsmit on 11/7/2016.
 */
public class ServerCredentials {
    private String username = new String();
    private String password = new String();
    private int id = -1;

    public ServerCredentials(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId(){return id;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String toString(){
        StringBuilder result=new StringBuilder();
        result.append("{\nUsername=");
        result.append(username);
        result.append("\nPassword=");
        result.append(password);
        result.append("\nID=");
        result.append(Integer.toString(id));
        result.append("\n}");
        return result.toString();
    }
}
