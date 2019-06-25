package com.example.studentlocator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPassword extends AppCompatActivity {

    private ImageView close;
    private EditText  email;
    private Button recover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //method to intialise layout views
        initUi();
    }

    private void initUi() {
        close = (ImageView) findViewById(R.id.close);

        email = (EditText) findViewById(R.id.email);
        recover = (Button) findViewById(R.id.recover);



        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String $email = String.valueOf(email.getText());

                if(!$email.isEmpty()){
                    //the method below is firebaseAuth class method for sending an link with a recovery password email to the user.
                    FirebaseAuth.getInstance().sendPasswordResetEmail($email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Email Status", "Email sent.");
                                        Toast.makeText(ForgotPassword.this, "Email sent successfully!", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(ForgotPassword.this, "Invalid email!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else{
                    Toast.makeText(ForgotPassword.this, "Enter email address", Toast.LENGTH_SHORT).show();
                }

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }



}
