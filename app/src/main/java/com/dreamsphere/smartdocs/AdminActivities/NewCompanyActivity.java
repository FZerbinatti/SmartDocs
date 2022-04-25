package com.dreamsphere.smartdocs.AdminActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dreamsphere.smartdocs.Models.Company;
import com.dreamsphere.smartdocs.Models.CompanyInfo;
import com.dreamsphere.smartdocs.Models.Document;
import com.dreamsphere.smartdocs.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NewCompanyActivity extends AppCompatActivity {

    public static final String TAG ="NewCompanyActivity";

    EditText edittext_company_name;
    Button button_new_company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_company);

        button_new_company= findViewById(R.id.button_new_company);
        edittext_company_name = findViewById(R.id.edittext_company_name);

        //genera un'azienda su firebase, primo campo: pool di email abilitate




        button_new_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String company_name = edittext_company_name.getText().toString();

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "onClick: firebaseUser: "+firebaseUser);

                //Inserisci il nome dell'azienda nel rack delle aziende
                /*FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_whitelist))
                        .child(company_name)
                        .child(getString(R.string.firebase_company_whitelist))
                        .setValue(" ");*/

                //crea nel database un'istanza di azienda vuota      public Company(String company_whitelist, CompanyInfo company_info, ArrayList<Document> company_documents) {
                Company company = new Company(new CompanyInfo(company_name," "," "," "," "," "," "), new ArrayList<Document>());
                FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Companies))
                        .child(company_name)
                        .setValue(company).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(NewCompanyActivity.this, "Company Created", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

    }
}