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

public class SpotsAdapter extends RecyclerView.Adapter<SpotsAdapter.ViewHolder> {
    private List<Spot> mSpotList;
    private List<String> mkeys;
    private Context mContext;
    private OnSpotListener mOnSpotListener;

    public SpotsAdapter(List<Spot> mSpotList, List<String> mkeys, Context mContext, OnSpotListener mOnSpotListener) {
        this.mSpotList = mSpotList;
        this.mkeys = mkeys;
        this.mContext = mContext;
        this.mOnSpotListener = mOnSpotListener;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spot_list_item,parent,false);
        return new ViewHolder(view, mContext,mOnSpotListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mSpotList.get(position), mkeys.get(position));
    }

    @Override
    public int getItemCount() {
        return mSpotList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements  View.OnClickListener{
        private TextView mAddress ;
        private TextView mTime ;

        private double lat ;
        private double lng ;
        private String key ;

        OnSpotListener onSpotListener  ;

        public  ViewHolder(View itemView,Context mContext,OnSpotListener onSpotListener){
            super(itemView);

            mAddress = itemView.findViewById(R.id.address_txtView);
            mTime = itemView.findViewById(R.id.time_txtView);
            this.onSpotListener = onSpotListener;
            itemView.setOnClickListener(this);
        }

        public void bind(Spot spot,String key){
            mAddress.setText(spot.getAddress());
            mTime.setText(spot.getTime());

            lat = spot.getLatitude();
            lng = spot.getLongitude();
            this.key = key ;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        @Override
        public void onClick(View v) {
            onSpotListener.onSpotClick(getAdapterPosition());
        }
    }

    public interface OnSpotListener{
        void onSpotClick(int position);
    }
}





