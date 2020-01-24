package com.example.myspot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class FreeSpotsActivity extends AppCompatActivity implements SpotsAdapter.OnSpotListener {

    private RecyclerView recyclerView ;
    private List<Spot> mSpots ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_spots);

        Toolbar freeSpotsToolbar = findViewById(R.id.freeSpotsToolbar);
        setSupportActionBar(freeSpotsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerview_spots);

        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        new FirebaseDatabaseHelper().readSpots(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Spot> spots, List<String> keys) {
                mSpots = spots;
                SpotsAdapter mSpotsAdapter = new SpotsAdapter(spots, keys,FreeSpotsActivity.this,FreeSpotsActivity.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(FreeSpotsActivity.this));
                recyclerView.setAdapter(mSpotsAdapter);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });



    }


    @Override
    public void onSpotClick(int position) {
        Intent intent = new Intent(this,MapsActivity.class);
        Log.d("Hi", mSpots.get(position).getAddress()) ;

        intent.putExtra("latitude",mSpots.get(position).getLatitude());
        intent.putExtra("longitude",mSpots.get(position).getLongitude());
        startActivity(intent);
    }
}
