package com.dreamsphere.smartdocs.AdminActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dreamsphere.smartdocs.Models.Document;
import com.dreamsphere.smartdocs.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class NewCompanyDocumentActivity extends AppCompatActivity {
    public static final String TAG ="New User Activity";
    Spinner dropdown;
    Button button_new_document;
    String current_whitelist;
    EditText edittext_users, edittext_document_type;
    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcompanydocument);

        dropdown = findViewById(R.id.spinner_company_list);
        button_new_document =findViewById(R.id.button_new_user);
        edittext_users = findViewById(R.id.edittext_users);
        edittext_document_type = findViewById(R.id.edittext_document_type);
        current_whitelist="";

        //load companies names from firebase
        loadDatabaseCompanies();

        button_new_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String new_docuemnt_name = edittext_users.getText().toString();
                String new_document_type = edittext_document_type.getText().toString();

                if (new_docuemnt_name.isEmpty() || new_document_type.isEmpty()){
                    Toast.makeText(NewCompanyDocumentActivity.this, getString(R.string.nod_doc_or_type), Toast.LENGTH_SHORT).show();
                }else {

                    String selected_company = dropdown.getSelectedItem().toString();
                    Log.d(TAG, "onClick: selceted company: "+selected_company);

                    Document new_document = new Document(new_docuemnt_name, selected_company, new_document_type);

                    FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Companies))
                            .child(selected_company)
                            .child(getString(R.string.firebase_company_documents))
                            .child(new_document_type)
                            .child(selected_company+"_"+new_docuemnt_name)
                            .setValue(new_document).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(NewCompanyDocumentActivity.this, "Successfully inserted new document under: "+getString(R.string.firebase_Companies)+"/"+selected_company+"/"+getString(R.string.firebase_company_documents)+"/"+new_docuemnt_name, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }

    private void loadDatabaseCompanies() {

        ArrayList<String> arraylist_companies = new ArrayList<>();
        // load da firebase le regioni
        DatabaseReference datareference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_whitelist));
        datareference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // tutti i team di quella regione, prendi solo il nome
                    String companyName = snapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: companyName: "+ companyName);
                    arraylist_companies.add(companyName);
                }

                Log.d(TAG, "onDataChange: "+ arraylist_companies.size());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(NewCompanyDocumentActivity.this, android.R.layout.simple_spinner_dropdown_item, arraylist_companies);
                //set the spinners adapter to the previously created one.
                dropdown.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });


    }

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        Log.d(TAG, "getRandomString: password generata: "+sb.toString());
        return sb.toString();
    }
}