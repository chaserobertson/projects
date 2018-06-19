package model.resources;

import debugger.Debugger;
import model.definitions.EnumConverter;
import model.definitions.ResourceHand;
import model.definitions.ResourceTypeQuantityPair;
import shared.definitions.*;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class ResourcePile implements Serializable {
	/**
	 * Description: amount of REsource types in traditional game
	 */
	private static final int RESOURCETYPECOUNT=5;
	/**
	 * Description: array of resources arranged inr order specified by EnumConverter
	 */
	private int[] resourceStock = new int[RESOURCETYPECOUNT];

	/**
	 * @pre none
	 * @post ResourcePile is initialized to have 0 of each resource Type
	 */
	public ResourcePile(){
		for(int i=0;i<RESOURCETYPECOUNT;++i){
			resourceStock[i]=0;
		}
	}
	/**
	 * @param wood
	 * 		-amount of wood
	 * @param brick
	 * 		-amount of brick
	 * @param sheep
	 * 		-amount of sheep
	 * @param wheat
	 * 		-amount of wheat
	 * @param ore
	 * 		-amount of ore
	 * @pre none
	 * @post ResourceAggreation is initialized with specified values
	 */
	public ResourcePile(int wood, int brick, int sheep, int wheat, int ore){
		resourceStock[EnumConverter.ResourceType(ResourceType.WOOD)]=wood;
		resourceStock[EnumConverter.ResourceType(ResourceType.BRICK)]=brick;
		resourceStock[EnumConverter.ResourceType(ResourceType.SHEEP)]=sheep;
		resourceStock[EnumConverter.ResourceType(ResourceType.WHEAT)]=wheat;
		resourceStock[EnumConverter.ResourceType(ResourceType.ORE)]=ore;
	}
	public ResourcePile(ResourceHand resourceHand){
		resourceStock[EnumConverter.ResourceType(ResourceType.WOOD)]=resourceHand.getResource(ResourceType.WOOD);
		resourceStock[EnumConverter.ResourceType(ResourceType.BRICK)]=resourceHand.getResource(ResourceType.BRICK);
		resourceStock[EnumConverter.ResourceType(ResourceType.SHEEP)]=resourceHand.getResource(ResourceType.SHEEP);
		resourceStock[EnumConverter.ResourceType(ResourceType.WHEAT)]=resourceHand.getResource(ResourceType.WHEAT);
		resourceStock[EnumConverter.ResourceType(ResourceType.ORE)]=resourceHand.getResource(ResourceType.ORE);
	}

	public int[] getResourceStock(){return resourceStock;}
	public void setResourceStock(int[] resourceStock){this.resourceStock=resourceStock;}
	public int getQuantityResourceType(ResourceType type){
		return resourceStock[EnumConverter.ResourceType(type)];
	}
	public void setQuantityResourceType(ResourceType type, int value){

		if(type!=null)resourceStock[EnumConverter.ResourceType(type)]=value;
	}
	public void addResource(ResourceType type,int quantity){
		resourceStock[EnumConverter.ResourceType(type)]+=quantity;
	}
	public void addResource(ResourceTypeQuantityPair toAdd){
		if(toAdd.type!=null)resourceStock[EnumConverter.ResourceType(toAdd.type)]+=toAdd.quantity;
	}
	public void addResources(ResourceHand requestForm){
		for(int i=0;i<RESOURCETYPECOUNT;++i){
			resourceStock[i]+=requestForm.getResource(i);
		}
	}
	public void addResources(ResourceTypeQuantityPair... toAdds){
		for(ResourceTypeQuantityPair toAdd : toAdds){
			if(toAdd.type!=null)resourceStock[EnumConverter.ResourceType(toAdd.type)]+=toAdd.quantity;
		}
	}
	public void addResources(List<ResourceTypeQuantityPair> toAdds){
		for(ResourceTypeQuantityPair toAdd : toAdds){
			if(toAdd.type!=null)resourceStock[EnumConverter.ResourceType(toAdd.type)]+=toAdd.quantity;
		}
	}
	/**
	 * @param receivingPile
	 * 		-resourceAggregation receiving
	 * @param requestForm
	 * 		-changes to be made
	 * @pre receivingPile and requestForm Are not null
	 * @post identified whether transfer can be made
	 * @return true if resources can be transfered
	 */
	public boolean canTransferResourcesTo(ResourcePile receivingPile, ResourceHand requestForm){
		for(int i=0;i<RESOURCETYPECOUNT;++i){
			if(requestForm.getResource(i)>resourceStock[i])return false;
			if(-requestForm.getResource(i)>receivingPile.getResourceStock()[i])return false;
			//The above is inverted from the rest. This is causing some problems.
			//To receive an inversion its highly reccomended to either call requestForm.createOpposite
			//Or do the canTransferResourcesFrom.
			//Both of which will work equally well.
		}
		return true;
	}
	/**
	 * @param receivingPile
	 * 		-resourceAggregation receiving
	 * @param requestForm
	 * 		-changes to be made
	 * @pre receivingPile and requestForm Are not null
	 * @post identified whether transfer can be made
	 * @return true if resources can be transfered
	 */
	public boolean canTransferResourcesTo(ResourcePile receivingPile, ResourceTypeQuantityPair... requestForm){
		for(ResourceTypeQuantityPair request : requestForm){
			if(request.type!=null) {
				if (request.quantity > resourceStock[EnumConverter.ResourceType(request.type)]) return false;
				if (-request.quantity > receivingPile.getResourceStock()[EnumConverter.ResourceType(request.type)])
					return false;
			}
		}
		return true;
	}
	/**
	 * @param receivingPile
	 * 		-resourceAggregation receiving
	 * @param requestForm
	 * 		-changes to be made
	 * @pre receivingPile and requestForm Are not null
	 * @post transferred resources if possible
	 * @return true if resources were transferred
	 */
	public boolean transferResourcesTo(ResourcePile receivingPile, ResourceHand requestForm){//Return false if insufficient
		if(!canTransferResourcesTo(receivingPile,requestForm))return false;
		for(int i=0;i<RESOURCETYPECOUNT;++i){
			receivingPile.addResource(EnumConverter.ResourceType(i),requestForm.getResource(i));
			resourceStock[i]-=requestForm.getResource(i);
		}
		return true;
	}
	/**
	 * @param givingPile
	 * 		-resourceAggregation  giving
	 * @param requestForm
	 * 		-changes to be made
	 * @pre receivingPile and requestForm Are not null
	 * @post identified whether transfer can be made
	 * @return true if resources can be transfered
	 */
	public boolean canTransferResourcesFrom(ResourcePile givingPile, ResourceHand requestForm){
		return givingPile.canTransferResourcesTo(this, requestForm);
	}
	/**
	 * @param givingPile
	 * 		-resourceAggregation  giving
	 * @param requestForm
	 * 		-changes to be made
	 * @pre receivingPile and requestForm Are not null
	 * @post identified whether transfer can be made
	 * @return true if resources can be transfered
	 */
	public boolean transferResourcesFrom(ResourcePile givingPile, ResourceHand requestForm){
		return givingPile.transferResourcesTo(this,requestForm);
	}
	/**
	 * @param receivingPile
	 * 		-resourceAggregation receiving
	 * @param requests
	 * 		-changes to be made
	 * @pre receivingPile and requestForm Are not null
	 * @post transferred resources if possible
	 * @return true if resources were transferred
	 */
	public boolean transferResourcesTo(ResourcePile receivingPile, ResourceTypeQuantityPair... requests){
		for(ResourceTypeQuantityPair request : requests){
			if(request.type!=null&&request.quantity>resourceStock[EnumConverter.ResourceType(request.type)])return false;
		}
		for(ResourceTypeQuantityPair request :requests){
			if(request.type!=null) {
				receivingPile.addResource(request);
				resourceStock[EnumConverter.ResourceType(request.type)] -= request.quantity;
			}
		}
		return true;
	}
	/**
	 * @param givingPile
	 * 		-resourceAggregation  giving
	 * @param requests
	 * 		-changes to be made
	 * @pre receivingPile and requestForm Are not null
	 * @post identified whether transfer can be made
	 * @return true if resources can be transfered
	 */
	public boolean transferResourcesFrom(ResourcePile givingPile, ResourceTypeQuantityPair... requests){
		return givingPile.transferResourcesTo(this,requests);
	}
	/**
	 * @param receivingPile
	 * 		-resourceAggregation receiving
	 * @param type
	 * 		-type of resource to be exchanges
	 * @param quantity
	 * 		-amount of resource to be exchanged
	 * @pre receivingPile and requestForm Are not null
	 * @post transferred resources if possible
	 * @return true if resources were transferred
	 */
	public boolean transferResourceTo(ResourcePile receivingPile, ResourceType type, int quantity){
		return transferResourcesTo(receivingPile,new ResourceTypeQuantityPair(type,quantity));
	}
	/**
	 * @param givingPile
	 * 		-resourceAggregation receiving
	 * @param type
	 * 		-type of resource to be exchanges
	 * @param quantity
	 * 		-amount of resource to be exchanged
	 * @pre receivingPile and requestForm Are not null
	 * @post transferred resources if possible
	 * @return true if resources were transferred
	 */
	public boolean transferResourceFrom(ResourcePile givingPile, ResourceType type, int quantity){
		return givingPile.transferResourceTo(this,type,quantity);
	}
	public boolean canRemoveResources(ResourceHand toRemove){
		for(int i=0;i<ResourceType.numberOfTypes();++i){
			if(resourceStock[i]<toRemove.getResource(i))return false;
		}
		return true;
	}
	/**
	 * @pre none
	 * @post returned values
	 * @return sum of resources in Aggregation
	 */
	public int sumOfResources(){
		int result=0;
		for(int i : resourceStock){
			result+=i;
		}
		return result;
	}
	public ResourceType chooseRandomResource(){
		try {
			int sum = sumOfResources();
			if (sum <= 0) return null;
			Random rand = new Random();
			int result = rand.nextInt(sum);//now it is an index between 0 and sum-1
			int i = 0;
			while (i < RESOURCETYPECOUNT) {
				if (result >= resourceStock[i]) result -= resourceStock[i];
				else break;
				++i;
			}
			if (i >= RESOURCETYPECOUNT) return null;
			return EnumConverter.ResourceType(i);
		}catch (Exception e){
			Debugger.LogMessage("Error in choosing random resource");
			return ResourceType.WOOD;
		}
	}
	/**
	 * @pre none
	 * @post Json string containing relevant information to ResourcePile
	 * @return JSON formatted string
	 */
	public String serialize(){
		//TODO:implement
		//Grab each value, associate it with the string name, and send it
		return "";
	}
	public boolean equals(ResourcePile resourcePile){
		if(resourcePile==null)return false;
		for(int i=0;i<resourceStock.length;++i){
			if(resourceStock[i]!=resourcePile.getResourceStock()[i])return false;
		}
		return true;
	}
	//To deserialize simply grab each of the values and set it, or do a constructor on it
	@Override
	public String toString() {
		return "{\n" +
				"\"brick\": \"" + resourceStock[EnumConverter.ResourceType(ResourceType.BRICK)] + "\",\n" +
				"\"ore\": \"" + resourceStock[EnumConverter.ResourceType(ResourceType.ORE)] + "\",\n" +
				"\"sheep\": \"" + resourceStock[EnumConverter.ResourceType(ResourceType.SHEEP)] + "\",\n" +
				"\"wheat\": \"" + resourceStock[EnumConverter.ResourceType(ResourceType.WHEAT)] + "\",\n" +
				"\"wood\": \"" + resourceStock[EnumConverter.ResourceType(ResourceType.WOOD)] + "\"\n" +
				'}';
	}
}
