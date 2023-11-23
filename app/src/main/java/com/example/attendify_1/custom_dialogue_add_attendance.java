package com.example.attendify_1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class custom_dialogue_add_attendance {

    public interface DialogCallback {
        void onDateTimeSelected(String selectedDate, String selectedTime);
    }

    public static void showDialog(Context context, final DialogCallback callback) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custome_dialogue_for_add_attendance, null);

        // Get the views from the layout
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        Spinner timeSpinner = dialogView.findViewById(R.id.timeSpinner);

        // Create the predefined time options
        List<String> timeOptions = new ArrayList<>();
        timeOptions.add("09:00 AM - 10:00 AM");
        timeOptions.add("10:00 AM - 11:00 AM");
        timeOptions.add("11:30 AM - 12:30 PM");
        timeOptions.add("12:30 PM - 01:30 PM");
        timeOptions.add("02:15 PM - 03:15 PM");
        timeOptions.add("03:15 AM - 04:15 PM");


        // Create an ArrayAdapter and set it to the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, timeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Retrieve the selected date and time
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth() + 1;  // Month is zero-based
                        int year = datePicker.getYear();
                        String selectedDate = String.format("%02d-%02d-%d", day, month, year);
                        String selectedTime = timeSpinner.getSelectedItem().toString();

                        // Callback to the activity
                        callback.onDateTimeSelected(selectedDate, selectedTime);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle cancel button click if needed
                    }
                });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

