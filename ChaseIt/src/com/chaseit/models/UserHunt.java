package com.chaseit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserHunt")
public class UserHunt extends ParseObject{
	
	public enum HuntStatus {
		IN_PROGRESS,
		COMPLETED
	};
	
	public UserHunt(){
		//empty constructor
	}
	
	public void setUser(ParseUser user){
		put("user", user);
	}

	public ParseUser getUser(){
		return getParseUser("user");
	}

	public void setHunt(Hunt hunt){
		put("hunt", hunt);
	}

	public Hunt getHunt(){
		return (Hunt)getParseObject("hunt");
	}

	public void setLastLocation(Location location){
		put("lastlocation", location);
	}

	public Location getLastLocation(){
		return (Location)getParseObject("lastlocation");
	}

	public void setHuntStatus(HuntStatus status){
		put("huntstatus", status.toString());
	}

	public HuntStatus getHuntStatus(){
		return HuntStatus.valueOf(getString("huntstatus"));
	}

}