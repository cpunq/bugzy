package com.bluestacks.bugzy.ui.caseevents;


import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.data.model.Attachment;
import com.bluestacks.bugzy.ui.editcase.CaseEditActivity;
import com.bumptech.glide.Glide;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class AttachmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int IMAGE_ATTACHMENT = 0;
    public static final int OTHER_ATTACHMENT = 1;
    private List<Attachment> mList;
    private OnItemClickListener mItemClickListener;
    public Context mContext;
    public String mToken;

    public AttachmentsAdapter(List<Attachment> list, Context context, String token) {
        this.mList = list;
        this.mToken = token;
        this.mContext = context;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public void setList(List<Attachment> list) {
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case IMAGE_ATTACHMENT:
                View v1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.attachment_item_image, viewGroup, false);
                return new ImageViewHolder(v1);
            default:
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.attachment_item_other, viewGroup, false);
                return new OtherAttachmentHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position).getUri() != null) {
            // Assuming we are only supporting image attachments as of now
            return IMAGE_ATTACHMENT;
        }
        String filename = mList.get(position).getFilename();
        if (TextUtils.isEmpty(filename)) {
            return OTHER_ATTACHMENT;
        }
        filename = filename.toLowerCase();
        if (filename.endsWith("png") || filename.endsWith("jpg") || filename.endsWith("jpeg")) {
            return IMAGE_ATTACHMENT;
        }
        return OTHER_ATTACHMENT;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case IMAGE_ATTACHMENT:
                ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;
                imageViewHolder.bind(mList.get(position));
                break;
            default:
                OtherAttachmentHolder holder = (OtherAttachmentHolder) viewHolder;
                holder.bind(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    // for both short and long click
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private class OtherAttachmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView textView;

        public OtherAttachmentHolder(View itemView) {
            super(itemView);
            textView =  itemView.findViewById(R.id.tv_filename);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Attachment attachment) {
            textView.setText(attachment.getFilename());
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemLongClick(view, getLayoutPosition());
                return true;
            }
            return false;
        }
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView view;

        public ImageViewHolder(View itemView) {
            super(itemView);
            view = (ImageView) itemView;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Attachment attachment) {
            if (attachment.getUri() != null) {
                // Local attachment
                Glide.with(mContext)
                        .load(attachment.getUri())
                        .thumbnail(Glide.with(mContext).load(R.drawable.loading_ring))
                        .into(view);
                return;
            }
            final String img_path = ("https://bluestacks.fogbugz.com/" + attachment.getUrl() + "&token=" + mToken).replaceAll("&amp;","&");
            Glide.with(mContext).load(img_path)
                    .thumbnail(Glide.with(mContext).load(R.drawable.loading_ring))
                    .into(view);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            }
            return false;
        }
    }
}