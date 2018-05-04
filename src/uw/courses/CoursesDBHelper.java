package uw.courses;

import com.google.gson.Gson;
import uw.SQLUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Helper class for accessing the courses.db (COURSES table)
 */
public class CoursesDBHelper {

    public static final String TABLE_NAME = "COURSES";

    /**
     * Sets a set of future courses via SQL update statement
     *
     * @param futureCourses Map<courseCode>,list of this course's future courses>
     */
    public static void setFutureCourses(Map<String, List<String>> futureCourses) {
        for (Map.Entry<String, List<String>> entry : futureCourses.entrySet()) {
            setFutureCourse(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Sets future course via SQL update statement
     *
     * @param courseCode
     * @param futureCourses list of the course's future courses
     */
    public static void setFutureCourse(String courseCode, List<String> futureCourses) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + SQLUtils.DB_NAME);

            String updateSQL = "UPDATE " + TABLE_NAME +
                    " SET " + Cols.FUTURE_COURSES_LIST + "=?" +
                    " WHERE " + Cols.COURSE_CODE + "=?;";
            PreparedStatement stmt = conn.prepareStatement(updateSQL);
            stmt.setString(1, new Gson().toJson(futureCourses));
            stmt.setString(2, courseCode);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Failed to update future courses of " + courseCode);
            e.printStackTrace();
        }
    }

    /**
     * Sets a set of prereqs via SQL update statement
     *
     * @param prereqs Map<course code, json string representation of prereqs>
     */
    public static void setPrereqs(Map<String, String> prereqs) {
        for (Map.Entry<String, String> entry : prereqs.entrySet()) {
            setPrereq(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @param courseCode  course code of the course that has prereqs
     * @param prereqsJSON prereqs of the course in JSON string format
     */
    public static void setPrereq(String courseCode, String prereqsJSON) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + SQLUtils.DB_NAME);

            String updateSQL = "UPDATE " + TABLE_NAME +
                    " SET " + Cols.PREREQS_LIST + "=?" +
                    " WHERE " + Cols.COURSE_CODE + "=?;";
            PreparedStatement stmt = conn.prepareStatement(updateSQL);
            stmt.setString(1, prereqsJSON);
            stmt.setString(2, courseCode);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Failed to update prereqs of " + courseCode);
            e.printStackTrace();
        }
    }

    /**
     * Inserts a set of courses in to the database via SQL insert statement
     *
     * @param courses
     */
    public static void insertCourses(Course... courses) {
        for (Course course : courses) {
            try {
                Class.forName("org.sqlite.JDBC");
                Connection conn = DriverManager.getConnection("jdbc:sqlite:" + SQLUtils.DB_NAME);

                PreparedStatement stmt = conn.prepareStatement(
                        SQLUtils.getInsertTemplate(TABLE_NAME, Cols.getColsList()));
                stmt = prepareCourseStatement(course, stmt);
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (SQLException | ClassNotFoundException e) {
                System.err.println("Failed to insert " + course.getCourseCode());
                e.printStackTrace();
            }
        }
    }

    /**
     * Deletes the given courses from the database
     *
     * @param courses courses to delete from the database
     */
    public static void deleteItems(Course... courses) {
        String deleteSQL = "";
        for (Course course : courses) {
            deleteSQL = deleteSQL.concat("DELETE FROM " + TABLE_NAME +
                    " WHERE " + Cols.COURSE_CODE + "=" + "\'" + course.getCourseCode() + "\';");
        }
        SQLUtils.execUpdateSQL(deleteSQL);
    }

    /**
     * Creates the COURSES table
     */
    public static void createCoursesTable() {
        String createTableSQL = getCreateCoursesTableSQL();
        SQLUtils.execUpdateSQL(createTableSQL);
    }

    /**
     * DELETES THE COURSES TABLE. USE WITH CAUTION!
     */
    public static void dropTable() {
        SQLUtils.execUpdateSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
    }

    /////////////////////////// SQL STATEMENT HELPERS ////////////////////

    private static String getCreateCoursesTableSQL() {
        return "CREATE TABLE " + TABLE_NAME +
                "(" + Cols.COURSE_CODE + " TEXT PRIMARY KEY   NOT NULL," +
                Cols.TITLE + " TEXT," +
                Cols.SUBJECT + " TEXT NOT NULL," +
                Cols.CATOLOG_NUMBER + " TEXT NOT NULL," +
                Cols.UNITS + " TEXT," +
                Cols.DESCRIPTION + " TEXT," +
                Cols.INSTRUCTIONS + " TEXT," +
                Cols.PREREQS_STRING + " TEXT," +
                Cols.ANTIREQS + " TEXT," +
                Cols.PREREQS_LIST + " TEXT," +
                Cols.FUTURE_COURSES_LIST + " TEXT," +
                Cols.TERMS_OFFERED + " TEXT," +
                Cols.NOTES + " TEXT," +
                Cols.IS_ONLINE + " INT," +
                Cols.URL + " TEXT," +
                Cols.FAVOURITE + " TEXT);";
    }

    /**
     * Sets the parameters for a course insert prepared statement
     *
     * @param course course to insert
     * @param stmt   PreparedStatement object used for the insert
     * @return
     * @throws SQLException
     */
    private static PreparedStatement prepareCourseStatement(Course course, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, course.getCourseCode());
        stmt.setString(2, course.getTitle());
        stmt.setString(3, course.getSubject());
        stmt.setString(4, course.getCatalogNumber());
        stmt.setDouble(5, course.getUnits());
        stmt.setString(6, course.getDescription());
        stmt.setString(7, course.getInstructionsJSONString());
        stmt.setString(8, course.getPrereqsString());
        stmt.setString(9, course.getAntiRequisites());
        stmt.setString(10, course.getPrereqsJSONString());
        stmt.setString(11, course.getFutureCoursesJSONString());
        stmt.setString(12, course.getTermsOfferedJSONString());
        stmt.setString(13, course.getNotes());
        stmt.setInt(14, course.isOnline() ? 1 : 0);
        stmt.setString(15, course.getURL());
        stmt.setInt(16, course.isFavourite() ? 1 : 0);
        return stmt;
    }

    /////////////////////////// COURSE TABLE SCHEMA //////////////////////////////////

    public static class Cols {
        public static final String COURSE_CODE = "course_code"; // CS246
        public static final String TITLE = "title"; // Object Oriented Software Development
        public static final String SUBJECT = "subject"; // CS
        public static final String CATOLOG_NUMBER = "cat_num"; // 246
        public static final String UNITS = "units";
        public static final String DESCRIPTION = "description";
        public static final String INSTRUCTIONS = "instructions";
        public static final String PREREQS_STRING = "prereqs_string"; // english description of prereqs
        public static final String ANTIREQS = "antireqs";
        public static final String PREREQS_LIST = "prereqs_list"; // list of prereqs in JSON string format
        public static final String FUTURE_COURSES_LIST = "future_courses_list";
        public static final String TERMS_OFFERED = "terms_offered";
        public static final String NOTES = "notes";
        public static final String IS_ONLINE = "is_online";
        public static final String URL = "url"; // link to UW website for official course info
        public static final String FAVOURITE = "favourite";

        private Cols() {
        }

        static List<String> getColsList() {
            return Arrays.asList(
                    COURSE_CODE,
                    TITLE, SUBJECT,
                    CATOLOG_NUMBER,
                    UNITS,
                    DESCRIPTION,
                    INSTRUCTIONS,
                    PREREQS_STRING,
                    ANTIREQS,
                    PREREQS_LIST,
                    FUTURE_COURSES_LIST,
                    TERMS_OFFERED,
                    NOTES,
                    IS_ONLINE,
                    URL,
                    FAVOURITE);
        }
    }


}
