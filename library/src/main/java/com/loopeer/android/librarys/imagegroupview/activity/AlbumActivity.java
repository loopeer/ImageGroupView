package com.loopeer.android.librarys.imagegroupview.activity;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.loopeer.android.librarys.imagegroupview.DisplayUtils;
import com.loopeer.android.librarys.imagegroupview.DividerItemImagesDecoration;
import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.adapter.FolderAdapter;
import com.loopeer.android.librarys.imagegroupview.adapter.ImageAdapter;
import com.loopeer.android.librarys.imagegroupview.model.Image;
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final int LOADER_ID_FOLDER = 10001;

    private RecyclerView mReyclerView;
    private ViewAnimator mViewAnimator;
    private View mFooterView;
    private TextView mTextImagesNum;
    private ListPopupWindow mFolderPopupWindow;

    private FolderAdapter mFolderAdapter;
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
        mTextImagesNum = (TextView) findViewById(R.id.text_images_num);
        mFooterView = findViewById(R.id.view_footer);

        setUpTextView();
        showProgressView();
        setUpRecyclerView();
    }

    private void setUpTextView() {
        mTextImagesNum.setText(R.string.album_all);
        mTextImagesNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mFolderPopupWindow == null) {
                    createPopupFolderList();
                    return;
                }
                trigglePopupWindow();

            }
        });
    }

    private void trigglePopupWindow() {
        if (mFolderPopupWindow.isShowing()) {
            mFolderPopupWindow.dismiss();
        } else {
            mFolderPopupWindow.setHeight(calculateWindowHeight());
            mFolderPopupWindow.show();
            int index = mFolderAdapter.getSelectIndex();
            mFolderPopupWindow.getListView().setSelection(index);
        }
    }

    private int calculateWindowHeight() {
        int itemHeight = getResources().getDimensionPixelSize(R.dimen.image_select_folder_height);
        int actualHeight = mFolderAdapter.getCount() * itemHeight;
        int maxPopupHeight = DisplayUtils.getScreenHeight(this) * 5 / 8;
        return Math.min(maxPopupHeight, actualHeight);
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

        mFolderAdapter = new FolderAdapter(this);
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

                mFolderAdapter.updateData(createFoldersWithAllImageFolder(folders));
                updateDefaultImages();
            }
        }
    }

    private void updateDefaultImages() {
        updateImages(mFolderAdapter.getItem(0));
    }

    private void updateImages(ImageFolder item) {
        updateContentView(item);
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

    private void createPopupFolderList() {
        mFolderPopupWindow = new ListPopupWindow(this);
        int width = DisplayUtils.getScreenWidth(this);
        int height = DisplayUtils.getScreenHeight(this);
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(AlbumActivity.this, R.color.image_group_popup_bg)));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height * 5 / 8);
        mFolderPopupWindow.setAnimationStyle(R.style.popup_window_anim_style);
        mFolderPopupWindow.setAnchorView(mFooterView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener(this);
        trigglePopupWindow();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mFolderAdapter.setSelectIndex(position);

        final int index = position;
        final AdapterView v = parent;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFolderPopupWindow.dismiss();
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
        }, 100);
    }
}
