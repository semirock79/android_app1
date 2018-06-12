package test.won.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import test.won.R;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {
    private ArrayList<String> mThumbnailPaths = null;
    private Context mContext = null;

    public ThumbnailAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_detail_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mThumbnailPaths == null || mThumbnailPaths.size() <= position) {
            return;
        }
        holder.thumbnailView.setImageBitmap(null);
        Picasso.with(mContext)
                .load(mThumbnailPaths.get(position))
                .into(holder.thumbnailView
                        , new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                holder.progressBar.setVisibility(View.GONE);
                            }
                        });
    }

    @Override
    public int getItemCount() {
        if (mThumbnailPaths != null && mThumbnailPaths.size() > 0) {
            return mThumbnailPaths.size();
        }
        return 0;
    }

    public void setThumbnailPaths(ArrayList<String> paths) {
        if (paths != null) {
            mThumbnailPaths = paths;
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView = null;
        public ProgressBar progressBar = null;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnailView = (ImageView)itemView.findViewById(R.id.thumbnail_image);
            progressBar = (ProgressBar)itemView.findViewById(R.id.thumbnail_loading);
        }
    }
}
