package common;

// partially reimplementing as enum
// https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/HttpURLConnection.html
public enum HttpResponseCode {
    HTTP_OK(200),
    HTTP_NOT_FOUND(404),
    HTTP_FORBIDDEN(403);

    private int value;

    HttpResponseCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}