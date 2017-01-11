package model.resources;
import shared.definitions.*;

import java.io.Serializable;

public class DevelopmentCard implements Serializable{
	/**
	 * Description: type of the development Card
	 */
	private DevCardType type;
	/**
	 * @param type
	 * 		-type of the card being created
	 * @pre type is a valid type of the enum DevCardType
	 * @post a new DevelopmentCard of the type indicated
	 */
	public DevelopmentCard(DevCardType type){
		this.type=type;
	}
	public DevCardType getType(){return type;}
	public void setType(DevCardType type){this.type=type;}
	/**
	 * @pre Development Card has a "type"
	 * @post A String in JSON format containing the DevelopmentCard type]
	 * @return a String in JSON format
	 */
	public String serialize(){
		//TODO:implement
		return "";
	}
}
