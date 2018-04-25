package in.bugzy.ui.caseevents;


import in.bugzy.R;
import in.bugzy.data.model.CaseEvent;
import in.bugzy.ui.common.EmailView;
import in.bugzy.ui.common.ItemOffsetDecoration;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CaseEventsAdapter extends RecyclerView.Adapter<CaseEventsAdapter.EventHolder> {
    private Context mContext;
    private List<CaseEvent> mCaseEvents;
    private HashMap<Integer, Parcelable> mLayoutManagerSavedStates;
    private OnAttachmentClickListener mOnAttachmentClickListener;
    private String mToken;

    public static interface OnAttachmentClickListener {
        public void onAttachmentClick(View v, CaseEvent event, int attachmentPosition);
    }

    public CaseEventsAdapter(Context context) {
        mLayoutManagerSavedStates = new HashMap<>();
        mContext = context;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public void setData(List<CaseEvent> events) {
        mCaseEvents = events;
    }

    public void setOnAttachmentClickListener(OnAttachmentClickListener onAttachmentClickListener) {
        mOnAttachmentClickListener = onAttachmentClickListener;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView;
        switch(viewType) {
            case 0:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bug_event_row_begin, parent, false);
                break;

            case 2:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bug_event_row_end, parent, false);
                break;

            default:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bug_event_row, parent, false);
                break;
        }

        return new EventHolder(inflatedView, mContext, mOnAttachmentClickListener, mToken);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        CaseEvent bug = mCaseEvents.get(position);
        holder.bindData(bug, mLayoutManagerSavedStates.get(bug.getBugEvent()));
    }

    @Override
    public void onViewDetachedFromWindow(EventHolder holder) {
        super.onViewDetachedFromWindow(holder);
        saveInstanceState(holder);
    }

    public void saveInstanceState(EventHolder holder) {
        if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) {
            return;
        }
        CaseEvent event = mCaseEvents.get(holder.getAdapterPosition());
        Parcelable p = holder.onSaveLayoutManagerState();
        if (p != null) {
            mLayoutManagerSavedStates.put(event.getBugEvent(), p);
        }
    }

    @Override
    public int getItemCount() {
        return mCaseEvents == null ? 0 : mCaseEvents.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return 0;
        }
        else if(position == mCaseEvents.size()-1) {
            return 2;
        }
        return 1;
    }

    @Override
    public void onViewRecycled(EventHolder holder) {
        saveInstanceState(holder);
        super.onViewRecycled(holder);
    }

    public class EventHolder extends RecyclerView.ViewHolder implements AttachmentsAdapter.OnItemClickListener {
        private TextView mItemDate;
        private TextView mItemDescription;
        private TextView mChanges;
        private TextView mChangesContent;
        private Context mContext;
        private String mToken;
        private LinearLayout mContentContainer;
        private EmailView mEmailView;
        @Nullable
        private LinearLayoutManager mLayoutManager;
        private RecyclerView mAttachmentsRecyclerView;
        private OnAttachmentClickListener mOnAttachmentClickListener;

        // Transient data
        private CaseEvent mCaseEvent;

        private EventHolder (View v, Context context, OnAttachmentClickListener attachmentClickListener, String token) {
            super(v);
            mItemDate =  v.findViewById(R.id.item_id);
            mItemDescription = v.findViewById(R.id.item_description);
            mChanges = v.findViewById(R.id.changes);
            mChangesContent = v.findViewById(R.id.change_content);
            mContentContainer = v.findViewById(R.id.content_container);
            mEmailView = v.findViewById(R.id.view_email);
            mAttachmentsRecyclerView = v.findViewById(R.id.recyclerview_attachments);
            mContext = context;
            mToken = token;
            mLayoutManager = new LinearLayoutManager(mContext);
            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mAttachmentsRecyclerView.setLayoutManager(mLayoutManager);
            mAttachmentsRecyclerView.addItemDecoration(new ItemOffsetDecoration(
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8f, mContext.getResources().getDisplayMetrics()
                    ),
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 0f, mContext.getResources().getDisplayMetrics()
                    )
            ));
            mOnAttachmentClickListener = attachmentClickListener;
            mAttachmentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // save instance state
                        CaseEventsAdapter.this.saveInstanceState(EventHolder.this);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }


        @Nullable
        public Parcelable onSaveLayoutManagerState() {
            if (mLayoutManager != null) {
                return mLayoutManager.onSaveInstanceState();
            }
            return null;
        }

        public void bindData(CaseEvent bug, Parcelable layoutManagerState) {
            mCaseEvent = bug;
            DateFormat format2 = new SimpleDateFormat("MMMM dd, yyyy, hh:mm a", Locale.US);
            mItemDate.setText(format2.format(bug.getDate()));
            mContentContainer.setVisibility(View.VISIBLE);
            mChanges.setVisibility(View.VISIBLE);
            mChangesContent.setVisibility(View.VISIBLE);

            mItemDescription.setText(Html.fromHtml( bug.getEventDescription()));

            // Decide to show Changes
            if(!TextUtils.isEmpty(bug.getsChanges())) {
                mChanges.setText(Html.fromHtml(bug.getsChanges()));
            } else {
                mChanges.setVisibility(View.GONE);
            }

            // Decide to show Email or mChangesContent
            if (bug.isfEmail()) {
                mEmailView.setVisibility(View.VISIBLE);
                mContentContainer.setVisibility(View.GONE);

                mEmailView.mFromView.setText(bug.getsFrom());
                mEmailView.mCcView.setText(bug.getsCC());
                mEmailView.mToView.setText(bug.getsTo());
                mEmailView.mSubject.setText(bug.getsSubject());
                mEmailView.mDateView.setText(bug.getsDate());
                mEmailView.mBodyContent.setText(Html.fromHtml(bug.getsBodyHTML()));
            } else {
                mEmailView.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(bug.getContentHtml())) {
                    mChangesContent.setText(Html.fromHtml(bug.getContentHtml()));
                } else if (!TextUtils.isEmpty(bug.getContent())) {
                    mChangesContent.setText(Html.fromHtml(bug.getContent()));
                } else {
                    mChangesContent.setVisibility(View.GONE);
                }
            }

            // Decide to show attachments
            if(bug.getsAttachments().size() > 0) {
                mContentContainer.setVisibility(View.VISIBLE);
                mAttachmentsRecyclerView.setVisibility(View.VISIBLE);

                mAttachmentsRecyclerView.setHasFixedSize(true);
                AttachmentsAdapter adapter = new AttachmentsAdapter(bug.getsAttachments(), mContext, mToken);
                mAttachmentsRecyclerView.setAdapter(adapter);
                if (mAttachmentsRecyclerView.getScrollState() != RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (layoutManagerState != null) {
                        mLayoutManager.onRestoreInstanceState(layoutManagerState);
                    }
                }
                adapter.setOnItemClickListener(this);
            } else {
                // If attachments and content are empty, then hide mContentContainer
                mAttachmentsRecyclerView.setVisibility(View.GONE);
                if (TextUtils.isEmpty(bug.getContent()) && TextUtils.isEmpty(bug.getContentHtml())) {
                    mContentContainer.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onItemClick(View view, int position) {
            if (mCaseEvent != null) {
                if (mOnAttachmentClickListener != null) {
                    mOnAttachmentClickListener.onAttachmentClick(view, mCaseEvent, position);
                }
            }
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    }
}
