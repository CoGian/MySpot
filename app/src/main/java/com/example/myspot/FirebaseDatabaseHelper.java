package com.example.myspot;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {

    private FirebaseDatabase mDatabase ;
    private DatabaseReference mReferenceSpots ;
    private List<Spot> spots = new ArrayList<>() ;

    public interface DataStatus{
        void DataIsLoaded(List<Spot> spots, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceSpots = mDatabase.getReference("spots");

    }

    public  void readSpots(final DataStatus dataStatus){
        mReferenceSpots.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                spots.clear();

                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Spot spot = keyNode.getValue(Spot.class);
                    spots.add(spot);
                }

                dataStatus.DataIsLoaded(spots,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void  addSpot(Spot spot, final  DataStatus dataStatus){
        String key = mReferenceSpots.push().getKey();
        mReferenceSpots.child(key).setValue(spot)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsInserted();
                    }
                });
    }


}
