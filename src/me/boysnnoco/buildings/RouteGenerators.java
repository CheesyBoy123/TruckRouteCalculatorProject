package me.boysnnoco.buildings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import me.boysnnoco.Main;

/**
 * 
 * This is the RouteGenerators Class. This is really only a holder for all the different types of route generators. This is mostly just
 * to keep things more tidy.
 * 
 */
public class RouteGenerators {

	/**
	 * @param r - Some route, this can be an empty route or a route with things in it already. (Since this is DFS)
	 * @param startingConnection - The connection we are currently looking at possibly adding to our route.
	 * @param maxOrderSize - The maximum cargo size that we can carry.
	 * @return The original route passed in; may have the startingConnection store added on.
	 */
	private static Route basicDFSRouteHelper(Route r, Connection startingConnection, int maxOrderSize) {
		//Basically we don't want to go over our size limit (determined by the warehouse)
		//This acts as our "base case" so that we have multiple DFS routes, not just one.
		r.incrementOperations();
		if(r.getTotalOrderSize() + startingConnection.getConnectedStore().getOrderSize() > maxOrderSize)
			return r;
		
		//Add this store to the route since we can.
		r.addStoreToRoute(startingConnection);
		
		for(int i = 0; i < startingConnection.getConnectedStore().getDifferentStoreConnections().size(); i++) {
			//The store we are looking at that is connected to the current one HASNT been visited yet.
			r.incrementOperations();
			if(!startingConnection.getConnectedStore().getDifferentStoreConnections().get(i).getConnectedStore().hasBeenVisisted())
				r = basicDFSRouteHelper(r, startingConnection.getConnectedStore().getDifferentStoreConnections().get(i), maxOrderSize);
		}
		//Return the route
		return r;
	}
	
	/**
	 * @param startingWarehouse - the warehouse that we are starting from.
	 * @return An array of routes, which contains directions on which stores we should travel to and from.
	 */
	public static ArrayList<Route> generateDFSRoute(Warehouse startingWarehouse) {
		//First of all set all the routes to false.
		
		//The array we return of all the routes we generate.
		ArrayList<Route> allWarehouseRoutes = new ArrayList<Route>();
		
		//Go through every store that our warehouse connects to, and do DFS at it (assuming it hasn't been visited yet).
		for(int i = 0; i < startingWarehouse.getConnectedStores().size(); i++) {
			if(!startingWarehouse.getConnectedStores().get(i).getConnectedStore().hasBeenVisisted()) {
				//Add the route that was generated from the basicDFSRouteHelper function.
				Route r = new Route(startingWarehouse);
				r = basicDFSRouteHelper(r, startingWarehouse.getConnectedStores().get(i), startingWarehouse.getMaximumSize());
				r.addTimeFromLastStore();
				allWarehouseRoutes.add(r);
			}
		}
		
		//Validate that we created a correct route. (All stores visited, all cargo taken).
		if(!validateRoutes(startingWarehouse))
			return null;
		
		//Return the array of routes.
		return allWarehouseRoutes;
	}

	/**
	 * @param ware - The warehouse that we are starting at.
	 * @param startingCon - The starting connection that we are looking at.
	 * @return a route starting from the startingCon to different stores.
	 */
	private static Route basicBFSRouter(Warehouse ware, Connection startingCon) {
		//Create a new Route
		Route r = new Route(ware);
		
		//Create our Queue for BFS of different connections.
		Queue<Connection> allConnections = new LinkedList<Connection>();
		allConnections.add(startingCon);
		
		//Loop through every connection in our queue
		while(!allConnections.isEmpty()) {
			//Get the connection at the front and delete it from the queue.
			Connection c = allConnections.poll();
			//Make sure we haven't gone here before, and it's order size isn't too big.
			r.incrementOperations();
			if(c.getConnectedStore().hasBeenVisisted()) continue;
			
			r.incrementOperations();
			if(r.getTotalOrderSize() + c.getConnectedStore().getOrderSize() > ware.getMaximumSize()) continue;
			
			//Add this store to our route
			r.addStoreToRoute(c);
			allConnections.clear();
			//Add all the connections from this store (to other stores) that haven't already been visisted.
			for(int i = 0; i < c.getConnectedStore().getDifferentStoreConnections().size(); i++) {
				r.incrementOperations();
				if(!c.getConnectedStore().getDifferentStoreConnections().get(i).getConnectedStore().hasBeenVisisted())
					allConnections.add(c.getConnectedStore().getDifferentStoreConnections().get(i));
			}
			
		}
		//Add the time from the last store to our total time.
		r.addTimeFromLastStore();
		//Make sure this is a valid route. If we aren't taking any cargo then we return null.
		return r.getTotalOrderSize() > 0 ? r : null;
	}
	
	/**
	 * @param startingWarehouse - The warehouse we are starting from.
	 * @return An array of routes, which contains directions on which stores we should travel to and from.
	 */
	public static ArrayList<Route> generateBFSRoute(Warehouse startingWarehouse) {
		
		//Create our array of routes.
		ArrayList<Route> routes = new ArrayList<Route>();
		
		//Go through every store and use BFS to create a route and add it to our array of routes.
		for(int i = 0; i < startingWarehouse.getConnectedStores().size(); i++) {
			Route r = basicBFSRouter(startingWarehouse, startingWarehouse.getConnectedStores().get(i));
			
			if(r != null)
				routes.add(r);
		}
		
		//Validate the routes
		if(!validateRoutes(startingWarehouse))
			return null;
		
		//Return our array of routes.
		return routes;
	}
	
	
	
	/**
	 * @param startingWarehouse - The warehouse we are starting from.
	 * @return An array of routes, which contains directions on which stores we should travel to and from.
	 */
	public static ArrayList<Route> generateSmartDFSTime(Main main, Warehouse startingWarehouse) {
		//Create our array of routes.
		ArrayList<Route> routes = new ArrayList<Route>();
		
		//These are temp variables meant to keep track of lowest times and total routes. (Max routes being the size of the warehouse connections + 1)
		int time = Integer.MAX_VALUE;
		int totalRoutes = startingWarehouse.getConnectedStores().size() + 1;
		
		//This determines were we start looking in our starting warehouse. It shifts with each loop in the main while loop.
		int offset = 0;
		
		//We do this because a route may be different if you start a different store, so we check every possible starting location.
		while(offset < startingWarehouse.getConnectedStores().size()) {
			//Reset our routes.
			main.setAllRoutesToFalse();
			//The current time on our route and the current routes.
			int currentTime = 0;
			int currentRoutes = 0;
			//This is an array of our routes that we generated on this pass through of the while loop.
			ArrayList<Route> tempRoute = new ArrayList<Route>();
			
			//Start at our offset and go for startingWarehouse.getConnectedStores().size() times. (since we want to start at 0->size we subtract i)
			for(int i = offset; (i - offset) < startingWarehouse.getConnectedStores().size(); i++) {
				//Make sure the store hasn't been visisted yet. We do i % size because we want it to loop (since i can go above size in most cases)
				if(!startingWarehouse.getConnectedStores().get(i % startingWarehouse.getConnectedStores().size()).getConnectedStore().hasBeenVisisted()) {
					//Generate a route using
					Route r = generateSmartDFSRouteHelper(startingWarehouse.getConnectedStores().get(i % startingWarehouse.getConnectedStores().size()), new Route(startingWarehouse), startingWarehouse.getMaximumSize());
					//Add the time from the last store onto the route and add the route times to our currentTime variable.
					r.addTimeFromLastStore();
					currentTime += r.getTotalTime();
					//Increase our currentRoutes by 1 and add our route to our temproutes array.
					currentRoutes++;
					tempRoute.add(r);
				}
			
			}
			
			
			//Prioritize LESS time rather than LESS routes! Also validate the route
			if((currentTime < time || (currentTime == time && currentRoutes < totalRoutes)) && validateRoutes(startingWarehouse)) {
				routes = tempRoute;
				totalRoutes = currentRoutes;
				time = currentTime;
			}
			
			//Increase our starting offset by 1.
			offset++;
		}	
		
		return routes;
	}
	
	
	/**
	 * This is just a DFS Helper
	 * 
	 * @param startingConnection - our starting connecting
	 * @param r - The route we pass in for DFS
	 * @param maxsize - the maximum size we can carry.
	 * @return the route passed in and any additional stores we add on.
	 */
	public static Route generateSmartDFSRouteHelper(Connection startingConnection, Route r, int maxsize) {
		//Don't do anything, our route is already at maximum size.
		r.incrementOperations();
		if(maxsize == r.getTotalOrderSize()) return r;
		
		//The entire order from the store can fit onto our route, so we add it all.
		r.incrementOperations();
		if(startingConnection.getConnectedStore().getOrderSize() + r.getTotalOrderSize() <= maxsize) {
			r.addStoreToRoute(startingConnection, startingConnection.getConnectedStore().getOrderSize());
		//Only a partial amount of the order can fit on the truck we only fit that much on.
		} else {
			//The truck is FULL now. No need to proceed, we just return our route now.
			int maxload = maxsize - r.getTotalOrderSize();
			r.addStoreToRoute(startingConnection, maxload);
			return r;
		}
		
		//Do DFS on the other routes
		for(int i = 0; i < startingConnection.getConnectedStore().getDifferentStoreConnections().size(); i++) {
			r.incrementOperations();
			if(!startingConnection.getConnectedStore().getDifferentStoreConnections().get(i).getConnectedStore().hasBeenVisisted())
				r = generateSmartDFSRouteHelper(startingConnection.getConnectedStore().getDifferentStoreConnections().get(i), r, maxsize);
		}
		
		//Route our new route.
		return r;
	}
	
	/**
	 * Basically optimize the maximum amount of routes we should take. (In our scope this minimizes the number of drivers
	 * you need to complete this route. This will break up store orders, it will also choose the fastest order.
	 * 
	 * @param startingwareHouse - the starting warehouse
	 * @return An array of routes, which contains directions on which stores we should travel to and from.
	 */
	
	public static ArrayList<Route> generateSmartDFSRoute(Main main, Warehouse startingWarehouse) {
		//Create our array of routes.
				ArrayList<Route> routes = new ArrayList<Route>();
				
				//These are temp variables meant to keep track of lowest times and total routes. (Max routes being the size of the warehouse connections + 1)
				int time = Integer.MAX_VALUE;
				int totalRoutes = startingWarehouse.getConnectedStores().size() + 1;
				
				//This determines were we start looking in our starting warehouse. It shifts with each loop in the main while loop.
				int offset = 0;
				
				//We do this because a route may be different if you start a different store, so we check every possible starting location.
				while(offset < startingWarehouse.getConnectedStores().size()) {
					//Reset our routes.
					main.setAllRoutesToFalse();
					//The current time on our route and the current routes.
					int currentTime = 0;
					int currentRoutes = 0;
					//This is an array of our routes that we generated on this pass through of the while loop.
					ArrayList<Route> tempRoute = new ArrayList<Route>();
					
					//Start at our offset and go for startingWarehouse.getConnectedStores().size() times. (since we want to start at 0->size we subtract i)
					for(int i = offset; (i - offset) < startingWarehouse.getConnectedStores().size(); i++) {
						//Make sure the store hasn't been visisted yet. We do i % size because we want it to loop (since i can go above size in most cases)
						if(!startingWarehouse.getConnectedStores().get(i % startingWarehouse.getConnectedStores().size()).getConnectedStore().hasBeenVisisted()) {
							//Generate a route using
							Route r = generateSmartDFSRouteHelper(startingWarehouse.getConnectedStores().get(i % startingWarehouse.getConnectedStores().size()), new Route(startingWarehouse), startingWarehouse.getMaximumSize());
							//Add the time from the last store onto the route and add the route times to our currentTime variable.
							r.addTimeFromLastStore();
							currentTime += r.getTotalTime();
							//Increase our currentRoutes by 1 and add our route to our temproutes array.
							currentRoutes++;
							tempRoute.add(r);
						}
					
					}
					
					//Prioritize LESS routes rather than LESS time! Also validate the route
					if((currentRoutes < totalRoutes || (currentRoutes == totalRoutes && currentTime < time)) && validateRoutes(startingWarehouse)) {
						routes = tempRoute;
						totalRoutes = currentRoutes;
						time = currentTime;
					}
					
					//Increase our starting offset by 1.
					offset++;
				}	
				
				return routes;
	}
	
	/**
	 * This is the Dijkstra helper function, We use BFS for this we just use the most time efficient connection between stores.
	 * 
	 * @param startingWarehouse - the starting warehouse
	 * @param startingConnection - the starting connection
	 * @return A route
	 */
	public static Route daijkstraRouteHelper(Warehouse startingWarehouse, Connection startingConnection) {
		
		//We setup just like normal BFS with a queue.
		Route r = new Route(startingWarehouse);
		
		Queue<Connection> allConnections = new LinkedList<Connection>();
		allConnections.add(startingConnection);
		allConnections.add(null);
		
		//The current top connection and the current maximum cargo size available.
		Connection currentCon = null;
		int currentMaxCargo = -1;
		
		while(!allConnections.isEmpty()) {
			//We are done with creating our route.
			r.incrementOperations();
			if(r.getTotalOrderSize() >= startingWarehouse.getMaximumSize()) break;
			//Get the next connection in the queue and delete it.
			Connection c = allConnections.poll();
			//If it's null then we hit the end of connections, and we add the currentCon to our route. (this is the best connection)
			r.incrementOperations();
			if(c == null) {
				//Make sure the currentConnection isn't null
				r.incrementOperations();
				if(currentCon != null) {
					//Add the currentConnection to the store along with currentMax cargo that is taken from the store.
					r.addStoreToRoute(currentCon, currentMaxCargo);	
					//Add all the connections from this store to the queue.
					for(int i = 0; i < currentCon.getConnectedStore().getDifferentStoreConnections().size(); i++) {
						r.incrementOperations();
						if(!currentCon.getConnectedStore().getDifferentStoreConnections().get(i).getConnectedStore().hasBeenVisisted())
							allConnections.add(currentCon.getConnectedStore().getDifferentStoreConnections().get(i));
					}
					//Add a null terminator as this means we hit all the connections from one store.
					allConnections.add(null);
				}
				//Reset the currentConnection and currentMax variable.
				currentCon = null;
				currentMaxCargo = -1;
				continue;
			}
			
			
			
			//No need to check the store if it's already been visited.
			r.incrementOperations();
			if(c.getConnectedStore().hasBeenVisisted()) continue;
			
			
			//If the current Connection is null or the time from the currentConnection to the one we are looking at is larger
			r.incrementOperations();
			if(currentCon == null || currentCon.getTimeToStore() > c.getTimeToStore()) { 
				//Set our currentCon to the connection we are looking at.
				currentCon = c;
				//If we can't fit all the cargo onto the route we split it up and set the currentMax to that
				r.incrementOperations();
				if(c.getConnectedStore().getOrderSize() + r.getTotalOrderSize() > startingWarehouse.getMaximumSize()) {
					currentMaxCargo = (startingWarehouse.getMaximumSize() - r.getTotalOrderSize());
				} else {
					currentMaxCargo = c.getConnectedStore().getOrderSize();
				}
			}
			
			
			
		}
		//Add the time from the last store onto the route.
		r.addTimeFromLastStore();
		//Return the route.
		return r;
	}
	
	/**
	 * Generates the array of routes
	 * 
	 * @param warehouse - the starting warehouse
	 * @return an array of routes
	 */
	public static ArrayList<Route> generateDijkstraRoute(Warehouse warehouse) {
		//Reset each store and create an array of routes.
		ArrayList<Route> routes = new ArrayList<Route>();
		
		//Loop through each connection and use the dijkstraRouteHelper function to generate a route.
		for(int i = 0; i < warehouse.getConnectedStores().size(); i++) {
			if(!warehouse.getConnectedStores().get(i).getConnectedStore().hasBeenVisisted())
				routes.add(daijkstraRouteHelper(warehouse, warehouse.getConnectedStores().get(i)));
			//If we can't make a valid route restart our loop.
			if(i + 1 == warehouse.getConnectedStores().size() && !validateRoutes(warehouse)) {
				i = -1;				
			}
		}
		
		//Validate again
		if(!validateRoutes(warehouse)) {
			return null;
		}
		
		return routes;
	}
	
	
	//small helper function to validate that ALL of the stores connected to this warehouse have been visited, and all cargo taken.
		private static boolean validateRoutes(Warehouse startingwareHouse) {
			//Basically just loop through all the stores and make sure they are visisted
			for(int i = 0; i < startingwareHouse.getConnectedStores().size(); i++) {
				if(!startingwareHouse.getConnectedStores().get(i).getConnectedStore().hasBeenVisisted()) {
					System.out.println(startingwareHouse.getConnectedStores().get(i).getConnectedStore().getStoreId() + " " + startingwareHouse.getConnectedStores().get(i).getConnectedStore().getOrderSize());
					return false;
				}
			}
			return true;
		}
	
	
}
