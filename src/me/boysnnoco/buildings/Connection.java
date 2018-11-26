package me.boysnnoco.buildings;

//Just a basic edge wrapper class.
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
