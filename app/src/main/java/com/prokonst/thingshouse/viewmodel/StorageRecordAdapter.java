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
import com.prokonst.thingshouse.databinding.ItemStoragerecordListBinding;
import com.prokonst.thingshouse.model.dataview.StorageRecord;

import java.util.ArrayList;
import java.util.Comparator;

public class StorageRecordAdapter extends RecyclerView.Adapter<StorageRecordAdapter.StorageRecordViewHolder>
        implements Filterable {

    private OnItemClickListener onItemClickListener;
    private ArrayList<StorageRecord> storageRecordArrayList = new ArrayList<>();
    private ArrayList<StorageRecord> storageRecordArrayListFiltered = new ArrayList<>();

    @NonNull
    @Override
    public StorageRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemStoragerecordListBinding itemStorageRecordListBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_storagerecord_list,
                parent, false
        );

        return new StorageRecordViewHolder(itemStorageRecordListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StorageRecordViewHolder holder, int position) {
        StorageRecord storageRecord = storageRecordArrayListFiltered.get(position);
        holder.itemStorageRecordListBinding.setStorageRecord(storageRecord);
    }

    @Override
    public int getItemCount() {
        if(storageRecordArrayListFiltered == null)
            return 0;

        return storageRecordArrayListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    storageRecordArrayListFiltered = storageRecordArrayList;
                } else {
                    ArrayList<StorageRecord> filteredList = new ArrayList<>();
                    for (StorageRecord row : storageRecordArrayList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getBarCode().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    storageRecordArrayListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = storageRecordArrayListFiltered;
                return filterResults;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                storageRecordArrayListFiltered = (ArrayList<StorageRecord>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class StorageRecordViewHolder extends RecyclerView.ViewHolder {

        ItemStoragerecordListBinding itemStorageRecordListBinding;

        public StorageRecordViewHolder(@NonNull ItemStoragerecordListBinding itemStorageRecordListBinding) {
            super(itemStorageRecordListBinding.getRoot());

            this.itemStorageRecordListBinding = itemStorageRecordListBinding;
            itemStorageRecordListBinding.getRoot().setOnClickListener( (view) -> {
                int position = getAdapterPosition();
                if(onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(storageRecordArrayListFiltered.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(StorageRecord storageRecord);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setStorageRecordArrayList(ArrayList<StorageRecord> storageRecordArrayList) {
        this.storageRecordArrayList = storageRecordArrayList;

        this.storageRecordArrayList.sort( Comparator.comparing(StorageRecord::getName) );

        notifyDataSetChanged();
    }
}
