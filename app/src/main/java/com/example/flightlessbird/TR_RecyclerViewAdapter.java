package com.example.flightlessbird;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TR_RecyclerViewAdapter extends RecyclerView.Adapter<TR_RecyclerViewAdapter.MyViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<TripRoute> tripRoutes;

    public TR_RecyclerViewAdapter(Context context, ArrayList<TripRoute> tripRoutes, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.tripRoutes = tripRoutes;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public TR_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.trip_details_row, parent, false);

        return new TR_RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull TR_RecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.originTextView.setText(tripRoutes.get(position).getOriginStationName());
        holder.providerTextView.setText(tripRoutes.get(position).getOperatorName());
        holder.dateTextView.setText(tripRoutes.get(position).getDepartureDate());
        holder.departureTimeTextView.setText(tripRoutes.get(position).getDepartureTime());
        holder.arrivalTimeTextView.setText(tripRoutes.get(position).getArrivalTime());
        holder.destinationTextView.setText(tripRoutes.get(position).getDestinationStationName());
    }

    @Override
    public int getItemCount() {
        return tripRoutes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView originTextView, providerTextView, dateTextView,
                departureTimeTextView, arrivalTimeTextView, destinationTextView;

        CardView cardView;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            originTextView = itemView.findViewById(R.id.originTextView);
            providerTextView = itemView.findViewById(R.id.providerTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            departureTimeTextView = itemView.findViewById(R.id.departureTimeTextView);
            arrivalTimeTextView = itemView.findViewById(R.id.arrivalTimeTextView);
            destinationTextView = itemView.findViewById(R.id.destinationTextView);

            cardView = itemView.findViewById(R.id.seeMapCardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
