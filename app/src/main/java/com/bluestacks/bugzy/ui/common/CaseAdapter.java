package com.bluestacks.bugzy.ui.common;

import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.utils.OnItemClickListener;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.CaseHolder> {
    private OnItemClickListener mItemClickListener;
    private List<Case> mBugs;

    public CaseAdapter(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public CaseAdapter(List<Case> bugs, OnItemClickListener listener) {
        mBugs = bugs ;
        mItemClickListener = listener;
    }

    public void setData(List<Case> bugs) {
        mBugs = bugs;
    }

    @Override
    public CaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bug_item_row, parent, false);
        final CaseHolder holder = new CaseHolder(inflatedView);
        inflatedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(pos);
                    }
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(CaseHolder holder, int position) {
        Case bug = mBugs.get(position);
        holder.bindData(bug);
    }

    @Override
    public int getItemCount() {
        return mBugs == null ? 0 : mBugs.size();
    }

    public static class CaseHolder extends RecyclerView.ViewHolder{
        private TextView mItemDate;
        private TextView mItemDescription;
        private TextView mFooter;
        private ImageView mPriority;

        public CaseHolder (View v) {
            super(v);
            mItemDate = (TextView) v.findViewById(R.id.item_id);
            mItemDescription = (TextView) v.findViewById(R.id.item_subtitle);
            mPriority = v.findViewById(R.id.priority);
            mFooter = v.findViewById(R.id.item_footer);
        }

        public void bindData(Case bug) {
            mItemDate.setText(String.valueOf(bug.getIxBug()));
            mItemDescription.setText(bug.getTitle());
            mFooter.setText("Assigned to: " + bug.getPersonAssignedTo());

            if (bug.getPriority() <= 3) {
                mPriority.setBackgroundColor(Color.parseColor("#E74E54"));
            }  else if(bug.getPriority() == 4) {
                mPriority.setBackgroundColor(Color.parseColor("#F1C15E"));
            } else if(bug.getPriority() == 5) {
                mPriority.setBackgroundColor(Color.parseColor("#E2C075"));
            } else if(bug.getPriority() <= 6) {
                mPriority.setBackgroundColor(Color.parseColor("#C9C9C9"));
            }
        }
    }
}