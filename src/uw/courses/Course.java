package uw.courses;

import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// model
public class Course {

    @NotNull
    private String mCourseCode; // CS246

    @Nullable
    private String mTitle; // Object Oriented Software Development

    @NotNull
    private String mSubject; // CS

    @NotNull
    private String mCatalogNumber; // 246

    @Nullable
    private Double mUnits; // 0.5

    @Nullable
    private String mDescription;

    @Nullable
    private List<String> mInstructions;

    @Nullable
    private List<String> mPrereqsList;

    @Nullable
    private String mPrereqsString;

    @Nullable
    private String mAntiReqs;

    @Nullable
    private List<String> mFutureCourses;

    @Nullable
    private List<String> mTermsOffered;

    @Nullable
    private String mNotes;

    @Nullable
    private Boolean mIsOnline;

    @Nullable
    private String mURL;

    @Nullable
    private Boolean mIsFavourite;

    public void addPrereq(@NotNull String prereqCourseCode) {
        if (mPrereqsList == null) {
            mPrereqsList = new LinkedList<>();
        }
        mPrereqsList.add(prereqCourseCode);
    }

    public void addFutureCourse(@NotNull String futureCourseCode) {
        if (mFutureCourses == null) {
            mFutureCourses = new LinkedList<>();
        }
        mFutureCourses.add(futureCourseCode);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Course && mCourseCode.equals(((Course) other).getCourseCode());
    }

    // basic getters and setters

    public String getCourseCode() {
        return mCourseCode;
    }

    public void setCourseCode(String courseCode) {
        mCourseCode = courseCode;
    }

    public String getTitle() {
        return mTitle != null ? mTitle : "";
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

    public double getUnits() {
        return mUnits != null ? mUnits : 0.5;
    }

    public void setUnits(double units) {
        mUnits = units;
    }

    public String getDescription() {
        return mDescription != null ? mDescription : "";
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public List<String> getInstructions() {
        return mInstructions != null ? mInstructions : new ArrayList<>();
    }

    public void setInstructions(List<String> instructions) {
        mInstructions = instructions;
    }

    public String getInstructionsJSONString() {
        return new Gson().toJson(mInstructions);
    }

    public List<String> getPrereqs() {
        return mPrereqsList != null ? mPrereqsList : new ArrayList<>();
    }

    public String getPrereqsJSONString() {
        return new Gson().toJson(mPrereqsList);
    }

    public void setPrerequisites(List<String> prerequisites) {
        mPrereqsList = prerequisites;
    }

    public String getPrereqsString() {
        return mPrereqsString != null ? mPrereqsString : "";
    }

    public void setPrereqsString(String prereqsString) {
        this.mPrereqsString = prereqsString;
    }

    public String getAntiRequisites() {
        return mAntiReqs != null ? mAntiReqs : "";
    }

    public void setAntiRequisites(String antiRequisites) {
        mAntiReqs = antiRequisites;
    }

    public List<String> getFutureCourses() {
        return mFutureCourses != null ? mFutureCourses : new ArrayList<>();
    }

    public void setFutureCourses(List<String> futureCourses) {
        mFutureCourses = futureCourses;
    }

    public String getFutureCoursesJSONString() {
        return new Gson().toJson(mFutureCourses);
    }

    public List<String> getTermsOffered() {
        return mTermsOffered != null ? mTermsOffered : new ArrayList<>();
    }

    public void setTermsOffered(List<String> termsOffered) {
        mTermsOffered = termsOffered;
    }

    public String getTermsOfferedJSONString() {
        return new Gson().toJson(mTermsOffered);
    }

    public String getNotes() {
        return mNotes != null ? mNotes : "";
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public boolean isOnline() {
        return mIsOnline != null ? mIsOnline : false;
    }

    public void setIsOnline(boolean online) {
        mIsOnline = online;
    }

    public String getURL() {
        return mURL != null ? mURL : "";
    }

    public void setURL(String URL) {
        mURL = URL;
    }

    public void setIsFavourite(boolean isFavourite) {
        mIsFavourite = isFavourite;
    }

    public boolean isFavourite() {
        return mIsFavourite == null ? false : mIsFavourite;
    }
}
