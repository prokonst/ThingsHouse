package com.prokonst.thingshouse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.prokonst.thingshouse.databinding.ThingListItemBinding;
import com.prokonst.thingshouse.model.Thing;

import java.util.ArrayList;

public class ThingAdapter extends RecyclerView.Adapter<ThingAdapter.ThingViewHolder>{

    private OnItemClickListener onItemClickListener;
    private ArrayList<Thing> thingArrayList = new ArrayList<>();

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
        Thing thing = thingArrayList.get(position);
        holder.thingListItemBinding.setThing(thing);
    }

    @Override
    public int getItemCount() {
        return thingArrayList.size();
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
                        onItemClickListener.onItemClick(thingArrayList.get(position));
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
