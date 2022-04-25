package com.dreamsphere.smartdocs.AdminActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dreamsphere.smartdocs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminAccessActivity extends AppCompatActivity {

    public static final String TAG ="AdminActivity";

    Button button_admin_login;
    EditText edittext_enter_admin_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_access);

        button_admin_login = findViewById(R.id.button_admin_login);
        edittext_enter_admin_password = findViewById(R.id.edittext_enter_admin_password);

        button_admin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: clicked");

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.firebase_user_code));

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals("uRkhFRICIvYMH34kphI8y8mEzeh1")&&edittext_enter_admin_password.getText().toString().equals(snapshot.getValue(String.class))){
                            Toast.makeText(AdminAccessActivity.this, "Access Granted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminAccessActivity.this, AdminActivity.class);
                            startActivity(intent);


                        }else {
                            Toast.makeText(AdminAccessActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //if password entered is equal to firebase->user->company code then login

            }
        });
    }
}