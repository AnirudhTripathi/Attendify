package com.example.attendify_1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class Subject_Adapter extends RecyclerView.Adapter<Subject_Adapter.SubViewHolder> {

    Context context;
    String []list;

    public Subject_Adapter(Context context, String[] list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item,parent,false);
        return new SubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.deptName.setText(list[position]);
        holder.deptName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "clicked "+list[position], Toast.LENGTH_SHORT).show();
                HashMap<String,String> data = Global_Data_Structure.getData();
                data.put("Subject",list[position]);
                Intent intent = new Intent(context.getApplicationContext(),AttendanceList_Activity.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public static class SubViewHolder extends RecyclerView.ViewHolder{
        TextView deptName;
        public SubViewHolder(@NonNull View itemView) {
            super(itemView);
            deptName = itemView.findViewById(R.id.dept_name);
        }
    }
}
