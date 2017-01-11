package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.ICommand;
import commands.RobPlayerCommand;
import shared.locations.HexLocation;

/**
 * Created by chase on 9/20/16.
 */
public class RobPlayerParams extends MoveParam {

    public int victimIndex;
    public HexLocation location;

    public RobPlayerParams(int playerIndex, int victimIndex, HexLocation location) {
        type = "robPlayer";
        this.playerIndex = playerIndex;
        this.victimIndex = victimIndex;
        this.location = location;
    }

    public RobPlayerParams() {
        type = "robPlayer";
    }

    public RobPlayerParams(String jsonParams) {
        super(jsonParams);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.victimIndex = jsonObject.get("victimIndex").getAsInt();
        this.location = new HexLocation(jsonObject.get("location").getAsJsonObject());
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty("victimIndex",victimIndex);
        jsonObject.add("location",location.toJson());
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new RobPlayerCommand(this);
    }
}
