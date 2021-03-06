package io.openliberty.guides.rest;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


@Path("test")
public class TestResource {
static final String DB_URL = "jdbc:mysql://localhost:3306/employees?useUnicode=true&useJDBCCompliantTimezoneShift=true&serverTimezone=UTC&useSSL=false";
    static final String USER = "mytestuser";//username 
    static final String PASS = "mypassword";//password
    @GET
    @Produces(MediaType.APPLICATION_JSON)
	public JsonArray getProperties() {
		Statement stmt = null;
        Connection conn = null;
        
        String query = "select * from employees limit 10";
        JsonArrayBuilder array = Json.createArrayBuilder();
		try{
			//Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");

			//Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			System.out.println("Executing statement.");
			ResultSet rs = stmt.executeQuery(query);
			//Extract data from result set
			System.out.println("Execution suceeeded");	
			while(rs.next()){
				//Make a new JsonObject within
				JsonObjectBuilder obj = Json.createObjectBuilder();
				String emp_no = rs.getString("emp_no");
				String first_name = rs.getString("first_name");
				String last_name = rs.getString("last_name");
				obj.add("first_name", first_name);
				obj.add("last_name", last_name);
				array.add(obj.build());
			}
			rs.close();
		 } catch(SQLException se){
            //Handle errors for JDBC
			se.printStackTrace();
			JsonObjectBuilder obj = Json.createObjectBuilder();
			obj.add("SQL error", se.toString());
         } catch(Exception e){
            //Handle errors for Class.forName
			e.printStackTrace();
			JsonObjectBuilder obj = Json.createObjectBuilder();
			obj.add("Error", e.toString());
         } finally{
            //finally block used to close resources
            try{
               if(stmt!=null)
                  	conn.close();
            } catch(SQLException se){ }// do nothing
            try{
               if(conn!=null)
                	conn.close();
            }catch(SQLException se){
			   	se.printStackTrace();
			   	JsonObjectBuilder obj = Json.createObjectBuilder();
				obj.add("SQL error", se.toString());
            	}//end finally try
         	} //end finally
		return array.build();
    }
}
