package model.resources;

import junit.framework.TestCase;
import model.definitions.ResourceHand;

/**
 * Created by MTAYS on 10/24/2016.
 */
public class ResourcePileTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }
    public void testCanTransferResourcesTo(){
        ResourcePile pile1=new ResourcePile();
        ResourcePile pile2=new ResourcePile();
        ResourceHand transfer=new ResourceHand(0,0,0,0,0);
        assertTrue(pile1.canTransferResourcesTo(pile2,transfer));
        transfer=new ResourceHand(1,1,1,1,1);
        assertFalse(pile1.canTransferResourcesTo(pile2,transfer));
        pile1=new ResourcePile(1,1,1,1,1);
        assertTrue(pile1.canTransferResourcesTo(pile2,transfer));
        pile1=new ResourcePile(2,2,2,2,2);
        assertTrue(pile1.canTransferResourcesTo(pile2,transfer));
        pile1=new ResourcePile(1,1,1,1,0);
        assertFalse(pile1.canTransferResourcesTo(pile2,transfer));
        transfer=new ResourceHand(-1,-1,-1,-1,0);
        assertFalse(pile1.canTransferResourcesTo(pile2,transfer));
        assertTrue(pile2.canTransferResourcesTo(pile1,transfer));
        pile2=new ResourcePile(2,0,2,0,2);
        pile1=new ResourcePile(0,2,0,2,0);
        transfer=new ResourceHand(2,-2,2,-2,2);
        assertFalse(pile1.canTransferResourcesTo(pile2,transfer));
        assertTrue(pile2.canTransferResourcesTo(pile1,transfer));

    }
}
