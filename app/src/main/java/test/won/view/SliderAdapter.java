package test.won.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import test.won.R;

public class SliderAdapter extends PagerAdapter {
    private ArrayList<String> mImagePaths = null;
    private LayoutInflater mInflater = null;
    private Context mContext = null;

    public SliderAdapter(Context context) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mImagePaths == null || mImagePaths.size() <= position) {
            return null;
        }
        final View view = mInflater.inflate(R.layout.slider_view, null);
        view.findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
        Picasso.with(mContext)
                .load(mImagePaths.get(position))
                .into((ImageView) view.findViewById(R.id.slider_image)
                        , new Callback() {
                            @Override
                            public void onSuccess() {
                                view.findViewById(R.id.loading_view).setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                view.findViewById(R.id.loading_view).setVisibility(View.GONE);
                            }
                        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        if (mImagePaths != null) {
            return mImagePaths.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setImages(ArrayList<String> paths) {
        if (paths != null) {
            mImagePaths = paths;
            this.notifyDataSetChanged();
        }
    }
}
