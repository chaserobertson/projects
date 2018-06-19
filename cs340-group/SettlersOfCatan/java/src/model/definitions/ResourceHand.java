package model.definitions;
import com.google.gson.JsonObject;
import com.sun.org.apache.regexp.internal.RE;
import model.resources.ResourcePile;
import shared.definitions.*;

import java.io.Serializable;
import java.util.Arrays;

public class ResourceHand implements Serializable {
	/**
	 * Description: array of resources in ResourceHand
	 */
	private int[] resources;
	/**
	 * @param wood
	 * @param brick
	 * @param sheep
	 * @param wheat
	 * @param ore
	 * @pre none
	 * @post ResourceHand initialized with specified values
	 */
	public ResourceHand(int wood,int brick, int sheep, int wheat, int ore){
		resources=new int[]{wood,brick,sheep,wheat,ore};
	}
	public ResourceHand(ResourcePile resourcePile){
		resources=new int[5];
		for(int i=0;i<5;++i){
			resources[i]=resourcePile.getQuantityResourceType(EnumConverter.ResourceType(i));
		}
	}
	public ResourceHand(JsonObject jsonObject){
		int wood = jsonObject.get("wood").getAsInt();
		int brick = jsonObject.get("brick").getAsInt();
		int sheep = jsonObject.get("sheep").getAsInt();
		int wheat = jsonObject.get("wheat").getAsInt();
		int ore = jsonObject.get("ore").getAsInt();
		resources=new int[]{wood,brick,sheep,wheat,ore};
	}
	/**
	 * @param i
	 * 		-index of resource, as specified by EnumConverter
	 * @param amount
	 * 		-quantity to be added
	 * @pre i is greater than 0 and less than  or equal to 4
	 * @post resource is added to ResourceHand
	 */
	public void addResource(int i,int amount)throws ArrayIndexOutOfBoundsException{
		if(i<0||i>4)throw new ArrayIndexOutOfBoundsException();
		resources[i]+=amount;
	}
	public void setResource(int i,int amount)throws ArrayIndexOutOfBoundsException{
		if(i<0||i>4)throw new ArrayIndexOutOfBoundsException();
		resources[i]=amount;
	}
	public void setResource(ResourceType type,int amount){
		if(type==null)return;
		resources[EnumConverter.ResourceType(type)]=amount;
	}
	/**
	 * @param type
	 * 		-type of resource to be added
	 * @param amount
	 * 		-amount to be added
	 * @pre none
	 * @post resource added
	 */
	public void addResource(ResourceType type, int amount){
		resources[EnumConverter.ResourceType(type)]+=amount;
	}
	public int[] getResources(){return resources;}
	public int getResource(int i){
		if(i<0||i>4)return -1;
		return resources[i];
	}
	public int getResource(ResourceType type){
		return resources[EnumConverter.ResourceType(type)];
	}
	/**
	 * @pre none
	 * @post creates a new ResourceHand negated
	 * @return a new ResourceHand which is negated from this one
	 */
	public ResourceHand createOpposite(){
		return new ResourceHand(-resources[0],-resources[1],-resources[2],-resources[3],-resources[4]);
	}
	public int sum(){
		int result=0;
		for(int i:resources){
			result+=i;
		}
		return result;
	}
	public static ResourceHand add(ResourceHand one, ResourceHand two){
		return new ResourceHand(
				one.getResource(ResourceType.WOOD)+two.getResource(ResourceType.WOOD),
				one.getResource(ResourceType.BRICK)+two.getResource(ResourceType.BRICK),
				one.getResource(ResourceType.SHEEP)+two.getResource(ResourceType.SHEEP),
				one.getResource(ResourceType.WHEAT)+two.getResource(ResourceType.WHEAT),
				one.getResource(ResourceType.ORE)+two.getResource(ResourceType.ORE)
		);
	}

	@Override
	public String toString() {
		return "{\n" +
				"\"brick\": \"" + getResource(ResourceType.BRICK) + "\",\n" +
				"\"ore\": \"" + getResource(ResourceType.ORE) + "\",\n" +
				"\"sheep\": \"" + getResource(ResourceType.SHEEP) + "\",\n" +
				"\"wheat\": \"" + getResource(ResourceType.WHEAT) + "\",\n" +
				"\"wood\": \"" + getResource(ResourceType.WOOD) + "\"\n" +
				'}';
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("brick",getResource(ResourceType.BRICK));
		jsonObject.addProperty("ore",getResource(ResourceType.ORE));
		jsonObject.addProperty("sheep",getResource(ResourceType.SHEEP));
		jsonObject.addProperty("wheat",getResource(ResourceType.WHEAT));
		jsonObject.addProperty("wood",getResource(ResourceType.WOOD));
		return jsonObject;
	}
	public boolean equals(ResourceHand resourceHand){
		if(resourceHand==null)return false;
		if(!toString().equals(resourceHand.toString()))return false;
		return true;
	}
}
