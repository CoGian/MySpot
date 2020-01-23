package com.example.myspot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

public class FreeSpotsActivity extends AppCompatActivity {

    private RecyclerView recyclerView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_spots);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_spots);


        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        new FirebaseDatabaseHelper().readSpots(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Spot> spots, List<String> keys) {
                new RecyclerView_Config().setConfig(recyclerView,FreeSpotsActivity.this,
                        spots,keys);
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
}
