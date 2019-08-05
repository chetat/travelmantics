package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.travelmantics.adapters.DealsAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DealsListActivity extends AppCompatActivity {

    private ArrayList<TravelDeal> deals = new ArrayList<>();
    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mFirebaseDbRef;
    private ChildEventListener mChildListener;

    private TextView travelText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_deal);

    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtils.detacheAuthStateListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtils.openReferenc("traveldeal", this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        DealsAdapter dealsAdapter = new DealsAdapter();
        recyclerView.setAdapter(dealsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        FirebaseUtils.attachListener();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_deal:
                Intent insertIntent = new Intent(this, DealActivity.class);
                startActivity(insertIntent);
                return true;
            case R.id.logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i("LOGOUT", "onComplete: Logout");
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.deals_activity_menu, menu);
        MenuItem insertMenu =  menu.findItem(R.id.new_deal);

        if (!FirebaseUtils.isAdmin){
            insertMenu.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }
}
