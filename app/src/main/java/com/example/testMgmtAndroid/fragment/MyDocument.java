package com.example.testMgmtAndroid.fragment;

public class MyDocument {
    private int documentId;
    private String documentName;
    private String documentFileName;
    private String documentUrl;
    private int studentId;
    private int documentType;

    public MyDocument(int documentId, String documentName, String documentFileName, String documentUrl, int studentId, int documentType) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.documentFileName = documentFileName;
        this.documentUrl = documentUrl;
        this.studentId = studentId;
        this.documentType = documentType;
    }

    public int getDocumentId() {
        return documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getDocumentFileName() {
        return documentFileName;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getDocumentType() {
        return documentType;
    }
}
