package uw.courses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uw.CourseServiceException;
import uw.JSONKeys;
import uw.UWAPIResultCodes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JSONConversionUtils {

    /**
     * https://github.com/uWaterloo/api-documentation/blob/master/v2/codes/subjects.md
     * @param subjectMapJSONString JSON string containing fetched data
     * @return subject code to full subject name map
     * @throws CourseServiceException
     */
    public static Map<String,String> JSONToSubjectMap(String subjectMapJSONString) throws CourseServiceException, JSONException {
        JSONArray subjectsJSONArray = (JSONArray) getResponseData(subjectMapJSONString);
        Map<String,String> subjectMap = new HashMap<>();

        for(int i = 0; i < subjectsJSONArray.length(); ++i) {
            JSONObject subjectInfo = (JSONObject) subjectsJSONArray.get(i);
            String subjectCode = subjectInfo.getString(JSONKeys.SUBJECT);
            String fullSubjectName = subjectInfo.getString(JSONKeys.DESCRIPTION);
            subjectMap.put(subjectCode,fullSubjectName);
        }

        return subjectMap;
    }

    /**
     * https://github.com/uWaterloo/api-documentation/blob/master/v2/courses/courses.md
     * @param coursesJSONString JSON string representation of a list of courses (in summary format)
     * @return Java object representation of the course summaries
     * @throws CourseServiceException
     */
    public static List<CourseSummary> JSONToCourseSummaryList(String coursesJSONString) throws CourseServiceException {

        JSONArray coursesJSONArray = (JSONArray) getResponseData(coursesJSONString);
        List<CourseSummary> courseSummaries = new LinkedList<>();

        for (int i = 0; i < coursesJSONArray.length(); ++i) {
            CourseSummary courseSummary = new CourseSummary();
            JSONObject courseSummaryJSON = (JSONObject) coursesJSONArray.get(i);
            courseSummary.setTitle(courseSummaryJSON.getString(JSONKeys.TITLE));
            courseSummary.setSubject(courseSummaryJSON.getString(JSONKeys.SUBJECT));
            courseSummary.setCatalogNumber(courseSummaryJSON.getString(JSONKeys.CATALOG_NUMBER));
            courseSummary.setCourseCode(courseSummary.getSubject() + courseSummary.getCatalogNumber());
            courseSummaries.add(courseSummary);
        }

        return courseSummaries;
    }

    /**
     * https://github.com/uWaterloo/api-documentation/blob/master/v2/courses/subject_catalog_number.md
     * Doesn't set prereqs or future courses. Those should be fetched using prereqs api.
     * @param courseJSONString response JSON string for course information
     * @return Instance of course object representation of {@code courseJSONString}
     * @throws CourseServiceException
     */

    public static Course JSONToCourse(String courseJSONString) throws CourseServiceException {

        JSONObject data = (JSONObject) getResponseData(courseJSONString);
        Course course = new Course();

        course.setSubject(data.getString(JSONKeys.SUBJECT));
        course.setCatalogNumber(data.getString(JSONKeys.CATALOG_NUMBER));
        course.setCourseCode(course.getSubject() + course.getCatalogNumber());
        course.setTitle(data.getString(JSONKeys.TITLE));
        course.setUnits(!data.isNull(JSONKeys.UNITS) ? data.getDouble(JSONKeys.UNITS) : null);
        course.setDescription(!data.isNull(JSONKeys.DESCRIPTION) ? data.getString(JSONKeys.DESCRIPTION).replaceAll("'", "\\'\\'") : null);
        course.setInstructions(!data.isNull(JSONKeys.INSTRUCTIONS) ? JSONArrayToLList(data.getJSONArray(JSONKeys.INSTRUCTIONS)) : null);
        course.setPrereqsString(!data.isNull(JSONKeys.PREREQS) ? data.getString(JSONKeys.PREREQS) : null);
        course.setAntiRequisites(!data.isNull(JSONKeys.ANTIREQS) ? data.getString(JSONKeys.ANTIREQS) : null);
        course.setTermsOffered(!data.isNull(JSONKeys.TERMS_OFFERED) ? JSONArrayToLList(data.getJSONArray(JSONKeys.TERMS_OFFERED)) : null);
        course.setNotes(!data.isNull(JSONKeys.NOTES) ? data.getString(JSONKeys.NOTES) : null);
        course.setURL(data.getString(JSONKeys.URL));
        course.setIsOnline(
                data.isNull(JSONKeys.OFFERINGS) || data.getJSONObject(JSONKeys.OFFERINGS).isNull(JSONKeys.ONLINE) ?
                        null : data.getJSONObject(JSONKeys.OFFERINGS).getBoolean(JSONKeys.ONLINE)
        );

        return course;
    }

    /**
     * https://github.com/uWaterloo/api-documentation/blob/master/v2/courses/subject_catalog_number_prerequisites.md
     * @param jsonPrereqsString prereqs response JSON string
     * @return list of prereq coursecodes (eg. given CS246, returns [CS146,CS136,CS138])
     * @throws CourseServiceException
     * @throws JSONException
     */
    public static List<String> JSONToPrereqs(String jsonPrereqsString) throws CourseServiceException, JSONException {
        try {
            JSONObject data = (JSONObject) getResponseData(jsonPrereqsString);

            if (data.isNull(JSONKeys.PREREQS_PARSED)) {
                return new LinkedList<>();
            }

            Object JSONData = data.get(JSONKeys.PREREQS_PARSED);

            if (JSONData instanceof JSONArray) {
                JSONArray prereqsJSON = (JSONArray) JSONData;
                List<String> prereqs = new LinkedList<>();
                recursePrereqsList(prereqs, prereqsJSON);
                return prereqs;
            } else if (JSONData instanceof String) {
                return new LinkedList<>();
            } else {
                throw new CourseServiceException("Response data is none of the above types");
            }

        } catch (CourseServiceException e) {
            if (e.getStatusCode() == UWAPIResultCodes.NO_DATA_RETURNED) {
                return new LinkedList<>();
            } else {
                throw e;
            }
        }
    }

    /**
     * Recursively parse the response prereqs data
     * @param prereqs     the list to create from JSON data
     * @param prereqsJSON JSON data
     * @throws JSONException
     */
    private static void recursePrereqsList(List<String> prereqs, JSONArray prereqsJSON) throws JSONException {
        for (int i = 0; i < prereqsJSON.length(); ++i) {
            if (prereqsJSON.get(i) instanceof JSONArray) {
                recursePrereqsList(prereqs, prereqsJSON.getJSONArray(i));
            } else if (prereqsJSON.get(i) instanceof String) {
                prereqs.add(prereqsJSON.getString(i));
            }
        }
    }

    /**
     * Checks the status code of response JSON. If status code is success, and data exists,
     * extract the data and return. Else, throw JSONException with error message.
     * @param responseJSON
     * @return
     * @throws CourseServiceException
     * @throws JSONException
     */
    private static Object getResponseData(String responseJSON) throws CourseServiceException, JSONException {
        JSONObject response = new JSONObject(responseJSON);
        JSONObject meta = response.getJSONObject(JSONKeys.META);
        Integer responseCode = (Integer) meta.get(JSONKeys.STATUS);
        switch (responseCode) {
            case UWAPIResultCodes.SUCCESS:
                break;
            case UWAPIResultCodes.NO_DATA_RETURNED:
                throw new CourseServiceException("Failed fetch: no data returned", UWAPIResultCodes.NO_DATA_RETURNED);
            default:
                throw new CourseServiceException("Failed fetch: response code != success code.", responseCode);
        }

        Object data = response.get(JSONKeys.DATA);

        if (data == null) {
            throw new CourseServiceException("Failed to fetch: response data is null");
        }

        return data;
    }

    /**
     * @param jsonArray
     * @param <T>
     * @return Linked list representation of {@code jsonArray} data
     * @throws JSONException
     */
    private static <T> List<T> JSONArrayToLList(JSONArray jsonArray) throws JSONException {
        List<T> list = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            list.add((T) jsonArray.get(i));
        }
        return list;
    }

}
