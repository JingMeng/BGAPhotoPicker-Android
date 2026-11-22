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
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import cn.bingoogolapple.baseadapter.BGAGridDivider;
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.adapter.BGAPhotoPickerAdapter;
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener;
import cn.bingoogolapple.photopicker.model.BGAPhotoFolderModel;
import cn.bingoogolapple.photopicker.pw.BGAPhotoFolderPw;
import cn.bingoogolapple.photopicker.util.BGAAsyncTask;
import cn.bingoogolapple.photopicker.util.BGAPhotoHelper;
import cn.bingoogolapple.photopicker.util.BGALoadPhotoTask;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;

/**
 * 浣滆€?鐜嬫旦 閭欢:bingoogolapple@gmail.com
 * 鍒涘缓鏃堕棿:16/6/24 涓嬪崍2:55
 * 鎻忚堪:鍥剧墖閫夋嫨鐣岄潰
 */
public class BGAPhotoPickerActivity extends BGAPPToolbarActivity implements BGAOnItemChildClickListener, BGAAsyncTask.Callback<ArrayList<BGAPhotoFolderModel>> {
    private static final String EXTRA_CAMERA_FILE_DIR = "EXTRA_CAMERA_FILE_DIR";
    private static final String EXTRA_SELECTED_PHOTOS = "EXTRA_SELECTED_PHOTOS";
    private static final String EXTRA_MAX_CHOOSE_COUNT = "EXTRA_MAX_CHOOSE_COUNT";
    private static final String EXTRA_PAUSE_ON_SCROLL = "EXTRA_PAUSE_ON_SCROLL";

    private static final String STATE_SELECTED_PHOTOS = "STATE_SELECTED_PHOTOS";

    /**
     * 鎷嶇収鐨勮姹傜爜
     */
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    /**
     * 棰勮鐓х墖鐨勮姹傜爜
     */
    private static final int RC_PREVIEW = 2;

    private static final int SPAN_COUNT = 3;

    private TextView mTitleTv;
    private ImageView mArrowIv;
    private TextView mSubmitTv;
    private RecyclerView mContentRv;

    private BGAPhotoFolderModel mCurrentPhotoFolderModel;

    /**
     * 鏄惁鍙互鎷嶇収
     */
    private boolean mTakePhotoEnabled;
    /**
     * 鏈€澶氶€夋嫨澶氬皯寮犲浘鐗囷紝榛樿绛変簬1锛屼负鍗曢€?     */
    private int mMaxChooseCount = 1;
    /**
     * 鍙充笂瑙掓寜閽枃鏈?     */
    private String mTopRightBtnText;
    /**
     * 鍥剧墖鐩綍鏁版嵁闆嗗悎
     */
    private ArrayList<BGAPhotoFolderModel> mPhotoFolderModels;

    private BGAPhotoPickerAdapter mPicAdapter;

    private BGAPhotoHelper mPhotoHelper;

    private BGAPhotoFolderPw mPhotoFolderPw;

    private BGALoadPhotoTask mLoadPhotoTask;
    private AppCompatDialog mLoadingDialog;

    private BGAOnNoDoubleClickListener mOnClickShowPhotoFolderListener = new BGAOnNoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            if (mPhotoFolderModels != null && mPhotoFolderModels.size() > 0) {
                showPhotoFolderPw();
            }
        }
    };

    public static class IntentBuilder {
        private Intent mIntent;

        public IntentBuilder(Context context) {
            mIntent = new Intent(context, BGAPhotoPickerActivity.class);
        }

        /**
         * 鎷嶇収鍚庡浘鐗囦繚瀛樼殑鐩綍銆傚鏋滀紶 null 琛ㄧず娌℃湁鎷嶇収鍔熻兘锛屽鏋滀笉涓?null 鍒欏叿鏈夋媿鐓у姛鑳斤紝
         */
        public IntentBuilder cameraFileDir(@Nullable File cameraFileDir) {
            mIntent.putExtra(EXTRA_CAMERA_FILE_DIR, cameraFileDir);
            return this;
        }

        /**
         * 鍥剧墖閫夋嫨寮犳暟鐨勬渶澶у€?         *
         * @param maxChooseCount
         * @return
         */
        public IntentBuilder maxChooseCount(int maxChooseCount) {
            mIntent.putExtra(EXTRA_MAX_CHOOSE_COUNT, maxChooseCount);
            return this;
        }

        /**
         * 褰撳墠宸查€変腑鐨勫浘鐗囪矾寰勯泦鍚堬紝鍙互浼?null
         */
        public IntentBuilder selectedPhotos(@Nullable ArrayList<String> selectedPhotos) {
            mIntent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, selectedPhotos);
            return this;
        }

        /**
         * 婊氬姩鍒楄〃鏃舵槸鍚︽殏鍋滃姞杞藉浘鐗囷紝榛樿涓?false
         */
        public IntentBuilder pauseOnScroll(boolean pauseOnScroll) {
            mIntent.putExtra(EXTRA_PAUSE_ON_SCROLL, pauseOnScroll);
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

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.bga_pp_activity_photo_picker);
        mContentRv = findViewById(R.id.rv_photo_picker_content);
    }

    @Override
    protected void setListener() {
        mPicAdapter = new BGAPhotoPickerAdapter(mContentRv);
        mPicAdapter.setOnItemChildClickListener(this);

        if (getIntent().getBooleanExtra(EXTRA_PAUSE_ON_SCROLL, false)) {
            mContentRv.addOnScrollListener(new BGARVOnScrollListener(this));
        }
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        // 鑾峰彇鎷嶇収鍥剧墖淇濆瓨鐩綍
        File cameraFileDir = (File) getIntent().getSerializableExtra(EXTRA_CAMERA_FILE_DIR);
        if (cameraFileDir != null) {
            mTakePhotoEnabled = true;
            mPhotoHelper = new BGAPhotoHelper(cameraFileDir);
        }
        mMaxChooseCount = getIntent().getIntExtra(EXTRA_MAX_CHOOSE_COUNT, 1);
        if (mMaxChooseCount < 1) {
            mMaxChooseCount = 1;
        }

        mTopRightBtnText = getString(R.string.bga_pp_confirm);

        GridLayoutManager layoutManager = new GridLayoutManager(this, SPAN_COUNT, LinearLayoutManager.VERTICAL, false);
        mContentRv.setLayoutManager(layoutManager);
        mContentRv.addItemDecoration(BGAGridDivider.newInstanceWithSpaceRes(R.dimen.bga_pp_size_photo_divider));

        ArrayList<String> selectedPhotos = getIntent().getStringArrayListExtra(EXTRA_SELECTED_PHOTOS);
        if (selectedPhotos != null && selectedPhotos.size() > mMaxChooseCount) {
            String selectedPhoto = selectedPhotos.get(0);
            selectedPhotos.clear();
            selectedPhotos.add(selectedPhoto);
        }

        mContentRv.setAdapter(mPicAdapter);
        mPicAdapter.setSelectedPhotos(selectedPhotos);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLoadingDialog();
        mLoadPhotoTask = new BGALoadPhotoTask(this, this, mTakePhotoEnabled).perform();
    }

    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new AppCompatDialog(this);
            mLoadingDialog.setContentView(R.layout.bga_pp_dialog_loading);
            mLoadingDialog.setCancelable(false);
        }
        mLoadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_pp_menu_photo_picker, menu);
        MenuItem menuItem = menu.findItem(R.id.item_photo_picker_title);
        View actionView = menuItem.getActionView();

        mTitleTv = actionView.findViewById(R.id.tv_photo_picker_title);
        mArrowIv = actionView.findViewById(R.id.iv_photo_picker_arrow);
        mSubmitTv = actionView.findViewById(R.id.tv_photo_picker_submit);

        mTitleTv.setOnClickListener(mOnClickShowPhotoFolderListener);
        mArrowIv.setOnClickListener(mOnClickShowPhotoFolderListener);
        mSubmitTv.setOnClickListener(new BGAOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                returnSelectedPhotos(mPicAdapter.getSelectedPhotos());
            }
        });

        mTitleTv.setText(R.string.bga_pp_all_image);
        if (mCurrentPhotoFolderModel != null) {
            mTitleTv.setText(mCurrentPhotoFolderModel.name);
        }

        renderTopRightBtn();

        return true;
    }

    /**
     * 杩斿洖宸查€変腑鐨勫浘鐗囬泦鍚?     *
     * @param selectedPhotos
     */
    private void returnSelectedPhotos(ArrayList<String> selectedPhotos) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, selectedPhotos);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showPhotoFolderPw() {
        if (mArrowIv == null) {
            return;
        }

        if (mPhotoFolderPw == null) {
            mPhotoFolderPw = new BGAPhotoFolderPw(this, mToolbar, new BGAPhotoFolderPw.Delegate() {
                @Override
                public void onSelectedFolder(int position) {
                    reloadPhotos(position);
                }

                @Override
                public void executeDismissAnim() {
                    ViewCompat.animate(mArrowIv).setDuration(BGAPhotoFolderPw.ANIM_DURATION).rotation(0).start();
                }
            });
        }
        mPhotoFolderPw.setData(mPhotoFolderModels);
        mPhotoFolderPw.show();

        ViewCompat.animate(mArrowIv).setDuration(BGAPhotoFolderPw.ANIM_DURATION).rotation(-180).start();
    }

    /**
     * 鏄剧ず鍙兘閫夋嫨 mMaxChooseCount 寮犲浘鐨勬彁绀?     */
    private void toastMaxCountTip() {
        BGAPhotoPickerUtil.show(getString(R.string.bga_pp_toast_photo_picker_max, mMaxChooseCount));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                ArrayList<String> photos = new ArrayList<>(Arrays.asList(mPhotoHelper.getCameraFilePath()));

                Intent photoPickerPreview = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                        .isFromTakePhoto(true)
                        .maxChooseCount(1)
                        .previewPhotos(photos)
                        .selectedPhotos(photos)
                        .currentPosition(0)
                        .build();
                startActivityForResult(photoPickerPreview, RC_PREVIEW);
            } else if (requestCode == RC_PREVIEW) {
                if (BGAPhotoPickerPreviewActivity.getIsFromTakePhoto(data)) {
                    // 浠庢媿鐓ч瑙堢晫闈㈣繑鍥烇紝鍒锋柊鍥惧簱
                    mPhotoHelper.refreshGallery();
                }

                returnSelectedPhotos(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == RC_PREVIEW) {
            if (BGAPhotoPickerPreviewActivity.getIsFromTakePhoto(data)) {
                // 浠庢媿鐓ч瑙堢晫闈㈣繑鍥烇紝鍒犻櫎涔嬪墠鎷嶇殑鐓х墖
                mPhotoHelper.deleteCameraFile();
            } else {
                mPicAdapter.setSelectedPhotos(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
                renderTopRightBtn();
            }
        }
    }

    /**
     * 娓叉煋鍙充笂瑙掓寜閽?     */
    private void renderTopRightBtn() {
        if (mSubmitTv == null) {
            return;
        }

        if (mPicAdapter.getSelectedCount() == 0) {
            mSubmitTv.setEnabled(false);
            mSubmitTv.setText(mTopRightBtnText);
        } else {
            mSubmitTv.setEnabled(true);
            mSubmitTv.setText(mTopRightBtnText + "(" + mPicAdapter.getSelectedCount() + "/" + mMaxChooseCount + ")");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BGAPhotoHelper.onSaveInstanceState(mPhotoHelper, outState);
        outState.putStringArrayList(STATE_SELECTED_PHOTOS, mPicAdapter.getSelectedPhotos());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        BGAPhotoHelper.onRestoreInstanceState(mPhotoHelper, savedInstanceState);
        mPicAdapter.setSelectedPhotos(savedInstanceState.getStringArrayList(STATE_SELECTED_PHOTOS));
    }

    @Override
    public void onItemChildClick(ViewGroup viewGroup, View view, int position) {
        if (view.getId() == R.id.iv_item_photo_camera_camera) {
            handleTakePhoto();
        } else if (view.getId() == R.id.iv_item_photo_picker_photo) {
            changeToPreview(position);
        } else if (view.getId() == R.id.iv_item_photo_picker_flag) {
            handleClickSelectFlagIv(position);
        }
    }

    /**
     * 澶勭悊鎷嶇収
     */
    private void handleTakePhoto() {
        if (mMaxChooseCount == 1) {
            takePhoto();
        } else if (mPicAdapter.getSelectedCount() == mMaxChooseCount) {
            toastMaxCountTip();
        } else {
            takePhoto();
        }
    }

    /**
     * 鎷嶇収
     */
    private void takePhoto() {
        try {
            startActivityForResult(mPhotoHelper.getTakePhotoIntent(), REQUEST_CODE_TAKE_PHOTO);
        } catch (Exception e) {
            BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_take_photo);
        }
    }

    /**
     * 璺宠浆鍒板浘鐗囬€夋嫨棰勮鐣岄潰
     *
     * @param position 褰撳墠鐐瑰嚮鐨刬tem鐨勭储寮曚綅缃?     */
    private void changeToPreview(int position) {
        int currentPosition = position;
        if (mCurrentPhotoFolderModel.isTakePhotoEnabled()) {
            currentPosition--;
        }

        Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                .previewPhotos((ArrayList<String>) mPicAdapter.getData())
                .selectedPhotos(mPicAdapter.getSelectedPhotos())
                .maxChooseCount(mMaxChooseCount)
                .currentPosition(currentPosition)
                .isFromTakePhoto(false)
                .build();
        startActivityForResult(photoPickerPreviewIntent, RC_PREVIEW);
    }

    /**
     * 澶勭悊鐐瑰嚮閫夋嫨鎸夐挳浜嬩欢
     *
     * @param position 褰撳墠鐐瑰嚮鐨刬tem鐨勭储寮曚綅缃?     */
    private void handleClickSelectFlagIv(int position) {
        String currentPhoto = mPicAdapter.getItem(position);
        if (mMaxChooseCount == 1) {
            // 鍗曢€?
            if (mPicAdapter.getSelectedCount() > 0) {
                String selectedPhoto = mPicAdapter.getSelectedPhotos().remove(0);
                if (TextUtils.equals(selectedPhoto, currentPhoto)) {
                    mPicAdapter.notifyItemChanged(position);
                } else {
                    int preSelectedPhotoPosition = mPicAdapter.getData().indexOf(selectedPhoto);
                    mPicAdapter.notifyItemChanged(preSelectedPhotoPosition);
                    mPicAdapter.getSelectedPhotos().add(currentPhoto);
                    mPicAdapter.notifyItemChanged(position);
                }
            } else {
                mPicAdapter.getSelectedPhotos().add(currentPhoto);
                mPicAdapter.notifyItemChanged(position);
            }
            renderTopRightBtn();
        } else {
            // 澶氶€?
            if (!mPicAdapter.getSelectedPhotos().contains(currentPhoto) && mPicAdapter.getSelectedCount() == mMaxChooseCount) {
                toastMaxCountTip();
            } else {
                if (mPicAdapter.getSelectedPhotos().contains(currentPhoto)) {
                    mPicAdapter.getSelectedPhotos().remove(currentPhoto);
                } else {
                    mPicAdapter.getSelectedPhotos().add(currentPhoto);
                }
                mPicAdapter.notifyItemChanged(position);

                renderTopRightBtn();
            }
        }
    }

    private void reloadPhotos(int position) {
        if (position < mPhotoFolderModels.size()) {
            mCurrentPhotoFolderModel = mPhotoFolderModels.get(position);
            if (mTitleTv != null) {
                mTitleTv.setText(mCurrentPhotoFolderModel.name);
            }

            mPicAdapter.setPhotoFolderModel(mCurrentPhotoFolderModel);
        }
    }

    @Override
    public void onPostExecute(ArrayList<BGAPhotoFolderModel> photoFolderModels) {
        dismissLoadingDialog();
        mLoadPhotoTask = null;
        mPhotoFolderModels = photoFolderModels;
        reloadPhotos(mPhotoFolderPw == null ? 0 : mPhotoFolderPw.getCurrentPosition());
    }

    @Override
    public void onTaskCancelled() {
        dismissLoadingDialog();
        mLoadPhotoTask = null;
    }

    private void cancelLoadPhotoTask() {
        if (mLoadPhotoTask != null) {
            mLoadPhotoTask.cancelTask();
            mLoadPhotoTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        cancelLoadPhotoTask();

        super.onDestroy();
    }
}
