package com.example.studentlocator;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentlocator.Interface.ItemClickListener;
import com.example.studentlocator.Model.Departments;
import com.example.studentlocator.Model.Student;
import com.example.studentlocator.ViewHolder.DepartmentsViewHolder;
import com.example.studentlocator.ViewHolder.StudentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //Firebase
    FirebaseDatabase database;
    DatabaseReference departmentList, student_profile;
    FirebaseRecyclerAdapter<Departments, DepartmentsViewHolder> adapter ;
    FirebaseRecyclerAdapter<Student, StudentViewHolder> adapterVertical ;

    //View
    RecyclerView recycler_menu, recycler_menu_vertical;
    RecyclerView.LayoutManager layoutManager;
    Bitmap profile_picture;

    String department;

    TextView search;


    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseUser User = mAuth.getCurrentUser();
    static final String userID = User.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise  Firebase
        database = FirebaseDatabase.getInstance();
        departmentList = database.getReference("departments");

        student_profile = database.getReference("student_profile");



        initUi();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.profile:
                        Intent intent = new Intent(MainActivity.this, StudentProfile.class);
                        startActivity(intent);
                        break;
                    case R.id.studentProfile:
                        Intent intents = new Intent(MainActivity.this, CreateStudentProfile.class);
                        startActivity(intents);
                        break;
                }
                return true;
            }
        });

    }


    private void initUi() {

        search = (TextView) findViewById(R.id.search);
        //Initialise View
        recycler_menu = (RecyclerView)findViewById(R.id.horizontal_recyclerview);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();


        //Initialise View
        recycler_menu_vertical = (RecyclerView) findViewById(R.id.vertical_recyclerview);
        RecyclerView.LayoutManager $LayoutManager = new GridLayoutManager(MainActivity.this, 2);
        recycler_menu_vertical.setLayoutManager($LayoutManager);
        recycler_menu_vertical.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recycler_menu_vertical.setItemAnimator(new DefaultItemAnimator());
        recycler_menu_vertical.setNestedScrollingEnabled(false);

        /*loadVerticalMenu();*/

    }


    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Departments, DepartmentsViewHolder>(
                Departments.class,
                R.layout.department_item,
                DepartmentsViewHolder.class,
                departmentList



        ) {
            @Override
            protected void populateViewHolder(final DepartmentsViewHolder viewHolder, final Departments model, int position) {
                viewHolder.department.setText(model.getMdepartment());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        department = String.valueOf(viewHolder.department.getText());
                        search.setVisibility(View.INVISIBLE);
                        loadVerticalMenu();

                    }
                });



            }
        };

        adapter.notifyDataSetChanged(); //Refresh data if changed
        recycler_menu.setAdapter(adapter);


    }


    private void loadVerticalMenu() {
        adapterVertical = new FirebaseRecyclerAdapter<Student, StudentViewHolder>(
                Student.class,
                R.layout.student_profile_item,
                StudentViewHolder.class,
                student_profile.orderByChild("department").equalTo(department)



        ) {
            @Override
            protected void populateViewHolder(final StudentViewHolder viewHolder, final Student model, int position) {

                viewHolder.student_name.setText(model.getUserName());
                viewHolder.student_department.setText(model.getDepartment());
                viewHolder.student_location.setText(model.getLocation());
                String encodedImage = model.getImageEncoded();

                //check if user has uploaded any image at all before decoding it
                if(encodedImage != null){
                    try {
                        Bitmap $decodedImage = decodeFromFirebaseBase64(encodedImage);
                        viewHolder.profile_image.setImageBitmap($decodedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });



            }
        };

        adapterVertical.notifyDataSetChanged(); //Refresh data if changed
        recycler_menu_vertical.setAdapter(adapterVertical);

    }

    private void getImage(String imageEncoded) {
        try {
            decodeFromFirebaseBase64(imageEncoded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Converting dp to pixel
     */

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }


    }


    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {

        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;

    }
}
