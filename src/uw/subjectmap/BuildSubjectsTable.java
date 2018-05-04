package uw.subjectmap;

import uw.CourseServiceException;
import uw.FetchCoursesData;
import uw.courses.JSONConversionUtils;

import java.util.Map;

/**
 * Set of methods that builds the UW subject-mapping table
 */
public class BuildSubjectsTable {

    public static void fullyBuildUWSubjectsTable() {
        SubjectDBHelper.dropTable();
        SubjectDBHelper.createTable();
        downloadAllSubjectMappings();
    }

    private static void downloadAllSubjectMappings() {
        try {
            Map<String,String> subjectMap = JSONConversionUtils.JSONToSubjectMap(FetchCoursesData.getSubjectCodeMapJSON());
            SubjectDBHelper.insertSubjectMap(subjectMap);
        } catch (CourseServiceException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
