package com.example.rligramm.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.rligramm.databinding.PlaceContainerBinding;
import com.example.rligramm.models.Place;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Place> placeList;

    public PlaceAdapter(List<Place> placeList) {
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaceAdapter.PlaceContainer(
                PlaceContainerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((PlaceContainer) holder).setData(placeList.get(position));
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }


    static class PlaceContainer extends RecyclerView.ViewHolder {

        private final PlaceContainerBinding binding;

        public PlaceContainer(PlaceContainerBinding placeContainerBinding) {
            super(placeContainerBinding.getRoot());
            binding = placeContainerBinding;
        }

        void setData(Place place) {
            binding.textPlaceName.setText(place.name);
            binding.textPlaceDesc.setText(place.description);
            binding.imagePlace.setImageBitmap(place.image);
        }
    }
}
