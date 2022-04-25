package com.dreamsphere.smartdocs.Models;

import java.util.ArrayList;

public class Company {


    CompanyInfo company_info;
    ArrayList<Document> company_documents;

    public Company() {
    }

    public Company(CompanyInfo company_info, ArrayList<Document> company_documents) {
        this.company_info = company_info;
        this.company_documents = company_documents;
    }

    public CompanyInfo getCompany_info() {
        return company_info;
    }

    public void setCompany_info(CompanyInfo company_info) {
        this.company_info = company_info;
    }

    public ArrayList<Document> getCompany_documents() {
        return company_documents;
    }

    public void setCompany_documents(ArrayList<Document> company_documents) {
        this.company_documents = company_documents;
    }
}
