package me.boysnnoco.buildings;

//Just a basic edge wrapper class with the corresponding store and time to that store
public class Connection {

	private Store connectedStore;
	private int timeToStore;
	
	public Connection(Store connectedStore, int timeToStore) {
		this.connectedStore = connectedStore;
		this.timeToStore = timeToStore;
	}
	
	public Store getConnectedStore() {
		return connectedStore;
	}
	
	public int getTimeToStore() {
		return timeToStore;
	}
	
}
