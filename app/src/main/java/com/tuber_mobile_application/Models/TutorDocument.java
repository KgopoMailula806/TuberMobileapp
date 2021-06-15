package com.tuber_mobile_application.Models;

public class TutorDocument {
    private int id;
    private String documentType;
    private int docID;
    private int tutorID;

    public TutorDocument(int id, String documentType, int docID, int tutorID)
    {
        this.setTutorID(tutorID);
        this.setId(id);
        this.setDocumentType(documentType);
        this.setDocID(docID);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public int getTutorID() {
        return tutorID;
    }

    public void setTutorID(int tutorID) {
        this.tutorID = tutorID;
    }
}
