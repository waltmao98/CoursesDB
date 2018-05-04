package uw;

public class CourseServiceException extends Exception {
    private Integer mStatusCode;

    public CourseServiceException(String message, int mStatusCode) {
        super(message);
        this.mStatusCode = mStatusCode;
    }

    public CourseServiceException(String message) {
        super(message);
    }

    public Integer getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int mStatusCode) {
        this.mStatusCode = mStatusCode;
    }
}
