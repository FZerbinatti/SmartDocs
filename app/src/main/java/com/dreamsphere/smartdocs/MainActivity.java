package com.dreamsphere.smartdocs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dreamsphere.smartdocs.AutenticationServices.LoginActivity;
import com.dreamsphere.smartdocs.Services.PreferencesData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    public static final String TAG ="Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isUserAlreadyLogged();



    }

    private void isUserAlreadyLogged() {

         PreferencesData.setUserLoggedInStatus(getApplicationContext(),false);
        Log.d(TAG, "isUserAlreadyLogged: "+ PreferencesData.getUserLoggedInStatus(this));
        if ( !PreferencesData.getUserLoggedInStatus(this) ||   FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())  ==null ) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);

            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}