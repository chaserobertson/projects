package model.peripherals;

import model.gameboard.Hex;
import model.structures.Road;

import java.io.Serializable;

public class Robber implements Serializable {
	/**
	 * Description: hex on which the robber is
	 */
	private Hex hex;
	public Robber(){
		hex=null;
	}
	public Hex getHex(){
		return hex;
	}
	public void setHex(Hex hex){
		this.hex=hex;
	}
	/**
	 * @pre none
	 * @post Robber is removed from current Hex, and Hex has robber value set to null
	 */
	public void pickUp(){
		if(hex!=null){
			this.hex.setRobber(null);
			hex=null;
		}
	}
	/**
	 * @pre none
	 * @post robber is picked up from current hex and placed at the specified one, with hex set to the robber as well
	 */
	public void moveTo(Hex hex){
		if(hex == null){
			System.out.println("HEX is NULL");
		}
		pickUp();
		this.hex=hex;
		hex.setRobber(this);
	}
	/**
	 * @pre none
	 * @post JSON formatted string containing Robber's data
	 * @return Json formatted String
	 */
	public String serialize(){
		//TODO:implement
		return "";
	}
	public boolean equals(Robber robber){
		if(robber==null)return false;
		if(robber.getHex()!=null){
			if(hex==null)return false;
			if(!hex.getHexLocation().equals(robber.getHex().getHexLocation())){
				return false;
			}
		}
		else if(hex!=null)return false;
		return true;
	}
}
