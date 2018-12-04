package me.boysnnoco.buildings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Route {
	
	private ArrayList<Store> storesToVisit;
	private Warehouse startingWarehouse;
	private int currentStore = 0;
	private int totalTime = 0;
	private int totalOrderSize = 0;
	
	public Route(Warehouse startWarehouse) {
		this.storesToVisit = new ArrayList<Store>();
		this.startingWarehouse = startWarehouse;
	}
	
	//Go to next store in the list (this also increments our currentStore
	//variable, this effectivly puts our focus on the next store.
	public void goToNextStore() {
		currentStore++;
	}
	
	public Store getCurrentStore() {
		if(storesToVisit.size() > currentStore)
			return storesToVisit.get(currentStore);
		return null;
	}
	
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
	
	public void addStoreToRoute(Connection c, int ordersize) {
		storesToVisit.add(c.getConnectedStore());
		totalTime += c.getTimeToStore();
		totalOrderSize += ordersize;
		c.getConnectedStore().removeInventory(ordersize);
	}
	
	public int getTotalOrderSize() {
		return totalOrderSize;
	}
	
	public int getTotalTime() {
		return totalTime;
	}
	
	
	private static Route basicDFSRouteHelper(Route r, Connection startingConnection, int maxOrderSize) {
		//Basically we don't want to go over our size limit (determined by the warehouse)
		//This acts as our "base case" so that we have multiple DFS routes, not just one.
		if(r.getTotalOrderSize() + startingConnection.getConnectedStore().getOrderSize() > maxOrderSize)
			return r;
		
		r.addStoreToRoute(startingConnection);
		
		for(int i = 0; i < startingConnection.getConnectedStore().getDifferentStoreConnections().size(); i++) {
			//The store we are looking at that is connected to the current one HASNT been visited yet.
			if(!startingConnection.getConnectedStore().getDifferentStoreConnections().get(i).getConnectedStore().hasBeenVisisted())
				r = basicDFSRouteHelper(r, startingConnection.getConnectedStore().getDifferentStoreConnections().get(i), maxOrderSize);
		}
		
		return r;
	}
	//Generates a bunch of routes that a truck can travel. This uses the DFS routes.
	public static ArrayList<Route> generateDFSRoute(Warehouse startingWarehouse) {
		//First of all set all the routes to false.
		startingWarehouse.setAllRoutesToFalse();
		ArrayList<Connection> warehouseConnects = startingWarehouse.getConnectedStores();
		ArrayList<Route> allWarehouseRoutes = new ArrayList<Route>();
		for(int i = 0; i < warehouseConnects.size(); i++) {
			if(!warehouseConnects.get(i).getConnectedStore().hasBeenVisisted())
				allWarehouseRoutes.add(basicDFSRouteHelper(new Route(startingWarehouse), warehouseConnects.get(i), startingWarehouse.getMaximumSize()));
		}
		return allWarehouseRoutes;
	}

	private static Route basicBFSRouter(Warehouse ware, Connection startingCon) {
		Route r = new Route(ware);
		
		Queue<Connection> allConnections = new LinkedList<Connection>();
		allConnections.add(startingCon);
		
		
		while(!allConnections.isEmpty()) {
			Connection c = allConnections.poll();
			if(c.getConnectedStore().hasBeenVisisted()) continue;
			
			if(r.getTotalOrderSize() + c.getConnectedStore().getOrderSize() > ware.getMaximumSize()) continue;
			
			r.addStoreToRoute(c);
			
			for(int i = 0; i < c.getConnectedStore().getDifferentStoreConnections().size(); i++) {
				if(!c.getConnectedStore().getDifferentStoreConnections().get(i).getConnectedStore().hasBeenVisisted())
					allConnections.add(c.getConnectedStore().getDifferentStoreConnections().get(i));
			}
			
		}
		
		return r.getTotalOrderSize() > 0 ? r : null;
	}
	
	public static ArrayList<Route> generateBFSRoute(Warehouse startingWarehouse) {
		startingWarehouse.setAllRoutesToFalse();
		ArrayList<Route> routes = new ArrayList<Route>();
		for(int i = 0; i < startingWarehouse.getConnectedStores().size(); i++) {
			Route r = basicBFSRouter(startingWarehouse, startingWarehouse.getConnectedStores().get(i));
			if(r != null)
				routes.add(r);
		}
		return routes;
	}
	
	
	
	
	public static ArrayList<Route> generateSmartDFSTime(Warehouse startingWarehouse) {
		ArrayList<Route> routes = new ArrayList<Route>();
		
		int time = Integer.MAX_VALUE;
		int totalRoutes = startingWarehouse.getConnectedStores().size() + 1;
		
		int offset = 0;
		
		//O(n^2) bad!!!!!
		while(offset < startingWarehouse.getConnectedStores().size()) {
			startingWarehouse.setAllRoutesToFalse();
			int currentTime = 0;
			int currentRoutes = 0;
			ArrayList<Route> tempRoute = new ArrayList<Route>();
			
			for(int i = offset; (i - offset) < startingWarehouse.getConnectedStores().size(); i++) {
				if(!startingWarehouse.getConnectedStores().get(i % startingWarehouse.getConnectedStores().size()).getConnectedStore().hasBeenVisisted()) {
					Route r = generateSmartDFSRouteHelper(startingWarehouse.getConnectedStores().get(i % startingWarehouse.getConnectedStores().size()), new Route(startingWarehouse), startingWarehouse.getMaximumSize());
					currentTime += r.getTotalTime();
					currentRoutes++;
					tempRoute.add(r);
				}
			
			}
			
			//Prioritize LESS routes rather than LESS time!
			if((currentTime < time || (currentTime == time && currentRoutes < totalRoutes)) && validateRoutes(startingWarehouse)) {
				routes = tempRoute;
				totalRoutes = currentRoutes;
				time = currentTime;
			}
			
			offset++;
		}
		
		
		
		return routes;
	}
	
	public static Route generateSmartDFSRouteHelper(Connection c, Route r, int maxsize) {
		//Don't do anything, our route is already at maximum size.
		if(maxsize == r.getTotalOrderSize()) return r;
		
		if(c.getConnectedStore().getOrderSize() + r.getTotalOrderSize() <= maxsize) {
			r.addStoreToRoute(c, c.getConnectedStore().getOrderSize());

		} else {
			//The truck is FULL now. No need to proceed.
			int maxload = maxsize - r.getTotalOrderSize();
			r.addStoreToRoute(c, maxload);
		}
		
		for(int i = 0; i < c.getConnectedStore().getDifferentStoreConnections().size(); i++) {
			if(!c.getConnectedStore().getDifferentStoreConnections().get(i).getConnectedStore().hasBeenVisisted())
				r = generateSmartDFSRouteHelper(c.getConnectedStore().getDifferentStoreConnections().get(i), r, maxsize);
		}
		
		return r;
	}
	
	//Basically optimize the maximum amount of routes we should take. (In our scope this minimizes the number of drivers
	//you need to complete this route. This will break up store orders, it will also choose the fastest order.
	public static ArrayList<Route> generateSmartDFSRoute(Warehouse startingwareHouse) {
		
		ArrayList<Route> routes = new ArrayList<Route>();
	
		int time = Integer.MAX_VALUE;
		int totalRoutes = startingwareHouse.getConnectedStores().size() + 1;
		
		int offset = 0;
		
		//O(n^2) bad!!!!!
		while(offset < startingwareHouse.getConnectedStores().size()) {
			startingwareHouse.setAllRoutesToFalse();
			int currentTime = 0;
			int currentRoutes = 0;
			ArrayList<Route> tempRoute = new ArrayList<Route>();
			
			for(int i = offset; (i - offset) < startingwareHouse.getConnectedStores().size(); i++) {
				if(!startingwareHouse.getConnectedStores().get(i % startingwareHouse.getConnectedStores().size()).getConnectedStore().hasBeenVisisted()) {
					Route r = generateSmartDFSRouteHelper(startingwareHouse.getConnectedStores().get(i % startingwareHouse.getConnectedStores().size()), new Route(startingwareHouse), startingwareHouse.getMaximumSize());
					currentTime += r.getTotalTime();
					currentRoutes++;
					tempRoute.add(r);
				}
			
			}
			
			//Prioritize LESS routes rather than LESS time!
			if((currentRoutes < totalRoutes || (currentRoutes == totalRoutes && currentTime < time)) && validateRoutes(startingwareHouse)) {
				routes = tempRoute;
				totalRoutes = currentRoutes;
				time = currentTime;
			}
			
			offset++;
		}
		
		
		
		return routes;
	}
	
	
	public static Route daijkstraRouteHelper(Warehouse w, Connection con) {
		Route r = new Route(w);
		
		Queue<Connection> allConnections = new LinkedList<Connection>();
		allConnections.add(con);
		
		Connection currentCon = null;
		int currentMax = -1;
		
		while(!allConnections.isEmpty()) {
			if(r.getTotalOrderSize() >= w.getMaximumSize()) break;
			Connection c = allConnections.poll();
			if(c == null) {
				if(currentCon != null) {
					//This could lead to problems

					r.addStoreToRoute(currentCon, currentMax);
					
					currentCon = null;
					currentMax = -1;
				}
				continue;
			}
			
			
			
			
			if(c.getConnectedStore().hasBeenVisisted()) continue;
			
			
			
			if(currentCon == null || currentCon.getTimeToStore() > c.getTimeToStore()) { 
				currentCon = c;
				//I don't like this else statement.
				if(c.getConnectedStore().getOrderSize() + r.getTotalOrderSize() > w.getMaximumSize()) {
					currentMax = (w.getMaximumSize() - r.getTotalOrderSize());
				} else {
					currentMax = c.getConnectedStore().getOrderSize();
				}
			}
			
			for(int i = 0; i < c.getConnectedStore().getDifferentStoreConnections().size(); i++) {
				if(!c.getConnectedStore().getDifferentStoreConnections().get(i).getConnectedStore().hasBeenVisisted())
					allConnections.add(c.getConnectedStore().getDifferentStoreConnections().get(i));
			}
			
			allConnections.add(null);
			
		}
		
		return r;
	}
	
	public static ArrayList<Route> generateDaijkstraRoute(Warehouse warehouse) {
		warehouse.setAllRoutesToFalse();
		ArrayList<Route> routes = new ArrayList<Route>();
		
		for(int i = 0; i < warehouse.getConnectedStores().size(); i++) {
			if(!warehouse.getConnectedStores().get(i).getConnectedStore().hasBeenVisisted())
				routes.add(daijkstraRouteHelper(warehouse, warehouse.getConnectedStores().get(i)));
			if(i + 1 == warehouse.getConnectedStores().size() && !validateRoutes(warehouse)) i = -1;
		}
		
		if(!validateRoutes(warehouse)) {
			return null;
		}
		
		return routes;
	}
	
	
	//small helper function to validate that ALL of the stores connected to this warehouse have been visited.
	private static boolean validateRoutes(Warehouse startingwareHouse) {
		for(int i = 0; i < startingwareHouse.getConnectedStores().size(); i++) {
			if(!startingwareHouse.getConnectedStores().get(i).getConnectedStore().hasBeenVisisted()) return false;
		}
		return true;
	}
	
}
