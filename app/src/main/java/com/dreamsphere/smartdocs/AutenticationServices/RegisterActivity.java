package com.dreamsphere.smartdocs.AutenticationServices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.dreamsphere.smartdocs.Models.User;
import com.dreamsphere.smartdocs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    EditText edittext_user_email, edittext_user_password, edittext_user_password_confirm;
    Button button_new_user;
    Spinner spinner_company_list;
    public static final String TAG ="RegisterActivity";
    private FirebaseAuth mAuth;
    ProgressBar register_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edittext_user_email = findViewById(R.id.edittext_user_email);
        edittext_user_password = findViewById(R.id.edittext_user_password);
        edittext_user_password_confirm = findViewById(R.id.edittext_user_password_confirm);
        button_new_user = findViewById(R.id.button_new_user);
        spinner_company_list = findViewById(R.id.spinner_company_list);
        register_progressbar = findViewById(R.id.register_progressbar);



        loadDatabaseCompanies();

        button_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                register_progressbar.setVisibility(View.VISIBLE);

                String email = edittext_user_email.getText().toString().trim();
                String password = edittext_user_password.getText().toString();
                String repeated_password = edittext_user_password_confirm.getText().toString();
                String selected_company = spinner_company_list.getSelectedItem().toString();



                if (email.isEmpty() || password.isEmpty() || repeated_password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                    if (email.isEmpty()) {
                        edittext_user_email.setError("Email Required");
                        edittext_user_email.requestFocus();
                    }

                } else if (!password.equals(repeated_password)) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show();
                    register_progressbar.setVisibility(View.GONE);
                } else if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.password_short), Toast.LENGTH_SHORT).show();
                    register_progressbar.setVisibility(View.GONE);
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                    register_progressbar.setVisibility(View.GONE);
                    edittext_user_email.requestFocus();
                } else {
                    //query per verificare se la mail è in whitelist
                    DatabaseReference datareference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_whitelist))
                            .child(selected_company)
                            .child(getResources().getString(R.string.firebase_company_whitelist));

                    datareference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.getValue(String.class).contains(email)){
                                Log.d(TAG, "onDataChange: email correttamente in whitelist");
                                //se tutti i campi sono stati compilati correttamente, prosegui con la creazione firebase dell'account
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            User user = new User(email, selected_company);

                            FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(getString(R.string.firebase_user_generalities))
                                    .setValue(user).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.user_registerd), Toast.LENGTH_SHORT).show();



                                    register_progressbar.setVisibility(View.GONE);


                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.user_registration_fail), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onClick: TASK 1 INSUCCESS");
                                    register_progressbar.setVisibility(View.GONE);

                                }
                            });
                        } else {
                            Log.d(TAG, "onClick: " + task.toString());
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.user_registration_fail), Toast.LENGTH_SHORT).show();
                            register_progressbar.setVisibility(View.GONE);
                        }
                    });

                            }else {
                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.email_not_whitelisted), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private void loadDatabaseCompanies() {
        Log.d(TAG, "loadDatabaseCompanies: ");

        ArrayList<String> arraylist_companies = new ArrayList<>();
        // load da firebase le regioni
        DatabaseReference datareference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_whitelist));
        datareference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // tutti i team di quella regione, prendi solo il nome
                    String regionName = snapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: regionName: "+regionName);
                    arraylist_companies.add(regionName);
                }

                Log.d(TAG, "onDataChange: "+ arraylist_companies.size());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, arraylist_companies);
                //set the spinners adapter to the previously created one.
                spinner_company_list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });


    }
}