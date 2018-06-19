package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.AddAICommand;
import commands.ICommand;
import shared.locations.VertexLocation;

/**
 * Created by chase on 9/20/16.
 */
public class AddAIRequest extends GameRequiredParam{

    public String AIType = "LARGEST_ARMY";

    public AddAIRequest() {
    }

    public AddAIRequest(String jsonParams) {

        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.AIType = jsonObject.get("AIType").getAsString();
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AIType", AIType);
        return jsonObject.toString();
    }

    @Override
    public ICommand generateCommand() {
        return new AddAICommand(this);
    }
}
