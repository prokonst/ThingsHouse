package com.prokonst.thingshouse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.prokonst.thingshouse.databinding.ThingListItemBinding;
import com.prokonst.thingshouse.model.Thing;

import java.util.ArrayList;
import java.util.List;

public class ThingAdapter extends RecyclerView.Adapter<ThingAdapter.ThingViewHolder>
        implements Filterable {

    private OnItemClickListener onItemClickListener;
    private ArrayList<Thing> thingArrayList = new ArrayList<>();
    private ArrayList<Thing> thingArrayListFiltered = new ArrayList<>();

    @NonNull
    @Override
    public ThingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ThingListItemBinding thingListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.thing_list_item,
                parent, false
        );

        return new ThingViewHolder(thingListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ThingViewHolder holder, int position) {
        Thing thing = thingArrayListFiltered.get(position);
        holder.thingListItemBinding.setThing(thing);
    }

    @Override
    public int getItemCount() {
        return thingArrayListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    thingArrayListFiltered = thingArrayList;
                } else {
                    List<Thing> filteredList = new ArrayList<>();
                    for (Thing row : thingArrayList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                    thingArrayListFiltered = (ArrayList<Thing>) filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = thingArrayListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                thingArrayListFiltered = (ArrayList<Thing>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ThingViewHolder extends RecyclerView.ViewHolder {

        ThingListItemBinding thingListItemBinding;

        public ThingViewHolder(@NonNull ThingListItemBinding thingListItemBinding) {
            super(thingListItemBinding.getRoot());

            this.thingListItemBinding = thingListItemBinding;
            thingListItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(thingArrayListFiltered.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Thing thing);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setThingArrayList(ArrayList<Thing> thingArrayList) {
        this.thingArrayList = thingArrayList;
        notifyDataSetChanged();
    }
}
