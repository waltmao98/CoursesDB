package uw.subjectmap;

import uw.SQLUtils;
import uw.courses.CoursesDBHelper;

import java.sql.*;
import java.util.Map;

public class SubjectDBHelper {

    public static final String TABLE_NAME = "SUBJECTS";

    /**
     * Creates the SUBJECTS table
     */
    public static void createTable() {
        String createTableSQL = "CREATE TABLE " + TABLE_NAME + "(" +
                Cols.SUBJECT_CODE + " TEXT PRIMARY KEY NOT NULL," +
                Cols.SUBJECT_NAME + " TEXT);";
        SQLUtils.execUpdateSQL(createTableSQL);
    }

    /**
     * Drops the SUBJECTS table
     */
    public static void dropTable() {
        String dropTableSQL = "DROP TABLE IF EXISTS " + TABLE_NAME +";";
        SQLUtils.execUpdateSQL(dropTableSQL);
    }

    public static void insertSubjectMap(Map<String, String> courseCodeMap) {
        for(Map.Entry<String,String> subjectMapping : courseCodeMap.entrySet()) {
            if(subjectExists(subjectMapping.getKey())) {
                insertSubjectMap(subjectMapping);
            }
        }
    }

    private static void insertSubjectMap(Map.Entry<String,String> subjectMapping) {
        try {
            String insertSQL = "INSERT INTO " + TABLE_NAME + " (" +
                    Cols.SUBJECT_CODE + ", " + Cols.SUBJECT_NAME +
                    ") VALUES(?,?);";

            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + SQLUtils.DB_NAME);

            PreparedStatement stmt = conn.prepareStatement(insertSQL);
            stmt.setString(1,subjectMapping.getKey());
            stmt.setString(2, subjectMapping.getValue());
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Failed to insert [" + subjectMapping.getKey() + ", " + subjectMapping.getValue() + "]");
            e.printStackTrace();
        }
    }

    /**
     * @return if at least a 1 course falls under the given subject
     */
    private static boolean subjectExists(String subjectCode) {
        String selectSQL = "SELECT COUNT(*) FROM " +
                CoursesDBHelper.TABLE_NAME +
                " WHERE " + CoursesDBHelper.Cols.SUBJECT + "=?";
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + SQLUtils.DB_NAME);PreparedStatement stmt = conn.prepareStatement(selectSQL);
            stmt.setString(1,subjectCode);
            ResultSet resultSet = stmt.executeQuery();
            boolean subjectExists = resultSet.getInt(1) > 0;
            stmt.close();
            conn.close();
            return subjectExists;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Couldn't check if " + subjectCode + " has any courses");
            e.printStackTrace();
            return true;
        }
    }

    public static class Cols {
        static final String SUBJECT_CODE = "subject_code";
        static final String SUBJECT_NAME = "subject_name";
    }

}
