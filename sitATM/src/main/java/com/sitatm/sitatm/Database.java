package com.sitatm.sitatm;

import java.sql.*;

public class Database {
    private final static String url = "jdbc:mysql://localhost:3306/sitatm";
    private final static String user = "root";
    private final static String password = "";
    private static Connection connection = null;
    private static Statement statement = null;

    public Database() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL driver not found.");
            System.exit(1);
        } catch (SQLException e) {
            System.out.println("Error connecting to database. Details: \n" + e.getMessage());
            System.exit(1);
        }
    }
    public Connection getConnection() {
        return connection;
    }
    public ResultSet executeQuery(PreparedStatement preparedStatement) throws SQLException {
        try {
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public ResultSet executeQuery(String query) throws SQLException {
        try{
            return statement.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int executeUpdate(PreparedStatement preparedStatement) throws SQLException {
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) affected.");
        }
        return rowsAffected;
    }

    public void closeConnection() throws SQLException {
        connection.close();
        statement.close();
    }

    /** -- Experimental Feature
    public static void main(String[] args){
        Database db = new Database();
        try {
            ResultSet rs = db.executeQuery("SELECT account_number,user_id,account_type,created_date,deactivation_date FROM account ORDER BY created_date DESC");
            int numCols = rs.getMetaData().getColumnCount();

            // Print header row
            for (int i = 1; i <= numCols; i++) {
                System.out.printf("%-20s", rs.getMetaData().getColumnLabel(i));
            }
            System.out.println();

            // Print data rows
            while (rs.next()) {
                for (int i = 1; i <= numCols; i++) {
                    System.out.printf("%-20s", rs.getString(i));
                }
                System.out.println();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }**/
}