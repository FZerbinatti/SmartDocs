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

import com.dreamsphere.smartdocs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class NewUsersPool extends AppCompatActivity {
    public static final String TAG ="New User Activity";
    Spinner dropdown;
    Button button_new_user;
    String current_whitelist;
    EditText edittext_users;
    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_users_pool);

        dropdown = findViewById(R.id.spinner_company_list);
        button_new_user=findViewById(R.id.button_new_user);
        edittext_users = findViewById(R.id.edittext_users);
        current_whitelist="";

        //load companies names from firebase
        loadDatabaseCompanies();

        button_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emails= edittext_users.getText().toString();
                if (emails.isEmpty()){
                    Toast.makeText(NewUsersPool.this, "No email addresses", Toast.LENGTH_SHORT).show();
                }else {
                    Log.d(TAG, "onClick: trimming");
                    //togli schifezze dalla stringa
                    String emails1= emails.replace(",", " ");
                    String emails2= emails1.replace(";", " ");
                    Log.d(TAG, "onClick: trimmed");

                    String selected_company = dropdown.getSelectedItem().toString();
                    Log.d(TAG, "onClick: selceted company: "+selected_company);

                    DatabaseReference datareference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_whitelist))
                            .child(selected_company)
                            .child(getString(R.string.firebase_company_whitelist));

                    Log.d(TAG, "onClick: path: " +getString(R.string.firebase_Companies) +"/" +selected_company+"/"+ getString(R.string.firebase_company_whitelist));

                    datareference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                         current_whitelist = snapshot.getValue(String.class);

                            Log.d(TAG, "onDataChange: current_whitelist: "+current_whitelist);


                            if (current_whitelist!=null){
                                Log.d(TAG, "onDataChange: not null");
                                FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_whitelist))
                                        .child(selected_company)
                                        .child(getString(R.string.firebase_company_whitelist))
                                        .setValue(current_whitelist +" "+ emails2);

                                Intent intent = new Intent(NewUsersPool.this, AdminActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Log.d(TAG, "onDataChange: data null");
                                FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_whitelist))
                                        .child(selected_company)
                                        .child(getString(R.string.firebase_company_whitelist))
                                        .setValue(emails2);

                                Intent intent = new Intent(NewUsersPool.this, AdminActivity.class);
                                startActivity(intent);
                                finish();
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(NewUsersPool.this, android.R.layout.simple_spinner_dropdown_item, arraylist_companies);
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