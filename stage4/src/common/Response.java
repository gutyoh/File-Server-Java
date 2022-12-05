package common;

import java.io.Serializable;

public class Response implements Serializable {
    private final HttpResponseCode httpResponseCode;
    private final byte[] fileContent;
    private final int fileID; // negative value represents unset

    public Response(HttpResponseCode httpResponseCode, byte[] fileContent) { // GET
        this.httpResponseCode = httpResponseCode;
        this.fileContent = fileContent;
        this.fileID = -666;
    }

    public Response(HttpResponseCode httpResponseCode, int fileID) { // PUT
        this.httpResponseCode = httpResponseCode;
        this.fileContent = null;
        this.fileID = fileID;
    }

    public Response(HttpResponseCode httpResponseCode) { // GENERIC
        this.httpResponseCode = httpResponseCode;
        this.fileContent = null;
        this.fileID = -777;
    }

    public HttpResponseCode getHttpResponseCode() {
        return httpResponseCode;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public int getFileID() {
        return fileID;
    }
}