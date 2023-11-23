package com.example.attendify_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

interface TeacherIdCallBack{
    void onTeacherIdFetched(String teacherKey);
}
public class Final_Attedance_Summary extends AppCompatActivity {

    private RecyclerView recyclerViewAbsentStudents;
    private RecyclerView recyclerViewPresentStudents;

    private AbsentStudentAdapter absentStudentAdapter;
    private PresentStudentAdapter presentStudentAdapter;

    private ArrayList<Integer> absentStudents;
    private ArrayList<Integer> presentStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_attedance_summary);
        getSupportActionBar().setTitle("Final Summary");

        // Get the current useraxc
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        //get department which is selected
        String departmentSelected = Global_Data_Structure.data.get("Department");

        //get year which is selected
        String yearSelected = Global_Data_Structure.data.get("Year");

        //get division which is selected
        String divisionSelected = Global_Data_Structure.data.get("Division");

        //get subject which is selected
        String subjectSelected = Global_Data_Structure.data.get("Subject");

        String dateSelected = Global_Data_Structure.data.get("dateSelected");
        String timeSelected = Global_Data_Structure.data.get("timeSelected");


        recyclerViewAbsentStudents = findViewById(R.id.recyclerViewAbsentStudents);
        recyclerViewPresentStudents = findViewById(R.id.recyclerViewPresentStudents);

        // Retrieve the lists of absent and present students from the previous activity
        absentStudents = getIntent().getIntegerArrayListExtra("absentStudents");
        presentStudents = getIntent().getIntegerArrayListExtra("presentStudents");

        // Initialize and set up the RecyclerView adapters
        absentStudentAdapter = new AbsentStudentAdapter(absentStudents);
        recyclerViewAbsentStudents.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        recyclerViewAbsentStudents.setAdapter(absentStudentAdapter);

        presentStudentAdapter = new PresentStudentAdapter(presentStudents);
        recyclerViewPresentStudents.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        recyclerViewPresentStudents.setAdapter(presentStudentAdapter);

        FloatingActionButton btn = findViewById(R.id.upload);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Final_Attedance_Summary.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                insertAttendance(email,departmentSelected,yearSelected,divisionSelected,subjectSelected,dateSelected,timeSelected);
                startActivity(intent);
                finish();
            }
        });
    }

    void insertAttendance(String email,String departmentId,String yearId,
                          String divisionId,String subject,String date,String time){


        // Assuming you have a DatabaseReference object for your Firebase database
        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child("attendance");

        String teacherId = Global_Data_Structure.data.get("teacherId");

// Check if the teacher with the specified ID exists and if conditions are met
        attendanceRef.child(teacherId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Teacher exists, check conditions
                    DataSnapshot teacherSnapshot = dataSnapshot;

                    String teacherDepartmentId = teacherSnapshot.child("departmentId").getValue(String.class);
                    String teacherDivisionId = teacherSnapshot.child("divisionId").getValue(String.class);
                    String teacherYearId = teacherSnapshot.child("yearId").getValue(String.class);
                    String teacherSubject = teacherSnapshot.child("subject").getValue(String.class);

                    if (teacherDepartmentId.equals(departmentId) && teacherDivisionId.equals(divisionId)
                            && teacherYearId.equals(yearId) && teacherSubject.equals(subject)) {

                        DataSnapshot lectureSnapshot = teacherSnapshot.child("lectures");
                        //Map<String, Object> studentsObject = new HashMap<>();
                        Map<String, Object> prnObject = new HashMap<>();
                        if(!lectureSnapshot.hasChild(date)){
                            DatabaseReference lecturesRef = attendanceRef.child(teacherId).child("lectures").child(date);

                            String lectureKey = lecturesRef.push().getKey();
                            for (Map.Entry<String, String> entry : Global_Data_Structure.allStudents.entrySet()) {
                                String prn = entry.getKey();
                                String rollNo = entry.getValue();
                                Student std;
                                if(absentStudents.contains(Integer.parseInt(rollNo))){
                                    System.out.println("In absent");
                                    std = new Student(rollNo, false);
                                } else {
                                    System.out.println("In present");
                                    std = new Student(rollNo, true);
                                }
                                prnObject.put(prn,std);
                            }
                        }
                        else{
                            DatabaseReference lecturesRef = attendanceRef.child(teacherId).child("lectures");
                            String lectureKey = date;
                            for (Map.Entry<String, String> entry : Global_Data_Structure.allStudents.entrySet()) {
                                String prn = entry.getKey();
                                String rollNo = entry.getValue();
                                Student std;
                                if(absentStudents.contains(Integer.parseInt(rollNo))){
                                    std = new Student(rollNo, false);
                                } else {
                                    std = new Student(rollNo, true);
                                }
                                prnObject.put(prn,std);
                            }
                        }
                        // Conditions met, add a new object to the "lectures" node under the specified date
                        Map<String, Object> lectureObject = new HashMap<>();
                        lectureObject.put("time", time);
                        System.out.println("time" + time);
                        lectureObject.put("students",prnObject);

                        attendanceRef.child(teacherId).child("lectures").child(date).push().setValue(lectureObject);
                    } else {
                        // Conditions not met
                        // Add your logic here
                    }
                } else {
                    // Teacher doesn't exist, add a new teacher object
                    Map<String, Object> teacherObject = new HashMap<>();
                    teacherObject.put("departmentId", departmentId);
                    teacherObject.put("divisionId", divisionId);
                    teacherObject.put("yearId", yearId);
                    teacherObject.put("subject", subject);

                   // attendanceRef.child(teacherId).setValue(teacherObject);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    }

}