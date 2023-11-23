package com.example.attendify_1;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {
    private static DatabaseReference databaseRef;

    public static DatabaseReference getDatabaseReference() {
        if (databaseRef == null) {
            databaseRef = FirebaseDatabase.getInstance().getReference();
        }
        return databaseRef;
    }
}
