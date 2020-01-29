/*
 * MIT License
 *
 * Copyright (c) 2018 aSoft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.addie.timesapp2.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.addie.timesapp2.R;
import com.addie.timesapp2.model.App;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for binding items of the list of apps to the RecyclerView in the MainActivity
 */
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppAdapterViewHolder> {

    private ArrayList<App> mAppsList;
    private Context mContext;
    private AppOnClickHandler mClickHandler;

    public AppAdapter(Context context, AppOnClickHandler handler) {
        mContext = context;
        mClickHandler = handler;
    }

    @Override
    public AppAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View createdView = inflater.inflate(R.layout.app_list_item, parent, false);
        return new AppAdapterViewHolder(createdView);
    }

    @Override
    public void onBindViewHolder(AppAdapterViewHolder holder, int position) {
        String title;
        Drawable imageIcon;
        try {
            App currentApp = mAppsList.get(position);
            title = currentApp.getmTitle();
            imageIcon = currentApp.getmIcon();

            holder.mAppTitleTextView.setText(title);
            holder.mAppIconImageView.setImageDrawable(imageIcon);
        } catch (Exception e) {

            e.printStackTrace();
            holder.itemView.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mAppsList == null ? 0 : mAppsList.size();
    }

    public interface AppOnClickHandler {
        void onClick(App selectedApp);
        void onLongClick(App selectedApp);
    }

    class AppAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {


        @BindView(R.id.tv_app_list_item_title)
        TextView mAppTitleTextView;
        @BindView(R.id.iv_app_list_item_icon)
        ImageView mAppIconImageView;


        AppAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            App app = mAppsList.get(adapterPosition);
            mClickHandler.onClick(app);
        }

        @Override
        public boolean onLongClick(View view) {
            int adapterPosition = getAdapterPosition();

            App app = mAppsList.get(adapterPosition);
            mClickHandler.onLongClick(app);
            return true;
        }
    }

    public void setListData(ArrayList<App> appsList) {
        mAppsList = appsList;
        notifyDataSetChanged();
    }
}
