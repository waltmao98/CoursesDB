package uw.courses;

import com.google.gson.Gson;
import uw.CourseServiceException;
import uw.FetchCoursesData;

import java.util.*;

/**
 * Set of methods that builds the UW courses table
 */
public class BuildCoursesTable {

    /**
     * RUN THIS PROGRAM TO DROP PREVIOUS TABLE AND CREATE A NEW FULLY COMPLETE TABLE
     */
    public static void fullyBuildUWCoursesTable() {
        try {
            CoursesDBHelper.dropTable();
            CoursesDBHelper.createCoursesTable();
            downloadAllCourses();
            downloadPrereqsAndFutureCourses();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Fetches every course from web server and inserts it into the database
     * Note: doesn't fetch or insert prereqs list or future courses. These should be downloaded separately
     */
    private static void downloadAllCourses() throws CourseServiceException {
        String allCoursesJSON = FetchCoursesData.getAllCoursesJSON();

        List<CourseSummary> courseSummaries = JSONConversionUtils.JSONToCourseSummaryList(allCoursesJSON);

        for (CourseSummary summary : courseSummaries) {
            try {
                downloadCourse(summary.getSubject(), summary.getCatalogNumber());
            } catch (CourseServiceException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Downloads a course from the web server to the database
     *
     * @param subject       subject of course to download
     * @param catalogNumber catalog number of the course to download
     * @throws CourseServiceException
     */
    static void downloadCourse(String subject, String catalogNumber) throws CourseServiceException {
        String courseJSONString = FetchCoursesData.getCourseDetailsJSON(
                subject,
                catalogNumber);
        Course course = JSONConversionUtils.JSONToCourse(courseJSONString);
        if (course != null) {
            CoursesDBHelper.insertCourses(course);
        } else {
            System.err.println("Error fetching: " + subject + catalogNumber);
        }
    }

    static void downloadPrereqsAndFutureCourses() throws CourseServiceException {
        Map<String, List<String>> futureCoursesMap = new HashMap<>();
        Map<String, String> prereqsMap = new LinkedHashMap<>();

        String allCoursesJSON = FetchCoursesData.getAllCoursesJSON();
        List<CourseSummary> courseSummaries = JSONConversionUtils.JSONToCourseSummaryList(allCoursesJSON);

        for (CourseSummary courseSummary : courseSummaries) {
            String courseCode = courseSummary.getCourseCode();
            String catalogNumber = courseSummary.getCatalogNumber();
            String subject = courseSummary.getSubject();

            try {
                String prereqJSON = FetchCoursesData.getPrereqJSON(subject, catalogNumber); //prereqJSON in nested format
                List<String> prereqs = JSONConversionUtils.JSONToPrereqs(prereqJSON); //convert to 1D list format

                prereqsMap.put(courseCode, new Gson().toJson(prereqs));

                for (String prereqCourseCode : prereqs) {
                    futureCoursesMap.computeIfAbsent(prereqCourseCode, k -> new LinkedList<>());
                    futureCoursesMap.get(prereqCourseCode).add(courseCode);
                }
            } catch (CourseServiceException e) {
                e.printStackTrace();
            }
        }

        CoursesDBHelper.setPrereqs(prereqsMap);
        CoursesDBHelper.setFutureCourses(futureCoursesMap);
    }
}
