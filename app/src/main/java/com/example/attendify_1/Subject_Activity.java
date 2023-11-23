package com.example.attendify_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

interface SubjectsCallback {
    void onSubjectsFetched(ArrayList<String> subjects);
}

public class Subject_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    Subject_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        getSupportActionBar().hide();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        //get department which is selected
        String departmentSelected = Global_Data_Structure.data.get("Department");

        //get year which is selected
        String yearSelected = Global_Data_Structure.data.get("Year");

        //get division which is selected
        String divisionSelected = Global_Data_Structure.data.get("Division");

        getSubjects(email, departmentSelected, yearSelected, divisionSelected, new SubjectsCallback() {
            @Override
            public void onSubjectsFetched(ArrayList<String> subjects) {
                recyclerView = findViewById(R.id.recyclerID);
                recyclerView.setLayoutManager(new LinearLayoutManager(Subject_Activity.this));
                adapter = new Subject_Adapter(Subject_Activity.this, subjects.toArray(new String[0]));
                recyclerView.setAdapter(adapter);
            }
        });
    }
    private void getSubjects(String email, String departmentSelected, String yearSelected, String divisionSelected, final SubjectsCallback callback) {
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
                                            if (divisionId != null && divisionId.equals(divisionSelected)) {
                                                DataSnapshot subjectsSnapshot = divisionSnapshot.child("subjects");

                                                for (DataSnapshot subjectSnapshot : subjectsSnapshot.getChildren()) {
                                                    String subject = subjectSnapshot.getValue(String.class);
                                                    if (subject != null) {
                                                        subjects.add(subject);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Invoke the callback with the subjects
                callback.onSubjectsFetched(subjects);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        };

        query.addListenerForSingleValueEvent(valueEventListener);
    }
}