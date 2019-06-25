package com.example.studentlocator;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private ImageView close;
    private EditText email, name, phoneNo, password;
    private Spinner city;
    private Button signUp;
    private RadioGroup radioGroup;
    private RadioButton male, female;
    private String mGender;

    //Initialising firebase objects

    //FirebasAuth class is used to handle authentication of user account
    private FirebaseAuth mAuth;

    //FirebasDatabase class is used to access your firebase database, the realtime database
    FirebaseDatabase db;

    //DatabaseReference is used to access the particular node on your database your refering to
    // for instance you can have a database that contains several user account details say staff seperate from app user
    // so in order to point the database reference object to the right place say if were concerned about the app user not staff you need a database reference
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //we initialised out firebase objects above now lets declare them, assign them to a real value
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        //now the code below means in my database ( variable db ) i want userID variable to hold reference to the node named "userId"
        // here is where i have decided to store all of my user data
        users = db.getReference("users");

        //method to intialise layout views
        initUi();
    }

    private void initUi() {
        close = (ImageView) findViewById(R.id.close);

        email = (EditText) findViewById(R.id.email);
        name = (EditText) findViewById(R.id.name);
        phoneNo = (EditText) findViewById(R.id.phoneNo);
        password = (EditText) findViewById(R.id.password);

        city = (Spinner) findViewById(R.id.city);

        signUp = (Button) findViewById(R.id.signUp);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);



        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(SignUpActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.city));
        dataAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        city.setAdapter(dataAdapter);

        city.setOnItemSelectedListener(listener);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate form is a method that checks if the user did not leave any field empty
                if(validateForm()){
                    //user has filled all the fields in the form

                    String $email = String.valueOf(email.getText());
                    String $name = String.valueOf(name.getText());
                    String $phoneNo = String.valueOf(phoneNo.getText());
                    String $password = String.valueOf(password.getText());
                    String $gender = getSex();
                    String $location = String.valueOf(city.getSelectedItem());

                    //call create account method and pass all user data
                    createAccount($email, $name, $phoneNo, $password, $gender, $location);
                }else{
                    //user has left some fields unfilled
                    Toast.makeText(SignUpActivity.this, "Please check the error in the form", Toast.LENGTH_SHORT ).show();


                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.grey));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void createAccount(final String $email, final String $name, final String $phoneNo, final String $password, final String $gender, final String $location) {

        //create an intent to pass user to the main activity when we are done creating his account but dont start activity yet
        final Intent intent = new Intent(SignUpActivity.this, MainActivity.class);

        //progress dialog to display loading when we are creating the account
        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        //mAuth has a createUserWithEmailAndPassword method and what the method does is create a user with the email and password argument passed to it
        mAuth.createUserWithEmailAndPassword($email, $password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // If the task of creating the users account was successful lets do whats below
                            Log.d("Log", "createUserWithEmail:success");
                            //now the users account has been created and we can now make reference to him by using the line of code below
                            FirebaseUser user = mAuth.getCurrentUser();

                            //lets save the users data, his email, password, phoneno, location etc to our database
                            // so we use the saveData method below
                            saveData($name, $email, $phoneNo, $password, $gender, $location);

                            //sendEmailVerification method sends a message to the users email telling him we have successfully created aan account for him on this app 
                            sendEmailVerification();
                            progressDialog.dismiss();

                            //now we have sucessfully created hi email account lets send him to the main activty
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Log", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Sign up failed, please check your network and try again",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Log", "sendEmailVerification", task.getException());
                            Toast.makeText(SignUpActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void saveData(String $name, String $email, String $phoneNo, String $password, String $gender, String $location) {
        //firebase gives every user a unique user id, since we have successfully created an account for this user he now has a userid
        //so the method below gets us the userid of this current user
        //ill like to save the data for this person as a child under a node that carries his userId
        //so every user will have his data saved as children under his userId
        final String uid = mAuth.getCurrentUser().getUid();

        //here since users variable already has a database reference pinting to the node called "users" in my database
        //ill also like it to have a chile called uid, which is the userId of the current user as  in code line 186
        //then ill go ahead and save each of this users data as a child under his uid.
        users = users.child(uid);

        //the stings in the quotes just shows the name i want to name the child 
        // and in the setValue method ill be passing the value i want to save to that child 
        users.child("userName").setValue($name);
        users.child("email").setValue($email);
        users.child("phone").setValue($phoneNo);
        users.child("password").setValue($password);
        users.child("gender").setValue($gender);
        users.child("location").setValue($location);
        
        

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
        String $name = name.getText().toString();
        if (TextUtils.isEmpty($name)) {
            name.setError("Required.");
            valid = false;
        } else {
            name.setError(null);
        }
        String $phoneNo = phoneNo.getText().toString();
        if (TextUtils.isEmpty($phoneNo)) {
            phoneNo.setError("Required.");
            valid = false;
        } else {
            phoneNo.setError(null);
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

    //this method gets the selected gender
    private String getSex() {
        if (female.isChecked()) {
            mGender = "female";
        } else if (male.isChecked()) {
            mGender = "male";
        }
        return mGender;
    }
}
