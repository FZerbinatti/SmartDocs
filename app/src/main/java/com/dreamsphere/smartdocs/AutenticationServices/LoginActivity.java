package com.dreamsphere.smartdocs.AutenticationServices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.storage.StorageReference;

public class LoginActivity extends AppCompatActivity {

    Button login_button;
    TextView go_to_admin, forgot_password;
    EditText login_email, login_password;
    ProgressBar login_progressbar;
    private FirebaseAuth mAuth;
    private String TAG ="LoginActivity: ";
    TextView personalized_dialog_description, personalized_dialog_percentage1, personalized_dialog_percentage2, personalized_dialog_percentage3;
    ProgressBar personalized_dialog_progressbar1, personalized_dialog_progressbar2, personalized_dialog_progressbar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button = findViewById(R.id.button_login);
        go_to_admin = findViewById(R.id.go_to_admin);
        forgot_password = findViewById(R.id.forgot_password);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_progressbar = findViewById(R.id.login_progressbar);
        FirebaseApp.initializeApp(LoginActivity.this);

        mAuth = FirebaseAuth.getInstance();

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        login();


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