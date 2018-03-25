package com.addie.maxfocus.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.addie.maxfocus.R;
import com.addie.maxfocus.model.App;

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
    }

    class AppAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R.id.tv_app_list_item_title)
        TextView mAppTitleTextView;
        @BindView(R.id.iv_app_list_item_icon)
        ImageView mAppIconImageView;


        AppAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            App app = mAppsList.get(adapterPosition);
            mClickHandler.onClick(app);
        }

    }

    public void setListData(ArrayList<App> appsList) {
        mAppsList = appsList;
        notifyDataSetChanged();
    }
}
