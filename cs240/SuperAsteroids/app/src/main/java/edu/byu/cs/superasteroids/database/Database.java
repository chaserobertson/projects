package edu.byu.cs.superasteroids.database;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import org.json.JSONObject;

/**
 * Created by chaserobertson on 5/19/16.
 */
public class Database {
    public static Database SINGLETON = new Database();
    public static DatabaseOpenHelper dbOpenHelper;
    public static SQLiteDatabase database;
    public Context baseContext;
    public static DatabaseAccessObject DAO;

    public void setContext(Context baseContext) {
        DAO = DatabaseAccessObject.SINGLETON;
        this.baseContext = baseContext;
        dbOpenHelper = new DatabaseOpenHelper(baseContext);
        database = dbOpenHelper.getWritableDatabase();
        DAO.setDatabase(database);
    }

    public static void initialize(JSONObject asteroidsGame) {
        if(!DAO.isEmpty()) {
            DAO.emptyModel();
        }

        dbOpenHelper.onCreate(database);
        DAO.fillDatabase(asteroidsGame);
    }
}
