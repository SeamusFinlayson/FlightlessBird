package com.example.flightlessbird;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class UT_RecyclerViewAdapter extends RecyclerView.Adapter<UT_RecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<UserTrip> userTrips;

    public UT_RecyclerViewAdapter(Context context, ArrayList<UserTrip> userTrips, RecyclerViewInterface recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        this.userTrips = userTrips;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_trips_row, parent, false);
        return new MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.utName.setText(userTrips.get(position).getTripName());
        holder.utCityNames.setText(userTrips.get(position).getStationNames());
    }

    @Override
    public int getItemCount() {
        return userTrips.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView utName, utCityNames;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            utName = itemView.findViewById(R.id.tripNameTextView);
            utCityNames = itemView.findViewById(R.id.cityNamesTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if(position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
