package edu.byu.cs.superasteroids.model;
import android.graphics.PointF;

import edu.byu.cs.superasteroids.core.GraphicsUtils;
import edu.byu.cs.superasteroids.drawing.DrawingHelper;
import edu.byu.cs.superasteroids.game.Viewport;

import android.graphics.RectF;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class Ship extends MovingObject {
    public static Ship SINGLETON = new Ship();

    public MainBody mainBody = null;
    public ExtraParts extraParts = null;
    public Cannon cannon = null;
    public Engine engine = null;
    public PowerCore powerCore = null;
    //public Projectile projectile = null;
    public PointF speed = new PointF(0f, -4f);
    public RectF boundaryBox = null;

    public boolean isInitialized() {
        boolean initialized = true;
        if(mainBody == null) initialized = false;
        if(extraParts == null) initialized = false;
        if(cannon == null) initialized = false;
        if(engine == null) initialized = false;
        if(powerCore == null) initialized = false;
        //if(projectile == null) initialized = false;
        return initialized;
    }

    public void initialize() {
        mainBody = null;
        extraParts = null;
        cannon = null;
        engine = null;
        powerCore = null;
        rotationDegrees = 0;
        speed = new PointF(0f, -4f);
        //projectile = null;
    }

    public boolean attach(ShipPart newAttachment) {
        if(newAttachment instanceof MainBody) {
            mainBody = (MainBody) newAttachment;
        }
        else if(newAttachment instanceof ExtraParts) {
            extraParts = (ExtraParts) newAttachment;
        }
        else if(newAttachment instanceof Cannon) {
            cannon = (Cannon) newAttachment;
            //projectile = new Projectile(cannon);
        }
        else if(newAttachment instanceof Engine) {
            engine = (Engine) newAttachment;
        }
        else if(newAttachment instanceof PowerCore) {
            powerCore = (PowerCore) newAttachment;
        }
        else return false;

        return true;
    }

    public void onCreate() {
        //ship is created
        position = new PointF(Viewport.SINGLETON.levelWidth / 2, (Viewport.SINGLETON.levelHeight / 2));

        float left = position.x - (width() / 2);
        float right = left + width();
        float top = position.y - (height() / 2);
        float bottom = top + height();
        boundaryBox = new RectF(left, top, right, bottom);
    }

    public float width() {
        float extraPartsWidth = extraParts.imageWidth;
        float mainBodyWidth = mainBody.imageWidth;
        float cannonWidth = cannon.imageWidth;

        float extraPartsOverlap = extraPartsWidth - extraParts.attachPoint.x;
        float mainBodyLeftOverlap = mainBody.extraAttach.x;
        float leftOverlap = extraPartsOverlap + mainBodyLeftOverlap;

        float cannonOverlap = cannon.attachPoint.x;
        float mainBodyRightOverlap = mainBodyWidth - mainBody.cannonAttach.x;
        float rightOverlap = cannonOverlap + mainBodyRightOverlap;

        float shipWidth = extraPartsWidth + mainBodyWidth + cannonWidth;
        shipWidth -= leftOverlap;
        shipWidth -= rightOverlap;

        return shipWidth * scaleX;
    }

    public float height() {
        float mainBodyHeight = mainBody.imageHeight;
        float engineHeight = engine.imageHeight;

        float mainBodyOverlap = mainBodyHeight - mainBody.engineAttach.y;
        float engineOverlap = engine.attachPoint.y;
        float totalOverlap = mainBodyOverlap + engineOverlap;

        float shipHeight = mainBodyHeight + engineHeight;
        shipHeight -= totalOverlap;

        return shipHeight * scaleY;
    }

    public void reposition() {
        SINGLETON.position = new PointF(DrawingHelper.getGameViewWidth() / 2, DrawingHelper.getGameViewHeight() / 3);
    }

    public void draw() {
        if(mainBody != null) {
            float mainBodyX = SINGLETON.position.x;
            float mainBodyY = SINGLETON.position.y;
            DrawingHelper.drawImage(mainBody.imageID, mainBodyX, mainBodyY, SINGLETON.rotationDegrees,
                    SINGLETON.scaleX, SINGLETON.scaleY, SINGLETON.alpha);

            if (cannon != null) {
                float cannonOffsetX = (mainBody.imageWidth / 2) + (cannon.imageWidth / 2);
                cannonOffsetX -= (cannon.attachPoint.x + (mainBody.imageWidth - mainBody.cannonAttach.x));
                float cannonOffsetY = (mainBody.imageHeight / 2) + (cannon.imageHeight / 2);
                cannonOffsetY -= (cannon.attachPoint.y + (mainBody.imageHeight - mainBody.cannonAttach.y));
                cannonOffsetX *= SINGLETON.scaleX;
                cannonOffsetY *= SINGLETON.scaleY;
                float cannonX = mainBodyX + cannonOffsetX;
                float cannonY = mainBodyY + cannonOffsetY;
                DrawingHelper.drawImage(cannon.imageID, cannonX, cannonY, SINGLETON.rotationDegrees,
                        SINGLETON.scaleX, SINGLETON.scaleY, SINGLETON.alpha);
            }

            if (extraParts != null) {
                float offsetX = (mainBody.imageWidth / 2) + (extraParts.imageWidth / 2);
                offsetX -= ((extraParts.imageWidth - extraParts.attachPoint.x) + mainBody.extraAttach.x);
                float offsetY = (mainBody.imageHeight / 2) + (extraParts.imageHeight / 2);
                offsetY -= (extraParts.attachPoint.y + (mainBody.imageHeight - mainBody.extraAttach.y));
                offsetX *= SINGLETON.scaleX;
                offsetY *= SINGLETON.scaleY;
                float X = mainBodyX - offsetX;
                float Y = mainBodyY + offsetY;
                DrawingHelper.drawImage(extraParts.imageID, X, Y, SINGLETON.rotationDegrees,
                        SINGLETON.scaleX, SINGLETON.scaleY, SINGLETON.alpha);
            }

            if (engine != null) {
                float offsetY = (mainBody.imageHeight / 2) + (engine.imageHeight / 2);
                offsetY -= (engine.attachPoint.y + (mainBody.imageHeight - mainBody.engineAttach.y));
                offsetY *= SINGLETON.scaleY;
                float X = mainBodyX;
                float Y = mainBodyY + offsetY;
                DrawingHelper.drawImage(engine.imageID, X, Y, SINGLETON.rotationDegrees,
                        SINGLETON.scaleX, SINGLETON.scaleY, SINGLETON.alpha);
            }
        }
    }

    public void update(PointF position) {
        this.position = position;

        float left = position.x - (width() / 2);
        float right = left + width();
        float top = position.y - (height() / 2);
        float bottom = top + height();
        boundaryBox = new RectF(left, top, right, bottom);
    }
}
