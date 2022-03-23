package com.dreamsphere.smartdocs.Models;

public class Document {

    String document_name;
    String document_company;
    String document_type;

    public Document(String document_name, String document_company, String document_type) {
        this.document_name = document_name;
        this.document_company = document_company;
        this.document_type = document_type;
    }

    public Document() {
    }

    public String getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name = document_name;
    }

    public String getDocument_company() {
        return document_company;
    }

    public void setDocument_company(String document_company) {
        this.document_company = document_company;
    }

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }
}
