package shared.locations;

import com.google.gson.JsonObject;
import model.definitions.EnumConverter;

import java.io.Serializable;

/**
 * Represents the location of an edge on a hex map
 */
public class EdgeLocation implements Serializable
{
	
	private HexLocation hexLoc;
	private EdgeDirection dir;
	
	public EdgeLocation(HexLocation hexLoc, EdgeDirection dir)
	{
		setHexLoc(hexLoc);
		setDir(dir);
	}

	public EdgeLocation(JsonObject jsonObject) {
		int x = jsonObject.get("x").getAsInt();
		int y = jsonObject.get("y").getAsInt();
		String edgeDir = jsonObject.get("direction").getAsString();
		setHexLoc(new HexLocation(x,y));
		setDir(EnumConverter.EdgeDirection(edgeDir));
	}
	
	public HexLocation getHexLoc()
	{
		return hexLoc;
	}
	
	private void setHexLoc(HexLocation hexLoc)
	{
		if(hexLoc == null)
		{
			throw new IllegalArgumentException("hexLoc cannot be null");
		}
		this.hexLoc = hexLoc;
	}
	
	public EdgeDirection getDir()
	{
		return dir;
	}
	
	private void setDir(EdgeDirection dir)
	{
		this.dir = dir;
	}

	@Override
	public String toString() {
		return "{\n" +
				"\"x\": \"" + hexLoc.getX() + "\",\n" +
				"\"y\": \"" + hexLoc.getY() + "\",\n" +
				"\"direction\": \"" + dir.toString() + "\"\n" +
				'}';
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("x",hexLoc.getX());
		jsonObject.addProperty("y",hexLoc.getY());
		jsonObject.addProperty("direction",dir.toString());
		return jsonObject;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
		result = prime * result + ((hexLoc == null) ? 0 : hexLoc.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		EdgeLocation other = (EdgeLocation)obj;
		if(dir != other.dir)
			return false;
		if(hexLoc == null)
		{
			if(other.hexLoc != null)
				return false;
		}
		else if(!hexLoc.equals(other.hexLoc))
			return false;
		return true;
	}
	
	/**
	 * Returns a canonical (i.e., unique) value for this edge location. Since
	 * each edge has two different locations on a map, this method converts a
	 * hex location to a single canonical form. This is useful for using hex
	 * locations as map keys.
	 * 
	 * @return Normalized hex location
	 */
	public EdgeLocation getNormalizedLocation()
	{
		
		// Return an EdgeLocation that has direction NW, N, or NE
		
		switch (dir)
		{
			case NorthWest:
			case North:
			case NorthEast:
				return this;
			case SouthWest:
			case South:
			case SouthEast:
				return new EdgeLocation(hexLoc.getNeighborLoc(dir),
										dir.getOppositeDirection());
			default:
				assert false;
				return null;
		}
	}
}

