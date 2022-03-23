package com.dreamsphere.smartdocs.AdminActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dreamsphere.smartdocs.R;

public class AdminActivity extends AppCompatActivity {

    Button new_user, new_company, button_new_company_docuemnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        new_user = findViewById(R.id.button_new_user);
        new_company= findViewById(R.id.button_new_company);
        button_new_company_docuemnt= findViewById(R.id.button_new_company_docuemnt);

        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AdminActivity.this,  NewUsersPool.class);
                startActivity(intent);
            }
        });

        new_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AdminActivity.this,  NewCompanyActivity.class);
                startActivity(intent);
            }
        });

        button_new_company_docuemnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AdminActivity.this,  NewCompanyDocumentActivity.class);
                startActivity(intent);
            }
        });
    }
}