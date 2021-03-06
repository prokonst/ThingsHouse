package com.prokonst.thingshouse.viewmodel;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.prokonst.thingshouse.R;
import com.prokonst.thingshouse.databinding.ItemThingListBinding;
import com.prokonst.thingshouse.model.tables.Thing;

import java.util.ArrayList;
import java.util.Comparator;

public class ThingAdapter extends RecyclerView.Adapter<ThingAdapter.ThingViewHolder>
        implements Filterable {

    private OnItemClickListener onItemClickListener;
    private ArrayList<Thing> thingArrayList = new ArrayList<>();
    private ArrayList<Thing> thingArrayListFiltered = new ArrayList<>();

    @NonNull
    @Override
    public ThingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemThingListBinding itemThingListBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_thing_list,
                parent, false
        );

        return new ThingViewHolder(itemThingListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ThingViewHolder holder, int position) {
        Thing thing = thingArrayListFiltered.get(position);
        holder.itemThingListBinding.setThing(thing);
    }

    @Override
    public int getItemCount() {
        if(thingArrayListFiltered == null)
            return 0;

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
                    ArrayList<Thing> filteredList = new ArrayList<>();
                    for (Thing row : thingArrayList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getBarCode().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    thingArrayListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = thingArrayListFiltered;
                return filterResults;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                thingArrayListFiltered = (ArrayList<Thing>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ThingViewHolder extends RecyclerView.ViewHolder {

        ItemThingListBinding itemThingListBinding;

        public ThingViewHolder(@NonNull ItemThingListBinding itemThingListBinding) {
            super(itemThingListBinding.getRoot());

            this.itemThingListBinding = itemThingListBinding;
            itemThingListBinding.getRoot().setOnClickListener( (view) -> {
                    int position = getAdapterPosition();
                    if(onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(thingArrayListFiltered.get(position));
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setThingArrayList(ArrayList<Thing> thingArrayList) {
        this.thingArrayList = thingArrayList;

        this.thingArrayList.sort( Comparator.comparing(Thing::getName) );

        notifyDataSetChanged();
    }
}
