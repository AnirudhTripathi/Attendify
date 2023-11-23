package com.example.attendify_1;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;
public class PresentStudentAdapter extends RecyclerView.Adapter<PresentStudentAdapter.ViewHolder> {

    private List<Integer> presentStudents;

    public PresentStudentAdapter(List<Integer> presentStudents) {
        this.presentStudents = presentStudents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int student = presentStudents.get(position);
        holder.buttonAttendance.setText(student+"");
        // Customize the UI as needed for present students
        holder.itemView.setBackgroundColor(Color.GREEN);
    }

    @Override
    public int getItemCount() {
        return presentStudents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton buttonAttendance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonAttendance = itemView.findViewById(R.id.buttonAttendance);
        }
    }
}
