package com.example.attendify_1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AttendanceButtonAdapter extends RecyclerView.Adapter<AttendanceButtonAdapter.ViewHolder> {

    private int totalStudents;
    private ArrayList<Integer> redMarkedList ;
    private  ArrayList<Integer> greenMarkedList ;
    Context context;

    public AttendanceButtonAdapter(int totalStudents, Context context, ArrayList<Integer> r,ArrayList<Integer> b) {
        this.totalStudents = totalStudents;
        this.context = context;
        this.greenMarkedList = b;
        this.redMarkedList = r;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        position++;
        holder.buttonAttendance.setText(String.valueOf(position));

        // Set the button's initial color based on the redMarkedList
        if (redMarkedList.contains(position)) {
            // Button should be red
            holder.buttonAttendance.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.red));
            holder.buttonAttendance.setTag("red");
        } else {
            // Button should be green
            holder.buttonAttendance.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
            holder.buttonAttendance.setTag("green");
        }

        // Set click listener for the button
        int finalPosition = position;
        holder.buttonAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle the button color
                toggleButtonColor(holder.buttonAttendance);

                // Store or update the list of red-marked numbers
                updateMarkedList(finalPosition);
            }
        });
    }


    private void toggleButtonColor(MaterialButton button) {
        int redColor = ContextCompat.getColor(context, R.color.red);
        int greenColor = ContextCompat.getColor(context, R.color.green);

        if (button.getBackgroundTintList().getDefaultColor() == redColor) {
            button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
            button.setTag("green");
        } else {
            button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.red));
            button.setTag("red");
        }
    }

    // Method to update the list of marked numbers
    private void updateMarkedList(int position) {
        if (redMarkedList.contains(position)) {
            // Number is already in the list, remove it
            redMarkedList.remove((Integer) position);
            greenMarkedList.add(position);
        } else {
            // Number is not in the list, add it
            redMarkedList.add(position);
            greenMarkedList.remove((Integer) position);
        }
    }


    @Override
    public int getItemCount() {
        return totalStudents;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton buttonAttendance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonAttendance = itemView.findViewById(R.id.buttonAttendance);
        }
    }
}
