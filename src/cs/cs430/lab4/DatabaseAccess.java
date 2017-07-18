package cs.cs430.lab4;

/**
 * A class that hold all the connection related attributes and helps to access the DB
 */

import java.sql.*;

class DatabaseAccess {
    private static Connection connection;
    private static DatabaseAccess INSTANCE = null;

    private DatabaseAccess () {
    }

    static DatabaseAccess getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseAccess();
            initializeConnection();
        }
        return INSTANCE;
    }

    private static void initializeConnection() {
        if(connection != null) {
            return ;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (final ClassNotFoundException cNFe) {
            cNFe.printStackTrace();
            System.exit(-1);
        }
        String url = "jdbc:mysql://cronus.ftc.hpeswlab.net/sanjusam";
        try {
            connection = DriverManager.getConnection(url,"sanjusam", "sanju123");
        } catch (SQLException sqlE) {
            sqlE.printStackTrace();
            System.exit(-1);
        }
    }

    ResultSet executeQuery(final String sqlStatement) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sqlStatement);
    }

    int updateDatabase(final String sqlStatement) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeUpdate(sqlStatement);
    }
}
