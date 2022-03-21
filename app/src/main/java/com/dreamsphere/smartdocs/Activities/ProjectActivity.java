package com.dreamsphere.smartdocs.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamsphere.smartdocs.Adapters.MyRecyclerViewAdapter;
import com.dreamsphere.smartdocs.AutenticationServices.AdminAccessActivity;
import com.dreamsphere.smartdocs.AutenticationServices.LoginActivity;
import com.dreamsphere.smartdocs.R;
import com.dreamsphere.smartdocs.Services.PreferencesData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProjectActivity extends AppCompatActivity {
    public static final String TAG ="Main Activity";

    TextView go_to_admin;
    Integer counter;
    ImageButton settings_button;
    Button button_new_project;
    Context context;
    RecyclerView recyclerview_projects;
    ProgressBar main_progressbar;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String userID;
    ArrayList<String> user_projects;
    MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        go_to_admin=findViewById(R.id.go_to_admin);
        settings_button = findViewById(R.id.settings_button);
        button_new_project = findViewById(R.id.button_new_project);
        recyclerview_projects = findViewById(R.id.recyclerview_projects);
        main_progressbar = findViewById(R.id.main_progressbar);


        main_progressbar.setVisibility(View.VISIBLE);
        counter =0;
        context = this;
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
        userID = user.getUid();

        //Controlla se lo user è dentro l'app, se non è loggato, torna alla pagina di login
        isUserAlreadyLogged();


        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProjectActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        loadRecyclerView();

        goToAdmin();


        addProject();

    }

    private void addProject() {

        button_new_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setTitle("Nuovo Progetto");
                alert.setMessage("Inserisci nome del Progetto");


                alert.setView(edittext);

                alert.setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String projectName = edittext.getText().toString();
                        //se lo user clicca su aggiungi, adda il progetto al suo firebase nella sezione Users/UserID/Projects
                        FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getString(R.string.firebase_user_projects))
                                .child(projectName)
                                .child(getString(R.string.firebase_user_project_name))
                                .setValue(projectName);

                        Intent intent = new Intent(ProjectActivity.this, ProjectActivity.class);
                        startActivity(intent);
                        finish();


                    }
                });

                alert.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
            }
        });



    }

    private void loadRecyclerView() {
        //load della listview
        ArrayList<String> all_user_projects = new ArrayList<>();

        // load da firebase le regioni
        DatabaseReference datareference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_user_projects));

        datareference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String regionName = snapshot.getKey().toString();
                    all_user_projects.add(regionName);
                }
                loadListviewRegions(all_user_projects);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }


    private void loadListviewRegions(ArrayList<String> all_regions) {

        // set up the RecyclerView

        recyclerview_projects.setLayoutManager(new LinearLayoutManager(this));
        //adapter = new RecyclerView.Adapter<>(getApplicationContext(), all_regions);
        adapter = new MyRecyclerViewAdapter(context, all_regions);
        recyclerview_projects.setAdapter(adapter);

        main_progressbar.setVisibility(View.GONE);


    }

    private void goToAdmin() {

        go_to_admin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onTouch: touched");
                counter++;
                counter++;
                Log.d(TAG, "onTouch: counter: "+counter);
                if (counter==10){
                    Log.d(TAG, "onTouch: counter 3");
                    Intent intent = new Intent(ProjectActivity.this, AdminAccessActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

    }

    private void isUserAlreadyLogged() {

         //PreferencesData.setUserLoggedInStatus(getApplicationContext(),false);
        Log.d(TAG, "isUserAlreadyLogged: "+ PreferencesData.getUserLoggedInStatus(this));
        if ( !PreferencesData.getUserLoggedInStatus(this) ||   FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())  ==null ) {
            Intent intent = new Intent(ProjectActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}