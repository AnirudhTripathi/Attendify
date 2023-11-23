package com.example.attendify_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
interface DepartmentsCallback {
    void onDepartmentsFetched(ArrayList<String> departmentIds);
}
interface TeacherKeyCallback {
    void onTeacherKeyFetched(String teacherKey);
}

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> deptIds;
    Dept_Adapter adapter;
    TextView logOut;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        String teacherEmail = "teacher1@kitcoek.in";

        getTeacherKeyByEmail(teacherEmail, new TeacherKeyCallback() {
            @Override
            public void onTeacherKeyFetched(String teacherKey) {
                if (teacherKey != null) {
                    // Teacher key fetched successfully
                    // Use the teacherKey variable for further processing
                    System.out.println("teacherKey"+teacherKey);
                    Global_Data_Structure.data.put("teacherId",teacherKey);
                } else {
                    // Teacher key not found or error occurred
                    // Handle the error or condition when teacher key is not available
                }
            }
        });

        getDepartments(email, new DepartmentsCallback() {
            @Override
            public void onDepartmentsFetched(ArrayList<String> departmentIds) {
                // Initialize the RecyclerView and set the adapter
                recyclerView = findViewById(R.id.recyclerID);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                adapter = new Dept_Adapter(MainActivity.this, departmentIds.toArray(new String[0]));
                recyclerView.setAdapter(adapter);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        logOut = findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                // Redirect the user to the login screen or any other desired activity
                Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getDepartments(String email, final DepartmentsCallback callback) {
        DatabaseReference databaseRef = FirebaseManager.getDatabaseReference().child("teachers");
        Query query = databaseRef.orderByChild("email").equalTo(email);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> departmentIds = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot departmentsSnapshot = teacherSnapshot.child("departments");

                        for (DataSnapshot departmentSnapshot : departmentsSnapshot.getChildren()) {
                            String departmentId = departmentSnapshot.child("departmentId").getValue(String.class);
                            if (departmentId != null) {
                                departmentIds.add(departmentId);
                            }
                        }
                    }
                }

                // Invoke the callback with the department IDs
                callback.onDepartmentsFetched(departmentIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                System.out.println("Error: " + databaseError.getMessage());
            }
        };

        query.addListenerForSingleValueEvent(valueEventListener);
    }

    void getTeacherKeyByEmail(String email, TeacherKeyCallback callback) {
        DatabaseReference teachersRef = FirebaseDatabase.getInstance().getReference().child("teachers");

        // Query the "teachers" node to search for the teacher with the specified email
        Query query = teachersRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Iterate through the result set to get the teacher key
                    for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                        String teacherKey = teacherSnapshot.getKey();
                        callback.onTeacherKeyFetched(teacherKey);
                        return; // Exit the loop after finding the first match
                    }
                } else {
                    // Teacher with the specified email not found
                }

                callback.onTeacherKeyFetched(null); // Notify the callback if teacher key is not found
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                callback.onTeacherKeyFetched(null); // Notify the callback about the error
            }
        });
    }

}