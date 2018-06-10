package in.bugzy.ui.about;


import in.bugzy.R;
import in.bugzy.data.model.GitUser;
import in.bugzy.utils.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ContributorsAdapter extends RecyclerView.Adapter<ContributorsAdapter.ContributorViewHolder> {
    private OnItemClickListener mItemClickListener;
    public List<GitUser> mContributorsList;
    private Fragment mFragment;
    private Resources.Theme mTheme;

    ContributorsAdapter(Fragment container, Resources.Theme theme) {
        mFragment = container;
        mTheme = theme;
    }

    public void setData(List<GitUser> contributorsList) {
        mContributorsList = contributorsList;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    private Spannable getContributorsText(GitUser user) {
        String contributionsString = user.getContributions() +"";
        String finalString = contributionsString +" contributions";
        Spannable spannable = new SpannableString(finalString);

        TypedArray a = mTheme.obtainStyledAttributes(new int[]{R.attr.headerTextColor});
        int color = a.getColor(0, 0);
        spannable.setSpan(new ForegroundColorSpan(color),
                finalString.indexOf(contributionsString),
                finalString.indexOf(contributionsString) + contributionsString.length() ,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    @Override
    public ContributorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contributor_row, parent, false);
        final ContributorViewHolder holder = new ContributorViewHolder(inflatedView);
        inflatedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) {
                    return;
                }
                if (mItemClickListener == null) {
                    return;
                }
                mItemClickListener.onItemClick(pos);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ContributorViewHolder holder, int position) {
        holder.bind(mContributorsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mContributorsList == null ? 0 : mContributorsList.size();
    }

    public class ContributorViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameView;
        private TextView mContriView;
        private ImageView mImage;

        public ContributorViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.iv_image);
            mNameView = itemView.findViewById(R.id.tv_name);
            mContriView = itemView.findViewById(R.id.tv_contri);
        }

        public void bind(GitUser user) {
            mNameView.setText(user.getLogin());
            mContriView.setText(getContributorsText(user));
            Glide.with(mFragment).load(user.getAvatarUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .thumbnail(Glide.with(mFragment).load(R.drawable.avatar_placeholder))
                    .into(mImage);
        }
    }
}
