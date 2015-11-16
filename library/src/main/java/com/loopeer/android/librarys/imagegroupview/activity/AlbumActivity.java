package com.loopeer.android.librarys.imagegroupview.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ViewAnimator;

import com.loopeer.android.librarys.imagegroupview.DividerItemImagesDecoration;
import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.adapter.ImageAdapter;
import com.loopeer.android.librarys.imagegroupview.model.Image;
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder;
import com.loopeer.android.librarys.imagegroupview.view.CustomPopupView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, CustomPopupView.FolderItemSelectListener {

    private static final int LOADER_ID_FOLDER = 10001;

    private RecyclerView mReyclerView;
    private CustomPopupView mCustomPopupWindowView;
    private ViewAnimator mViewAnimator;
    private ImageAdapter mImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        setUpView();
    }

    private void setUpView() {
        mReyclerView = (RecyclerView) findViewById(R.id.recycler_album);
        mViewAnimator = (ViewAnimator) findViewById(R.id.view_album_animator);
        mCustomPopupWindowView = (CustomPopupView) findViewById(R.id.view_popup_folder_window);

        setUpTextView();
        mCustomPopupWindowView.setFolderItemSelectListener(this);
        showProgressView();
        setUpRecyclerView();
    }

    private void setUpTextView() {
        mCustomPopupWindowView.setNumText(getString(R.string.album_all));
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(LOADER_ID_FOLDER, null, this);
    }

    private void updateContentView(ImageFolder floder) {
        if (floder.images.size() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }
        mImageAdapter.updateFolderImageData(floder);
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
        mReyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mReyclerView.addItemDecoration(
                new DividerItemImagesDecoration(
                        getResources().getDimensionPixelSize(R.dimen.inline_padding)));
        mReyclerView.setPadding(
                getResources().getDimensionPixelSize(R.dimen.inline_padding) / 2,
                0,
                getResources().getDimensionPixelSize(R.dimen.inline_padding) / 2,
                0
        );
        mImageAdapter = new ImageAdapter(this);
        mReyclerView.setAdapter(mImageAdapter);
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
            List<ImageFolder> folders = new ArrayList();
            int count = data.getCount();
            if (count > 0) {
                data.moveToFirst();
                do {
                    String path = data.getString(data.getColumnIndexOrThrow(NavigatorImage.IMAGE_PROJECTION[0]));
                    String name = data.getString(data.getColumnIndexOrThrow(NavigatorImage.IMAGE_PROJECTION[1]));
                    long dateTime = data.getLong(data.getColumnIndexOrThrow(NavigatorImage.IMAGE_PROJECTION[2]));
                    Image image = new Image(path, name, dateTime);

                    File imageFile = new File(path);
                    File folderFile = imageFile.getParentFile();
                    ImageFolder folder = new ImageFolder();
                    folder.name = folderFile.getName();
                    folder.dir = folderFile.getAbsolutePath();
                    folder.firstImagePath = path;
                    int picSize = folderFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;
                    folder.count = picSize;
                    if (!folders.contains(folder)) {
                        List<Image> imageList = new ArrayList<>();
                        imageList.add(image);
                        folder.images = imageList;
                        folders.add(folder);
                    } else {
                        ImageFolder f = folders.get(folders.indexOf(folder));
                        f.images.add(image);
                    }
                } while (data.moveToNext());

                mCustomPopupWindowView.updateFolderData(createFoldersWithAllImageFolder(folders));
            }
        }
    }

    private List createFoldersWithAllImageFolder(List<ImageFolder> folders) {
        if (folders.size() > 0) {
            ImageFolder folder = new ImageFolder();
            folder.name = getResources().getString(R.string.album_all);
            folder.dir = null;
            folder.firstImagePath = folders.get(0).firstImagePath;
            int imageCount = 0;
            for (ImageFolder imageFolder : folders) {
                imageCount += imageFolder.count;
                folder.images.addAll(imageFolder.images);
            }
            folder.count = imageCount;
            folders.add(0, folder);
        }
        return folders;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //mFolderAdapter.setSelectIndex(position);
/*
        final int index = position;
        final AdapterView v = parent;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //mFolderPopupWindow.dismiss();
                mImageAdapter.updateData(((ImageFolder) v.getAdapter().getItem(index)).images);
                if (index == 0) {
                    mTextImagesNum.setText(R.string.album_all);
                } else {
                    ImageFolder folder = (ImageFolder) v.getAdapter().getItem(index);
                    if (null != folder) {
                        mTextImagesNum.setText(folder.name);
                    }
                }
            }
        }, 100);*/
    }

    @Override
    public void onFolderItemSelected(ImageFolder imageFolder) {
        updateContentView(imageFolder);
    }
}
