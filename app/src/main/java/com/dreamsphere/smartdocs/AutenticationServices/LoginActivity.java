package com.dreamsphere.smartdocs.AutenticationServices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamsphere.smartdocs.Activities.MainActivity;
import com.dreamsphere.smartdocs.R;
import com.dreamsphere.smartdocs.Services.PreferencesData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button login_button;
    TextView forgot_password, registration_new_user;
    EditText login_email, login_password;
    ProgressBar login_progressbar;
    private FirebaseAuth mAuth;
    Context context;

    private String TAG ="LoginActivity: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button = findViewById(R.id.button_new_company);
        forgot_password = findViewById(R.id.forgot_password);


        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_progressbar = findViewById(R.id.login_progressbar);
        registration_new_user = findViewById(R.id.registration_new_user);

        context=this;

        FirebaseApp.initializeApp(LoginActivity.this);

        mAuth = FirebaseAuth.getInstance();

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        login();




        registration_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    private void login() {

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = login_email.getText().toString();
                String password = login_password.getText().toString();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


                if (email.isEmpty()){
                    login_email.setError("email address required to login");
                }else if(password.isEmpty()){
                    login_password.setError("password required to login");
                }else if (password.length() <6){
                    Toast.makeText(LoginActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                }else {
                    login_progressbar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            Log.d(TAG, "onComplete: task? "+task.isSuccessful());
                            Log.d(TAG, "onComplete: user? "+ firebaseUser);
                            if (task.isSuccessful() && (firebaseUser != null )){

                                login_progressbar.setVisibility(View.GONE);
                                PreferencesData.setUserLoggedInStatus(getApplicationContext(),true);

                                Intent intent = new Intent( LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }else {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                                login_progressbar.setVisibility(View.GONE);
                            }
                        }
                    });
                }

            }
        });

    }
}