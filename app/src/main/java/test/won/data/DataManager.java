package test.won.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import java.util.Iterator;

public class DataManager extends SQLiteOpenHelper {
    public static final int IMAGE_TYPE_SLIDE = 0;
    public static final int IMAGE_TYPE_THUMB = 1;
    public static final String IMAGE_URL = "http://www.gettyimagesgallery.com/exhibitions/archive/poolside.aspx";

    protected String mAuthority = null;
    protected DataLoadListener mListener = null;

    public interface DataLoadListener {
        public void onDataLoaded();
    }

    public DataManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        try {
            URL url = new URL(IMAGE_URL);
            mAuthority = url.getAuthority();
            Log.d("won", "authority : " + mAuthority);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("won", "onCreate");
        db.execSQL("CREATE TABLE IMAGES (_id INTEGER PRIMARY KEY AUTOINCREMENT, image_path TEXT, type INTEGER);");
        loadAndParsingPage();
    }

    public void setDataLoadListener(DataLoadListener listener) {
        mListener = listener;
    }

    public void loadAndParsingPage() {
        new PageLoader().execute();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void refresh() {
        deleteAllData();
        loadAndParsingPage();
    }

    public void insert(String imagePath, int type) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO IMAGES VALUES(null, '" + imagePath + "', " + type + ");");
        db.close();
    }

    public void deleteAllData() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("IMAGES", null, null);
    }

    public ArrayList<String> getImageArray(int type) {
        SQLiteDatabase db = getReadableDatabase();
        String tableName = "IMAGES";
        String[] projection = {"image_path"};
        String selection = "type=?";
        String[] args = {Integer.toString(type)};
        Cursor cursor = db.query(tableName, projection, selection, args, null, null, null);
        int count = cursor.getCount();
        ArrayList<String> imagePaths = new ArrayList();
        if (cursor.moveToFirst()) {
            for (int i = 0; i < count; i++) {
                imagePaths.add(cursor.getString(0));
                if (!cursor.moveToNext()) {
                    break;
                }
            }
        }
        db.close();
        cursor.close();
        return imagePaths;
    }

    class PageLoader extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            extractSlideImagePath();
            extractThumbnailPath();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (mListener != null) {
                mListener.onDataLoaded();
            }
        }
    }

    private void extractSlideImagePath() {
        Connection connection = Jsoup.connect(IMAGE_URL);
        Document doc = null;
        try {
            doc = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc == null) {
            return;
        }
        Elements elements = doc.select("div#slider ul li img");
        Log.d("won", "elements size : " + elements.size());
        Iterator<Element> iterator = elements.iterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            String path = "http://" + mAuthority + element.attr("src");
            Log.d("won", "path : " + path);
            this.insert(path, IMAGE_TYPE_SLIDE);
        }
    }

    private void extractThumbnailPath() {
        Connection connection = Jsoup.connect(IMAGE_URL);
        Document doc = null;
        try {
            doc = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc == null) {
            return;
        }
        Elements elements = doc.select("img.picture");
        Log.d("won", "elements thumb size : " + elements.size());
        Iterator<Element> iterator = elements.iterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            String path = "http://" + mAuthority + element.attr("src");
            Log.d("won", "path thumb : " + path);
            this.insert(path, IMAGE_TYPE_THUMB);
        }
    }

    private void loadPage() throws IOException {
        URL url = null;
        URLConnection urlConnection = null;
        InputStreamReader ir = null;
        try {
            url = new URL(IMAGE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        urlConnection = url.openConnection();
        ir = new InputStreamReader(urlConnection.getInputStream());
        BufferedReader br = new BufferedReader(ir);
        String temp = "";
        while((temp = br.readLine()) != null) {
            Log.d("won", temp + "\n");
        }
    }
}
