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

public class Division_Adapter extends RecyclerView.Adapter<Division_Adapter.DivViewHolder> {

    Context context;
    String []list;

    public Division_Adapter(Context context, String[] list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DivViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item,parent,false);
        return new DivViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DivViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.deptName.setText(list[position]);
        holder.deptName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "clicked "+list[position], Toast.LENGTH_SHORT).show();
                HashMap<String,String> data = Global_Data_Structure.getData();
                data.put("Division",list[position]);
                Intent intent = new Intent(context.getApplicationContext(),Subject_Activity.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public static class DivViewHolder extends RecyclerView.ViewHolder{
        TextView deptName;
        public DivViewHolder(@NonNull View itemView) {
            super(itemView);
            deptName = itemView.findViewById(R.id.dept_name);
        }
    }
}
