package com.sitatm.sitatm;

import java.sql.*;

public class test {
    public static void main(String[] args) {
        String user = "root";
        String password = "";
        String url = "jdbc:mysql://localhost/test";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();

            String load_data_query = "LOAD DATA INFILE 'C:/xampp/mysql/data/test/bank.csv' " +
                    "INTO TABLE java_transactions " +
                    "FIELDS TERMINATED BY ',' " +
                    "ENCLOSED BY '\"' " +
                    "LINES TERMINATED BY '\r\n' " +
                    "IGNORE 1 ROWS";

            stmt.execute(load_data_query);

            conn.commit();
            System.out.println("Database successfully imported!");

        } catch (SQLException e) {
            System.out.println("Failed to import database: " + e.getMessage());
        }
    }
}
