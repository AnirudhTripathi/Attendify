package com.example.attendify_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.util.ScopeUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

interface StudentsCallback{
    void onStudentsFetched(ArrayList<String> allNames,ArrayList<String> allRollNos,ArrayList<String> allPRNs,ArrayList<String> status);
}
public class Student_Record extends AppCompatActivity {

    private RecyclerView recyclerViewStudentList;
    private StudentListAdapter studentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_record);
        getSupportActionBar().setTitle("Student Record");

        //get department which is selected
        String departmentSelected = Global_Data_Structure.data.get("Department");

        //get year which is selected
        String yearSelected = Global_Data_Structure.data.get("Year");

        //get division which is selected
        String divisionSelected = Global_Data_Structure.data.get("Division");

        //get subject which is selected
        String subjectSelected = Global_Data_Structure.data.get("Subject");

        String teacherKey = Global_Data_Structure.data.get("teacherId");

        String date = getIntent().getStringExtra("dateSelected");
        String time = getIntent().getStringExtra("timeSelected");

        recyclerViewStudentList = findViewById(R.id.recyclerViewStudentList);

        getStudents(teacherKey,departmentSelected,yearSelected,divisionSelected,subjectSelected,date,time,new StudentsCallback(){

            @Override
            public void onStudentsFetched(ArrayList<String> allNames,ArrayList<String> allRollNos,ArrayList<String> allPRNs,ArrayList<String> status) {
                // Create and set the adapter
                studentListAdapter = new StudentListAdapter(allNames,allRollNos,allPRNs,status);
                recyclerViewStudentList.setAdapter(studentListAdapter);

                // Set the layout manager
                recyclerViewStudentList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });




    }

    private void getStudents(String teacherKey, String departmentSelected, String yearSelected, String divisionSelected,String subjectSelected,String date,String time, final StudentsCallback callback) {


        DatabaseReference databaseRef = FirebaseManager.getDatabaseReference().child("attendance");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> studentsRollNo = new ArrayList<>();
                ArrayList<String> studentPRN  = new ArrayList<>();
                ArrayList<String> studentName  = new ArrayList<>();
                ArrayList<String> status = new ArrayList<>();
                if(dataSnapshot.exists()) {
                    for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                        String teacherId = teacherSnapshot.getKey(); // Get the teacher ID
                        if(teacherId.equals(teacherKey)) {
                            String departmentId = teacherSnapshot.child("departmentId").getValue(String.class); // Get the department ID
                            String yearId = teacherSnapshot.child("yearId").getValue(String.class); // Get the year ID
                            String divisionId = teacherSnapshot.child("divisionId").getValue(String.class); // Get the division ID
                            String subject = teacherSnapshot.child("subject").getValue(String.class);
                            // Check if the teacher matches the selected criteria
                            if (departmentId.equals(departmentSelected) && yearId.equals(yearSelected) && divisionId.equals(divisionSelected) && subject.equals(subjectSelected)) {
                                DataSnapshot lecturesSnapshot = teacherSnapshot.child("lectures");
                                for (DataSnapshot lectureSnapshot : lecturesSnapshot.getChildren()) {
                                    if(lectureSnapshot.child(date).getKey().equals(date)) {
                                        for (DataSnapshot lectureDetailSnapshot : lectureSnapshot.getChildren()) {
                                            // Access the students attendance details
                                            if(lectureDetailSnapshot.child("time").getValue(String.class).equals(time)) {
                                                for (DataSnapshot studentSnapshot : lectureDetailSnapshot.child("students").getChildren()) {
                                                    String studentId = studentSnapshot.getKey(); // Get the student ID
                                                    String rollNo = studentSnapshot.child("rollNo").getValue(String.class); // Get the roll number
                                                    boolean attendanceStatus = studentSnapshot.child("attendanceStatus").getValue(Boolean.class); // Get the attendance status

                                                    if (attendanceStatus == true) {
                                                        status.add("Present");
                                                    } else if (attendanceStatus == false) {
                                                        status.add("Absent");
                                                    }
                                                    DatabaseReference databaseRef = FirebaseManager.getDatabaseReference().child("students").child(studentId);
                                                    databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                String name = dataSnapshot.child("name").getValue(String.class);
                                                                String rollNo = dataSnapshot.child("rollNo").getValue(String.class);
                                                                studentName.add(name);
                                                                studentsRollNo.add(rollNo);
                                                                studentPRN.add(studentId);
                                                                callback.onStudentsFetched(studentName,studentsRollNo,studentPRN,status);
                                                            } else {
                                                                // No students found
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Handle error
                                                        }
                                                    });

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else{
                    System.out.println("Error");
                }
                // Invoke the callback with the subjects
//                callback.onStudentsFetched(studentName,studentsRollNo,studentPRN);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
}
