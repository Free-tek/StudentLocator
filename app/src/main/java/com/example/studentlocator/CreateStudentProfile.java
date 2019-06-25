package com.example.studentlocator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.security.AccessController.getContext;

public class CreateStudentProfile extends AppCompatActivity {

    private ImageView profile_image, edit, close;
    private EditText userName, phoneNo;
    private Spinner department, city;
    private Button create;

    private String imageEncoded;
    private Bitmap imageBitmap;


    //Initialising firebase
    FirebaseDatabase db;
    DatabaseReference studentProfile, users;
    private FirebaseAuth mAuth;

    private  final  int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    int count, existingCount;
    boolean existing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        //i am poiniting my database refrence to the node on firebase containing students profile
        studentProfile = db.getReference("student_profile");
        users = db.getReference("users");


        studentProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String uid = mAuth.getUid();

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(uid).child("studentProfile").child("count").exists()){
                    existing = true;
                    existingCount = Integer.parseInt(String.valueOf(dataSnapshot.child(uid).child("studentProfile").child("count").getValue()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        initUi();

    }

    private void initUi() {
        profile_image = (ImageView) findViewById(R.id.profile_image);
        edit = (ImageView) findViewById(R.id.edit);
        close = (ImageView) findViewById(R.id.close);

        userName = (EditText) findViewById(R.id.userName);
        phoneNo = (EditText) findViewById(R.id.phoneNo);

        department = (Spinner) findViewById(R.id.department);
        city = (Spinner) findViewById(R.id.city);

        create = (Button)  findViewById(R.id.create);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(CreateStudentProfile.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.city));
        dataAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        city.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(CreateStudentProfile.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.department));
        dataAdapter2.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        department.setAdapter(dataAdapter2);


        profile_image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent, "Select profile picture"), PICK_IMAGE_REQUEST);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent, "Select profile picture"), PICK_IMAGE_REQUEST);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateStudentProfile.this, MainActivity.class);
                startActivity(intent);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String $userName = String.valueOf(userName.getText());
                String $phoneNo = String.valueOf(phoneNo.getText());
                String $department = String.valueOf(department.getSelectedItem());
                String $city = String.valueOf(city.getSelectedItem());


                //base64 encode the profile image to base 64 numbers
                //if you save the images to firebase directly as images since your running a social media app
                //you will be needing large clous storage space so its better to encode the images as numbers, strings
                //since text files are smaller than image files;

                if(imageBitmap != null){
                    try {
                        //call encode image method which automatically takes the bitmap image and stores its encoded
                        //value in the global variable imageEncoded, so its image Encoded that we will be saving to the database
                        encodeBitmapAndSaveToFirebase(imageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                createStudentProfile($userName, $phoneNo, $department, $city, imageEncoded);

                //we are done creating user profile let us display a toast and show the user his student profile
                Toast.makeText(CreateStudentProfile.this, "Student profile created successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CreateStudentProfile.this, StudentProfile.class);
                startActivity(intent);
            }
        });



    }

    private void createStudentProfile(String $userName, String $phoneNo, String $department, String $city, String imageEncoded) {

        //get the current user id of the logged in user
        //so we can save his student profile under his uid node

        final String uid = mAuth.getUid();

        //now we are saving the users data in the student profile node under uid child on the database

        if(!existing){
            studentProfile.child(String.valueOf(count+1)).child("userName").setValue($userName);
            studentProfile.child(String.valueOf(count+1)).child("phoneNo").setValue($phoneNo);
            studentProfile.child(String.valueOf(count+1)).child("department").setValue($department);
            studentProfile.child(String.valueOf(count+1)).child("location").setValue($city);
            studentProfile.child(String.valueOf(count+1)).child("imageEncoded").setValue(imageEncoded);


            users.child(uid).child("studentProfile").child("userName").setValue($userName);
            users.child(uid).child("studentProfile").child("phoneNo").setValue($phoneNo);
            users.child(uid).child("studentProfile").child("department").setValue($department);
            users.child(uid).child("studentProfile").child("location").setValue($city);
            users.child(uid).child("studentProfile").child("imageEncoded").setValue(imageEncoded);
            users.child(uid).child("studentProfile").child("count").setValue(String.valueOf(count+1));

        }else{

            studentProfile.child(String.valueOf(existingCount)).child("userName").setValue($userName);
            studentProfile.child(String.valueOf(existingCount)).child("phoneNo").setValue($phoneNo);
            studentProfile.child(String.valueOf(existingCount)).child("department").setValue($department);
            studentProfile.child(String.valueOf(existingCount)).child("location").setValue($city);
            studentProfile.child(String.valueOf(existingCount)).child("imageEncoded").setValue(imageEncoded);


            users.child(uid).child("studentProfile").child("userName").setValue($userName);
            users.child(uid).child("studentProfile").child("phoneNo").setValue($phoneNo);
            users.child(uid).child("studentProfile").child("department").setValue($department);
            users.child(uid).child("studentProfile").child("location").setValue($city);
            users.child(uid).child("studentProfile").child("imageEncoded").setValue(imageEncoded);
            users.child(uid).child("studentProfile").child("count").setValue(String.valueOf(existingCount));

        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                profile_image.setImageBitmap(bitmap);
                imageBitmap = bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        /*if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            Bundle extras = data.getExtras();
            Bitmap $imageBitmap = (Bitmap) extras.get("data");

            profile_image.setImageBitmap($imageBitmap);
            //getting the image Uri
            Uri imageUri = data.getData();
            Picasso
                    .with(CreateStudentAccount.this)
                    .load(imageUri)
                    .into(profile_image);


        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

}
