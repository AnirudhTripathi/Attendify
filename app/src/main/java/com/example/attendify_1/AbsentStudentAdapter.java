package com.example.attendify_1;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AbsentStudentAdapter extends RecyclerView.Adapter<AbsentStudentAdapter.ViewHolder> {

    private List<Integer> absentStudents;

    public AbsentStudentAdapter(List<Integer> absentStudents) {
        this.absentStudents = absentStudents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int student = absentStudents.get(position);
        holder.buttonAttendance.setText(student+"");
        // Customize the UI as needed for absent students
        holder.itemView.setBackgroundColor(Color.RED);
    }

    @Override
    public int getItemCount() {
        return absentStudents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton buttonAttendance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonAttendance = itemView.findViewById(R.id.buttonAttendance);
        }
    }
}
