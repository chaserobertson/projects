package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.ICommand;
import commands.RollCommand;

import java.util.Random;

/**
 * Created by chase on 9/20/16.
 * params for rollNumber server command
 */
public class RollNumberParams extends MoveParam {

    public int number;

    public RollNumberParams(int playerIndex) {
        type = "rollNumber";
        this.playerIndex = playerIndex;
        //random roll of two dice, so 2-12 result
        Random random = new Random();
        int dieOne = random.nextInt(6) + 1;
        int dieTwo = random.nextInt(6) + 1;
        this.number = dieOne + dieTwo;//Nifty :)
    }

    public RollNumberParams() {
        type = "rollNumber";
    }

    public RollNumberParams(String jsonParams) {
        super(jsonParams);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.number = jsonObject.get("number").getAsInt();
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty("number", number);
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new RollCommand(this);
    }
}
