package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.AcceptTradeCommand;
import commands.BuildSettlementCommand;
import commands.ICommand;
import shared.locations.VertexLocation;

/**
 * Created by chase on 9/20/16.
 */
public class BuildSettlementParams extends MoveParam {

    public VertexLocation vertexLocation;
    public boolean setupRound;
    public boolean free;

    public BuildSettlementParams() {
        type = "buildSettlement";
    }

    public BuildSettlementParams(VertexLocation vertexLocation, boolean setupRound, boolean free) {
        type = "buildSettlement";
        this.vertexLocation = vertexLocation;
        this.setupRound = setupRound;
        this.free = free;
    }

    public BuildSettlementParams(String jsonParams) {
        super(jsonParams);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.vertexLocation = new VertexLocation(jsonObject.get("vertexLocation").getAsJsonObject());
        this.free = jsonObject.get("free").getAsBoolean();
        this.setupRound = free; //TODO: determine what the heck to do with setupRound
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        jsonObject.add("vertexLocation",vertexLocation.toJson());
        jsonObject.addProperty("free",free);
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new BuildSettlementCommand(this);
    }
}
