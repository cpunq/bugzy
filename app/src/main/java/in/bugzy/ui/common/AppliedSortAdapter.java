package in.bugzy.ui.common;

import in.bugzy.R;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AppliedSortAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_SORT_LABEL = 32;
    public static final int TYPE_ADD_SORTING = 33;
    private OnAppliedSortItemClickListener mItemClickListener;
    private View.OnClickListener mOnAddClickListener;
    private int mMaxItems;
    @Nullable
    private List<String> mAppliedSorting;

    public AppliedSortAdapter(int maxItems) {
        mMaxItems = maxItems;
    }

    public void setItemClickListener(@Nullable OnAppliedSortItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setOnAddClickListener(View.OnClickListener onAddClickListener) {
        this.mOnAddClickListener = onAddClickListener;
    }

    public void setData(List<String> data) {
        mAppliedSorting = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_ADD_SORTING) {
            View v =inflater.inflate(R.layout.item_sort_add, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnAddClickListener != null) {
                        mOnAddClickListener.onClick(view);
                    }
                }
            });
            return new AddSortButtonHolder(v);
        }

        if (viewType == TYPE_SORT_LABEL) {
            View v =inflater.inflate(R.layout.item_sort_label, parent, false);
            return new SortLabelHolder(v);
        }

        // Get the item
        View inflatedView = inflater.inflate(R.layout.item_sort_row, parent, false);
        final AppliedSortHolder holder = new AppliedSortHolder(inflatedView);

        inflatedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(pos-1, view);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AppliedSortHolder) {
            String bug = mAppliedSorting.get(position-1);
            ((AppliedSortHolder) holder).bindData(bug);
        }
    }

    @Override
    public int getItemCount() {
        int listSize = getListSize();
        return  listSize + 1 + (listSize == mMaxItems ? 0 : 1);
    }

    public int getListSize() {
        return Math.min((mAppliedSorting == null ? 0 : mAppliedSorting.size()), mMaxItems);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_SORT_LABEL;
        }
        if (getListSize() == mMaxItems) {
            return super.getItemViewType(position);
        }

        if (position == getItemCount() - 1) {
            return TYPE_ADD_SORTING;
        }
        return super.getItemViewType(position);
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

    public static class AddSortButtonHolder extends RecyclerView.ViewHolder {
        public AddSortButtonHolder(View itemView) {
            super(itemView);
        }
    }

    public static class SortLabelHolder extends RecyclerView.ViewHolder {
        public SortLabelHolder(View itemView) {
            super(itemView);
        }
    }
}