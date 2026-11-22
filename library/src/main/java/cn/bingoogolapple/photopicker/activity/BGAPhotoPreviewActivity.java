/**
 * Copyright 2016 bingoogolapple
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.bingoogolapple.photopicker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.adapter.BGAPhotoPageAdapter;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.imageloader.BGAImageLoader;
import cn.bingoogolapple.photopicker.util.BGAAsyncTask;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.util.BGASavePhotoTask;
import cn.bingoogolapple.photopicker.widget.BGAHackyViewPager;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 浣滆€?鐜嬫旦 閭欢:bingoogolapple@gmail.com
 * 鍒涘缓鏃堕棿:16/6/24 涓嬪崍2:59
 * 鎻忚堪:鍥剧墖棰勮鐣岄潰
 */
public class BGAPhotoPreviewActivity extends BGAPPToolbarActivity implements PhotoViewAttacher.OnViewTapListener, BGAAsyncTask.Callback<Void> {
    private static final String EXTRA_SAVE_PHOTO_DIR = "EXTRA_SAVE_PHOTO_DIR";
    private static final String EXTRA_PREVIEW_PHOTOS = "EXTRA_PREVIEW_PHOTOS";
    private static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";

    private TextView mTitleTv;
    private ImageView mDownloadIv;
    private BGAHackyViewPager mContentHvp;
    private BGAPhotoPageAdapter mPhotoPageAdapter;

    private boolean mIsSinglePreview;

    private File mSavePhotoDir;

    private boolean mIsHidden = false;
    private BGASavePhotoTask mSavePhotoTask;

    /**
     * 涓婁竴娆℃爣棰樻爮鏄剧ず鎴栭殣钘忕殑鏃堕棿鎴?     */
    private long mLastShowHiddenTime;

    public static class IntentBuilder {
        private Intent mIntent;

        public IntentBuilder(Context context) {
            mIntent = new Intent(context, BGAPhotoPreviewActivity.class);
        }

        /**
         * 淇濆瓨鍥剧墖鐨勭洰褰曪紝濡傛灉浼?null锛屽垯娌℃湁淇濆瓨鍥剧墖鍔熻兘
         */
        public IntentBuilder saveImgDir(@Nullable File saveImgDir) {
            mIntent.putExtra(EXTRA_SAVE_PHOTO_DIR, saveImgDir);
            return this;
        }

        /**
         * 褰撳墠棰勮鐨勫浘鐗囪矾寰?         */
        public IntentBuilder previewPhoto(String photoPath) {
            mIntent.putStringArrayListExtra(EXTRA_PREVIEW_PHOTOS, new ArrayList<>(Arrays.asList(photoPath)));
            return this;
        }

        /**
         * 褰撳墠棰勮鐨勫浘鐗囪矾寰勯泦鍚?         */
        public IntentBuilder previewPhotos(ArrayList<String> previewPhotos) {
            mIntent.putStringArrayListExtra(EXTRA_PREVIEW_PHOTOS, previewPhotos);
            return this;
        }

        /**
         * 褰撳墠棰勮鐨勫浘鐗囩储寮?         */
        public IntentBuilder currentPosition(int currentPosition) {
            mIntent.putExtra(EXTRA_CURRENT_POSITION, currentPosition);
            return this;
        }

        public Intent build() {
            return mIntent;
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setNoLinearContentView(R.layout.bga_pp_activity_photo_preview);
        mContentHvp = findViewById(R.id.hvp_photo_preview_content);
    }

    @Override
    protected void setListener() {
        mContentHvp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                renderTitleTv();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mSavePhotoDir = (File) getIntent().getSerializableExtra(EXTRA_SAVE_PHOTO_DIR);
        if (mSavePhotoDir != null && !mSavePhotoDir.exists()) {
            mSavePhotoDir.mkdirs();
        }

        ArrayList<String> previewPhotos = getIntent().getStringArrayListExtra(EXTRA_PREVIEW_PHOTOS);
        int currentPosition = getIntent().getIntExtra(EXTRA_CURRENT_POSITION, 0);
        mIsSinglePreview = previewPhotos.size() == 1;
        if (mIsSinglePreview) {
            currentPosition = 0;
        }

        mPhotoPageAdapter = new BGAPhotoPageAdapter(this, previewPhotos);
        mContentHvp.setAdapter(mPhotoPageAdapter);
        mContentHvp.setCurrentItem(currentPosition);

        // 杩?绉掗殣钘忔爣棰樻爮
        mToolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                hiddenTitleBar();
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_pp_menu_photo_preview, menu);
        MenuItem menuItem = menu.findItem(R.id.item_photo_preview_title);
        View actionView = menuItem.getActionView();

        mTitleTv = actionView.findViewById(R.id.tv_photo_preview_title);
        mDownloadIv = actionView.findViewById(R.id.iv_photo_preview_download);
        mDownloadIv.setOnClickListener(new BGAOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (mSavePhotoTask == null) {
                    savePic();
                }
            }
        });

        if (mSavePhotoDir == null) {
            mDownloadIv.setVisibility(View.INVISIBLE);
        }

        renderTitleTv();

        return true;
    }

    private void renderTitleTv() {
        if (mTitleTv == null || mPhotoPageAdapter == null) {
            return;
        }

        if (mIsSinglePreview) {
            mTitleTv.setText(R.string.bga_pp_view_photo);
        } else {
            mTitleTv.setText((mContentHvp.getCurrentItem() + 1) + "/" + mPhotoPageAdapter.getCount());
        }
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        if (System.currentTimeMillis() - mLastShowHiddenTime > 500) {
            mLastShowHiddenTime = System.currentTimeMillis();
            if (mIsHidden) {
                showTitleBar();
            } else {
                hiddenTitleBar();
            }
        }
    }

    private void showTitleBar() {
        if (mToolbar != null) {
            ViewCompat.animate(mToolbar).translationY(0).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(View view) {
                    mIsHidden = false;
                }
            }).start();
        }
    }

    private void hiddenTitleBar() {
        if (mToolbar != null) {
            ViewCompat.animate(mToolbar).translationY(-mToolbar.getHeight()).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(View view) {
                    mIsHidden = true;
                }
            }).start();
        }
    }

    private synchronized void savePic() {
        if (mSavePhotoTask != null) {
            return;
        }

        final String url = mPhotoPageAdapter.getItem(mContentHvp.getCurrentItem());
        File file;
        if (url.startsWith("file")) {
            file = new File(url.replace("file://", ""));
            if (file.exists()) {
                BGAPhotoPickerUtil.showSafe(getString(R.string.bga_pp_save_img_success_folder, file.getParentFile().getAbsolutePath()));
                return;
            }
        }

        file = new File(mSavePhotoDir, BGAPhotoPickerUtil.md5(url) + ".png");
        if (file.exists()) {
            BGAPhotoPickerUtil.showSafe(getString(R.string.bga_pp_save_img_success_folder, mSavePhotoDir.getAbsolutePath()));
            return;
        }

        mSavePhotoTask = new BGASavePhotoTask(this, this, file);
        BGAImage.download(url, new BGAImageLoader.DownloadDelegate() {
            @Override
            public void onSuccess(String url, Bitmap bitmap) {
                if (mSavePhotoTask != null) {
                    mSavePhotoTask.setBitmapAndPerform(bitmap);
                }
            }

            @Override
            public void onFailed(String url) {
                mSavePhotoTask = null;
                BGAPhotoPickerUtil.show(R.string.bga_pp_save_img_failure);
            }
        });
    }

    @Override
    public void onPostExecute(Void aVoid) {
        mSavePhotoTask = null;
    }

    @Override
    public void onTaskCancelled() {
        mSavePhotoTask = null;
    }

    @Override
    protected void onDestroy() {
        if (mSavePhotoTask != null) {
            mSavePhotoTask.cancelTask();
            mSavePhotoTask = null;
        }
        super.onDestroy();
    }
}
