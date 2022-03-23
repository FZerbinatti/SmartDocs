package com.dreamsphere.smartdocs.Models;

import java.util.ArrayList;

public class Company {

    String company_whitelist;
    String company_name;
    String company_logo;
    String company_address;
    String company_number;
    String company_PIVA;
    String company_data;
    String company_name_full;
    ArrayList<Document> company_documents;

    public Company() {
    }

    public Company(String company_whitelist, String company_name, String company_logo, String company_address, String company_number, String company_PIVA, String company_data, String company_name_full, ArrayList<Document> company_documents) {
        this.company_whitelist = company_whitelist;
        this.company_name = company_name;
        this.company_logo = company_logo;
        this.company_address = company_address;
        this.company_number = company_number;
        this.company_PIVA = company_PIVA;
        this.company_data = company_data;
        this.company_name_full = company_name_full;
        this.company_documents = company_documents;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_logo() {
        return company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getCompany_number() {
        return company_number;
    }

    public void setCompany_number(String company_number) {
        this.company_number = company_number;
    }

    public String getCompany_PIVA() {
        return company_PIVA;
    }

    public void setCompany_PIVA(String company_PIVA) {
        this.company_PIVA = company_PIVA;
    }

    public String getCompany_data() {
        return company_data;
    }

    public void setCompany_data(String company_data) {
        this.company_data = company_data;
    }

    public String getCompany_name_full() {
        return company_name_full;
    }

    public void setCompany_name_full(String company_name_full) {
        this.company_name_full = company_name_full;
    }

    public String getCompany_whitelist() {
        return company_whitelist;
    }

    public void setCompany_whitelist(String company_whitelist) {
        this.company_whitelist = company_whitelist;
    }

    public ArrayList<Document> getCompany_documents() {
        return company_documents;
    }

    public void setCompany_documents(ArrayList<Document> company_documents) {
        this.company_documents = company_documents;
    }
}
