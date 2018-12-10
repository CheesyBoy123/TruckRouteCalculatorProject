package me.boysnnoco;

import java.awt.Color;	
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.boysnnoco.buildings.Route;
import me.boysnnoco.buildings.RouteGenerators;
import me.boysnnoco.buildings.Store;
import me.boysnnoco.buildings.Warehouse;

@SuppressWarnings("serial")
public class Main extends JPanel {

	//An array of all the stores and all the warehouses
	private static ArrayList<Store> allStores;
	private static ArrayList<Warehouse> allWarehouses;
	
	//See if our files have been initted yet or not.
	private boolean routeOutputInit = false;
	private boolean analyticInit = false;
	
	//An instance of the main class.
	public static Main main;
	
	//Out constructor	
	public Main() {
		allStores = new ArrayList<Store>();
		allWarehouses = new ArrayList<Warehouse>();
	}
	
	//Main method
	public static void main(String[] args) {
		//Create a new instance of ourself
		main = new Main();
		
		
		//Attempt to load our json data from our files
		try {
			main.loadData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Create a new frame
		JFrame frame = new JFrame("Truck Router");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Get our background and add it to the frame, pack the frame as well.
		JLabel background = new JLabel(new ImageIcon("./Data/Images/colorado.png"));
		
		frame.add(background);
		frame.pack();
		frame.add(main);
				
		frame.setComponentZOrder(main, 0);
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		
		System.out.println("Successfully loaded!");
		System.out.println("");
		
		//Start generating
		main.start();
	}
	
	
	//Paint some basics on the screen, squares and lines connecting different stores and warehouses.
	public void paint(Graphics g) {
		
		try {
			//Create our graphics2d 
			Graphics2D g2d;
			g2d = (Graphics2D)g;
			//Get our store and warehouse images
			Image store  = getToolkit().getImage("./Data/Images/store.png");
			Image ware = getToolkit().getImage("./Data/Images/warehouse.png");
			
			//Some offsets determined by the width and heigh of the images, this is used for drawing the
			//connections in the middle of the image.
			int storeXOff = store.getWidth(null) / 2;
			int storeYOff = store.getHeight(null) / 2;
			
			int warehouseXOff = ware.getWidth(null) / 2;
			int warehouseYOff = ware.getHeight(null) / 2;
			
			g2d.setColor(Color.RED);
			for(Store s : allStores) {
				//Draw the store
				g2d.drawImage(store, s.getX(), s.getY(), this);
				//Draw the connections to the other stores
				for(int i = 0; i < s.getDifferentStoreConnections().size(); i++) {
					g2d.drawLine(s.getX() + storeXOff, s.getY() + storeYOff, s.getDifferentStoreConnections().get(i).getConnectedStore().getX() + storeXOff, s.getDifferentStoreConnections().get(i).getConnectedStore().getY() + storeYOff);
				}
				
			}
			
			g2d.setColor(Color.black);
			for(Warehouse w : allWarehouses) {
				//Draw the warehouse
				g2d.drawImage(ware, w.getX(), w.getY(), this);
				//Draw the connections to the stores
				for(int i = 0; i < w.getConnectedStores().size(); i++) {
					g2d.drawLine(w.getX() + warehouseXOff, w.getY() + warehouseYOff, w.getConnectedStores().get(i).getConnectedStore().getX() + storeXOff, w.getConnectedStores().get(i).getConnectedStore().getY() + storeYOff);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param routes - the routes we generated
	 * @param name - the name of the procedure we used to generate the routes
	 * @param warehouseNumber - the warehouse ID number that we are looking at.
	 */
	private void displayRoutes(ArrayList<Route> routes, String name, int warehouseNumber) {
		//Null routes are bad.
		if(routes == null) {
			System.out.println("Route is null");
			return;
		}
		
		//Load in our .txt output file for the warehouse
		File file = new File("./Data/WarehouseLog_" + warehouseNumber + ".txt");
		FileWriter fr = null;
		try {
				
			//If the file already existed and we haven't already been inited we delete the file.
			//This is so that we write to a fresh new file
			if(file.exists() && !routeOutputInit) {
				routeOutputInit = true;
				file.delete();
			}
				
			//The file doesn't exist so we create a new one.
			if(!file.exists()) {
				file.createNewFile();
			}
				
			
			//Create a file writer, and set it to append mode.
			fr = new FileWriter(file, true);
		
			fr.write("---" + name + "---" + "\n");
			System.out.println("---" + name + "---");
			//Variables to keep track of the total time,cargo, and comparisons.
			int totalTime = 0;
			int totalCargo = 0;
			int totalComparisons = 0;
			
			//Go through each route in the array
			for(int i = 0; i < routes.size(); i++) {
				Route r = routes.get(i);
				//Write the order size and time related to the route.
				fr.write("Route " + i + " total order size " + r.getTotalOrderSize() + "\n");
				fr.write("Total time taken on route: " + r.getTotalTime() + "\n");
				System.out.println("Route " + i + " total order size: " + r.getTotalOrderSize());
				System.out.println("Total time taken on route: " + r.getTotalTime());
				totalTime += r.getTotalTime();
				totalCargo += r.getTotalOrderSize();
				totalComparisons += r.getTotalComparisons();
				Store s = null;
				StringBuilder sb = new StringBuilder();
				//Go through each route and get the stores that we visit in each. Print and write that out.
				while((s = r.getCurrentStore()) != null) {
					r.goToNextStore();
					sb.append("Store " + s.getStoreId() + "-->");
			
				}
				System.out.print(sb.toString());
				fr.write(sb.toString() + "\n");
				fr.write("\n");
				System.out.println("");
				System.out.println("");
				
			}
			//Write out the total time on the route, cargo, and total comparisons
			fr.write("Total time taken: " + totalTime + " minutes, total cargo taken: " + totalCargo + ", total routes taken: "  + routes.size() +  ", total comparisons: " + totalComparisons + "\n");
			fr.write("\n" + "\n");
			System.out.println("Total time taken: " + totalTime + " minutes, total cargo taken: " + totalCargo + ", total routes taken: "  + routes.size() +  ", total comparisons: " + totalComparisons);
			System.out.println("");
			System.out.println("");
		
		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				//Close and flush the FileWriter
				fr.flush();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param route1 - The first route we are comparing
	 * @param route2 - The second route we are comparing
	 * @param number - The warehouse number
	 * @param route1Name - The name of the first route
	 * @param route2Name - The name of the second route
	 */
	private void displayAnalytics(ArrayList<Route> route1, ArrayList<Route> route2, int number, String route1Name, String route2Name) {
		//Make sure neither are null.
		if(route1 == null || route2 == null) {
			System.out.println("Route(s) are null");
			return;
		}
		
		//Open up our AnalyticsLog file
		File file = new File("./Data/AnalyticsLog_" + number + ".txt");
		FileWriter fr = null;
		try {

			//If the file existed but it hasn't been inited we delete it (so we start fresh)
			if(file.exists() && !analyticInit) {
				analyticInit = true;
				file.delete();
			}
			
			//The file doesn't exist so we generate a new one.
			if(!file.exists()) {
				file.createNewFile();
			}
				
			
			//Create our file writer in append mode true
			fr = new FileWriter(file, true);
			
			fr.write("---" + route1Name + " VS. " + route2Name + "---" + "\n");
			
			//The total route times for each route
			float routeTime1 = 0;
			float routeTime2 = 0;
			
			//The total comparisons for each route
			float route1Comparisons = 0;
			float route2Comparisons = 0;
			
			//Go through each route and get the time and comparisons and add it to the total for each route.
			for(int i = 0; i < route1.size(); i++) {
				routeTime1 += route1.get(i).getTotalTime();
				route1Comparisons += route1.get(i).getTotalComparisons();
			}
			
			for(int i = 0; i < route2.size(); i++) {
				routeTime2 += route2.get(i).getTotalTime();
				route2Comparisons += route2.get(i).getTotalComparisons();
			}
			
			//Get the ratio of time, route size, and comparisons
			float timeRatio = (routeTime1 / routeTime2);
			float routeSizeRatio = ((float)route1.size() / (float)route2.size());
			float comparisonRatio = route1Comparisons / route2Comparisons;
			
			//This goes for the next 3:
			//	if the ratio > 1 then route1 has a larger numerator (increase when compared to route2 IE, route1 is x% increase)
			//	if the ratio < 1 then route2 has a larger denominator (decrease when compared to route2 IE, route1 is x% decrease)
			
			//We write out that information
			if(timeRatio > 1) {
				timeRatio = timeRatio - 1;
				fr.write(route1Name + " has a " + (timeRatio * 100.0) + "% increase in travel time, compared to " + route2Name);
			} else {
				timeRatio = 1 - timeRatio;
				fr.write(route1Name + " has a " + (timeRatio * 100.0) + "% decrease in travel time, compared to " + route2Name);
			}
			fr.write("\n");
			if(routeSizeRatio > 1) {
				routeSizeRatio = routeSizeRatio - 1;
				fr.write(route1Name + " has a " + (routeSizeRatio * 100.0) + "% increase in total routes, compared to " + route2Name);
			} else {
				routeSizeRatio = 1 - routeSizeRatio;
				fr.write(route1Name + " has a " + (routeSizeRatio * 100.0) + "% decrease in total routes, compared to " + route2Name);
			}
			fr.write("\n");
			if(comparisonRatio > 1) {
				comparisonRatio = comparisonRatio - 1;
				fr.write(route1Name + " has a " + (comparisonRatio * 100.0) + "% increase in total comparison, compared to " + route2Name);
			} else {
				comparisonRatio = 1 - comparisonRatio;
				fr.write(route1Name + " has a " + (comparisonRatio * 100.0) + "% decrease in total comparison, compared to " + route2Name);
			}
			fr.write("\n");
			fr.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				//Flush the file writer and close it.
				fr.flush();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//TODO::: The BASIC DFS and BFS Routes REQUIRE all stores to have less cargo than the maximum a truck can handle, or it will NOT work correctly. This
	//may or may not be a bad thing.
	
	//This is the start function, which goes through all the warehouses loaded and uses each route generator to create different routes.
	public void start(){
		for(int i = 0; i < allWarehouses.size(); i++) {
			System.out.println("Warehouse " + i + " routing: ");
			//reset all the stores
			setAllRoutesToFalse();
			
			//Generate and display the basicDFSRoute
			ArrayList<Route> basicDFSRoute = RouteGenerators.generateDFSRoute(allWarehouses.get(i));
			displayRoutes(basicDFSRoute, "Basic DFS Route", i);

			//reset all the stores
			setAllRoutesToFalse();
			
			//Generate and display the basicBFSRoute
			ArrayList<Route> basicBFSRoute = RouteGenerators.generateBFSRoute(allWarehouses.get(i));
			displayRoutes(basicBFSRoute, "Basic BFS Route",i);
			
			//reset all the stores
			setAllRoutesToFalse();
			
			//Generate and display the smartDFSRoute (Minimum Routes)
			ArrayList<Route> smartDFSRoute = RouteGenerators.generateSmartDFSRoute(this, allWarehouses.get(i));
			displayRoutes(smartDFSRoute, "Smart DFS Route (Minimum Routes)",i);
			
			//reset all the stores
			setAllRoutesToFalse();
			
			//Generate and display the smartDFSRouteTime (Minimum Time)
			ArrayList<Route> smartDFSRouteTime = RouteGenerators.generateSmartDFSTime(this, allWarehouses.get(i));
			displayRoutes(smartDFSRouteTime, "Smart DFS Route (Minimum Time)",i);
			
			//reset all the stores
			setAllRoutesToFalse();
			//Generate and display the dijkstraRoute
			ArrayList<Route> dikjstraRoute = RouteGenerators.generateDijkstraRoute(allWarehouses.get(i));
			displayRoutes(dikjstraRoute, "Dijkstra Route",i);
			
			//Display the analytics comparing some routes that I thought would be interesting.
			displayAnalytics(basicDFSRoute, basicBFSRoute,  i, "Basic DFS", "Basic BFS");
			displayAnalytics(basicDFSRoute, smartDFSRouteTime, i, "Basic DFS", "Smart DFS (Min Routes)");
			displayAnalytics(basicDFSRoute, smartDFSRouteTime, i, "Basic DFS", "Smart DFS (Min Time)");
			displayAnalytics(basicBFSRoute, dikjstraRoute, i, "Basic BFS", "Dijkstra");
			
			//Reset our init booleans (since we could go on to a different file)
			routeOutputInit = false;
			analyticInit = false;
			
		}
		
	}
	
	//Load the store and warehouse data
	private boolean loadData() throws IOException {
		//The file path to our stores
		File storeFileDir = new File("./Data/Store");
		//The file path to our warehoues
		File warehouseFileDir = new File("./Data/Warehouse");
		
		//An array of all the .json files in that path
		File[] storeListings = storeFileDir.listFiles();
		File[] warehouseListings = warehouseFileDir.listFiles();
		
		//Something has gone wrong if this happens.
		if(storeListings == null) {
			System.out.println("No stores found in " + storeFileDir.getAbsolutePath() + " exiting...");
			return false;
		}
		
		//Something has gone wrong if this happens.
		if(warehouseListings == null) {
			System.out.println("No warehouses found in " + warehouseFileDir.getAbsolutePath() + " exiting...");
			return false;
		}
		
		//Create a instance of our gson builder, while exluding expose annotations.
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		
		//Loop through each file, and create a store object using GSON (Thanks google).
		for(File storeData : storeListings) {
			
			//Read in our data and save it as a string for right now
			BufferedReader reader = new BufferedReader(new FileReader(storeData));
			StringBuilder fileBuilder = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				fileBuilder.append(line);
			}
			reader.close();
			
			

			
			//parse our file using GSON
			allStores.add(gson.fromJson(fileBuilder.toString(), Store.class));
		}
		
		//Init all our stores
		for(int i = 0; i < allStores.size(); i++) {
			allStores.get(i).initStore();
		}
		
		//Loop through each file in our warehouse path
		for(File warehouseData : warehouseListings) {
			//Read in our data and save it as a string
			BufferedReader reader = new BufferedReader(new FileReader(warehouseData));
			StringBuilder fileBuilder = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				fileBuilder.append(line);
			}
			reader.close();
			
			
			//parse our file using GSON
			Warehouse warehouse = gson.fromJson(fileBuilder.toString(), Warehouse.class);
			//init the warehouse
			warehouse.initWarehouse();
			allWarehouses.add(warehouse);
		}
		
		//We inited succesfully.
		return true;
	}
	
	//Get a store with a specific store ID
	public Store getStoreAtIndex(int storeId) {
		for(int i = 0; i < allStores.size(); i++) {
			if(allStores.get(i).getStoreId() == storeId) {
				return allStores.get(i);
			}
		}
		return null;
	}
	
	//Reset all our routes
	public void setAllRoutesToFalse() {
		for(int i = 0; i < allStores.size(); i++) {
			allStores.get(i).reset();
		}
	}
	
}
