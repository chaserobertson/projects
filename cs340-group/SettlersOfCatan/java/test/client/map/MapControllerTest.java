package client.map;

import junit.framework.TestCase;
import model.overhead.Model;
import shared.locations.HexLocation;

import java.util.List;

/**
 * Created by MTAYS on 10/17/2016.
 */
public class MapControllerTest extends TestCase {
    MapController controller;
    public void setUp() throws Exception{
        super.setUp();
        //controller=new MapController(null, null, null);
    }
    public void tearDown() throws Exception {
        super.tearDown();
    }
    public void testGenerateSeaHexes(){
        Model model=new Model();
        model.initializeModelDefault();
        List<HexLocation>seaHexes=MapController.generateSeaHexLocation(model.getGameBoard().getHexes());
        assertTrue(seaHexes.size()==18);
    }
}

