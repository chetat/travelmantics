package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.Resource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DealActivity extends AppCompatActivity {

    private static final int PICTURE_RESULT = 56;
    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mFirbaseRef;
    private EditText titleField;
    private EditText priceField;
    private EditText descriptionField;
    private TravelDeal mDeal;
    private ImageView mImageView;
    private Button butttonImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        titleField = findViewById(R.id.deal_title_field);
        priceField = findViewById(R.id.price_field);
        descriptionField = findViewById(R.id.description_field);
        mImageView = findViewById(R.id.imaged);
        butttonImage = findViewById(R.id.btn_img);

        butttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICTURE_RESULT);

            }
        });

        FirebaseUtils.openReferenc("traveldeal", this);

        mFirebaseDb = FirebaseUtils.firebaseDb;
        mFirbaseRef = FirebaseUtils.mFirebaseDbRef;
        Intent dealIntent  = getIntent();
        TravelDeal deal =  dealIntent.getParcelableExtra("deal");

        if (deal == null){
            deal = new TravelDeal();
        }
        this.mDeal = deal;

        titleField.setText(mDeal.getTitle());
        priceField.setText(mDeal.getPrice());
        descriptionField.setText(mDeal.getDescriprion());
        showImage(mDeal.getImageUrl());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            assert data != null;
            Uri imageUri = data.getData();
            assert imageUri != null;
            StorageReference ref = FirebaseUtils.mStorageRef.child(Objects.requireNonNull(imageUri.getLastPathSegment()));
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String imageName = taskSnapshot.getStorage().getPath();
                 taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Log.i("URL", "onSuccess: " + uri.toString());
                            mDeal.setImageUrl(uri.toString());
                            showImage(uri.toString());
                        }
                    });

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_SHORT).show();
                cleanFields();
                backToList();
                return true;
            case R.id.delete_deal:
                deleteDeal();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_SHORT).show();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showImage(String url){
        if (url != null && !url.isEmpty()){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get().load(url).resize(width, width * 2/3)
                    .centerCrop()
                    .into(mImageView);
        }
    }

    private void cleanFields() {
        titleField.setText("");
        priceField.setText("");
        descriptionField.setText("");
        titleField.requestFocus();
    }

    private void saveDeal() {
        mDeal.setTitle(titleField.getText().toString());
        mDeal.setPrice(priceField.getText().toString());
        mDeal.setDescriprion(descriptionField.getText().toString());
        if (mDeal.getId() == null){
            mFirbaseRef.push().setValue(mDeal);
        }else {
            mFirbaseRef.child(mDeal.getId()).setValue(mDeal);
        }
    }

    private void backToList(){
        Intent intent = new Intent(this, DealsListActivity.class);
        startActivity(intent);
    }

    private void deleteDeal(){
        if (mDeal == null) {
            Toast.makeText(this, "Please Save Deal before Deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        mFirbaseRef.child(mDeal.getId()).removeValue();
        if (mDeal.getImageName() != null && !mDeal.getImageName().isEmpty()){
            StorageReference picRef = FirebaseUtils.mFirebaseStorage.getReference().child(mDeal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("ON SUCCESS", "onSuccess: ");
                    Toast.makeText(getBaseContext(), "Deal Deletedwith Success", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ON FAILURE", "onFailure: " + "FAILED TO DELETE" );
                }
            });
        }
    }

    private void enableEditText(boolean isEnabled){
        titleField.setEnabled(isEnabled);
        priceField.setEnabled(isEnabled);
        descriptionField.setEnabled(isEnabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        MenuItem savemenu = menu.findItem(R.id.save);
        MenuItem deletemenu = menu.findItem(R.id.delete_deal);
        if (FirebaseUtils.isAdmin){
            savemenu.setVisible(true);
            deletemenu.setVisible(true);
            enableEditText(true);
            butttonImage.setEnabled(true);
        }else {
            enableEditText(false);
            savemenu.setVisible(false);
            deletemenu.setVisible(false);
            butttonImage.setVisibility(View.INVISIBLE);
        }
        return true;
    }
}
