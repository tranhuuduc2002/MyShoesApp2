package com.tranhuuduc.myshoesapp;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/*
NOT DONE YET!!! NOT DONE YET!!! NOT DONE YET!!! NOT DONE YET!!! NOT DONE YET!!! NOT DONE YET!!! NOT
 */
public class MyShoesApplication extends Application {

    public static MyShoesApplication get(Context context) {
        return (MyShoesApplication) context.getApplicationContext();
    }

/*
I have no Knowledge about firebase now, so I just comment it
Wait ...
 */
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }

    public DatabaseReference getUnitDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("/my_unit");
    }

    public DatabaseReference getShoesDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("/shoes");
    }

    public DatabaseReference getHistoryDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("/history");
    }

    public DatabaseReference getQuantityDatabaseReference(long ShoesId) {
        return FirebaseDatabase.getInstance().getReference("/shoes/" + ShoesId + "/quantity");
    }
}
