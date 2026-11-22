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
import android.os.Bundle;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.adapter.BGAPhotoPageAdapter;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.widget.BGAHackyViewPager;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 浣滆€?鐜嬫旦 閭欢:bingoogolapple@gmail.com
 * 鍒涘缓鏃堕棿:16/6/24 涓嬪崍2:57
 * 鎻忚堪:鍥剧墖閫夋嫨棰勮鐣岄潰
 */
public class BGAPhotoPickerPreviewActivity extends BGAPPToolbarActivity implements PhotoViewAttacher.OnViewTapListener {
    private static final String EXTRA_PREVIEW_PHOTOS = "EXTRA_PREVIEW_PHOTOS";
    private static final String EXTRA_SELECTED_PHOTOS = "EXTRA_SELECTED_PHOTOS";
    private static final String EXTRA_MAX_CHOOSE_COUNT = "EXTRA_MAX_CHOOSE_COUNT";
    private static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";
    private static final String EXTRA_IS_FROM_TAKE_PHOTO = "EXTRA_IS_FROM_TAKE_PHOTO";

    private TextView mTitleTv;
    private TextView mSubmitTv;
    private BGAHackyViewPager mContentHvp;
    private RelativeLayout mChooseRl;
    private TextView mChooseTv;

    private ArrayList<String> mSelectedPhotos;
    private BGAPhotoPageAdapter mPhotoPageAdapter;
    private int mMaxChooseCount = 1;
    /**
     * 鍙充笂瑙掓寜閽枃鏈?     */
    private String mTopRightBtnText;

    private boolean mIsHidden = false;
    /**
     * 涓婁竴娆℃爣棰樻爮鏄剧ず鎴栭殣钘忕殑鏃堕棿鎴?     */
    private long mLastShowHiddenTime;
    /**
     * 鏄惁鏄媿瀹岀収鍚庤烦杞繃鏉?     */
    private boolean mIsFromTakePhoto;

    public static class IntentBuilder {
        private Intent mIntent;

        public IntentBuilder(Context context) {
            mIntent = new Intent(context, BGAPhotoPickerPreviewActivity.class);
        }

        /**
         * 鍥剧墖閫夋嫨寮犳暟鐨勬渶澶у€?         */
        public IntentBuilder maxChooseCount(int maxChooseCount) {
            mIntent.putExtra(EXTRA_MAX_CHOOSE_COUNT, maxChooseCount);
            return this;
        }

        /**
         * 褰撳墠宸查€変腑鐨勫浘鐗囪矾寰勯泦鍚?         */
        public IntentBuilder selectedPhotos(ArrayList<String> selectedPhotos) {
            mIntent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, selectedPhotos);
            return this;
        }

        /**
         * 褰撳墠棰勮鐨勫浘鐗囪矾寰勯泦鍚?         */
        public IntentBuilder previewPhotos(ArrayList<String> previewPhotos) {
            mIntent.putStringArrayListExtra(EXTRA_PREVIEW_PHOTOS, previewPhotos);
            return this;
        }

        /**
         * 褰撳墠棰勮鍥剧墖鐨勭储寮?         */
        public IntentBuilder currentPosition(int currentPosition) {
            mIntent.putExtra(EXTRA_CURRENT_POSITION, currentPosition);
            return this;
        }

        /**
         * 鏄惁鏄媿瀹岀収鍚庤烦杞繃鏉?         */
        public IntentBuilder isFromTakePhoto(boolean isFromTakePhoto) {
            mIntent.putExtra(EXTRA_IS_FROM_TAKE_PHOTO, isFromTakePhoto);
            return this;
        }

        public Intent build() {
            return mIntent;
        }
    }

    /**
     * 鑾峰彇宸查€夋嫨鐨勫浘鐗囬泦鍚?     *
     * @param intent
     * @return
     */
    public static ArrayList<String> getSelectedPhotos(Intent intent) {
        return intent.getStringArrayListExtra(EXTRA_SELECTED_PHOTOS);
    }

    /**
     * 鏄惁鏄媿鐓ч瑙?     *
     * @param intent
     * @return
     */
    public static boolean getIsFromTakePhoto(Intent intent) {
        return intent.getBooleanExtra(EXTRA_IS_FROM_TAKE_PHOTO, false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setNoLinearContentView(R.layout.bga_pp_activity_photo_picker_preview);
        mContentHvp = findViewById(R.id.hvp_photo_picker_preview_content);
        mChooseRl = findViewById(R.id.rl_photo_picker_preview_choose);
        mChooseTv = findViewById(R.id.tv_photo_picker_preview_choose);
    }

    @Override
    protected void setListener() {
        mChooseTv.setOnClickListener(new BGAOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String currentPhoto = mPhotoPageAdapter.getItem(mContentHvp.getCurrentItem());
                if (mSelectedPhotos.contains(currentPhoto)) {
                    mSelectedPhotos.remove(currentPhoto);
                    mChooseTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_normal, 0, 0, 0);
                    renderTopRightBtn();
                } else {
                    if (mMaxChooseCount == 1) {
                        // 鍗曢€?
                        mSelectedPhotos.clear();
                        mSelectedPhotos.add(currentPhoto);
                        mChooseTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_checked, 0, 0, 0);
                        renderTopRightBtn();
                    } else {
                        // 澶氶€?
                        if (mMaxChooseCount == mSelectedPhotos.size()) {
                            BGAPhotoPickerUtil.show(getString(R.string.bga_pp_toast_photo_picker_max, mMaxChooseCount));
                        } else {
                            mSelectedPhotos.add(currentPhoto);
                            mChooseTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_checked, 0, 0, 0);
                            renderTopRightBtn();
                        }
                    }
                }
            }
        });

        mContentHvp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                handlePageSelectedStatus();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mMaxChooseCount = getIntent().getIntExtra(EXTRA_MAX_CHOOSE_COUNT, 1);
        if (mMaxChooseCount < 1) {
            mMaxChooseCount = 1;
        }

        mSelectedPhotos = getIntent().getStringArrayListExtra(EXTRA_SELECTED_PHOTOS);
        if (mSelectedPhotos == null) {
            mSelectedPhotos = new ArrayList<>();
        }

        ArrayList<String> previewPhotos = getIntent().getStringArrayListExtra(EXTRA_PREVIEW_PHOTOS);
        if (TextUtils.isEmpty(previewPhotos.get(0))) {
            // 浠嶣GAPhotoPickerActivity璺宠浆杩囨潵鏃讹紝濡傛灉鏈夊紑鍚媿鐓у姛鑳斤紝鍒欑0椤逛负""
            previewPhotos.remove(0);
        }

        mIsFromTakePhoto = getIntent().getBooleanExtra(EXTRA_IS_FROM_TAKE_PHOTO, false);
        if (mIsFromTakePhoto) {
            mChooseRl.setVisibility(View.INVISIBLE);
        }
        int currentPosition = getIntent().getIntExtra(EXTRA_CURRENT_POSITION, 0);

        mTopRightBtnText = getString(R.string.bga_pp_confirm);


        mPhotoPageAdapter = new BGAPhotoPageAdapter(this, previewPhotos);
        mContentHvp.setAdapter(mPhotoPageAdapter);
        mContentHvp.setCurrentItem(currentPosition);


        mToolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                hiddenToolBarAndChooseBar();
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_pp_menu_photo_picker_preview, menu);
        MenuItem menuItem = menu.findItem(R.id.item_photo_picker_preview_title);
        View actionView = menuItem.getActionView();

        mTitleTv = actionView.findViewById(R.id.tv_photo_picker_preview_title);
        mSubmitTv = actionView.findViewById(R.id.tv_photo_picker_preview_submit);
        mSubmitTv.setOnClickListener(new BGAOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, mSelectedPhotos);
                intent.putExtra(EXTRA_IS_FROM_TAKE_PHOTO, mIsFromTakePhoto);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        renderTopRightBtn();
        handlePageSelectedStatus();

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, mSelectedPhotos);
        intent.putExtra(EXTRA_IS_FROM_TAKE_PHOTO, mIsFromTakePhoto);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void handlePageSelectedStatus() {
        if (mTitleTv == null || mPhotoPageAdapter == null) {
            return;
        }

        mTitleTv.setText((mContentHvp.getCurrentItem() + 1) + "/" + mPhotoPageAdapter.getCount());
        if (mSelectedPhotos.contains(mPhotoPageAdapter.getItem(mContentHvp.getCurrentItem()))) {
            mChooseTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_checked, 0, 0, 0);
        } else {
            mChooseTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_normal, 0, 0, 0);
        }
    }

    /**
     * 娓叉煋鍙充笂瑙掓寜閽?     */
    private void renderTopRightBtn() {
        if (mIsFromTakePhoto) {
            mSubmitTv.setEnabled(true);
            mSubmitTv.setText(mTopRightBtnText);
        } else if (mSelectedPhotos.size() == 0) {
            mSubmitTv.setEnabled(false);
            mSubmitTv.setText(mTopRightBtnText);
        } else {
            mSubmitTv.setEnabled(true);
            mSubmitTv.setText(mTopRightBtnText + "(" + mSelectedPhotos.size() + "/" + mMaxChooseCount + ")");
        }
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        if (System.currentTimeMillis() - mLastShowHiddenTime > 500) {
            mLastShowHiddenTime = System.currentTimeMillis();
            if (mIsHidden) {
                showTitleBarAndChooseBar();
            } else {
                hiddenToolBarAndChooseBar();
            }
        }
    }

    private void showTitleBarAndChooseBar() {
        if (mToolbar != null) {
            ViewCompat.animate(mToolbar).translationY(0).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(View view) {
                    mIsHidden = false;
                }
            }).start();
        }

        if (!mIsFromTakePhoto && mChooseRl != null) {
            mChooseRl.setVisibility(View.VISIBLE);
            ViewCompat.setAlpha(mChooseRl, 0);
            ViewCompat.animate(mChooseRl).alpha(1).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }

    private void hiddenToolBarAndChooseBar() {
        if (mToolbar != null) {
            ViewCompat.animate(mToolbar).translationY(-mToolbar.getHeight()).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(View view) {
                    mIsHidden = true;
                    if (mChooseRl != null) {
                        mChooseRl.setVisibility(View.INVISIBLE);
                    }
                }
            }).start();
        }

        if (!mIsFromTakePhoto) {
            if (mChooseRl != null) {
                ViewCompat.animate(mChooseRl).alpha(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        }
    }

}
