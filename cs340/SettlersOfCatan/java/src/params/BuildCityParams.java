package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.AcceptTradeCommand;
import commands.BuildCityCommand;
import commands.ICommand;
import shared.locations.VertexLocation;

/**
 * Created by chase on 9/20/16.
 */
public class BuildCityParams extends MoveParam {

    public VertexLocation vertexLocation;

    public BuildCityParams(int playerIndex, VertexLocation vertexLocation) {
        type = "buildCity";
        this.playerIndex = playerIndex;
        this.vertexLocation = vertexLocation;
    }

    public BuildCityParams() {
        type = "buildCity";
    }

    public BuildCityParams(String jsonParams) {
        super(jsonParams);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.vertexLocation = new VertexLocation(jsonObject.get("vertexLocation").getAsJsonObject());
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        jsonObject.add("vertexLocation",vertexLocation.toJson());
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new BuildCityCommand(this);
    }
}
