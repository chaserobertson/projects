package params;

import com.google.gson.JsonObject;

/**
 * Created by chase on 9/24/16.
 */
public class ChangeLogLevelRequest {

    public String logLevel;

    public ChangeLogLevelRequest(String logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("logLevel", logLevel);
        return jsonObject.toString();
    }
}
