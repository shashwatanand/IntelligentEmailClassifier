package org.samsha.iec.db;

// java packages
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

// samsha packages
import org.samsha.iec.util.OutputResult;
/**
 * This class which will acts as agent to take request to database operation 
 * @author Samiksha
 * @author Shashwat
 */
public class ServerProxy {
	public static boolean isConnected() throws SQLException {
		return DbProxy.isConnected();
	}
	
	public static void disconnect() {
		DbProxy.disconnect();
	}
	
	public static OutputResult insertUpdateEmailTuple(String label, String fileName, String toId, String fromId) {
		OutputResult result = new OutputResult();
		try {
			ArrayList<Object> parameters = new ArrayList<Object>();
			ArrayList<Object> outputParameterTypes = new ArrayList<Object>();
			parameters.add(fileName);
			parameters.add(toId);
			parameters.add(fromId);
			parameters.add(label);
			outputParameterTypes.add(Types.TINYINT);
			outputParameterTypes.add(Types.VARCHAR);
			ArrayList<Object> output = DbProxy.executeProcedure("p_InsertUpdateEmailTuple", parameters, outputParameterTypes);
			if (output.size() > 0 && output.get(0).toString().equals("1")) {
				String message = output.get(1).toString();
				result.markSuccessful(message, message);
			} else {
				String message = output.get(1).toString();
				result.markAsFailed(message);
			}
		} catch (Exception ex) {
			result.markAsFailed(ex.getMessage());
		}
		return result;
	}
}
