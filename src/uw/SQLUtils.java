package uw;

import java.sql.*;
import java.util.List;

/**
 * General SQL utility methods for dealing with courses.db
 */
public class SQLUtils {

    public static final String DB_NAME = "courses.db";

    public static void addColumn(String tableName, String columnName, String columnType) {
        String addColSQL = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType + ";";
        execUpdateSQL(addColSQL);
    }

    /**
     * @param tableName table of model class
     * @param cols ordered list of the model class's columns in the table
     * @return prepared insert statement SQL string: INSERT INTO (column_names...) ... VALUES (?,?,?...?)
     */
    public static String getInsertTemplate(String tableName, List<String> cols) {
        String colsSQL = "INSERT INTO " + tableName + " (";
        for (int i = 0; i < cols.size() - 1; ++i) {
            colsSQL = colsSQL.concat(cols.get(i).concat(","));
        }
        colsSQL = colsSQL.concat(cols.get(cols.size() - 1).concat(") VALUES ("));
        for (int i = 0; i < cols.size() - 1; ++i) {
            colsSQL = colsSQL.concat("?,");
        }
        colsSQL = colsSQL.concat("?);");
        return colsSQL;
    }

    public static void execUpdateSQL(String sql) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);

            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(sql);
            e.printStackTrace();
        }
    }

    public static ResultSet execQuerySQL(String sql, String dbName) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbName);

            Statement stmt = c.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(sql);
            e.printStackTrace();
            return null;
        }
    }

}
