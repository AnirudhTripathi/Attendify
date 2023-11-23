package com.example.attendify_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

interface DivisionCallback {
    void onDivisionsFetched(ArrayList<String> subjects);
}

public class Division_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    Division_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_division);
        getSupportActionBar().hide();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        //get department which is selected
        String departmentSelected = Global_Data_Structure.data.get("Department");

        //get year which is selected
        String yearSelected = Global_Data_Structure.data.get("Year");

        getSubjects(email, departmentSelected, yearSelected, new DivisionCallback() {
            @Override
            public void onDivisionsFetched(ArrayList<String> subjects) {
                recyclerView = findViewById(R.id.recyclerID);
                recyclerView.setLayoutManager(new LinearLayoutManager(Division_Activity.this));
                adapter = new Division_Adapter(Division_Activity.this, subjects.toArray(new String[0]));
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void getSubjects(String email, String departmentSelected, String yearSelected, final DivisionCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("teachers");
        Query query = databaseRef.orderByChild("email").equalTo(email);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> subjects = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot departmentsSnapshot = teacherSnapshot.child("departments");

                        for (DataSnapshot departmentSnapshot : departmentsSnapshot.getChildren()) {
                            String departmentId = departmentSnapshot.child("departmentId").getValue(String.class);
                            if (departmentId != null && departmentId.equals(departmentSelected)) {
                                DataSnapshot yearsSnapshot = departmentSnapshot.child("years");

                                for (DataSnapshot yearSnapshot : yearsSnapshot.getChildren()) {
                                    String yearId = yearSnapshot.child("yearId").getValue(String.class);
                                    if (yearId != null && yearId.equals(yearSelected)) {
                                        DataSnapshot divisionsSnapshot = yearSnapshot.child("division");

                                        for (DataSnapshot divisionSnapshot : divisionsSnapshot.getChildren()) {
                                            String divisionId = divisionSnapshot.child("divisionId").getValue(String.class);
                                            if (divisionId != null) {
                                                subjects.add(divisionId);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Invoke the callback with the subjects
                callback.onDivisionsFetched(subjects);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        };

        query.addListenerForSingleValueEvent(valueEventListener);
    }
}