package me.boysnnoco;

import java.awt.Color;	
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
			g2d.setColor(Color.RED);
			for(Store s : allStores) {
				g2d.drawImage(store, s.getX(), s.getY(), this);				
				for(int i = 0; i < s.getDifferentStoreConnections().size(); i++) {
					g2d.drawLine(s.getX(), s.getY(), s.getDifferentStoreConnections().get(i).getConnectedStore().getX(), s.getDifferentStoreConnections().get(i).getConnectedStore().getY());
				}
				
			}
			
			g2d.setColor(Color.GREEN);
			for(Warehouse w : allWarehouses) {
				g2d.drawImage(ware, w.getX(), w.getY(), this);
				for(int i = 0; i < w.getConnectedStores().size(); i++) {
					g2d.drawLine(w.getX(), w.getY(), w.getConnectedStores().get(i).getConnectedStore().getX(), w.getConnectedStores().get(i).getConnectedStore().getY());
				}
			}

			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void displayRoutes(ArrayList<Route> routes) {
		if(routes == null) {
			System.out.println("Route is null");
			return;
		}
		int totalTime = 0;
		int totalCargo = 0;
		for(int i = 0; i < routes.size(); i++) {
			Route r = routes.get(i);
			System.out.println("Route " + i + " total order size: " + r.getTotalOrderSize());
			System.out.println("Total time taken on route: " + r.getTotalTime());
			totalTime += r.getTotalTime();
			totalCargo += r.getTotalOrderSize();
			Store s = null;
			while((s = r.getCurrentStore()) != null) {
				r.goToNextStore();
				System.out.print("Store " + s.getStoreId() + "-->");
			
			}
			System.out.println("");
			System.out.println("");
		}
		System.out.println("Total time taken: " + totalTime + " total cargo taken: " + totalCargo);
		System.out.println("");
		System.out.println("");
	}
	
	public void start(){
		for(int i = 0; i < allWarehouses.size(); i++) {
			System.out.println("Warehouse " + i + " routing: ");
		
//			System.out.println("---Basic DFS Route---");
//			ArrayList<Route> basicDFSRoute = Route.generateDFSRoute(allWarehouses.get(i));
//			displayRoutes(basicDFSRoute);
//			System.out.println("");
//			System.out.println("");
//		
//			System.out.println("---Basic BFS Route---");
//			ArrayList<Route> basicBFSRoute = Route.generateBFSRoute(allWarehouses.get(i));
//			displayRoutes(basicBFSRoute);
//			System.out.println("");
//			System.out.println("");
//			
//			System.out.println("---Smart DFS Route (Minimum Routes)---");
//			ArrayList<Route> smartDFSRoute = Route.generateSmartDFSRoute(allWarehouses.get(i));
//			displayRoutes(smartDFSRoute);
//			System.out.println("");
//			System.out.println("");
//			
//			System.out.println("---Smart DFS Route (Minimum Time)---");
//			ArrayList<Route> smartDFSRouteTime = Route.generateSmartDFSTime(allWarehouses.get(i));
//			displayRoutes(smartDFSRouteTime);
//			System.out.println("");
//			System.out.println("");
			
			System.out.println("---Daikjstra Route---");
			ArrayList<Route> daikjstraRoute = Route.generateDaijkstraRoute(allWarehouses.get(i));
			displayRoutes(daikjstraRoute);
			System.out.println("");
			System.out.println("");
			
			
			
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
