package com.bluestacks.bugzy.ui.search;

import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.data.model.SearchSuggestion;
import com.bluestacks.bugzy.utils.OnItemClickListener;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter.SearchSuggestionHolder> {
    private OnItemClickListener mItemClickListener;
    @Nullable
    private List<SearchSuggestion> mSearchSuggestions;

    public SearchSuggestionAdapter(@Nullable  OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setData(List<SearchSuggestion> data) {
        mSearchSuggestions = data;
    }

    @Override
    public SearchSuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_suggestion_row, parent, false);
        final SearchSuggestionHolder holder = new SearchSuggestionHolder(inflatedView);
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
    public void onBindViewHolder(SearchSuggestionHolder holder, int position) {
        SearchSuggestion bug = mSearchSuggestions.get(position);
        holder.bindData(bug);
    }

    @Override
    public int getItemCount() {
        return mSearchSuggestions == null ? 0 : mSearchSuggestions.size();
    }

    public static class SearchSuggestionHolder extends RecyclerView.ViewHolder{
        private TextView mFooter;

        public SearchSuggestionHolder (View v) {
            super(v);
            mFooter = v.findViewById(R.id.tv_suggestion);
        }

        public void bindData(SearchSuggestion bug) {
            mFooter.setText(bug.getText());
        }
    }
}