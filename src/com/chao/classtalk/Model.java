package com.chao.classtalk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.util.Log;

public class Model extends Observable implements Serializable{
	String Building;
	String Room;
	String personName;
	String user_password;
	String real_name;
	int isVisible = 0;
	ArrayList<String> DCrooms;
	ArrayList<String> MCrooms;
	
	StringBuffer buffer = new StringBuffer();
	
	boolean canEnter = false;
	
	public void setDCrooms (ArrayList<String> input) {
		DCrooms = input;
	}
	
	public void setMCrooms(ArrayList<String> input) {
		MCrooms = input;
	}
	
	public ArrayList<String> getDCrooms() {
		return DCrooms;
	}
	
	public ArrayList<String> getMCrooms() {
		return MCrooms;
	}
	
	public void setInvisible() {
		isVisible = 0;
	}
	
	public void setVisible() {
		isVisible = 1;
	}
	
	public int getVisible() {
		return isVisible;
	}
	
	public String getName() {
		return personName;
	}
	
	public void setName(String name) {
		personName = name;
	}
	
	public String get_real_name(){
		return real_name;
	}
	
	public void set_real_name(String name) {
		real_name = name;
	}
	
	public String getPassword() {
		return user_password;
	}
	
	public void setPassword(String password) {
		user_password = password;
	}
	
	public void EnableEnter() {
		canEnter = true;
		setChanged();
		notifyObservers();
	}
	
	public void DisableEnter() {
		canEnter = false;
		setChanged();
		notifyObservers();
	}
	
	//Set Building and Room
	public void SetBuilding(String name) {
		Building = name;
		setChanged();
		notifyObservers();
	}
	
	public void SetRoom(String name) {
		Room = name;
		setChanged();
		notifyObservers();
	}
	
	public String GetBuilding() {
		return Building;
	}
	
	public String GetRoom() {
		return Room;
	}
	
	//chat buffer
	
	public synchronized void setBuffer(String s) {
		buffer.append(s + "\n");
	}
	public synchronized String getBuffer(){
		return buffer.toString();
	}
	
	//Observer methods
	
	public synchronized void initObservers() {
		setChanged();
		notifyObservers();
	}
	
	@Override
	public void addObserver(Observer observer) {
		Log.d("Model", "addObserver");
		
		super.addObserver(observer);
	}
	
	@Override
	public void notifyObservers() {
		Log.d ("Model", "notify observer");
		super.notifyObservers();
	}
}
