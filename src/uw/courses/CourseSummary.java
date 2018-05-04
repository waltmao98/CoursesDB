package uw.courses;

public class CourseSummary {
    private String mCourseCode;
    private String mTitle;
    private String mSubject;
    private String mCatalogNumber;

    public String getCourseCode() {
        return mCourseCode;
    }

    public void setCourseCode(String courseCode) {
        mCourseCode = courseCode;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getCatalogNumber() {
        return mCatalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        mCatalogNumber = catalogNumber;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof CourseSummary && mCourseCode.equals(((CourseSummary) other).getCourseCode());
    }
}
