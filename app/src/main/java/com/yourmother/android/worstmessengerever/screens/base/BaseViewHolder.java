package com.yourmother.android.worstmessengerever.screens.base;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.annotations.NotNull;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected int dpToPx(@NotNull Context context, @DimenRes int id) {
        return context.getResources().getDimensionPixelSize(id);
    }
}
