package in.bugzy.ui.common;

import in.bugzy.R;
import in.bugzy.data.model.Case;
import in.bugzy.utils.OnItemClickListener;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int BUG = 0;
    private static final int HEADER = 1;

    private OnItemClickListener mItemClickListener;
    private List<Case> mBugs;
    private Spannable mHeaderText = null;

    public CaseAdapter(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setHeaderText(Spannable text) {
        mHeaderText = text;
    }

    public CaseAdapter(List<Case> bugs, OnItemClickListener listener) {
        mBugs = bugs ;
        mItemClickListener = listener;
    }

    public void setData(List<Case> bugs) {
        mBugs = bugs;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cases_header, parent, false);
            final HeaderHolder h = new HeaderHolder(inflatedView);
            return h;
        }
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bug_item_row, parent, false);
        final CaseHolder holder = new CaseHolder(inflatedView);
        inflatedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(getBugPosition(pos));
                    }
                }
            }
        });
        return holder;
    }

    int getBugPosition(int itemPosition) {
        if (mHeaderText == null) {
            return itemPosition;
        } else {
            return itemPosition - 1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bind(mHeaderText);
        }
        if (holder instanceof CaseHolder) {
            Case bug = mBugs.get(getBugPosition(position));
            ((CaseHolder) holder).bindData(bug);
        }
    }

    @Override
    public int getItemCount() {
        return mBugs == null ? 0 : mHeaderText == null ? mBugs.size() : mBugs.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderText == null) {
            // Bug always
            return BUG;
        }
        if (position == 0) {
            return HEADER;
        }
        return BUG;
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public HeaderHolder(View v) {
            super(v);
            mTextView = (TextView) v;
        }

        public void bind(Spannable text) {
            mTextView.setText(text);
        }
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