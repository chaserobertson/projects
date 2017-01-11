package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.AcceptTradeCommand;
import commands.BuildRoadCommand;
import commands.ICommand;
import shared.locations.EdgeLocation;

/**
 * Created by chase on 9/20/16.
 */
public class BuildRoadParams extends MoveParam {

    public EdgeLocation roadLocation;
    public boolean free;
    public boolean setupRound;

    public BuildRoadParams(int playerIndex, EdgeLocation roadLocation, boolean free, boolean setupRound) {
        type = "buildRoad";
        this.playerIndex = playerIndex;
        this.roadLocation = roadLocation;
        this.free = free;
        this.setupRound = setupRound;
    }

    public BuildRoadParams() {
        type = "buildRoad";
    }

    public BuildRoadParams(String jsonParams) {
        super(jsonParams);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.roadLocation = new EdgeLocation(jsonObject.get("roadLocation").getAsJsonObject());
        this.free = jsonObject.get("free").getAsBoolean();
        this.setupRound = free; //TODO: determine what the heck to do with setupRound
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        jsonObject.add("roadLocation", roadLocation.toJson());
        jsonObject.addProperty("free",free);
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new BuildRoadCommand(this);
    }
}
