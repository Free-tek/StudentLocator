package com.example.studentlocator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class StudentProfile extends AppCompatActivity {

    private TextView userName, location, department, phoneNo, updateText;
    private ImageView profile_image, icon1, icon2, icon3, icon4, update, edit;

    //Initialising firebase
    FirebaseDatabase db;
    DatabaseReference users;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        //i am poiniting my database refrence to the node on firebase containing students profile
        users = db.getReference("users");


        initUi();

    }

    private void initUi() {
        userName = (TextView) findViewById(R.id.username);
        department = (TextView) findViewById(R.id.department);
        location = (TextView) findViewById(R.id.location);
        phoneNo = (TextView) findViewById(R.id.phoneNo);
        updateText = (TextView) findViewById(R.id.update_text);

        profile_image = (ImageView) findViewById(R.id.profile_image);
        icon1 = (ImageView) findViewById(R.id.icon1);
        icon2 = (ImageView) findViewById(R.id.icon2);
        icon3 = (ImageView) findViewById(R.id.icon3);
        icon4 = (ImageView) findViewById(R.id.icon4);
        update = (ImageView) findViewById(R.id.update);
        edit = (ImageView) findViewById(R.id.edit);


        update.setVisibility(View.INVISIBLE);
        updateText.setVisibility(View.INVISIBLE);


        //check if user has a student profile first
        //else lets tell him to upload one

        //get the usersID first
        final String uid = mAuth.getCurrentUser().getUid();
        //check if the users student profile (variable studentProfile) i.e the users data is available in the database under user node of student profile child
        users.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("studentProfile").exists()){
                    //okay the users data exist lets set the visibility of the upload profile off


                   ///now lets set the users username department and profile picture
                    String $userName = String.valueOf(dataSnapshot.child("studentProfile").child("userName").getValue());
                    String $department = String.valueOf(dataSnapshot.child("studentProfile").child("department").getValue());
                    String $location = String.valueOf(dataSnapshot.child("studentProfile").child("location").getValue());
                    String $phoneNo = String.valueOf(dataSnapshot.child("studentProfile").child("phoneNo").getValue());
                    String $encodedImage = String.valueOf(dataSnapshot.child("studentProfile").child("imageEncoded").getValue());

                    userName.setText($userName);
                    department.setText($department);
                    location.setText($location);
                    phoneNo.setText($phoneNo);

                    //decode the encoded image and set the profile picture image view
                    if($encodedImage.length() >= 10){
                        try {
                            Bitmap $decodedImage = decodeFromFirebaseBase64($encodedImage);
                            profile_image.setImageBitmap($decodedImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }




                }else{
                    //the user does not have a student profile lets tell him to create one
                    userName.setVisibility(View.INVISIBLE);
                    department.setVisibility(View.INVISIBLE);
                    location.setVisibility(View.INVISIBLE);
                    phoneNo.setVisibility(View.INVISIBLE);
                    profile_image.setVisibility(View.INVISIBLE);
                    icon1.setVisibility(View.INVISIBLE);
                    icon2.setVisibility(View.INVISIBLE);
                    icon3.setVisibility(View.INVISIBLE);
                    icon4.setVisibility(View.INVISIBLE);
                    edit.setVisibility(View.INVISIBLE);

                    update.setVisibility(View.VISIBLE);
                    updateText.setVisibility(View.VISIBLE);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentProfile.this, CreateStudentProfile.class);
                startActivity(intent);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentProfile.this, CreateStudentProfile.class);
                startActivity(intent);
            }
        });
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {

        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;

    }
}
