package edu.byu.cs.superasteroids.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chaserobertson on 5/16/16.
 * mostly onCreate:
 * make SQL string
 * db.execSQL(SQL string);
 *
 * string dbname
 * db_version 1
 * constructor takes context, makes super(context, dbname, null, db_version)
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    //public static DatabaseOpenHelper SINGLETON = null;
    private static final String DB_NAME = "asteroidsGame.sqlite";
    private static final int DB_VERSION = 1;

    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        //SINGLETON = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DROP_TABLES);
        db.execSQL(DROP_TABLES1);
        db.execSQL(DROP_TABLES2);
        db.execSQL(DROP_TABLES3);
        db.execSQL(DROP_TABLES4);
        db.execSQL(DROP_TABLES5);
        db.execSQL(DROP_TABLES6);
        db.execSQL(DROP_TABLES7);
        db.execSQL(DROP_TABLES8);
        db.execSQL(DROP_TABLES9);

        db.execSQL(CREATE_OBJECTS);
        db.execSQL(CREATE_ASTEROIDS);
        db.execSQL(CREATE_LEVELS);
        db.execSQL(CREATE_LEVEL_OBJECTS);
        db.execSQL(CREATE_LEVEL_ASTEROIDS);
        db.execSQL(CREATE_MAIN_BODIES);
        db.execSQL(CREATE_CANNONS);
        db.execSQL(CREATE_EXTRA_PARTS);
        db.execSQL(CREATE_ENGINES);
        db.execSQL(CREATE_POWER_CORES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        return;
    }

    private static final String DROP_TABLES = "drop table if exists objects";
    private static final String DROP_TABLES1 = "drop table if exists asteroids";
    private static final String DROP_TABLES2 = "drop table if exists levels";
    private static final String DROP_TABLES3 = "drop table if exists levelAsteroids";
    private static final String DROP_TABLES4 = "drop table if exists levelObjects";
    private static final String DROP_TABLES5 = "drop table if exists mainBodies";
    private static final String DROP_TABLES6 = "drop table if exists cannons";
    private static final String DROP_TABLES7 = "drop table if exists extraParts";
    private static final String DROP_TABLES8 = "drop table if exists engines";
    private static final String DROP_TABLES9 = "drop table if exists powerCores";
    private static final String CREATE_OBJECTS = "create table objects " +
            "( " +
            "image text not null " +
            ")\n";
    private static final String CREATE_ASTEROIDS = "create table asteroids " +
            "( " +
            "name text not null, " +
            "image text not null, " +
            "imageWidth integer not null, " +
            "imageHeight integer not null " +
            ")\n";
    private static final String CREATE_LEVELS = "create table levels " +
            "(\n" +
            "number integer not null,\n" +
            "title text not null,\n" +
            "hint text not null,\n" +
            "width integer not null,\n" +
            "height integer not null,\n" +
            "music text not null\n" +
            ")\n";
    private static final String CREATE_LEVEL_ASTEROIDS = "create table levelAsteroids " +
            "(\n" +
            "number integer not null,\n" +
            "asteroidID integer not null,\n" +
            "levelID integer not null\n" +
            ")\n";
    private static final String CREATE_LEVEL_OBJECTS = "create table levelObjects " +
            "(\n" +
            "position text not null,\n" +
            "objectID integer not null,\n" +
            "scale real not null,\n" +
            "levelID integer not null\n" +
            ");\n";
    private static final String CREATE_MAIN_BODIES = "create table mainBodies " +
            "(\n" +
            "cannonAttach text not null,\n" +
            "engineAttach text not null,\n" +
            "extraAttach text not null,\n" +
            "image text not null,\n" +
            "imageWidth integer not null,\n" +
            "imageHeight integer not null\n" +
            ")\n";
    private static final String CREATE_CANNONS = "create table cannons " +
            "(\n" +
            "attachPoint text not null,\n" +
            "emitPoint text not null,\n" +
            "image text not null,\n" +
            "imageWidth integer not null,\n" +
            "imageHeight integer not null,\n" +
            "attackImage text not null,\n" +
            "attackImageWidth integer not null,\n" +
            "attackImageHeight integer not null,\n" +
            "attackSound text not null,\n" +
            "damage integer not null\n" +
            ")\n";
    private static final String CREATE_EXTRA_PARTS = "create table extraParts " +
            "(\n" +
            "attachPoint text not null,\n" +
            "image text not null,\n" +
            "imageWidth integer not null,\n" +
            "imageHeight integer not null\n" +
            ")\n";
    private static final String CREATE_ENGINES = "create table engines " +
            "(\n" +
            "baseSpeed integer not null,\n" +
            "baseTurnRate integer not null,\n" +
            "attachPoint text not null,\n" +
            "image text not null,\n" +
            "imageWidth integer not null,\n" +
            "imageHeight integer not null\n" +
            ")\n";
    private static final String CREATE_POWER_CORES = "create table powerCores " +
            "(\n" +
            "cannonBoost integer not null,\n" +
            "engineBoost integer not null,\n" +
            "image text not null\n" +
            ")";

}
