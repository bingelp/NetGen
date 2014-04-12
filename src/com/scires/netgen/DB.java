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
	private String TAG 							= "DB ";
	private Connection connection 				= null;
	private Server server						= null;
	private static final int QUERY_TYPE_READ	= 0;
	private static final int QUERY_TYPE_WRITE	= 1;

	public static final String c				= ",";
	public static final String d				= ".";
	public static final String tableFiles	   = "FILES";
	public static final String tableItems	   = "ITEMS";
	public static final String colID			= "ID";
	public static final String colInputFileName = "INPUT_FILE_NAME";
	public static final String colOutputFileName= "OUTPUT_FILE_NAME";
	public static final String colFileID		= "FILE_ID";
	public static final String colLineNumber	= "LINE_NUMBER";
	public static final String colTarget		= "TARGET";
	public static final String colReplacement   = "REPLACEMENT";

	public void reset(){
		String query =
				"DROP TABLE IF EXISTS " + tableFiles + ";" +
				"DROP TABLE IF EXISTS " + tableItems + ";" +
				"CREATE TABLE " + tableFiles + "(" +
					colID + " INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
					colInputFileName + " VARCHAR(255) UNIQUE," +
					colOutputFileName + " VARCHAR(255) UNIQUE);" +
				"CREATE TABLE Items(" +
					colID + " INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
					colFileID + " INT NOT NULL," +
					"FOREIGN KEY (" + colFileID + ") REFERENCES Files(" + colID + ")," +
					colLineNumber + " INT NOT NULL," +
					colTarget + " VARCHAR(255) NOT NULL," +
					colReplacement + " VARCHAR(255));";
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
			"INSERT INTO " + tableFiles + " (" + colInputFileName + c + colOutputFileName +
			") VALUES ('" + fileName + "'" + c + "'" + fileName + "');";
		return(int)execute(query, QUERY_TYPE_WRITE);
	}
	public void saveItem(String fileName, int Line_Number, String Target){
		String query = "SELECT " + colID + " FROM " + tableFiles + " WHERE " + colInputFileName + "='" + fileName + "'";
		ArrayList<Map<String, String>> rows = (ArrayList<Map<String, String>>)execute(query, QUERY_TYPE_READ);
		String File_ID = null;
		if(rows.size() == 1){
			File_ID=rows.get(0).get("ID");
		}
		if(File_ID != null) {
			query =
				"INSERT INTO " + tableItems +
				"(" + colFileID + c + colLineNumber + c + colTarget + ")" +
				"VALUES ('" + File_ID + "', '" + String.valueOf(Line_Number) + "', '" + Target +"');";
			int result = (int)execute(query, QUERY_TYPE_WRITE);
			if(result != 1)
				new ErrorDialog("Error saving item");
		}
	}

	public void setOutputFileName(ArrayList<Location> locations, String outputFileName){
		String query =
			"UPDATE " + tableFiles + " SET " + colOutputFileName + " = '" + outputFileName +
			"' WHERE " + colID + " IN";
		String fileIDRange = "(";
		for (Location l : locations)
			fileIDRange += l.fileIndex+1 + c;
		StringBuilder temp = new StringBuilder(fileIDRange);
		temp.setCharAt(fileIDRange.lastIndexOf(c), ')');
		fileIDRange = temp.toString();
		query += fileIDRange + ";";
		execute(query, QUERY_TYPE_WRITE);
	}
	public void setReplacement(ArrayList<Location> locations, String replacement, String target){
		String query =
				"UPDATE " + tableItems + " SET " + colReplacement + " = '" + replacement +  "' WHERE " + colFileID + " IN";
		String fileIDRange = "(";
		String lineNumberRange = fileIDRange;
		for(Location l : locations){
			fileIDRange += l.fileIndex+1 + c;
			lineNumberRange += l.lineNumber + c;
		}
		StringBuilder temp = new StringBuilder(fileIDRange);
		temp.setCharAt(fileIDRange.lastIndexOf(','), ')');
		fileIDRange=temp.toString();
		temp = new StringBuilder(lineNumberRange);
		temp.setCharAt(lineNumberRange.lastIndexOf(','), ')');
		lineNumberRange = temp.toString();
		query += fileIDRange + " AND " + colLineNumber + " IN " + lineNumberRange + "AND " + colTarget + " = '" + target + "';";
		int result = (int)execute(query, QUERY_TYPE_WRITE);
		if(result == 0)
			new ErrorDialog("DB write failed");
	}

	public ArrayList<Map<String, String>> getItemsToGenerate(){
		String query =
			"SELECT " + tableItems + d + colID + c +tableFiles + d + colInputFileName + c +
					tableFiles + d + colOutputFileName + c + tableItems + d + colLineNumber + c +
					tableItems + d + colTarget + c + tableItems + d + colReplacement +
			" FROM " + tableItems +
			" INNER JOIN " + tableFiles + " ON " + tableFiles + d + colID + " = " + tableItems + d + colFileID +
			" WHERE " + tableItems + d + colTarget + " != " + tableItems + d + colReplacement + ";";
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
