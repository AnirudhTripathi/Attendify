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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AttendanceList_Adapter extends RecyclerView.Adapter<AttendanceList_Adapter.AttListViewHolder> {

    Context context;
    String []date;
    String []time;
    ArrayList<Integer> absent;
    ArrayList<Integer> mark;


    public AttendanceList_Adapter(Context context, String[] date,String []time, String[] studentsRollNo,String[] status,ArrayList<Integer> absentStudents, ArrayList<Integer>  mark) {
        this.context = context;
        this.date = date;
        this.time = time;
        this.absent = absentStudents;
        this.mark = mark;
    }

    @NonNull
    @Override
    public AttendanceList_Adapter.AttListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.custom_attendance_list,parent,false);
        return new AttendanceList_Adapter.AttListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceList_Adapter.AttListViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.dateid.setText(date[position]);
        holder.timeid.setText(time[position]);
        holder.indexid.setText((position+1)+"");
        holder.absentid.setText(absent.get(position).toString());
        holder.cardid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mark.get(position) == 0)
                {
                    Intent intent = new Intent(context.getApplicationContext(),Attendance_Taking.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else
                {
                    Toast.makeText(context, "hello :"+ mark.get(position), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context.getApplicationContext(),Student_Record.class);
                    intent.putExtra("dateSelected",date[position]);
                    intent.putExtra("timeSelected",time[position]);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return date.length;
    }

    public void addItem(String date, String time, int absentStudents, int m) {
        // Add the new date, time, and number of absent students to the existing arrays
        String[] updatedDate = Arrays.copyOf(this.date, this.date.length + 1);
        String[] updatedTime = Arrays.copyOf(this.time, this.time.length + 1);


        updatedDate[updatedDate.length - 1] = date;
        updatedTime[updatedTime.length - 1] = time;

        this.date = updatedDate;
        this.time = updatedTime;
        this.mark.add(m);
        this.absent.add(absentStudents);

        // Notify the adapter that the data set has changed
        notifyDataSetChanged();
    }

    public static class AttListViewHolder extends RecyclerView.ViewHolder{
        TextView dateid,timeid,absentid,indexid;
        CardView cardid;
        public AttListViewHolder(@NonNull View itemView) {
            super(itemView);
            dateid = itemView.findViewById(R.id.dateID);
            timeid = itemView.findViewById(R.id.timeID);
            absentid = itemView.findViewById(R.id.absentID);
            cardid = itemView.findViewById(R.id.cardID);
            indexid = itemView.findViewById(R.id.index);
        }
    }
}
