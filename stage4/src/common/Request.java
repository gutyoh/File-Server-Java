package common;

import java.io.Serializable;

public class Request implements Serializable {
    private final HttpRequestMethod httpRequestMethod;
    private final OperationMode operationMode;
    private final String fileName; // '###' represents unset
    private final int fileId; // negative value represents unset
    private final byte[] fileContent;

    public Request(HttpRequestMethod httpRequestMethod, String fileName, byte[] fileContent) { // PUT
        this.httpRequestMethod = httpRequestMethod;
        this.fileName = fileName;
        this.operationMode = OperationMode.NOT_SET;
        this.fileId = -666;
        this.fileContent = fileContent;
    }

    public Request(HttpRequestMethod httpRequestMethod, String fileName) { // GET || DELETE BY_NAME
        this.httpRequestMethod = httpRequestMethod;
        this.operationMode = OperationMode.BY_NAME;
        this.fileName = fileName;
        this.fileId = -777;
        this.fileContent = null;
    }

    public Request(HttpRequestMethod httpRequestMethod, int fileId) { // GET || DELETE BY_ID
        this.httpRequestMethod = httpRequestMethod;
        this.operationMode = OperationMode.BY_ID;
        this.fileId = fileId;
        this.fileName = "###";
        this.fileContent = null;
    }

    public Request(HttpRequestMethod httpRequestMethod) { // EXIT
        this.httpRequestMethod = httpRequestMethod;
        this.operationMode = OperationMode.NOT_SET;
        this.fileId = -999;
        this.fileName = "###";
        this.fileContent = null;
    }

    public HttpRequestMethod getHttpRequestMethod() {
        return httpRequestMethod;
    }

    public OperationMode getOperationMode() {
        return operationMode;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileId() {
        return fileId;
    }

    public byte[] getFileContent() {
        return fileContent;
    }
}