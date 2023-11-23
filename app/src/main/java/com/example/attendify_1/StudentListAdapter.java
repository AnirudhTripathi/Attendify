package com.example.attendify_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {
    ArrayList<String> name;
    ArrayList<String> roll;
    ArrayList<String> prn;
    ArrayList<String> status;


    public StudentListAdapter(ArrayList<String> name, ArrayList<String> roll, ArrayList<String> prn,ArrayList<String> status) {
        this.name = name;
        this.status = status;
        this.prn = prn;
        this.roll = roll;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewRollNumber.setText(roll.get(position));
        holder.textViewName.setText(name.get(position));
        holder.textViewUniqueId.setText(prn.get(position));
        holder.statusId.setText(status.get(position));
    }

    @Override
    public int getItemCount() {
        return roll.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRollNumber;
        TextView textViewName;
        TextView textViewUniqueId;
        TextView statusId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRollNumber = itemView.findViewById(R.id.textViewRollNumber);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewUniqueId = itemView.findViewById(R.id.textViewUniqueId);
            statusId = itemView.findViewById(R.id.status);
        }
    }
}
