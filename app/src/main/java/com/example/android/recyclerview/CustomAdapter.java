
package com.example.android.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringChain;

import dodola.spring.SpringFrameLayout;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private String[] mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final SpringFrameLayout container;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            textView = (TextView) v.findViewById(R.id.person_name);
            container = (SpringFrameLayout) v.findViewById(R.id.container);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    private SpringChain mSpringChain;

    public CustomAdapter(String[] dataSet, SpringChain springChain) {
        mDataSet = dataSet;
        mSpringChain = springChain;
        for (int i = 0; i < dataSet.length; i++) {
            this.mSpringChain.addSpring(null);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        viewHolder.getTextView().setText(mDataSet[position]);
        viewHolder.container.setPositionInSpringChain(position);
        viewHolder.container.setSpringChain(this.mSpringChain);
        this.mSpringChain.addSpring(position, viewHolder.container);
        Spring sp = this.mSpringChain.getAllSprings().get(position);
        viewHolder.container.setTranslationY(0.0f);
        viewHolder.container.setLastTranslationY(0.0f);
    }

    public void onViewRecycled(ViewHolder holder) {
        this.mSpringChain.addSpring(holder.container.getPositionInSpringChain(), null);
        holder.container.setTranslationY(0.0f);
        holder.container.setLastTranslationY(0.0f);
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
