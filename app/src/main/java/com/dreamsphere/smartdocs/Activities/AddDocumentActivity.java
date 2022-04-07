package com.dreamsphere.smartdocs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.dreamsphere.smartdocs.Adapters.RecyclerViewCompanyDocumentsAdapter;
import com.dreamsphere.smartdocs.Adapters.RecyclerViewUserDocumentsAdapter;
import com.dreamsphere.smartdocs.Models.Document;
import com.dreamsphere.smartdocs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddDocumentActivity extends AppCompatActivity {
    public static final String TAG ="SelectDocumentAc";
    Integer counter;
    ImageButton settings_button;
    Button button_new_document;
    Context context;
    RecyclerView recyclerview_company_documents;
    ProgressBar main_progressbar;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String userID, project_name;
    ArrayList<String> user_projects;
    RecyclerViewCompanyDocumentsAdapter adapter;
    String user_company, document_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdocument);


        settings_button = findViewById(R.id.settings_button);
        button_new_document = findViewById(R.id.button_add_document);
        recyclerview_company_documents = findViewById(R.id.recyclerview_company_documents);
        main_progressbar = findViewById(R.id.main_progressbar);
        main_progressbar.setVisibility(View.VISIBLE);
        context = this;



        //ottieni il nome del progetto corrente dall'activity precendente per poter salvare il doc sotto il progetto utente corrente
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            project_name = extras.getString(getString(R.string.extra_project_name));
            user_company = extras.getString(getString(R.string.extra_user_company));
            document_type = extras.getString(getString(R.string.extra_document_type));
            Log.d(TAG, "onCreate: "+user_company+"/"+ project_name+"/"+document_type);
            loadCompanyDocuments(user_company, project_name,document_type);
        }





/*        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectDocumentActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });*/

        //load dei documenti di questo user di questo progetto


        //pulsante per aggiungere un nuovo documento

    }



    //qui devo far comparire la lista classe Documents con il nome solo se appare nella lista documenti company, on tap listener su quello mi porta direttamente al docuemnto aperto,
    //salvare il documento crea il pdf, lo mette sul cloud e sotto Documents appaiono i documenti giò compilati



    private void loadCompanyDocuments(String company_name, String user_project, String document_type) {

        ArrayList<Document> all_company_documents = new ArrayList<>();

        // load da dei documenti dell'azienda accessibili ai suoi utenti
        DatabaseReference datareference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Companies))
                .child(company_name)
                .child(getString(R.string.firebase_company_documents))
                .child(document_type);

        Log.d(TAG, "loadCompanyDocuments: path: "+getString(R.string.firebase_Companies)+"/"+company_name+"/"+getString(R.string.firebase_company_documents)+"/"+document_type);

        datareference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: "+dataSnapshot.getChildrenCount());
                    Log.d(TAG, "onDataChange: "+snapshot.getKey());
                    Document company_document = snapshot.getValue(Document.class);
                    Log.d("TAG", "onDataChange: "+company_document.getDocument_name());
                    all_company_documents.add(company_document);
                }
                loadRecyclerviewDocuments(all_company_documents);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadRecyclerviewDocuments(ArrayList<Document> all_company_documents) {

        // set up the RecyclerView
        recyclerview_company_documents.setLayoutManager(new LinearLayoutManager(this));
        //adapter = new RecyclerView.Adapter<>(getApplicationContext(), all_regions);
        Log.d(TAG, "loadRecyclerviewDocuments: starting data: +project_name "+project_name);
        adapter = new RecyclerViewCompanyDocumentsAdapter(context, all_company_documents, project_name, new Document("", user_company, document_type));
        recyclerview_company_documents.setAdapter(adapter);
        main_progressbar.setVisibility(View.GONE);

    }


}