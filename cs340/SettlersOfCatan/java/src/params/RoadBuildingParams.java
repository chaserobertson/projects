package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.ICommand;
import commands.RoadBuildingCommand;
import shared.locations.EdgeDirection;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;

/**
 * Created by chase on 9/20/16.
 */
public class RoadBuildingParams extends MoveParam {

    public EdgeLocation spot1;
    public EdgeLocation spot2;

    public RoadBuildingParams(int playerIndex, EdgeLocation spot1, EdgeLocation spot2) {
        type = "Road_Building";
        this.playerIndex = playerIndex;
        this.spot1 = spot1;
        this.spot2 = spot2;
    }

    public RoadBuildingParams() {
        type = "Road_Building";
    }

    public RoadBuildingParams(String jsonParams) {
        super(jsonParams);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.spot1 = new EdgeLocation(jsonObject.get("spot1").getAsJsonObject());
        this.spot2 = new EdgeLocation(jsonObject.get("spot2").getAsJsonObject());
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        if(spot1==null)spot1=new EdgeLocation(new HexLocation(-99,-99), EdgeDirection.North);
        jsonObject.add("spot1",spot1.toJson());
        if(spot2==null)spot2=new EdgeLocation(new HexLocation(-99,-99), EdgeDirection.North);
        jsonObject.add("spot2",spot2.toJson());
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new RoadBuildingCommand(this);
    }
}
