package edu.byu.cs.superasteroids.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;

import edu.byu.cs.superasteroids.content.ContentManager;
import edu.byu.cs.superasteroids.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayList;
import org.json.*;

/**
 * Created by chase robertson on 5/16/16.
 * From here, initialize in-memory-model
 * based on json array
 * save background objects
 * save asteroids
 * save levels
 * save ss parts
 */
public class DatabaseAccessObject {
    public static final DatabaseAccessObject SINGLETON = new DatabaseAccessObject();
    public SQLiteDatabase sqLiteDatabase;
    public ArrayList<VisibleObject> content = new ArrayList<>();

    public DatabaseAccessObject() {}
    public DatabaseAccessObject(SQLiteDatabase db) {
        sqLiteDatabase = db;
    }

    public void setDatabase(SQLiteDatabase db) {
        sqLiteDatabase = db;
    }

    public ArrayList<BackgroundObject> getBackgroundObjects() {
        ArrayList<BackgroundObject> backgroundObjects = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_BACKGROUND_OBJECTS, EMPTY_ARRAY_OF_STRINGS);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int ID = cursor.getPosition();
                String image = cursor.getString(0);
                int imageId = ContentManager.getInstance().loadImage(image);

                BackgroundObject new_BO = new BackgroundObject(ID, image, imageId);
                backgroundObjects.add(new_BO);
                content.add(new_BO);

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }


        return backgroundObjects;
    }
    public ArrayList<AsteroidType> getAsteroidTypes() {
        ArrayList<AsteroidType> asteroidTypes = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_ASTEROID_TYPES, EMPTY_ARRAY_OF_STRINGS);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                //int ID = cursor.getPosition();
                String name = cursor.getString(0);
                String image = cursor.getString(1);
                int imageWidth = cursor.getInt(2);
                int imageHeight = cursor.getInt(3);
                int imageId = ContentManager.getInstance().loadImage(image);

                AsteroidType new_AT = new AsteroidType(name, image, imageWidth, imageHeight, imageId);
                asteroidTypes.add(new_AT);
                content.add(new_AT);

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return asteroidTypes;
    }
    public ArrayList<Level> getLevels() {
        ArrayList<Level> levels = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_LEVELS, EMPTY_ARRAY_OF_STRINGS);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                int number = cursor.getInt(0);
                String title = cursor.getString(1);
                String hint = cursor.getString(2);
                int width = cursor.getInt(3);
                int height = cursor.getInt(4);
                String music = cursor.getString(5);

                Level new_level = new Level(number, title, hint, width, height, music);

                ArrayList<LevelBackgroundObject> levelBackgroundObjects = getLevelBackgroundObjects(number);
                new_level.addLevelBackgroundObjects(levelBackgroundObjects);

                ArrayList<LevelAsteroidType> levelAsteroidTypes = getLevelAsteroidType(number);
                new_level.addLevelAsteroidType(levelAsteroidTypes);

                levels.add(new_level);

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return levels;
    }
    private ArrayList<LevelBackgroundObject> getLevelBackgroundObjects(int levelNumber) {
        ArrayList<LevelBackgroundObject> output = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_LEVEL_BACKGROUND_OBJECTS_FOR + levelNumber, EMPTY_ARRAY_OF_STRINGS);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                //int ID = cursor.getPosition();
                String position = cursor.getString(0);
                int objectId = cursor.getInt(1);
                float scale = cursor.getInt(2);
                //int levelId = cursor.getInt(3);


                LevelBackgroundObject levelBackgroundObject = new LevelBackgroundObject(position, objectId, scale);
                output.add(levelBackgroundObject);

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return output;
    }
    private ArrayList<LevelAsteroidType> getLevelAsteroidType(int levelNumber) {
        ArrayList<LevelAsteroidType> output = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_LEVEL_ASTEROID_TYPES_FOR + levelNumber, EMPTY_ARRAY_OF_STRINGS);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                //int ID = cursor.getPosition();
                int number = cursor.getInt(0);
                int asteroidId = cursor.getInt(1);
                //int levelId = cursor.getInt(2);

                LevelAsteroidType levelAsteroidType = new LevelAsteroidType(number, asteroidId);
                output.add(levelAsteroidType);

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return output;
    }
    public ArrayList<MainBody> getMainBodies() {
        ArrayList<MainBody> mainBodies = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_MAIN_BODIES, EMPTY_ARRAY_OF_STRINGS);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                //int ID = cursor.getPosition();
                String cannonAttach = cursor.getString(0);
                String engineAttach = cursor.getString(1);
                String extraAttach = cursor.getString(2);
                String image = cursor.getString(3);
                int imageWidth = cursor.getInt(4);
                int imageHeight = cursor.getInt(5);
                int imageId = ContentManager.getInstance().loadImage(image);

                MainBody mainBody = new MainBody(cannonAttach, engineAttach, extraAttach, image, imageWidth, imageHeight, imageId);
                mainBodies.add(mainBody);
                content.add(mainBody);

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return mainBodies;
    }
    public ArrayList<Cannon> getCannons() {
        ArrayList<Cannon> output = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_CANNONS, EMPTY_ARRAY_OF_STRINGS);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                //int ID = cursor.getPosition();
                String attachPoint = cursor.getString(0);
                String emitPoint = cursor.getString(1);
                String image = cursor.getString(2);
                int imageWidth = cursor.getInt(3);
                int imageHeight = cursor.getInt(4);
                String attackImage = cursor.getString(5);
                int attackImageWidth = cursor.getInt(6);
                int attackImageHeight = cursor.getInt(7);
                String attackSound = cursor.getString(8);
                int damage = cursor.getInt(9);
                int imageId = ContentManager.getInstance().loadImage(image);
                int attackImageId = ContentManager.getInstance().loadImage(attackImage);
                int attackSoundId = -1;
                try {
                    attackSoundId = ContentManager.getInstance().loadSound(attackSound);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Cannon cannon = new Cannon(attachPoint, emitPoint, image, imageWidth, imageHeight, attackImage,
                        attackImageWidth, attackImageHeight, attackSound, damage, imageId, attackImageId, attackSoundId);
                output.add(cannon);
                content.add(cannon);
                content.add(cannon.attack);
                content.add(cannon.sound);

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return output;
    }
    public ArrayList<ExtraParts> getExtraPartses() {
        ArrayList<ExtraParts> output = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_EXTRA_PARTS, EMPTY_ARRAY_OF_STRINGS);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                //int ID = cursor.getPosition();
                String attachPoint = cursor.getString(0);
                String image = cursor.getString(1);
                int imageWidth = cursor.getInt(2);
                int imageHeight = cursor.getInt(3);
                int imageId = ContentManager.getInstance().loadImage(image);

                ExtraParts extraParts = new ExtraParts(attachPoint, image, imageWidth, imageHeight, imageId);
                output.add(extraParts);
                content.add(extraParts);

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return output;
    }
    public ArrayList<Engine> getEngines() {
        ArrayList<Engine> output = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_ENGINES, EMPTY_ARRAY_OF_STRINGS);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                //int ID = cursor.getPosition();
                int baseSpeed = cursor.getInt(0);
                int baseTurnRate = cursor.getInt(1);
                String attachPoint = cursor.getString(2);
                String image = cursor.getString(3);
                int imageWidth = cursor.getInt(4);
                int imageHeight = cursor.getInt(5);
                int imageId = ContentManager.getInstance().loadImage(image);

                Engine engine = new Engine(baseSpeed, baseTurnRate, attachPoint, image, imageWidth, imageHeight, imageId);
                output.add(engine);
                content.add(engine);

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return output;
    }
    public ArrayList<PowerCore> getPowerCores() {
        ArrayList<PowerCore> output = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_POWER_CORES, EMPTY_ARRAY_OF_STRINGS);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                //int ID = cursor.getPosition();
                int cannonBoost = cursor.getInt(0);
                int engineBoost = cursor.getInt(1);
                String image = cursor.getString(2);
                int imageId = ContentManager.getInstance().loadImage(image);

                PowerCore powerCore = new PowerCore(cannonBoost, engineBoost, image, imageId);
                output.add(powerCore);
                content.add(powerCore);

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return output;
    }
    public StarField getStarField() {
        int imageID = ContentManager.getInstance().loadImage("images/space.bmp");
        return new StarField("images/space.bmp", imageID);
    }

    public boolean fillDatabase(JSONObject asteroidsGame) {
        try {
            JSONArray backgroundObjects = asteroidsGame.getJSONArray("objects");
            JSONArray asteroidTypes = asteroidsGame.getJSONArray("asteroids");
            JSONArray levels = asteroidsGame.getJSONArray("levels");
            JSONArray mainBodies = asteroidsGame.getJSONArray("mainBodies");
            JSONArray cannons = asteroidsGame.getJSONArray("cannons");
            JSONArray extraParts = asteroidsGame.getJSONArray("extraParts");
            JSONArray engines = asteroidsGame.getJSONArray("engines");
            JSONArray powerCores = asteroidsGame.getJSONArray("powerCores");

            //sqLiteDatabase.beginTransaction();
            setBackgroundObjects(backgroundObjects);
            setAsteroidTypes(asteroidTypes);
            setLevels(levels);
            setMainBodies(mainBodies);
            setCannons(cannons);
            setExtraParts(extraParts);
            setEngines(engines);
            setPowerCores(powerCores);
            //sqLiteDatabase.setTransactionSuccessful();
            //sqLiteDatabase.endTransaction();
        } catch (JSONException e) {
            return false;
        }

        return true;
    }
    public void emptyModel() {
        Model.SINGLETON.clear();
        content.clear();
    }
    public void fillModel() {
        //sqLiteDatabase.beginTransaction();
        Model.SINGLETON.addBackgroundObjects( getBackgroundObjects() );
        Model.SINGLETON.addAsteroidTypes( getAsteroidTypes() );
        Model.SINGLETON.addLevels( getLevels() );
        Model.SINGLETON.addMainBodies( getMainBodies() );
        Model.SINGLETON.addCannons( getCannons() );
        Model.SINGLETON.addExtraParts( getExtraPartses() );
        Model.SINGLETON.addEngines( getEngines() );
        Model.SINGLETON.addPowerCores( getPowerCores() );
        Model.SINGLETON.addStarField( getStarField() );
        Model.SINGLETON.loadContent( content );
        //sqLiteDatabase.setTransactionSuccessful();
        //sqLiteDatabase.endTransaction();
    }
    public boolean isEmpty() {
        return Model.SINGLETON.isEmpty();
    }

    private void setBackgroundObjects(JSONArray backgroundObjects) throws JSONException {
        for(int i = 0; i < backgroundObjects.length(); i++) {
            ContentValues values = new ContentValues();
            String image = backgroundObjects.getString(i);
            values.put("image", image);
            sqLiteDatabase.insert("objects", null, values);
        }
    }
    private void setAsteroidTypes(JSONArray asteroidTypes) throws JSONException {
        for(int i = 0; i < asteroidTypes.length(); i++) {
            JSONObject currentAsteroid = asteroidTypes.getJSONObject(i);
            String name = currentAsteroid.getString("name");
            String image = currentAsteroid.getString("image");
            int imageWidth = currentAsteroid.getInt("imageWidth");
            int imageHeight = currentAsteroid.getInt("imageHeight");

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("image", image);
            values.put("imageWidth", imageWidth);
            values.put("imageHeight", imageHeight);

            sqLiteDatabase.insert("asteroids", null, values);
        }
    }
    private void setLevels(JSONArray levels) throws JSONException {
        for(int i = 0; i < levels.length(); i++) {
            JSONObject currentLevel = levels.getJSONObject(i);
            int number = currentLevel.getInt("number");
            String title = currentLevel.getString("title");
            String hint = currentLevel.getString("hint");
            int width = currentLevel.getInt("width");
            int height = currentLevel.getInt("height");
            String music = currentLevel.getString("music");

            ContentValues values = new ContentValues();
            values.put("number", number);
            values.put("title", title);
            values.put("hint", hint);
            values.put("width", width);
            values.put("height", height);
            values.put("music", music);

            sqLiteDatabase.insert("levels", null, values);

            JSONArray levelObjects = currentLevel.getJSONArray("levelObjects");
            for(int j = 0; j < levelObjects.length(); j++) {
                JSONObject currentLevelObject = levelObjects.getJSONObject(j);
                String position = currentLevelObject.getString("position");
                int objectId = currentLevelObject.getInt("objectId");
                float scale = (float)currentLevelObject.getDouble("scale");
                int levelId = number;

                ContentValues valuesHere = new ContentValues();
                valuesHere.put("position", position);
                valuesHere.put("objectId", objectId);
                valuesHere.put("scale", scale);
                valuesHere.put("levelId", levelId);

                sqLiteDatabase.insert("levelObjects", null, valuesHere);
            }

            JSONArray levelAsteroids = currentLevel.getJSONArray("levelAsteroids");
            for(int j = 0; j < levelAsteroids.length(); j++) {
                JSONObject currentLevelAsteroid = levelAsteroids.getJSONObject(j);
                int numAsteroids = currentLevelAsteroid.getInt("number");
                int asteroidId = currentLevelAsteroid.getInt("asteroidId");
                int levelId = number;

                ContentValues valuesHere = new ContentValues();
                valuesHere.put("number", numAsteroids);
                valuesHere.put("asteroidId", asteroidId);
                valuesHere.put("levelId", levelId);

                sqLiteDatabase.insert("levelAsteroids", null, valuesHere);
            }
        }
    }
    private void setMainBodies(JSONArray mainBodies) throws JSONException {
        for(int i = 0; i < mainBodies.length(); i++) {
            JSONObject currentMainBody = mainBodies.getJSONObject(i);
            String cannonAttach = currentMainBody.getString("cannonAttach");
            String engineAttach = currentMainBody.getString("engineAttach");
            String extraAttach = currentMainBody.getString("extraAttach");
            String image = currentMainBody.getString("image");
            int imageWidth = currentMainBody.getInt("imageWidth");
            int imageHeight = currentMainBody.getInt("imageHeight");

            ContentValues values = new ContentValues();
            values.put("cannonAttach", cannonAttach);
            values.put("engineAttach", engineAttach);
            values.put("extraAttach", extraAttach);
            values.put("image", image);
            values.put("imageWidth", imageWidth);
            values.put("imageHeight", imageHeight);

            sqLiteDatabase.insert("mainBodies", null, values);
        }
    }
    private void setCannons(JSONArray cannons) throws JSONException {
        for(int i = 0; i < cannons.length(); i++) {
            JSONObject currentCannon = cannons.getJSONObject(i);
            String attachPoint = currentCannon.getString("attachPoint");
            String emitPoint = currentCannon.getString("emitPoint");
            String image = currentCannon.getString("image");
            int imageWidth = currentCannon.getInt("imageWidth");
            int imageHeight = currentCannon.getInt("imageHeight");
            String attackImage = currentCannon.getString("attackImage");
            int attackImageWidth = currentCannon.getInt("attackImageWidth");
            int attackImageHeight = currentCannon.getInt("attackImageHeight");
            String attackSound = currentCannon.getString("attackSound");
            int damage = currentCannon.getInt("damage");

            ContentValues values = new ContentValues();
            values.put("attachPoint", attachPoint);
            values.put("emitPoint", emitPoint);
            values.put("image", image);
            values.put("imageWidth", imageWidth);
            values.put("imageHeight", imageHeight);
            values.put("attackImage", attackImage);
            values.put("attackImageWidth", attackImageWidth);
            values.put("attackImageHeight", attackImageHeight);
            values.put("attackSound", attackSound);
            values.put("damage", damage);

            sqLiteDatabase.insert("cannons", null, values);

        }
    }
    private void setExtraParts(JSONArray extraParts) throws JSONException {
        for(int i = 0; i < extraParts.length(); i++) {
            JSONObject currentExtraParts = extraParts.getJSONObject(i);
            String attachPoint = currentExtraParts.getString("attachPoint");
            String image = currentExtraParts.getString("image");
            int imageWidth = currentExtraParts.getInt("imageWidth");
            int imageHeight = currentExtraParts.getInt("imageHeight");

            ContentValues values = new ContentValues();
            values.put("attachPoint", attachPoint);
            values.put("image", image);
            values.put("imageWidth", imageWidth);
            values.put("imageHeight", imageHeight);

            sqLiteDatabase.insert("extraParts", null, values);
        }
    }
    private void setEngines(JSONArray engines) throws JSONException {
        for(int i = 0; i < engines.length(); i++) {
            JSONObject currentEngine = engines.getJSONObject(i);
            int baseSpeed = currentEngine.getInt("baseSpeed");
            int baseTurnRate = currentEngine.getInt("baseTurnRate");
            String attachPoint = currentEngine.getString("attachPoint");
            String image = currentEngine.getString("image");
            int imageWidth = currentEngine.getInt("imageWidth");
            int imageHeight = currentEngine.getInt("imageHeight");

            ContentValues values = new ContentValues();
            values.put("baseSpeed", baseSpeed);
            values.put("baseTurnRate", baseTurnRate);
            values.put("attachPoint", attachPoint);
            values.put("image", image);
            values.put("imageWidth", imageWidth);
            values.put("imageHeight", imageHeight);

            sqLiteDatabase.insert("engines", null, values);
        }
    }
    private void setPowerCores(JSONArray powerCores) throws JSONException {
        for(int i = 0; i < powerCores.length(); i++) {
            JSONObject currentPowerCore = powerCores.getJSONObject(i);
            int cannonBoost = currentPowerCore.getInt("cannonBoost");
            int engineBoost = currentPowerCore.getInt("engineBoost");
            String image = currentPowerCore.getString("image");

            ContentValues values = new ContentValues();
            values.put("cannonBoost", cannonBoost);
            values.put("engineBoost", engineBoost);
            values.put("image", image);

            sqLiteDatabase.insert("powerCores", null, values);
        }
    }

    private static final String[] EMPTY_ARRAY_OF_STRINGS = {};
    private static final String SELECT_BACKGROUND_OBJECTS = "select * from objects";
    private static final String SELECT_ASTEROID_TYPES = "select * from asteroids";
    private static final String SELECT_LEVELS = "select * from levels";
    private static final String SELECT_MAIN_BODIES = "select * from mainBodies";
    private static final String SELECT_CANNONS = "select * from cannons";
    private static final String SELECT_EXTRA_PARTS = "select * from extraParts";
    private static final String SELECT_ENGINES = "select * from engines";
    private static final String SELECT_POWER_CORES = "select * from powerCores";
    private static final String SELECT_LEVEL_BACKGROUND_OBJECTS_FOR = "select * from levelObjects where levelID=";
    private static final String SELECT_LEVEL_ASTEROID_TYPES_FOR = "select * from levelAsteroids where levelID=";
}
