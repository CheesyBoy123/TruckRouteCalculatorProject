package me.boysnnoco.buildings;

import java.util.ArrayList;

/**
 * Our route wrapper class
 */
public class Route {
	
	//The stores that we are going to on this route
	private ArrayList<Store> storesToVisit;
	//The warehouse we start from
	private Warehouse startingWarehouse;
	//The index of the store we are current on
	private int currentStore = 0;
	//The total time on the route
	private int totalTime = 0;
	//The total order size of the route
	private int totalOrderSize = 0;
	//The number of comparisons it took to create this route.
	private int totalOpperations = 0;
	
	//Constructor for our route
	public Route(Warehouse startWarehouse) {
		this.storesToVisit = new ArrayList<Store>();
		this.startingWarehouse = startWarehouse;
	}
	
	//Go to next store in the list (this also increments our currentStore
	//variable, this effectivly puts our focus on the next store.
	public void goToNextStore() {
		currentStore++;
	}
	
	//Get the store we are currently on
	public Store getCurrentStore() {
		if(storesToVisit.size() > currentStore)
			return storesToVisit.get(currentStore);
		return null;
	}
	
	//Get the warehouse we started at
	public Warehouse getStartingWarehouse() {
		return startingWarehouse;
	}
	
	//Add a store to a route and update time and order size variables.
	public void addStoreToRoute(Connection c) {
		storesToVisit.add(c.getConnectedStore());
		totalTime += c.getTimeToStore();
		totalOrderSize += c.getConnectedStore().getOrderSize();
		c.getConnectedStore().setVisited(true);
	}
	
	//Add a store to our route with an specific order size
	public void addStoreToRoute(Connection c, int ordersize) {
		storesToVisit.add(c.getConnectedStore());
		totalTime += c.getTimeToStore();
		totalOrderSize += ordersize;
		c.getConnectedStore().removeInventory(ordersize);
	}
	
	//Get the total order size
	public int getTotalOrderSize() {
		return totalOrderSize;
	}
	
	//Get the total time
	public int getTotalTime() {
		return totalTime;
	}
	
	//Get the total comparisons
	public int getTotalComparisons() {
		return totalOpperations;
	}
	
	//Add the time from the last store to the warehouse to our total time
	public void addTimeFromLastStore() {
		if(storesToVisit.isEmpty()) return;
		totalTime += startingWarehouse.getTimeToStore(storesToVisit.get(storesToVisit.size() - 1));
	}
	
	//Increment the number of comparisons
	public void incrementOperations() {
		totalOpperations++;
	}
	
}
