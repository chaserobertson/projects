package edu.byu.cs.superasteroids.importer;

import android.content.Context;
import android.util.Config;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import edu.byu.cs.superasteroids.database.Database;
import edu.byu.cs.superasteroids.model.Model;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class GameDataImporter implements IGameDataImporter {
    public static android.content.Context context;
    public Database database = Database.SINGLETON;
    /**
     * parses json file to values
     * Calls DAO methods to initialize database
     *
     * @param baseContext The InputStreamReader connected to the .json file needing to be imported.
     * @return
     */
    public GameDataImporter(android.content.Context baseContext) {
        context = baseContext;
    }

    @Override
    public boolean importData(InputStreamReader dataInputReader) {
        try {
            JSONObject rootObject = new JSONObject(makeString(dataInputReader));
            JSONObject asteroidsGame = rootObject.getJSONObject("asteroidsGame");

            database.initialize(asteroidsGame);

            return true;
        } catch(IOException e) { return false; }
        catch(JSONException e) { return false; }
    }

    public static void setContext(Context context) {
        Database.SINGLETON.setContext(context);
    }

    private static String makeString(Reader reader) throws IOException {

        StringBuilder sb = new StringBuilder();
        char[] buf = new char[512];

        int n = 0;
        while ((n = reader.read(buf)) > 0) {
            sb.append(buf, 0, n);
        }

        return sb.toString();
    }
}
