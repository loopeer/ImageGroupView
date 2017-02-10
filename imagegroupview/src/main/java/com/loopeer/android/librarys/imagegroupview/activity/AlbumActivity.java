package com.loopeer.android.librarys.imagegroupview.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.adapter.ImageAdapter;
import com.loopeer.android.librarys.imagegroupview.model.Image;
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder;
import com.loopeer.android.librarys.imagegroupview.utils.Album;
import com.loopeer.android.librarys.imagegroupview.utils.PermissionUtils;
import com.loopeer.android.librarys.imagegroupview.view.CustomPopupView;
import com.loopeer.android.librarys.imagegroupview.view.DividerItemImagesDecoration;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.loopeer.android.librarys.imagegroupview.NavigatorImage.PERMISSION_CAMERA_STARTREQUEST;
import static com.loopeer.android.librarys.imagegroupview.NavigatorImage.PERMISSION_WRITE_STARTREQUEST;
import static com.loopeer.android.librarys.imagegroupview.NavigatorImage.REQUEST_CAMERA_STARTREQUEST;
import static com.loopeer.android.librarys.imagegroupview.NavigatorImage.REQUEST_WRITE_STARTREQUEST;

public class AlbumActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, CustomPopupView.FolderItemSelectListener, ImageAdapter.OnImageClickListener, View.OnClickListener {

    private static final int LOADER_ID_FOLDER = 10001;

    public static final int ALL = 0;
    public static final int TAKE_PHOTO = 1;
    public static final int ALBUM = 2;

    @IntDef(flag = true,
            value = {
                    ALL,
                    TAKE_PHOTO,
                    ALBUM
            })
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlbumType {
    }

    private RecyclerView mRecyclerView;
    private CustomPopupView mCustomPopupWindowView;
    private ViewAnimator mViewAnimator;
    private ImageAdapter mImageAdapter;
    private List<Image> mSelectedImages;
    private int mMaxSelectedNum;
    private MenuItem mSubmitMenu;
    private TextView mTextSubmit;
    private int mImageGroupId;
    private int mAlbumType;
    private boolean mIsAvatarType;
    private int mToolbarColor;
    private int mStatusBarColor;
    private int mSubmitButtonDrawable;
    private String mToolbarTitle;
    private String mSubmitButtonTextPrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        parseIntent();
        mAlbumType = getIntent().getIntExtra(NavigatorImage.EXTRA_ALBUM_TYPE, 0);
        mIsAvatarType = getIntent().getBooleanExtra(NavigatorImage.EXTRA_IS_AVATAR_CROP, false);
        mSelectedImages = new ArrayList<>();
        checkPermissionToStartAlbum();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        mImageGroupId = intent.getIntExtra(NavigatorImage.EXTRA_IMAGE_GROUP_ID, 0);
        mMaxSelectedNum = intent.getIntExtra(NavigatorImage.EXTRA_IMAGE_SELECT_MAX_NUM, 0);

        mToolbarColor = intent.getIntExtra(Album.Options.EXTRA_TOOL_BAR_COLOR, ContextCompat.getColor(this, R.color.image_group_theme_primary));
        mStatusBarColor = intent.getIntExtra(Album.Options.EXTRA_STATUS_BAR_COLOR, ContextCompat.getColor(this, R.color.image_group_theme_primary_dark));
        mSubmitButtonDrawable = intent.getIntExtra(Album.Options.EXTRA_SUBMIT_BUTTON_DRAWABLE, R.drawable.image_group_button_background_primary_corner_selector);
        mToolbarTitle = intent.getStringExtra(Album.Options.EXTRA_TOOL_BAR_TITLE);
        mSubmitButtonTextPrefix = intent.getStringExtra(Album.Options.EXTRA_SUBMIT_BUTTON_TEXT_PREFIX);
        if (TextUtils.isEmpty(mToolbarTitle)) {
            mToolbarTitle = getString(R.string.toolbar_title);
        }
        if (TextUtils.isEmpty(mSubmitButtonTextPrefix)) {
            mSubmitButtonTextPrefix = getString(R.string.submit_button_text_prefix);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_group_submit, menu);
        if (mAlbumType != TAKE_PHOTO) updateSubmitMenu(menu);
        return true;
    }

    private void updateSubmitMenu(Menu menuItem) {
        mSubmitMenu = menuItem.findItem(R.id.action_submit);
        View view = mSubmitMenu.getActionView();
        mTextSubmit = (TextView) view.findViewById(R.id.text_image_submit);
        mTextSubmit.setBackground(ContextCompat.getDrawable(this, mSubmitButtonDrawable));
        if (mIsAvatarType) {
            mTextSubmit.setVisibility(View.INVISIBLE);
        }
        mTextSubmit.setOnClickListener(this);
        updateSubmitText();
    }

    private void updateSubmitText() {
        mTextSubmit.setEnabled(mSelectedImages.size() > 0);
        mTextSubmit.setText(getSubmitText());
    }

    private String getSubmitText() {
        return mSelectedImages.size() == 0
                ? getResources().getString(R.string.action_submit, mSubmitButtonTextPrefix)
                :
                mMaxSelectedNum == 0
                        ? getResources().getString(R.string.action_submit_string_no_max, mSubmitButtonTextPrefix, mSelectedImages.size())
                        : getResources().getString(R.string.action_submit_string, mSubmitButtonTextPrefix, mSelectedImages.size(), mMaxSelectedNum);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishWithResult() {
        Intent intent = getIntent();
        intent.putExtra(NavigatorImage.EXTRA_PHOTOS_URL, createUrls(mSelectedImages));
        intent.putExtra(NavigatorImage.EXTRA_IMAGE_GROUP_ID, mImageGroupId);
        setResult(RESULT_OK, intent);
        finish();
    }

    private ArrayList<Image> createUrls(List<Image> selectedImages) {
        return (ArrayList<Image>) selectedImages;
    }

    private void setUpView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_album);
        mViewAnimator = (ViewAnimator) findViewById(R.id.view_album_animator);
        mCustomPopupWindowView = (CustomPopupView) findViewById(R.id.view_popup_folder_window);

        getSupportActionBar().setTitle(mToolbarTitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mToolbarColor));
        setStatusBarColor(mStatusBarColor);
        setUpTextView();
        showProgressView();
        setUpRecyclerView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        }
    }

    private void setUpTextView() {
        mCustomPopupWindowView.setNumText(getString(R.string.album_all));
        mCustomPopupWindowView.setFolderItemSelectListener(this);
    }

    private void updateContentView(ImageFolder floder) {
        if (floder.images.size() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }
        mImageAdapter.setAlbumType(mAlbumType);
        mImageAdapter.setIsAvatarType(mIsAvatarType);
        mImageAdapter.updateFolderImageData(floder);
        mRecyclerView.scrollToPosition(0);
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
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(
                new DividerItemImagesDecoration(
                        getResources().getDimensionPixelSize(R.dimen.inline_padding)));
        mRecyclerView.setPadding(
                getResources().getDimensionPixelSize(R.dimen.inline_padding) / 2,
                0,
                getResources().getDimensionPixelSize(R.dimen.inline_padding) / 2,
                0
        );
        mImageAdapter = new ImageAdapter(this);
        mImageAdapter.setOnImageClickListener(this);
        mRecyclerView.setAdapter(mImageAdapter);
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
        doParseData(data);
    }

    private void doParseData(Cursor cursor) {
        new AsyncTask<Cursor, Void, List>() {

            @Override
            protected List doInBackground(Cursor... params) {
                Cursor data = params[0];
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
                            if (!folders.contains(folder)) {
                                List<Image> imageList = new ArrayList<>();
                                imageList.add(image);
                                folder.count++;
                                folder.images = imageList;
                                folders.add(folder);
                            } else {
                                ImageFolder f = folders.get(folders.indexOf(folder));
                                f.images.add(image);
                                f.count++;
                            }
                        } while (data.moveToNext());

                        return folders;
                    }
                }

                return new ArrayList();
            }

            @Override
            protected void onPostExecute(List list) {
                super.onPostExecute(list);
                mCustomPopupWindowView.updateFolderData(createFoldersWithAllImageFolder(list));
            }
        }.execute(cursor);
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
    public void onFolderItemSelected(ImageFolder imageFolder) {
        updateContentView(imageFolder);
    }

    @Override
    public int onImageSelected(Image image, int position) {
        //0:无反应  1:选中  2:取消选中
        int index;
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
            index = 2;
        } else if (mSelectedImages.size() == mMaxSelectedNum && mMaxSelectedNum != 0) {
            return 0;
        } else {
            if (mIsAvatarType) {
                startCrop(image.url);
                return 0;
            }
            image.time = System.currentTimeMillis();
            mSelectedImages.add(image);
            index = 1;
        }
        mImageAdapter.updateSelectImages(mSelectedImages, position);
        updateSubmitText();
        return index;
    }

    @Override
    public void onCameraSelected() {
        startCamera();
    }


    private void startCamera() {
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            ActivityCompat.startActivityForResult(AlbumActivity.this,
                    new Intent(AlbumActivity.this, UserCameraActivity.class), NavigatorImage.RESULT_TAKE_PHOTO,
                    null);
        } else {
            Toast.makeText(AlbumActivity.this, "内存卡不存在", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCrop(String url) {
        NavigatorImage.startCropActivity(this, "file://" + url, true,
                R.color.image_group_theme_primary, R.color.image_group_theme_primary_dark);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != RESULT_OK && mAlbumType == TAKE_PHOTO) {
            this.finish();
        }
        if (data == null || resultCode != RESULT_OK) return;

        switch (requestCode) {
            case UCrop.REQUEST_CROP:
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    mSelectedImages.clear();
                    mSelectedImages.add(new Image(copyFileToDownloads(resultUri), "", System.currentTimeMillis()));
                    finishWithResult();
                } else {
                    finish();
                }
                break;
            default:
                String photoTakeUrl = data.getStringExtra(NavigatorImage.EXTRA_PHOTO_URL);
                if (requestCode == NavigatorImage.RESULT_TAKE_PHOTO && null != photoTakeUrl) {
                    mSelectedImages.add(new Image(photoTakeUrl, "", System.currentTimeMillis()));
                    if (mIsAvatarType) {
                        startCrop(photoTakeUrl);
                    }
                } else {
                    finishWithResult();
                }
                break;
        }
    }

    private String copyFileToDownloads(Uri croppedFileUri) {
        String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), croppedFileUri.getLastPathSegment());

        File saveFile = new File(downloadsDirectoryPath, filename);

        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(new File(croppedFileUri.getPath()));
            FileOutputStream outStream = new FileOutputStream(saveFile);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
            return saveFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        finishWithResult();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void startAlbumOrCamera() {
        if (mAlbumType != TAKE_PHOTO) {
            setContentView(R.layout.activity_album);
            setUpView();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportLoaderManager().initLoader(LOADER_ID_FOLDER, null, this);
        } else {
            startCamera();
        }
    }

    private void checkPermissionToStartAlbum() {
        if (!PermissionUtils.hasSelfPermissions(this, PERMISSION_WRITE_STARTREQUEST)) {
            ActivityCompat.requestPermissions(this, PERMISSION_WRITE_STARTREQUEST, REQUEST_WRITE_STARTREQUEST);
        } else {
            if (!PermissionUtils.hasSelfPermissions(this, PERMISSION_CAMERA_STARTREQUEST)) {
                ActivityCompat.requestPermissions(this, PERMISSION_CAMERA_STARTREQUEST, REQUEST_CAMERA_STARTREQUEST);
            } else {
                startAlbumOrCamera();
            }
        }
    }

    public void onRequestPermissionsResult(Activity target, int requestCode, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_STARTREQUEST:
                if (PermissionUtils.getTargetSdkVersion(target) < 23
                        && !PermissionUtils.hasSelfPermissions(target, PERMISSION_CAMERA_STARTREQUEST)) {
                    finish();
                    return;
                }
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    startAlbumOrCamera();
                } else {
                    if (!PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_CAMERA_STARTREQUEST)) {
                        Toast.makeText(this, com.loopeer.android.librarys.imagegroupview.R.string.camera_permission_setting_reject, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
                break;
            case REQUEST_WRITE_STARTREQUEST:
                if (PermissionUtils.getTargetSdkVersion(target) < 23
                        && !PermissionUtils.hasSelfPermissions(target, PERMISSION_WRITE_STARTREQUEST)) {
                    finish();
                    return;
                }
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    if (!PermissionUtils.hasSelfPermissions(this, PERMISSION_CAMERA_STARTREQUEST)) {
                        ActivityCompat.requestPermissions(this, PERMISSION_CAMERA_STARTREQUEST, REQUEST_CAMERA_STARTREQUEST);
                    } else {
                        startAlbumOrCamera();
                    }
                } else {
                    if (!PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_WRITE_STARTREQUEST)) {
                        Toast.makeText(this, com.loopeer.android.librarys.imagegroupview.R.string.camera_permission_setting_reject, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
                break;
            default:
                break;
        }
    }

}
