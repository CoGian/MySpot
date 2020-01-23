package com.example.myspot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerView_Config {
    private Context mContext;
    private SpotsAdapter mSpotsAdapter ;

    public void setConfig(RecyclerView recyclerView, Context context, List<Spot> spots, List<String> keys){
        mContext = context;
        mSpotsAdapter = new SpotsAdapter(spots,keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mSpotsAdapter);
    }

    class SpotItemView extends RecyclerView.ViewHolder{

        private TextView mAddress ;
        private TextView mTime ;

        private String key ;

        public  SpotItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext)
            .inflate(R.layout.spot_list_item, parent, false));

            mAddress = itemView.findViewById(R.id.address_txtView);
            mTime = itemView.findViewById(R.id.time_txtView);
        }

        public void bind(Spot spot,String key){
            mAddress.setText(spot.getAddress());
            mTime.setText(spot.getTime());

            this.key = key ;
        }


    }
    class SpotsAdapter extends RecyclerView.Adapter<SpotItemView>{
        private List<Spot> mSpotList ;
        private List<String> mkeys ;

        public SpotsAdapter(List<Spot> mSpotList, List<String> mkeys) {
            this.mSpotList = mSpotList;
            this.mkeys = mkeys;
        }

        @NonNull
        @Override
        public SpotItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SpotItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SpotItemView holder, int position) {
            holder.bind(mSpotList.get(position),mkeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mSpotList.size();
        }
    }
}
