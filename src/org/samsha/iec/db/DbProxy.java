package org.samsha.iec.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.samsha.iec.ui.LiveDataConsole;
import org.samsha.iec.util.ConfigProperties;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
/**
 * This class main worker class which runs the stored procedures 
 * @author Samiksha
 * @author Shashwat
 */
class DbProxy {
	private static String databaseHost;
	private static int databasePort;
	private static String databaseUser;
	private static String databasePassword;
	private static String databaseName;
	private static String databaseSource;
	private static Connection dbConnection;
	
	static {
		try {
			ConfigProperties config = ConfigProperties.getInstance();
			databaseSource = config.getProperty("DataSource");
			databaseHost = config.getProperty("DBHost");
			databasePort = config.getIntProperty("DBPort");
			databaseUser = config.getProperty("DBUser");
			databasePassword = config.getProperty("DBPassword");
			databaseName = config.getProperty("DBName");
			connect();
		} catch(Exception ex) {
		}
	}
	
	protected static void connect() {
		try {
			Class.forName(databaseSource);
			SQLServerDataSource dataSource = new SQLServerDataSource();
			dataSource.setServerName(databaseHost);
			dataSource.setPortNumber(databasePort);
			dataSource.setDatabaseName(databaseName);
			dataSource.setUser(databaseUser);
			dataSource.setPassword(databasePassword);
			dbConnection = dataSource.getConnection();
		} catch (Exception ex) {
			LiveDataConsole.getInstance().setLiveData(ex.getMessage());
		}
	}
	
	protected static void disconnect() {
		try {
			if (dbConnection != null && !dbConnection.isClosed())
				dbConnection.close();
		}
		catch(Exception ex) {
		}
	}
	
	protected static boolean isConnected() throws SQLException {
		try {
			if (dbConnection == null) {
				connect();
			}
			dbConnection.setAutoCommit(false);
			return !dbConnection.isClosed();
		} catch (Exception ex) {
			return false;
		}
	}
	
	public static ArrayList<Object> executeProcedure(String procedureName, ArrayList<Object> parameters,
			ArrayList<Object> outputParameterTypes) throws Exception {
		try {
			dbConnection.setAutoCommit(false);
			ArrayList<Object> outputParams = new ArrayList<Object>();
			int parameterCount = parameters.size();
			if (outputParameterTypes != null) {
				parameterCount += outputParameterTypes.size();
			}
			CallableStatement statement = dbConnection.prepareCall(prepareProcedureParam(procedureName, parameterCount),
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			int position = 1;
			for (int i = 0; i < parameters.size(); i++, position++) {
				if (parameters.get(i).equals("")) {
					statement.setObject(i+1, null);
				} else {
					statement.setObject(i+1, parameters.get(i));
				}
			}
			if (outputParameterTypes != null) {
				for (int i = 0; i < outputParameterTypes.size(); i++, position++) {
					statement.registerOutParameter(position, Integer.parseInt(outputParameterTypes.get(i).toString()));
				}
			}
			
			ResultSet result = null;
			try {
				result = statement.executeQuery();
			} catch (SQLServerException ex) {
				dbConnection.rollback();
				statement.execute();
			}
			
			if (result != null) {
				try {
					if (!result.isClosed()) {
						ResultSetMetaData metaData = result.getMetaData();
						int columnCount = metaData.getColumnCount(); 
						if ( columnCount > 0) {
							//result.beforeFirst();
							ArrayList<Object> rowList = new ArrayList<Object>();
							boolean rowExist = result.next();
							while(rowExist) {
								for(int index = 1; index <= columnCount; index++) {
									rowList.add(result.getObject(index));
								}
								rowExist = result.next();
							}
							outputParams.add(rowList);
						}
					}
				} catch (Exception ex) {
					String message = "Error on while retrieve procedure results : "  + ex.getMessage();
					LiveDataConsole.getInstance().setLiveData(message);
				}
				if (outputParameterTypes != null) {
					int initial = parameters.size() + 1;
					int total = parameters.size() + outputParameterTypes.size();
					Collection<Object> tempParams = new ArrayList<Object>();
					for(int i = initial; i <= total; i++)
						tempParams.add(statement.getObject(i));
					outputParams.addAll(0, tempParams);
				}
			} else {
				if (outputParameterTypes != null) {
					int initial = parameters.size() + 1;
					int total = parameters.size() + outputParameterTypes.size();
					for (int i = initial; i <= total; i++)
						outputParams.add(statement.getObject(i));
				}
			}
			
			dbConnection.commit();
			return outputParams;
		} catch (Exception ex) {
			dbConnection.rollback();
			throw new Exception("Unable to execute procedure '" + procedureName + "' : " + ex.getMessage());
		}		
	}
	
	private static String prepareProcedureParam(String procedureName, int length) {
		StringBuilder parameter = new StringBuilder();
		for(int i = 0; i < length; i++) {
			parameter.append("?,");
		}
		if (parameter.length() > 0)
			parameter.deleteCharAt(parameter.length() - 1);
		parameter.insert(0, "(").insert(0, procedureName).insert(0, "{CALL ").append(")}");
		return parameter.toString();
	}
}
