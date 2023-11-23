package com.example.attendify_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

interface LecturesCallback {
    void  onLecturesFetched(ArrayList<String> date, ArrayList<String> time,ArrayList<String> studentsRollNo,ArrayList<String> status,ArrayList<Integer> absents);
}

public class AttendanceList_Activity extends AppCompatActivity implements custom_dialogue_add_attendance.DialogCallback {

    RecyclerView recyclerView;
    AttendanceList_Adapter adapter;

    ArrayList<Integer> absentStudents;
    String teacherKey = null;
    int noOfLectures = 0;
    ArrayList<String> date;
    ArrayList<String> time;
    ArrayList<Integer> marked;
    private FloatingActionButton showDialogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_list);

        date = new ArrayList<>();
        time = new ArrayList<>();
        marked = new ArrayList<>();
        absentStudents = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        showDialogButton = findViewById(R.id.add_attendance);

        //get department which is selected
        String departmentSelected = Global_Data_Structure.data.get("Department");

        //get year which is selected
        String yearSelected = Global_Data_Structure.data.get("Year");

        //get division which is selected
        String divisionSelected = Global_Data_Structure.data.get("Division");

        //get subject which is selected
        String subjectSelected = Global_Data_Structure.data.get("Subject");

        getLectures(email, departmentSelected, yearSelected, divisionSelected,subjectSelected, new LecturesCallback() {
            @Override
            public void onLecturesFetched(ArrayList<String> date,ArrayList<String> time,ArrayList<String> studentsRollNo,ArrayList<String> status,ArrayList<Integer> absents) {
                ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle(subjectSelected+" Attendance List");

                for(int i=0;i<noOfLectures;i++)
                {
                    marked.add(1);
                }
                recyclerView = findViewById(R.id.recyclerID);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new AttendanceList_Adapter(getApplicationContext(),date.toArray(new String[0]),time.toArray(new String[0]),studentsRollNo.toArray(new String[0]),status.toArray(new String[0]),absents,marked);
                recyclerView.setAdapter(adapter);

            }
        });



        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });
    }
    private void showCustomDialog() {
        // Call the showDialog() method from CustomDialog class
        custom_dialogue_add_attendance.showDialog(this, this);
    }

    // Implement the onDateTimeSelected() method from DialogCallback interface
    @Override
    public void onDateTimeSelected(String selectedDate, String selectedTime) {
        // Handle the selected date and time here
        int mark = 0;
        if(this.date.contains(selectedDate) && this.time.contains(selectedTime))
        {
            Toast.makeText(getApplicationContext(),"Lecture Already Exists !",Toast.LENGTH_SHORT).show();
            return;
        }
        addItem(selectedDate,selectedTime,0,mark);
        Global_Data_Structure.data.put("dateSelected",selectedDate);
        Global_Data_Structure.data.put("timeSelected",selectedTime);
        // Add the selected date, time, and absent students to the RecyclerView
        adapter.addItem(selectedDate, selectedTime, 0, mark);

    }

    public void addItem(String date, String time, int absentStudents, int mark) {
        // Add the new date, time, and absent students to the existing arrays

        this.date.add(date);
        this.time.add(time);
        this.marked.add(mark);
        this.absentStudents.add(absentStudents);


        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();
    }

    private void getLectures(String email, String departmentSelected, String yearSelected, String divisionSelected,String subjectSelected, final LecturesCallback callback) {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("teachers");
        Query query = dbRef.orderByChild("email").equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    teacherKey = snapshot.getKey(); // Get the key of the teacher with the matching email
                    break; // Assuming there is only one teacher with the given email, exit the loop
                }

                if (teacherKey != null) {
                    // Do something with the teacher key
                    // For example, print it to the console
                } else {
                    // No teacher found with the given email
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });


        DatabaseReference databaseRef = FirebaseManager.getDatabaseReference().child("attendance");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> studentsRollNo = new ArrayList<>();
                ArrayList<String> status  = new ArrayList<>();
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
                                    String lectureDate = lectureSnapshot.getKey(); // Get the lecture date
                                    for (DataSnapshot lectureDetailSnapshot : lectureSnapshot.getChildren()) {
                                        String timeOfLecture = lectureDetailSnapshot.child("time").getValue(String.class); // Get the start time
                                        date.add(lectureDate);
                                        time.add(timeOfLecture);
                                        noOfLectures++;
                                        // Access the students attendance details
                                        int cnt = 0;
                                        for (DataSnapshot studentSnapshot : lectureDetailSnapshot.child("students").getChildren()) {
                                            String studentId = studentSnapshot.getKey(); // Get the student ID
                                            String rollNo = studentSnapshot.child("rollNo").getValue(String.class); // Get the roll number
                                            boolean attendanceStatus = studentSnapshot.child("attendanceStatus").getValue(Boolean.class); // Get the attendance status
                                            studentsRollNo.add(rollNo);
                                            if (attendanceStatus == true) {
                                                status.add("Present");
                                            } else if (attendanceStatus == false) {
                                                status.add("Absent");
                                                cnt++;
                                            }
                                            Global_Data_Structure.allStudents.put(studentId,rollNo);
                                        }
                                        absentStudents.add(cnt);
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
                callback.onLecturesFetched(date,time,studentsRollNo,status,absentStudents);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
}