package com.example.attendify_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

interface  AttendanceTakingCallBack{
    void onAttendanceFetched();
}
public class Attendance_Taking extends AppCompatActivity {

    private RecyclerView recyclerViewAttendance;
    private AttendanceButtonAdapter adapter;
    private ArrayList<Integer> redMarkedList = new ArrayList<>();
    private ArrayList<Integer> greenMarkedList = new ArrayList<>();
    private Toolbar toolbar;
    private int totalStudents; // Set the total number of students

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_taking);
        getSupportActionBar().setTitle("Mark Attendance");
        totalStudents = 0;


        //get department which is selected
        String departmentSelected = Global_Data_Structure.data.get("Department");

        //get year which is selected
        String yearSelected = Global_Data_Structure.data.get("Year");

        //get division which is selected
        String divisionSelected = Global_Data_Structure.data.get("Division");

        //get subject which is selected
        String subjectSelected = Global_Data_Structure.data.get("Subject");

        getCountOfStudents(departmentSelected, yearSelected, divisionSelected,new AttendanceTakingCallBack(){

            @Override
            public void onAttendanceFetched() {
                System.out.println(totalStudents+"hii");
                recyclerViewAttendance = findViewById(R.id.recyclerViewAttendance);
                recyclerViewAttendance.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4)); // Set the number of columns

                for (int i = 0; i < totalStudents; i++) {
                    greenMarkedList.add(i + 1);
                }
                // Create and set the adapter
                adapter = new AttendanceButtonAdapter(totalStudents, getApplicationContext(), redMarkedList, greenMarkedList);
                recyclerViewAttendance.setAdapter(adapter);
            }
        });


        FloatingActionButton button = findViewById(R.id.done_attendance);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Final_Attedance_Summary.class);
                intent.putIntegerArrayListExtra("absentStudents", new ArrayList<>(redMarkedList));
                intent.putIntegerArrayListExtra("presentStudents", new ArrayList<>(greenMarkedList));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_switch);
        Switch switchButton = item.getActionView().findViewById(R.id.switchButton);

        switchButton.setChecked(true);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle switch state change
                if (isChecked) {
                    // Switch is on
                    redMarkedList.clear();
                    greenMarkedList.clear();
                    for (int i = 0; i < totalStudents; i++) {
                        greenMarkedList.add(i + 1);
                    }
                } else {
                    // Switch is off
                    redMarkedList.clear();
                    greenMarkedList.clear();
                    for (int i = 0; i < totalStudents; i++) {
                        redMarkedList.add(i + 1);
                    }
                }
                // Notify the adapter of the data change
                adapter.notifyDataSetChanged();
            }
        });

        return true;
    }

    private void getCountOfStudents(String department, String year, String division, AttendanceTakingCallBack attendanceTakingCallBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference studentsRef = database.getReference("students");
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String studentId = studentSnapshot.getKey();
                    DataSnapshot depsnap = studentSnapshot.child("department");
                    String dep = depsnap.getValue(String.class);
                    DataSnapshot divsnap = studentSnapshot.child("division");
                    String div = divsnap.getValue(String.class);
                    DataSnapshot yrsnap = studentSnapshot.child("year");
                    String yr = yrsnap.getValue(String.class);
                    String rollNo = studentSnapshot.child("rollNo").getValue(String.class);
                    String name = studentSnapshot.child("name").getValue(String.class);
                    if(dep.equals(department) && div.equals(division) && yr.equals(year)){
                        totalStudents++;
                    }

                }
                attendanceTakingCallBack.onAttendanceFetched();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }
}