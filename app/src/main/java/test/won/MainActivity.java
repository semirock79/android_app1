package test.won;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

import test.won.data.DataManager;
import test.won.view.SliderAdapter;
import test.won.view.ThumbnailAdapter;

public class MainActivity extends AppCompatActivity implements DataManager.DataLoadListener, SwipeRefreshLayout.OnRefreshListener {
    protected DataManager mDataManaer = null;
    protected ArrayList<String> mSlideImagePaths = null;
    protected ArrayList<String> mThumbnailImagePaths = null;

    protected LinearLayout mContentLayout = null;
    protected ViewPager mSliderLayout = null;
    protected RecyclerView mThumbnailView = null;
    protected SliderAdapter mPagerAdapter = null;
    protected ThumbnailAdapter mThumbnailAdapter = null;
    protected SwipeRefreshLayout mSwipeRefresh = null;
    protected ProgressBar mLoadingView = null;
    protected GestureDetector mGestureDetector = null;
    private boolean isLockOnHorizontialAxis = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDataManaer = new DataManager(this.getApplicationContext(), "images.db", null, 1);
        mDataManaer.setDataLoadListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeUI();
        setImagePathArray();
    }

    private void initializeUI() {
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        mContentLayout = (LinearLayout) inflater.inflate(R.layout.content_main, null);
        mSwipeRefresh = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(this);
        mSliderLayout = (ViewPager)this.findViewById(R.id.slider);
        mGestureDetector = new GestureDetector(getApplicationContext(), new XScrollDetector());
        mSliderLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isLockOnHorizontialAxis)
                    isLockOnHorizontialAxis = mGestureDetector.onTouchEvent(event);

                if (event.getAction() == MotionEvent.ACTION_UP)
                    isLockOnHorizontialAxis = false;

                if (isLockOnHorizontialAxis) {
                    mSwipeRefresh.setEnabled(false);
                } else if (!isLockOnHorizontialAxis) {
                    mSwipeRefresh.setEnabled(true);
                }
                return false;
            }
        });
        mPagerAdapter = new SliderAdapter(getApplicationContext());
        mSliderLayout.setAdapter(mPagerAdapter);
        mSliderLayout.setVisibility(View.VISIBLE);

        mThumbnailView = (RecyclerView)this.findViewById(R.id.grid_view);
        mThumbnailAdapter = new ThumbnailAdapter(getApplicationContext());
        mThumbnailView.setAdapter(mThumbnailAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mThumbnailView.setLayoutManager(layoutManager);
        mThumbnailView.setItemAnimator(new DefaultItemAnimator());
        mThumbnailView.setVisibility(View.VISIBLE);

        mLoadingView = (ProgressBar)this.findViewById(R.id.loading_page);
    }

    @Override
    public void onDataLoaded() {
        Log.d("won", "onDataLoaded");
        setImagePathArray();
        mContentLayout.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
        mSliderLayout.setVisibility(View.VISIBLE);
        mThumbnailView.setVisibility(View.VISIBLE);
    }

    private synchronized void setImagePathArray() {
        if (mDataManaer != null) {
            mSlideImagePaths = mDataManaer.getImageArray
                    (DataManager.IMAGE_TYPE_SLIDE);
            mThumbnailImagePaths = mDataManaer.getImageArray(DataManager.IMAGE_TYPE_THUMB);
        }
        mPagerAdapter.setImages(mSlideImagePaths);
        mThumbnailAdapter.setThumbnailPaths(mThumbnailImagePaths);
    }

    @Override
    public void onRefresh() {
        Log.d("won", "refresh");
        mSlideImagePaths = new ArrayList<String>();
        mPagerAdapter.setImages(mSlideImagePaths);
        mThumbnailImagePaths = new ArrayList<String>();
        mThumbnailAdapter.setThumbnailPaths(mThumbnailImagePaths);
        mContentLayout.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        mSliderLayout.setVisibility(View.GONE);
        mThumbnailView.setVisibility(View.GONE);
        mDataManaer.refresh();
        mSwipeRefresh.setRefreshing(false);
    }

    class XScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceX) > Math.abs(distanceY);
        }
    }
}
