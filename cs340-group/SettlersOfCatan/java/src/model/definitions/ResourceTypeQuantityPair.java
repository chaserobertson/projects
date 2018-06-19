package model.definitions;

import shared.definitions.ResourceType;

import java.io.Serializable;

public class ResourceTypeQuantityPair implements Serializable {
	public ResourceType type;
	public int quantity;
	public ResourceTypeQuantityPair(ResourceType type, int quantity){
		this.type=type;
		this.quantity=quantity;
	}
}
