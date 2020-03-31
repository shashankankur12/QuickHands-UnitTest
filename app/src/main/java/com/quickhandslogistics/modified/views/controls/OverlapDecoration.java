package com.quickhandslogistics.modified.views.controls;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class OverlapDecoration extends RecyclerView.ItemDecoration {

    private final static int vertOverlap = -12;

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        final int itemCount = state.getItemCount();

        // Overlaps all position views except last position.
        if (itemCount > 0 && itemPosition != itemCount - 1) {
            outRect.set(0, 0, vertOverlap, 0);
        }
    }
}