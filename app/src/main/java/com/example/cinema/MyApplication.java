package com.example.cinema;

import android.app.Application;
import android.content.Context;

import com.example.cinema.prefs.DataStoreManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    private static final String FIREBASE_URL = "https://cinema-c68f6-default-rtdb.firebaseio.com";
    private DatabaseReference mDatabaseReference;

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DataStoreManager.init(getApplicationContext());
        FirebaseApp.initializeApp(this);
        initFirebase();
    }

    public void initFirebase() {
        String mReference = "cinema";
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_URL);
        mDatabaseReference = mFirebaseDatabase.getReference(mReference);
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabaseReference;
    }
}
