package me.boysnnoco.buildings;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

import me.boysnnoco.Main;

/**
 * This is our store container class
 */
public class Store {

	//These variables are exposed the GSON for data parsing from our JSON files.
	//The size of our order
	@Expose private int orderSize;
	//The edges that are connected to this store along with the relevant time. StoresIndex is an array of store IDs
	@Expose private ArrayList<Integer> storesIndex = new ArrayList<Integer>();
	@Expose private ArrayList<Integer> time = new ArrayList<Integer>();
	//The stores ID
	@Expose private int storeId;
	//The stores X,Y location on the screen
	@Expose private int locX, locY;
	
	//These variables ARE NOT exposed to GSON
	//An array of connections from this store to others
	private ArrayList<Connection> routeConnections;
	//A boolean flag to show if it's been visited
	private boolean hasBeenVisited = false;
	
	//The amount of cargo current being taken.
	private int currentlyTakenOrder = 0;
	
	//Our constructor for the class
	public Store(int ordersize, int storeId,  ArrayList<Integer> storesIndex, ArrayList<Integer> time) {
		this.orderSize = ordersize;
		this.storesIndex = storesIndex;
		this.time = time;
		this.storeId = storeId;
		//call initStore function to initialize our store.
		initStore();
	}
	
	//Initialize our store
	public void initStore() {
		//Get an instance of our main class
		Main main = Main.main;
		//Reset our connections from this store
		routeConnections = new ArrayList<Connection>();
		//Loop through each of the storesIndex IDs and get the store that corresponds with that ID
		for(int i = 0; i < storesIndex.size(); i++) {
			if(main.getStoreAtIndex(storesIndex.get(i)) == null) {
				System.out.println("Couldn't find the store at index " + storesIndex.get(i));
				continue;
			}
			//Create a new connection from that store with the corresponding time
			Connection c = new Connection(main.getStoreAtIndex(storesIndex.get(i)), time.get(i));
			routeConnections.add(c);
		}
		
	}
	
	//Get our differnt connections
	public ArrayList<Connection> getDifferentStoreConnections() {
		return routeConnections;
	}
	
	//Set us being visited
	public void setVisited(boolean visisted) {
		hasBeenVisited = visisted;
	}
	
	//We have "taken" cargo from this store for a route
	public void removeInventory(int a) {
		currentlyTakenOrder += a;
		if(currentlyTakenOrder >= orderSize)
			setVisited(true);
	}
	
	//Returns if we've been visisted or not.
	public boolean hasBeenVisisted() {
		return hasBeenVisited;
	}
	
	//Get the "current" order size (orderSize - how much has been taken)
	public int getOrderSize() {
		return orderSize - currentlyTakenOrder;
	}
	
	//Get the store ID
	public int getStoreId() {
		return storeId;
	}
	
	//Get the X Location
	public int getX() {
		return locX;
	}
	
	//Get the Y Location
	public int getY() {
		return locY;
	}

	//reset the store.
	public void reset() {
		
		setVisited(false);
		currentlyTakenOrder = 0;
	}
	
}
