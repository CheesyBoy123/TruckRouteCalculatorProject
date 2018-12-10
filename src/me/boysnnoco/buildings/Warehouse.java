package me.boysnnoco.buildings;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

import me.boysnnoco.Main;

//A basic wrapper class for things that are NOT stores, but
//have connections TO stores.
public class Warehouse {

	//These variables are exposed to GSON for Json parsingg
	//An array of store IDs that our warehouse can go to
	@Expose private ArrayList<Integer> storesIndex = new ArrayList<Integer>();
	//The time corresponding to going to those stores
	@Expose private ArrayList<Integer> time = new ArrayList<Integer>();
	//The maximum amount of cargo we can take per route
	@Expose private int maximumSizePerTruck;
	//The X,Y location of the warehouse
	@Expose private int locX, locY;
	
	//The final array of connections from the warehouse to different stores
	private ArrayList<Connection> storeConnections;
	
	
	//Constructor of our warehouse
	public Warehouse(ArrayList<Integer> storeConnections, ArrayList<Integer> time, int maximumSizePerTruck) {
		this.storesIndex = storeConnections;
		this.time = time;
		this.maximumSizePerTruck = maximumSizePerTruck;
		//Init our warehouse
		initWarehouse();
	}
	
	//Initialize our warehouse
	public void initWarehouse() {
		//Get our main instance
		Main main = Main.main;
		
		
		//Loop through all our storeIDs and find the store and create a new connection
		this.storeConnections = new ArrayList<Connection>();
		for(int i = 0; i < storesIndex.size(); i++) {
			if(main.getStoreAtIndex(storesIndex.get(i)) == null) {
				System.out.println("Couldn't find the store at index " + storesIndex.get(i));
				continue;
			}
			Connection c = new Connection(main.getStoreAtIndex(storesIndex.get(i)), time.get(i));
			storeConnections.add(c);
		}
	}
	
	//Get all our connected stores
	public ArrayList<Connection> getConnectedStores() {
		return storeConnections;
	}
	
	//Get the maximum size
	public int getMaximumSize() {
		return maximumSizePerTruck;
	}
	
	
	
	//Get the time from the warehouse to a specific store
	public int getTimeToStore(Store store) {
		for(int i = 0; i < storeConnections.size(); i++) {
			if(storeConnections.get(i).getConnectedStore() == store) {
				return storeConnections.get(i).getTimeToStore();
			}
		}
		return -1;
	}
	
	//Get the X location
	public int getX() {
		return locX;
	}
	
	//Get the Y Location
	public int getY() {
		return locY;
	}
	
	
}
