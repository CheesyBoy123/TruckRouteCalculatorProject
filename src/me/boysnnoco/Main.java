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
import me.boysnnoco.buildings.Store;
import me.boysnnoco.buildings.Warehouse;

@SuppressWarnings("serial")
public class Main extends JPanel {

	private static ArrayList<Store> allStores;
	private static ArrayList<Warehouse> allWarehouses;
	
	private boolean init = false;
	private boolean analyticInit = false;
	
	
	public static Main main;
	
	public Main() {
		allStores = new ArrayList<Store>();
		allWarehouses = new ArrayList<Warehouse>();
	}
	
	public static void main(String[] args) {
		main = new Main();
		
		
		try {
			main.loadData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JFrame frame = new JFrame("Truck Router");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel background = new JLabel(new ImageIcon("./Data/Images/colorado.png"));
		
		frame.add(background);
		frame.pack();
		frame.add(main);
				
		frame.setComponentZOrder(main, 0);
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		
		
		
		System.out.println("Successfully loaded!");
		System.out.println("");
		main.start();
	}
	
	
	public void paint(Graphics g) {
		
		try {
			Graphics2D g2d;
			g2d = (Graphics2D)g;
			Image store  = getToolkit().getImage("./Data/Images/store.png");
			Image ware = getToolkit().getImage("./Data/Images/warehouse.png");
			
			int storeXOff = store.getWidth(null) / 2;
			int storeYOff = store.getHeight(null) / 2;
			
			int warehouseXOff = ware.getWidth(null) / 2;
			int warehouseYOff = ware.getHeight(null) / 2;
			
			g2d.setColor(Color.RED);
			for(Store s : allStores) {
				g2d.drawImage(store, s.getX(), s.getY(), this);				
				for(int i = 0; i < s.getDifferentStoreConnections().size(); i++) {
					g2d.drawLine(s.getX() + storeXOff, s.getY() + storeYOff, s.getDifferentStoreConnections().get(i).getConnectedStore().getX() + storeXOff, s.getDifferentStoreConnections().get(i).getConnectedStore().getY() + storeYOff);
				}
				
			}
			
			g2d.setColor(Color.black);
			for(Warehouse w : allWarehouses) {
				g2d.drawImage(ware, w.getX(), w.getY(), this);
				for(int i = 0; i < w.getConnectedStores().size(); i++) {
					g2d.drawLine(w.getX() + warehouseXOff, w.getY() + warehouseYOff, w.getConnectedStores().get(i).getConnectedStore().getX() + storeXOff, w.getConnectedStores().get(i).getConnectedStore().getY() + storeYOff);
				}
			}

			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void displayRoutes(ArrayList<Route> routes, String name, int warehouseNumber) {
		if(routes == null) {
			System.out.println("Route is null");
			return;
		}
		
		File file = new File("./Data/WarehouseLog_" + warehouseNumber + ".txt");
		FileWriter fr = null;
		try {

				if(file.exists() && !init) {
					init = true;
					file.delete();
				}
				
				if(!file.exists()) {
					file.createNewFile();
				}
				
				
				fr = new FileWriter(file, true);
		
			fr.write("---" + name + "---" + "\n");
			System.out.println("---" + name + "---");
			int totalTime = 0;
			int totalCargo = 0;
			
			for(int i = 0; i < routes.size(); i++) {
				Route r = routes.get(i);
				fr.write("Route " + i + " total order size " + r.getTotalOrderSize() + "\n");
				fr.write("Total time taken on route: " + r.getTotalTime() + "\n");
				System.out.println("Route " + i + " total order size: " + r.getTotalOrderSize());
				System.out.println("Total time taken on route: " + r.getTotalTime());
				totalTime += r.getTotalTime();
				totalCargo += r.getTotalOrderSize();
				Store s = null;
				StringBuilder sb = new StringBuilder();
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
			fr.write("Total time taken: " + totalTime + " minutes, total cargo taken: " + totalCargo + ", total routes taken: "  + routes.size() + "\n");
			fr.write("\n" + "\n");
			System.out.println("Total time taken: " + totalTime + " minutes, total cargo taken: " + totalCargo + ", total routes taken: " + routes.size());
			System.out.println("");
			System.out.println("");
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fr.flush();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void displayAnalytics(ArrayList<Route> route1, ArrayList<Route> route2, int number, String route1Name, String route2Name) {
		if(route1 == null || route2 == null) {
			System.out.println("Route(s) are null");
			return;
		}
		
		File file = new File("./Data/AnalyticsLog_" + number + ".txt");
		FileWriter fr = null;
		try {

				if(file.exists() && !analyticInit) {
					analyticInit = true;
					file.delete();
				}
				
				if(!file.exists()) {
					file.createNewFile();
				}
				
				
				fr = new FileWriter(file, true);
		
			fr.write("---" + route1Name + " VS. " + route2Name + "---" + "\n");
			
			float routeTime1 = 0;
			float routeTime2 = 0;
			
			for(int i = 0; i < route1.size(); i++) {
				routeTime1 += route1.get(i).getTotalTime();
			}
			
			for(int i = 0; i < route2.size(); i++) {
				routeTime2 += route2.get(i).getTotalTime();
			}
			
			float timeRatio = (routeTime1 / routeTime2);
			float routeSizeRatio = ((float)route1.size() / (float)route2.size());
			
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
			fr.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fr.flush();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//TODO::: The BASIC DFS and BFS Routes REQUIRE all stores to have less cargo than the maximum a truck can handle, or it will NOT work correctly. This
	//may or may not be a bad thing.
	
	public void start(){
		for(int i = 0; i < allWarehouses.size(); i++) {
			System.out.println("Warehouse " + i + " routing: ");
		
			ArrayList<Route> basicDFSRoute = Route.generateDFSRoute(allWarehouses.get(i));
			displayRoutes(basicDFSRoute, "Basic DFS Route", i);

		
			ArrayList<Route> basicBFSRoute = Route.generateBFSRoute(allWarehouses.get(i));
			displayRoutes(basicBFSRoute, "Basic BFS Route",i);
			
			ArrayList<Route> smartDFSRoute = Route.generateSmartDFSRoute(allWarehouses.get(i));
			displayRoutes(smartDFSRoute, "Smart DFS Route (Minimum Routes)",i);
			
			ArrayList<Route> smartDFSRouteTime = Route.generateSmartDFSTime(allWarehouses.get(i));
			displayRoutes(smartDFSRouteTime, "Smart DFS Route (Minimum Time)",i);
			
			ArrayList<Route> daikjstraRoute = Route.generateDaijkstraRoute(allWarehouses.get(i));
			displayRoutes(daikjstraRoute, "Dijkstra Route",i);
			
			displayAnalytics(basicDFSRoute, basicBFSRoute,  i, "Basic DFS", "Basic BFS");
			displayAnalytics(basicDFSRoute, smartDFSRouteTime, i, "Basic DFS", "Smart DFS (Min Routes)");
			displayAnalytics(basicDFSRoute, smartDFSRouteTime, i, "Basic DFS", "Smart DFS (Min Time)");
			displayAnalytics(basicBFSRoute, daikjstraRoute, i, "Basic BFS", "Dijkstra");
			
			init = false;
			analyticInit = false;
			
		}
		
	}
	
	private boolean loadData() throws IOException {
		File storeFileDir = new File("./Data/Store");
		File warehouseFileDir = new File("./Data/Warehouse");
		File[] storeListings = storeFileDir.listFiles();
		File[] warehouseListings = warehouseFileDir.listFiles();
		//Something has gone wrong if this happens.
		if(storeListings == null) {
			System.out.println("No stores found in " + storeFileDir.getAbsolutePath() + " exiting...");
			return false;
		}
		
		if(warehouseListings == null) {
			System.out.println("No warehouses found in " + warehouseFileDir.getAbsolutePath() + " exiting...");
			return false;
		}
		
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		//Loop through each file, and create a store object using GSON (Thanks google).
		for(File storeData : storeListings) {
			BufferedReader reader = new BufferedReader(new FileReader(storeData));
			StringBuilder fileBuilder = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				fileBuilder.append(line);
			}
			reader.close();
			
			//Read in our data using gson here.

			
			//parse our file using GSON
			allStores.add(gson.fromJson(fileBuilder.toString(), Store.class));
		}
		
		for(int i = 0; i < allStores.size(); i++) {
			allStores.get(i).initStore();
		}
		
		for(File warehouseData : warehouseListings) {
			BufferedReader reader = new BufferedReader(new FileReader(warehouseData));
			StringBuilder fileBuilder = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				fileBuilder.append(line);
			}
			reader.close();
			
			//Read in our data using gson here.

			
			//parse our file using GSON
			Warehouse warehouse = gson.fromJson(fileBuilder.toString(), Warehouse.class);
			warehouse.initWarehouse();
			allWarehouses.add(warehouse);
		}
	
		return true;
	}
	
	public Store getStoreAtIndex(int storeId) {
		for(int i = 0; i < allStores.size(); i++) {
			if(allStores.get(i).getStoreId() == storeId) {
				return allStores.get(i);
			}
		}
		return null;
	}
	
}
