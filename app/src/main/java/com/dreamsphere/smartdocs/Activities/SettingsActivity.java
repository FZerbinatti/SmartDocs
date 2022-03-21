package com.dreamsphere.smartdocs.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.dreamsphere.smartdocs.AutenticationServices.LoginActivity;
import com.dreamsphere.smartdocs.R;
import com.dreamsphere.smartdocs.Services.PreferencesData;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    ImageButton button_logout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        firebaseAuth = FirebaseAuth.getInstance();

        button_logout = findViewById(R.id.button_logout);


        logout();
    }

    private void logout() {
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
                alertDialog.setTitle("Logout");
                alertDialog.setMessage("Do you want to Logout?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                PreferencesData.setUserLoggedInStatus(getApplicationContext(),false);
                                firebaseAuth.signOut();
                                startActivity(intent);
                                finish();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();


            }
        });
    }
}