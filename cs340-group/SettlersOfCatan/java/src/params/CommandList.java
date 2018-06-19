package params;

import java.util.List;

/**
 * Created by chase on 9/23/16.
 */
public class CommandList {
    List<String> commandList;

    @Override
    public String toString() {
        return '{'+commandList.toString()+'}';
    }
}
