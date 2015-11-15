package com.loopeer.android.librarys.imagegroupview.activity;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ViewAnimator;
import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.adapter.AlbumRecyclerAdapter;
import com.loopeer.android.librarys.imagegroupview.model.ImageFloder;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID_FOLDER = 10001;

    private RecyclerView mReyclerView;
    private ViewAnimator mViewAnimator;
    private AlbumRecyclerAdapter mAlbumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        mReyclerView = (RecyclerView) findViewById(R.id.recycler_album);
        mViewAnimator = (ViewAnimator) findViewById(R.id.view_album_animator);

        showProgressView();
        setUpRecyclerView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(LOADER_ID_FOLDER, null, this);
    }

    private void updateContentView(List floders) {
        if (floders.size() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }
    }

    private void showContentView() {
        mViewAnimator.setDisplayedChild(2);
    }

    private void showEmptyView() {
        mViewAnimator.setDisplayedChild(1);
    }

    private void showProgressView() {
        mViewAnimator.setDisplayedChild(0);
    }

    private void setUpRecyclerView() {
        mReyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAlbumAdapter = new AlbumRecyclerAdapter(this);
        mReyclerView.setAdapter(mAlbumAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, NavigatorImage.IMAGE_PROJECTION,
                null, null, NavigatorImage.IMAGE_PROJECTION[2] + " DESC");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            List<ImageFloder> floders = new ArrayList();
            int count = data.getCount();
            if (count > 0) {
                data.moveToFirst();
                do {
                    String path = data.getString(data.getColumnIndexOrThrow(NavigatorImage.IMAGE_PROJECTION[0]));

                    File imageFile = new File(path);
                    File folderFile = imageFile.getParentFile();
                    ImageFloder folder = new ImageFloder();
                    folder.name = folderFile.getName();
                    folder.dir = folderFile.getAbsolutePath();
                    folder.firstImagePath = path;
                    int picSize = folderFile.list(new FilenameFilter()
                    {
                        @Override
                        public boolean accept(File dir, String filename)
                        {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;
                    folder.count = picSize;
                    if (!floders.contains(folder)) {
                        floders.add(folder);
                    }
                } while (data.moveToNext());
                updateContentView(floders);
                mAlbumAdapter.setData(floders);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
