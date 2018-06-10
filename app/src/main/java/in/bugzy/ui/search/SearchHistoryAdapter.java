package in.bugzy.ui.search;

import in.bugzy.R;
import in.bugzy.data.model.RecentSearch;
import in.bugzy.utils.OnItemClickListener;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchSuggestionHolder> {
    private OnItemClickListener mItemClickListener;
    @Nullable
    private List<RecentSearch> mSearchSuggestions;

    public SearchHistoryAdapter(@Nullable  OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setData(List<RecentSearch> data) {
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
        RecentSearch search = mSearchSuggestions.get(position);
        holder.bindData(search);
    }

    @Override
    public int getItemCount() {
        return mSearchSuggestions == null ? 0 : mSearchSuggestions.size();
    }

    public class SearchSuggestionHolder extends RecyclerView.ViewHolder{
        private TextView mFooter;

        public SearchSuggestionHolder (View v) {
            super(v);
            mFooter = v.findViewById(R.id.tv_suggestion);
            v.findViewById(R.id.iv_history).setVisibility(View.VISIBLE);
        }

        public void bindData(RecentSearch bug) {
            mFooter.setText(bug.getText());
        }
    }
}