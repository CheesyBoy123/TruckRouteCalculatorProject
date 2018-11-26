package me.boysnnoco.buildings;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

import me.boysnnoco.Main;

//A basic wrapper class for things that are NOT stores, but
//have connections TO stores.
public class Warehouse {

	
	@Expose private ArrayList<Integer> storesIndex = new ArrayList<Integer>();
	@Expose private ArrayList<Integer> time = new ArrayList<Integer>();
	@Expose private int maximumSizePerTruck;
	@Expose private int locX, locY;
	
	private Main main;
	private ArrayList<Connection> storeConnections;
	
	
	
	public Warehouse(Main main, ArrayList<Integer> storeConnections, ArrayList<Integer> time, int maximumSizePerTruck) {
		this.storesIndex = storeConnections;
		this.time = time;
		this.maximumSizePerTruck = maximumSizePerTruck;
		this.main = main;
		initWarehouse();
	}
	
	public void initWarehouse() {
		main = Main.main;
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
	
	public ArrayList<Connection> getConnectedStores() {
		return storeConnections;
	}
	
	public int getMaximumSize() {
		return maximumSizePerTruck;
	}
	
	public void setAllRoutesToFalse() {
		for(int i = 0; i < storeConnections.size(); i++) {
			storeConnections.get(i).getConnectedStore().reset();
		}
	}
	
	public int getTimeToStore(Store store) {
		for(int i = 0; i < storeConnections.size(); i++) {
			if(storeConnections.get(i).getConnectedStore() == store) {
				return storeConnections.get(i).getTimeToStore();
			}
		}
		return -1;
	}
	
	public int getX() {
		return locX;
	}
	
	public int getY() {
		return locY;
	}
	
	
}
