package com.dreamsphere.smartdocs.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamsphere.smartdocs.Adapters.RecyclerViewUserDocumentsAdapter;
import com.dreamsphere.smartdocs.Models.DocumentDownload;
import com.dreamsphere.smartdocs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DocumentsActivity extends AppCompatActivity {
    public static final String TAG ="Main Activity";

    TextView go_to_admin, textview_project_name;
    Integer counter;
    ImageButton settings_button;
    Button button_add_document;
    Context context;
    RecyclerView recyclerview_documents;
    ProgressBar main_progressbar;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String userID, project_name;
    ArrayList<String> user_projects;
    RecyclerViewUserDocumentsAdapter adapter;
    String user_company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        settings_button = findViewById(R.id.settings_button);
        button_add_document = findViewById(R.id.button_add_document);
        recyclerview_documents = findViewById(R.id.recyclerview_documents);
        main_progressbar = findViewById(R.id.main_progressbar);
        textview_project_name= findViewById(R.id.textview_project_name);

        //ottieni il nome del progetto corrente dall'activity precendente e settalo come titolo
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            project_name = extras.getString(getString(R.string.extra_project_name));
            textview_project_name.setText(project_name);
            //dopo che hai ricevuto le info sul progetto, corrente, carica i documenti relativi a questo progetto dell'utente
            user_company = extras.getString(getString(R.string.firebase_user_company));
            loadUserDocuments();
        }

        main_progressbar.setVisibility(View.VISIBLE);
        context = this;
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
        userID = user.getUid();


        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DocumentsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        //load dei documenti di questo user di questo progetto


        //pulsante per aggiungere un nuovo documento
        addDocument();

    }

    private void addDocument() {

        button_add_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //apri un'activity dove viene visualizzata una lista con i documenti accessibili a questo utente
                Intent intent = new Intent(DocumentsActivity.this, SelectDocumentTypeActivity.class);
                intent.putExtra(getString(R.string.extra_project_name),project_name);
                startActivity(intent);
            }
        });
    }

    private void loadUserDocuments() {
        //load della listview
        ArrayList<DocumentDownload> all_user_documents = new ArrayList<>();

        // load da firebase le regioni
        DatabaseReference datareference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_user_projects))
                .child(project_name);

        datareference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    DocumentDownload user_document = snapshot.getValue(DocumentDownload.class);
                    if (!user_document.getDocument_download_name().equals(" ")){

                        all_user_documents.add(user_document);
                    }

                }
                loadRecyclerviewDocuments(all_user_documents);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadRecyclerviewDocuments(ArrayList<DocumentDownload> all_regions) {

        // set up the RecyclerView
        recyclerview_documents.setLayoutManager(new LinearLayoutManager(this));
        //adapter = new RecyclerView.Adapter<>(getApplicationContext(), all_regions);
        adapter = new RecyclerViewUserDocumentsAdapter(context, all_regions, user_company);
        recyclerview_documents.setAdapter(adapter);
        main_progressbar.setVisibility(View.GONE);

    }



    //fixa inserisci nuovo documento con dropdown con tipologie gia esistenti, se non ce allora edittext























}
