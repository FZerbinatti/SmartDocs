package com.dreamsphere.smartdocs.AutenticationServices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamsphere.smartdocs.MainActivity;
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
    TextView go_to_admin, forgot_password, registration_new_user;
    EditText login_email, login_password;
    ProgressBar login_progressbar;
    private FirebaseAuth mAuth;
    Integer counter;
    private String TAG ="LoginActivity: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button = findViewById(R.id.button_new_company);
        forgot_password = findViewById(R.id.forgot_password);

        go_to_admin=findViewById(R.id.go_to_admin);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_progressbar = findViewById(R.id.login_progressbar);
        registration_new_user = findViewById(R.id.registration_new_user);

        FirebaseApp.initializeApp(LoginActivity.this);
        counter =0;
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

        go_to_admin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onTouch: touched");
                counter++;
                    counter++;
                    Log.d(TAG, "onTouch: counter: "+counter);
                    if (counter==10){
                        Log.d(TAG, "onTouch: counter 3");
                        Intent intent = new Intent(LoginActivity.this, AdminAccessActivity.class);
                        startActivity(intent);
                    }
                return false;
            }
        });

/*        go_to_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

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
                                PreferencesData.setUserLoggedInStatus(getApplicationContext(),true);
                                startActivity(intent);

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