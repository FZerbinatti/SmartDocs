package com.dreamsphere.smartdocs.Models;

public class DocumentDownload {

    String document_name;
    String document_download_name;

    public DocumentDownload(String document_name, String document_download_name) {
        this.document_name = document_name;
        this.document_download_name = document_download_name;
    }

    public DocumentDownload() {

    }

    public String getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name = document_name;
    }

    public String getDocument_download_name() {
        return document_download_name;
    }

    public void setDocument_download_name(String document_download_name) {
        this.document_download_name = document_download_name;
    }
}
