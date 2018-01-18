package com.bluestacks.bugzy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.common.RealmController;
import com.bluestacks.bugzy.models.db.Case;
import com.bluestacks.bugzy.models.resp.Person;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by msharma on 12/07/17.
 */
public class CasesAdapter extends RealmRecyclerViewAdapter<Case> {

    final Context context;
    private Realm realm;
    private LayoutInflater inflater;

    public CasesAdapter(Context context) {

        this.context = context;
    }

    @Override
    public BugHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bug_item_row, parent, false);
        return new BugHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        realm = RealmController.getInstance().getRealm();

        final Case bug = getItem(position);
        final BugHolder holder = (BugHolder) viewHolder;

        holder.mItemDate.setText(bug.getTitle());
        holder.mItemDescription.setText(bug.getTitle());
    }

    public int getItemCount() {

        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    public static class BugHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mItemDate;
        private TextView mItemDescription;
        private LinearLayout mPriority;
        private Person mPerson;

        public BugHolder (View v) {
            super(v);
            mItemDate = (TextView) v.findViewById(R.id.item_id);
            mItemDescription = (TextView) v.findViewById(R.id.item_description);
            mPriority = (LinearLayout) v.findViewById(R.id.priority);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("RecyclerView", "CLICK!");
        }

        public void bindData(Person person) {
            mPerson = person;
            mItemDate.setText(String.valueOf(person.getFullname()));
            mItemDescription.setText(person.getEmail());
            mPriority.setBackgroundColor(Color.parseColor("#ddb65b"));

        }
    }
}
