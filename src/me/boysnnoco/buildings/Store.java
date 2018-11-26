package me.boysnnoco.buildings;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

import me.boysnnoco.Main;

public class Store {

	@Expose private int orderSize;
	@Expose private ArrayList<Integer> storesIndex = new ArrayList<Integer>();
	@Expose private ArrayList<Integer> time = new ArrayList<Integer>();
	@Expose private int storeId;
	@Expose private int locX, locY;
	
	private ArrayList<Connection> routeConnections;
	private boolean hasBeenVisited = false;
	private Main main;
	private int currentlyTakenOrder = 0;
	
	public Store(int ordersize, int storeId,  ArrayList<Integer> storesIndex, ArrayList<Integer> time) {
		this.orderSize = ordersize;
		this.storesIndex = storesIndex;
		this.time = time;
		this.storeId = storeId;
		initStore();
	}
	
	public void initStore() {
		main = Main.main;
		routeConnections = new ArrayList<Connection>();
		for(int i = 0; i < storesIndex.size(); i++) {
			if(main.getStoreAtIndex(storesIndex.get(i)) == null) {
				System.out.println("Couldn't find the store at index " + storesIndex.get(i));
				continue;
			}
			Connection c = new Connection(main.getStoreAtIndex(storesIndex.get(i)), time.get(i));
			routeConnections.add(c);
		}
		
	}
	
	public ArrayList<Connection> getDifferentStoreConnections() {
		return routeConnections;
	}
	
	public void setVisited(boolean visisted) {
		hasBeenVisited = visisted;
	}
	
	public void removeInventory(int a) {
		currentlyTakenOrder += a;
		if(currentlyTakenOrder >= orderSize)
			setVisited(true);
	}
	
	public boolean hasBeenVisisted() {
		return hasBeenVisited;
	}
	
	public int getOrderSize() {
		return orderSize - currentlyTakenOrder;
	}
	
	public int getStoreId() {
		return storeId;
	}
	
	public int getX() {
		return locX;
	}
	
	public int getY() {
		return locY;
	}

	//reset the store.
	public void reset() {
		
		setVisited(false);
		currentlyTakenOrder = 0;
	}
	
}
