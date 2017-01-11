package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.ICommand;
import commands.SendChatCommand;

/**
 * Created by chase on 9/20/16.
 */
public class SendChatParams extends MoveParam {

    public String content;

    public SendChatParams() {
        type = "sendChat";
    }

    public SendChatParams(int playerIndex, String content) {
        type = "sendChat";
        this.playerIndex = playerIndex;
        this.content = content;
    }

    public SendChatParams(String jsonParams) {
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        type = jsonObject.get("type").getAsString();
        this.playerIndex = jsonObject.get("playerIndex").getAsInt();
        this.content = jsonObject.get("content").getAsString();
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty("content", content);
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new SendChatCommand(this);
    }
}
