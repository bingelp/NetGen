package com.scires.netgen;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.h2.tools.Server;

/**
 * Created by Justin on 4/8/2014
 * <P>Custom DB class to read/write file names and text to replace</P>
 *
 * @author Justin Robinson
 * @version 0.0.2
 */

@SuppressWarnings("unchecked")
public class DB {
	private String TAG								= "DB ";
	private Connection connection					= null;
	private Server server							= null;
	private static final int QUERY_TYPE_READ		= 0;
	private static final int QUERY_TYPE_WRITE		= 1;
	public static final String C					= ",";
	public static final String D					= ".";
	public static final String TABLE_FILES			= "FILES";
	public static final String TABLE_ITEMS			= "ITEMS";
	public static final String COL_ID				= "ID";
	public static final String COL_INPUT_FILE_NAME	= "INPUT_FILE_NAME";
	public static final String COL_OUTPUT_FILE_NAME	= "OUTPUT_FILE_NAME";
	public static final String COL_FILE_ID			= "FILE_ID";
	public static final String COL_LINE_NUMBER		= "LINE_NUMBER";
	public static final String COL_TARGET			= "TARGET";
	public static final String COL_REPLACEMENT		= "REPLACEMENT";

	public void reset(){
		String query =
				"DROP TABLE IF EXISTS " + TABLE_FILES + ";" +
				"DROP TABLE IF EXISTS " + TABLE_ITEMS + ";" +
				"CREATE TABLE " + TABLE_FILES + "(" +
						COL_ID + " INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
						COL_INPUT_FILE_NAME + " VARCHAR(255) UNIQUE," +
						COL_OUTPUT_FILE_NAME + " VARCHAR(255) UNIQUE);" +
				"CREATE TABLE Items(" +
						COL_ID + " INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
						COL_FILE_ID + " INT NOT NULL," +
					"FOREIGN KEY (" + COL_FILE_ID + ") REFERENCES Files(" + COL_ID + ")," +
						COL_LINE_NUMBER + " INT NOT NULL," +
						COL_TARGET + " VARCHAR(255) NOT NULL," +
						COL_REPLACEMENT + " VARCHAR(255));";
		execute(query, QUERY_TYPE_WRITE);
	}

	private boolean startServer(){
		boolean running = (server != null);
		if(running)
			running = server.isRunning(false);
		if(!running){
			try{
				server = Server.createTcpServer().start();
				running = server.isRunning(false);
			} catch (Exception e){
				new ErrorDialog(TAG + e.getMessage());
			}
		}

		return running;
	}
	private boolean connect(){
		boolean connected = isConnected();
		if(!connected){
			try {
				if(startServer()) {
					connection = DriverManager.getConnection("jdbc:h2:~/"+NetGen.PROGRAM_NAME, "sa", "");
					connected = !connection.isClosed();
				}
			} catch (Exception e) {
				new ErrorDialog(TAG + e.getMessage());
			}
		}
		return connected;
	}
	public void disconnect(){
		if(isConnected()) {
			try {
				connection.close();
				if(server.isRunning(false)){
					server.shutdown();
				}
			} catch (Exception e) {
				System.out.println(TAG + e.getMessage());
			}
		}
	}
	private boolean isConnected(){
		boolean connected = (connection != null);
		if (connected) {
			try {
				connected = !connection.isClosed();
			}catch (Exception e) {
				System.out.println(TAG + e.getMessage());
			}
		}
		return connected;
	}

	public Object execute(String query, int type){
		Statement stmt = null;
		Object output = null;
		if(connect()) {
			try {
				Class.forName("org.h2.Driver");
				stmt = connection.createStatement();
				switch(type){
					case QUERY_TYPE_READ:
						output=ResultSet2ArrayList(stmt.executeQuery(query));
						break;
					case QUERY_TYPE_WRITE:
						output = stmt.executeUpdate(query);
						break;
					default:
						output = stmt.execute(query);
						break;
				}
			} catch (Exception e) {
				new ErrorDialog(TAG + e.getMessage());
			} finally {
				try {
					if (stmt != null)
						stmt.close();
				} catch (Exception e){
					new ErrorDialog(TAG + e.getMessage());
				}
			}
		}

		return output;
	}

	public int saveFile(String fileName){
		String query =
			"INSERT INTO " + TABLE_FILES + " (" + COL_INPUT_FILE_NAME + C + COL_OUTPUT_FILE_NAME +
			") VALUES ('" + fileName + "'" + C + "'" + fileName + "');";
		return(int)execute(query, QUERY_TYPE_WRITE);
	}
	public void saveItem(String fileName, int Line_Number, String Target){
		String query = "SELECT " + COL_ID + " FROM " + TABLE_FILES + " WHERE " + COL_INPUT_FILE_NAME + "='" + fileName + "'";
		ArrayList<Map<String, String>> rows = (ArrayList<Map<String, String>>)execute(query, QUERY_TYPE_READ);
		String File_ID = null;
		if(rows.size() == 1){
			File_ID=rows.get(0).get("ID");
		}
		if(File_ID != null) {
			query =
				"INSERT INTO " + TABLE_ITEMS +
				"(" + COL_FILE_ID + C + COL_LINE_NUMBER + C + COL_TARGET + ")" +
				"VALUES ('" + File_ID + "', '" + String.valueOf(Line_Number) + "', '" + Target +"');";
			int result = (int)execute(query, QUERY_TYPE_WRITE);
			if(result != 1)
				new ErrorDialog("Error saving item");
		}
	}

	public void setOutputFileName(ArrayList<Location> locations, String outputFileName){
		String query =
			"UPDATE " + TABLE_FILES + " SET " + COL_OUTPUT_FILE_NAME + " = '" + outputFileName +
			"' WHERE " + COL_ID + " IN";
		String fileIDRange = "(";
		for (Location l : locations)
			fileIDRange += l.fileIndex+1 + C;
		StringBuilder temp = new StringBuilder(fileIDRange);
		temp.setCharAt(fileIDRange.lastIndexOf(C), ')');
		fileIDRange = temp.toString();
		query += fileIDRange + ";";
		execute(query, QUERY_TYPE_WRITE);
	}
	public void setReplacement(ArrayList<Location> locations, String replacement, String target){
		String query =
				"UPDATE " + TABLE_ITEMS + " SET " + COL_REPLACEMENT + " = '" + replacement +  "' WHERE " + COL_FILE_ID + " IN";
		String fileIDRange = "(";
		String lineNumberRange = fileIDRange;
		for(Location l : locations){
			fileIDRange += l.fileIndex+1 + C;
			lineNumberRange += l.lineNumber + C;
		}
		StringBuilder temp = new StringBuilder(fileIDRange);
		temp.setCharAt(fileIDRange.lastIndexOf(','), ')');
		fileIDRange=temp.toString();
		temp = new StringBuilder(lineNumberRange);
		temp.setCharAt(lineNumberRange.lastIndexOf(','), ')');
		lineNumberRange = temp.toString();
		query += fileIDRange + " AND " + COL_LINE_NUMBER + " IN " + lineNumberRange + "AND " + COL_TARGET + " = '" + target + "';";
		int result = (int)execute(query, QUERY_TYPE_WRITE);
		if(result == 0)
			new ErrorDialog("DB write failed");
	}

	public ArrayList<Map<String, String>> getItemsToGenerate(){
		String query =
			"SELECT " + TABLE_ITEMS + D + COL_ID + C + TABLE_FILES + D + COL_INPUT_FILE_NAME + C +
					TABLE_FILES + D + COL_OUTPUT_FILE_NAME + C + TABLE_ITEMS + D + COL_LINE_NUMBER + C +
					TABLE_ITEMS + D + COL_TARGET + C + TABLE_ITEMS + D + COL_REPLACEMENT +
			" FROM " + TABLE_ITEMS +
			" INNER JOIN " + TABLE_FILES + " ON " + TABLE_FILES + D + COL_ID + " = " + TABLE_ITEMS + D + COL_FILE_ID +
			" WHERE " + TABLE_ITEMS + D + COL_TARGET + " != " + TABLE_ITEMS + D + COL_REPLACEMENT + ";";
		return (ArrayList<Map<String, String>>)execute(query, QUERY_TYPE_READ);
	}
	private ArrayList<Map<String, String>> ResultSet2ArrayList(ResultSet rs){
		ArrayList<Map<String, String>> rows = new ArrayList<>();
		Map<String, String> row;
		try {
			int columnCount = rs.getMetaData().getColumnCount() + 1;
			ResultSetMetaData rsmd = rs.getMetaData();
			String colName, colValue;
			while (rs.next()) {
				row = new HashMap<>();
				for (int i = 1; i < columnCount; i++) {
					colName = rsmd.getColumnName(i);
					colValue = rs.getString(i);
					row.put(colName, colValue);
				}
				rows.add(row);
			}
		}catch (Exception e){
			new ErrorDialog(TAG + e.getMessage());
		}

		return rows;
	}
}
