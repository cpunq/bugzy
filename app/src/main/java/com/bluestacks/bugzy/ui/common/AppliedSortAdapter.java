package com.bluestacks.bugzy.ui.common;

import com.bluestacks.bugzy.R;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AppliedSortAdapter extends RecyclerView.Adapter<AppliedSortAdapter.AppliedSortHolder> {
    private OnAppliedSortItemClickListener mItemClickListener;
    @Nullable
    private List<String> mAppliedSorting;

    public AppliedSortAdapter(@Nullable  OnAppliedSortItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setData(List<String> data) {
        mAppliedSorting = data;
    }

    @Override
    public AppliedSortHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sort_row, parent, false);
        final AppliedSortHolder holder = new AppliedSortHolder(inflatedView);

        inflatedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(pos, view);
                    }
                }
            }
        });
        return holder;
    }

    public static interface OnAppliedSortItemClickListener {
        public void onItemClick(int position, View v);
    }

    @Override
    public void onBindViewHolder(AppliedSortHolder holder, int position) {
        String bug = mAppliedSorting.get(position);
        holder.bindData(bug);
    }

    @Override
    public int getItemCount() {
        return mAppliedSorting == null ? 0 : mAppliedSorting.size();
    }

    public static class AppliedSortHolder extends RecyclerView.ViewHolder{
        private TextView mChip;

        public AppliedSortHolder (View v) {
            super(v);
            mChip = v.findViewById(R.id.chip);
        }

        public void bindData(String text) {
            mChip.setText(text);
        }
    }
}