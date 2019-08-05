package com.example.travelmantics.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelmantics.DealActivity;
import com.example.travelmantics.FirebaseUtils;
import com.example.travelmantics.R;
import com.example.travelmantics.TravelDeal;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.DealsHolder>{
    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mFirebaseDbRef;
    private ChildEventListener mChildListener;
    private ArrayList<TravelDeal> deals;


    public DealsAdapter(){


        mFirebaseDb = FirebaseUtils.firebaseDb;
        mFirebaseDbRef = FirebaseUtils.mFirebaseDbRef;
        deals = FirebaseUtils.mDeals;
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
                Log.i("DEAL", "onChildAdded: " + travelDeal.getTitle());
                travelDeal.setId(dataSnapshot.getKey());
                deals.add(travelDeal);
                notifyItemInserted(deals.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mFirebaseDbRef.addChildEventListener(mChildListener);
    }

    @NonNull
    @Override
    public DealsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.recycler_item, parent, false);
        return new DealsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealsHolder holder, int position) {
        TravelDeal deal = deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        if (deals == null){
            return 0;
        }
        return deals.size();
    }

    class DealsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView dealTitle;
        public TextView dealDescription;
        public TextView dealPrice;
        public ImageView mImageView;

        public DealsHolder(@NonNull View itemView) {
            super(itemView);
            dealTitle = itemView.findViewById(R.id.td_title);
            dealPrice = itemView.findViewById(R.id.td_price);
            dealDescription = itemView.findViewById(R.id.td_description);
            mImageView = itemView.findViewById(R.id.td_image);

            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal deal){
            dealTitle.setText(deal.getTitle());
            dealPrice.setText(deal.getPrice());
            dealDescription.setText(deal.getDescriprion());
            showImage(deal.getImageUrl());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            TravelDeal selectedDeal = deals.get(position);
            Intent dealIntent = new Intent(v.getContext(), DealActivity.class);
            dealIntent.putExtra("deal", selectedDeal);
            v.getContext().startActivity(dealIntent);

        }

        private void showImage(String url){
            if (url != null && !url.isEmpty()){
                int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                Picasso.get().load(url).resize(160, 160)
                        .centerCrop()
                        .into(mImageView);
            }
        }
    }
}


