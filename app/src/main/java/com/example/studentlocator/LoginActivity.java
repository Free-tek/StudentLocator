package com.example.studentlocator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button login, signUp;
    private TextView forgotPassword;

    //FirebasAuth class is used to handle authentication of user account
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //we dont want to boder the user with having to login every time since this is the default launch activity
        //so using the Authentication class of firebase we can check if the phone has an account logged in on it befor
        // Checking for first time launch - before calling setContentView()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent intent = new Intent(LoginActivity.this, MainActivity.class );
            startActivity(intent);
            finish();
        }


        setContentView(R.layout.activity_login);

        //we initialised out firebase objects above now lets declare them, assign them to a real value
        mAuth = FirebaseAuth.getInstance();


        //method to intialise layout views
        initUi();
    }

    private void initUi() {
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        login = (Button) findViewById(R.id.login);
        signUp = (Button) findViewById(R.id.signUp);

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call sigIn method to handle account signIn
                String $email = String.valueOf(email.getText());
                String $password = String.valueOf(password.getText());
                signIn($email, $password);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

    }

    private void signIn(String $email, String $password) {

        //lets  validate that the user didnot leave the email or password field empty
        if (!validateForm()) {
            return;
        }

        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword($email, $password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Log", "signInWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(intent);
                            progressDialog.dismiss();
                        } else if(!isNetworkAvailable()){
                            //user might have not been able to login because their is no network
                            //lets check if that is the cause and tell him his data is off
                            Toast.makeText(LoginActivity.this, "Please check your internet connection",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }else {
                            // If sign in fails, display a message to the user.
                            Log.w("Tag", "signInWithEmail:failure", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Wrong login details, please try again",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean validateForm() {
        boolean valid = true;

        String $email = email.getText().toString();
        if (TextUtils.isEmpty($email)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String $password = password.getText().toString();
        if (TextUtils.isEmpty($password)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }
}
