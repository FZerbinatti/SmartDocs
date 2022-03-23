package com.dreamsphere.smartdocs.Activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamsphere.smartdocs.Adapters.RecyclerViewCompanyDocumentsAdapter;
import com.dreamsphere.smartdocs.Adapters.RecyclerView_DocumentTypeAdapter;
import com.dreamsphere.smartdocs.Adapters.RecyclerView_ProjectAdapter;
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

public class SelectDocumentTypeActivity extends AppCompatActivity {
    public static final String TAG ="SelectDocument";
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
    RecyclerView_DocumentTypeAdapter adapter;
    String user_company;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdocumenttype);


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
            //trova l'azienda dello user per poter caricare i relativi documenti
            findUserCompany(project_name);
        }
    }



    private void findUserCompany(String project_name) {

        DatabaseReference datareference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_user_generalities))
                .child(getString(R.string.firebase_user_company));

        datareference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_company = snapshot.getValue(String.class);
                Log.d("TAG", "onDataChange: questo user company: "+user_company);
                loadCompanyDocumentsTypes(user_company, project_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadCompanyDocumentsTypes(String company_name, String user_project) {

        ArrayList<String> all_company_documents_types = new ArrayList<>();

        // load da dei documenti dell'azienda accessibili ai suoi utenti
        DatabaseReference datareference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Companies))
                .child(company_name)
                .child(getString(R.string.firebase_company_documents));

        Log.d(TAG, "loadCompanyDocuments: path: "+getString(R.string.firebase_Companies)+"/"+company_name+"/"+getString(R.string.firebase_company_documents));

        datareference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: "+dataSnapshot.getChildrenCount());

                    String company_document_type_name = snapshot.getKey();
                    Log.d(TAG, "onDataChange: "+company_document_type_name);
                    all_company_documents_types.add(company_document_type_name);
                }
                loadRecyclerviewDocumentsTypes(all_company_documents_types);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadRecyclerviewDocumentsTypes(ArrayList<String> all_company_documents_types) {
        for(int i=0; i<all_company_documents_types.size(); i++){
            Log.d(TAG, "loadRecyclerviewDocumentsTypes: "+all_company_documents_types.get(i));
        }

        // set up the RecyclerView
        recyclerview_company_documents.setLayoutManager(new LinearLayoutManager(this));
        //adapter = new RecyclerView.Adapter<>(getApplicationContext(), all_regions);
        adapter = new RecyclerView_DocumentTypeAdapter (context, all_company_documents_types, user_company ,project_name);
        recyclerview_company_documents.setAdapter(adapter);
        main_progressbar.setVisibility(View.GONE);

    }

}