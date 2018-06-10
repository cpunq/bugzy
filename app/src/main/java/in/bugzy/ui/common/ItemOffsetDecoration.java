package in.bugzy.ui.common;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int mHorizontalOffset;
    private int mVerticalOffset;

    public ItemOffsetDecoration(int itemOffset) {
        mHorizontalOffset = itemOffset;
        mVerticalOffset = itemOffset;
    }

    public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }

    public ItemOffsetDecoration(int horizontalOffset, int verticalOffset) {
        mHorizontalOffset = horizontalOffset;
        mVerticalOffset = verticalOffset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mHorizontalOffset, mVerticalOffset, mHorizontalOffset, mVerticalOffset);
    }
}