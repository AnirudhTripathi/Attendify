package com.example.attendify_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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

interface YearsCallback {
    void  onYearsFetched(ArrayList<String> departmentIds);
}

public class Year_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    Year_Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);
        getSupportActionBar().hide();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        //get department which is selected
        String departmentSelected = Global_Data_Structure.data.get("Department");
        System.out.println(departmentSelected+"jjj");
        getYears(email, departmentSelected,new YearsCallback() {
            @Override
            public void onYearsFetched( ArrayList<String> years) {
                recyclerView = findViewById(R.id.recyclerID);
                recyclerView.setLayoutManager(new LinearLayoutManager(Year_Activity.this));
                adapter = new Year_Adapter(Year_Activity.this, years.toArray(new String[0]));
                recyclerView.setAdapter(adapter);
            }
        });

    }

    private void getYears(String email, String departmentSelected, final YearsCallback callback) {
        DatabaseReference databaseRef = FirebaseManager.getDatabaseReference().child("teachers");
        Query query = databaseRef.orderByChild("email").equalTo(email);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> years = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot departmentsSnapshot = teacherSnapshot.child("departments");

                        for (DataSnapshot departmentSnapshot : departmentsSnapshot.getChildren()) {
                            String departmentId = departmentSnapshot.child("departmentId").getValue(String.class);
                            if (departmentId != null && departmentId.equals(departmentSelected)) {
                                DataSnapshot yearsSnapshot = departmentSnapshot.child("years");

                                for (DataSnapshot yearSnapshot : yearsSnapshot.getChildren()) {
                                    String year = yearSnapshot.child("yearId").getValue(String.class);
                                    Toast.makeText(getApplicationContext(),year,Toast.LENGTH_SHORT).show();
                                    if (year != null) {
                                        years.add(year);
                                    }
                                }
                            }
                        }
                    }
                }

                // Invoke the callback with the years
                callback.onYearsFetched(years);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                System.out.println("Error: " + databaseError.getMessage());
            }
        };

        query.addListenerForSingleValueEvent(valueEventListener);
    }


}