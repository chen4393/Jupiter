package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import external.TicketMasterAPI;

public class MySQLConnection implements DBConnection {
	private Connection conn;
	
	public MySQLConnection() {
        try {
            // Register driver
            Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
            // Connect to MySQL
            conn = DriverManager.getConnection(MySQLDBUtil.URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public void close() {
		if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if (conn == null) {
            return;
        }
        String sql = "INSERT IGNORE INTO history(user_id, item_id) VALUES(?, ?)";
        try {
            for (String itemId : itemIds) {
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, userId);
                statement.setString(2, itemId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (conn == null) {
            return;
        }
        String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
        try {
            for (String itemId : itemIds) {
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, userId);
                statement.setString(2, itemId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		/*
         * Similar code with SearchItem.java: call TickMasterAPI.search
         * */
        TicketMasterAPI tmAPI = new TicketMasterAPI();
        /*
         * 1. Send HTTP GET request to get all JSONObject events
         * and purify the data into Item objects
         * */
        List<Item> items = tmAPI.search(lat, lon, term);
        // 2. Save item data into db
        for (Item item : items) {
            saveItem(item);
        }
        return items;
	}

	@Override
	public void saveItem(Item item) {
		if (conn == null) {
            return;
        }
        try {
            // 1. Insert data from item object into items table
            // IGNORE: handle duplicate records
            String sql = "INSERT IGNORE INTO items VALUES(?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, item.getItemId());
            statement.setString(2, item.getName());
            statement.setString(3, item.getAddress());
            statement.setString(4, item.getImageUrl());
            statement.setString(5, item.getUrl());
            statement.setDouble(6, item.getDistance());
            statement.executeUpdate();

            // 2. Update categories table for each category
            sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
            for (String category : item.getCategories()) {
                statement = conn.prepareStatement(sql);
                statement.setString(1, item.getItemId());
                statement.setString(2, category);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	@Override
	public String getFullName(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}

}
