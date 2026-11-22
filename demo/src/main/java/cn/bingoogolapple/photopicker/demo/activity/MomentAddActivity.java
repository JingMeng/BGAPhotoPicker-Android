package cn.bingoogolapple.photopicker.demo.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.demo.R;
import cn.bingoogolapple.photopicker.demo.model.Moment;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 浣犺嚜宸遍」鐩噷銆屽彲浠ヤ笉缁ф壙 BGAPPToolbarActivity銆嶏紝鎴戝湪杩欓噷缁ф壙 BGAPPToolbarActivity 鍙槸涓轰簡鏂逛究鍐?Demo
 */
public class MomentAddActivity extends BGAPPToolbarActivity implements EasyPermissions.PermissionCallbacks, BGASortableNinePhotoLayout.Delegate {
    private static final int PRC_PHOTO_PICKER = 1;

    private static final int RC_CHOOSE_PHOTO = 1;
    private static final int RC_PHOTO_PREVIEW = 2;

    private static final String EXTRA_MOMENT = "EXTRA_MOMENT";

    // ==================================== 娴嬭瘯鍥剧墖閫夋嫨鍣?START ====================================
    /**
     * 鏄惁鏄崟閫夈€屾祴璇曟帴鍙ｇ敤鐨勩€?     */
    private CheckBox mSingleChoiceCb;
    /**
     * 鏄惁鍏锋湁鎷嶇収鍔熻兘銆屾祴璇曟帴鍙ｇ敤鐨勩€?     */
    private CheckBox mTakePhotoCb;
    // ==================================== 娴嬭瘯鍥剧墖閫夋嫨鍣?END ====================================

    // ==================================== 娴嬭瘯鎷栨嫿鎺掑簭涔濆鏍煎浘鐗囨帶浠?START ====================================
    /**
     * 鏄惁鍙紪杈?     */
    private CheckBox mEditableCb;
    /**
     * 鏄惁鏄剧ず涔濆浘鎺т欢鐨勫姞鍙锋寜閽€屾祴璇曟帴鍙ｇ敤鐨勩€?     */
    private CheckBox mPlusCb;
    /**
     * 鏄惁寮€鍚嫋鎷芥帓搴忓姛鑳姐€屾祴璇曟帴鍙ｇ敤鐨勩€?     */
    private CheckBox mSortableCb;
    /**
     * 鎷栨嫿鎺掑簭涔濆鏍兼帶浠?     */
    private BGASortableNinePhotoLayout mPhotosSnpl;
    // ==================================== 娴嬭瘯鎷栨嫿鎺掑簭涔濆鏍煎浘鐗囨帶浠?END ====================================

    private EditText mContentEt;

    public static Moment getMoment(Intent intent) {
        return intent.getParcelableExtra(EXTRA_MOMENT);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_moment_add);
        mSingleChoiceCb = findViewById(R.id.cb_moment_add_single_choice);
        mTakePhotoCb = findViewById(R.id.cb_moment_add_take_photo);

        mEditableCb = findViewById(R.id.cb_moment_add_editable);
        mPlusCb = findViewById(R.id.cb_moment_add_plus);
        mSortableCb = findViewById(R.id.cb_moment_add_sortable);

        mContentEt = findViewById(R.id.et_moment_add_content);
        mPhotosSnpl = findViewById(R.id.snpl_moment_add_photos);
    }

    @Override
    protected void setListener() {
        mSingleChoiceCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    mPhotosSnpl.setData(null);
                    mPhotosSnpl.setMaxItemCount(1);
                } else {
                    mPhotosSnpl.setMaxItemCount(9);
                }
            }
        });
        mEditableCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                mPhotosSnpl.setEditable(checked);
            }
        });
        mPlusCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                mPhotosSnpl.setPlusEnable(checked);
            }
        });
        mSortableCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                mPhotosSnpl.setSortable(checked);
            }
        });

        // 璁剧疆鎷栨嫿鎺掑簭鎺т欢鐨勪唬鐞?        mPhotosSnpl.setDelegate(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("娣诲姞鏈嬪弸鍦?);

        mEditableCb.setChecked(mPhotosSnpl.isEditable());
        mPlusCb.setChecked(mPhotosSnpl.isPlusEnable());
        mSortableCb.setChecked(mPhotosSnpl.isSortable());
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tv_moment_add_choice_photo) {
            choicePhotoWrapper();
        } else if (v.getId() == R.id.tv_moment_add_publish) {
            String content = mContentEt.getText().toString().trim();
            if (content.length() == 0 && mPhotosSnpl.getItemCount() == 0) {
                Toast.makeText(this, "蹇呴』濉啓杩欎竴鍒荤殑鎯虫硶鎴栭€夋嫨鐓х墖锛?, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent();
            intent.putExtra(EXTRA_MOMENT, new Moment(mContentEt.getText().toString().trim(), mPhotosSnpl.getData()));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
        choicePhotoWrapper();
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        mPhotosSnpl.removeItem(position);
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                .previewPhotos(models) // 褰撳墠棰勮鐨勫浘鐗囪矾寰勯泦鍚?                .selectedPhotos(models) // 褰撳墠宸查€変腑鐨勫浘鐗囪矾寰勯泦鍚?                .maxChooseCount(mPhotosSnpl.getMaxItemCount()) // 鍥剧墖閫夋嫨寮犳暟鐨勬渶澶у€?                .currentPosition(position) // 褰撳墠棰勮鍥剧墖鐨勭储寮?                .isFromTakePhoto(false) // 鏄惁鏄媿瀹岀収鍚庤烦杞繃鏉?                .build();
        startActivityForResult(photoPickerPreviewIntent, RC_PHOTO_PREVIEW);
    }

    @Override
    public void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout, int fromPosition, int toPosition, ArrayList<String> models) {
        Toast.makeText(this, "鎺掑簭鍙戠敓鍙樺寲", Toast.LENGTH_SHORT).show();
    }

    @AfterPermissionGranted(PRC_PHOTO_PICKER)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 鎷嶇収鍚庣収鐗囩殑瀛樻斁鐩綍锛屾敼鎴愪綘鑷繁鎷嶇収鍚庤瀛樻斁鐓х墖鐨勭洰褰曘€傚鏋滀笉浼犻€掕鍙傛暟鐨勮瘽灏辨病鏈夋媿鐓у姛鑳?            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");

            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    .cameraFileDir(mTakePhotoCb.isChecked() ? takePhotoDir : null) // 鎷嶇収鍚庣収鐗囩殑瀛樻斁鐩綍锛屾敼鎴愪綘鑷繁鎷嶇収鍚庤瀛樻斁鐓х墖鐨勭洰褰曘€傚鏋滀笉浼犻€掕鍙傛暟鐨勮瘽鍒欎笉寮€鍚浘搴撻噷鐨勬媿鐓у姛鑳?                    .maxChooseCount(mPhotosSnpl.getMaxItemCount() - mPhotosSnpl.getItemCount()) // 鍥剧墖閫夋嫨寮犳暟鐨勬渶澶у€?                    .selectedPhotos(null) // 褰撳墠宸查€変腑鐨勫浘鐗囪矾寰勯泦鍚?                    .pauseOnScroll(false) // 婊氬姩鍒楄〃鏃舵槸鍚︽殏鍋滃姞杞藉浘鐗?                    .build();
            startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "鍥剧墖閫夋嫨闇€瑕佷互涓嬫潈闄?\n\n1.璁块棶璁惧涓婄殑鐓х墖\n\n2.鎷嶇収", PRC_PHOTO_PICKER, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == PRC_PHOTO_PICKER) {
            Toast.makeText(this, "鎮ㄦ嫆缁濅簡銆屽浘鐗囬€夋嫨銆嶆墍闇€瑕佺殑鐩稿叧鏉冮檺!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            if (mSingleChoiceCb.isChecked()) {
                mPhotosSnpl.setData(BGAPhotoPickerActivity.getSelectedPhotos(data));
            } else {
                mPhotosSnpl.addMoreData(BGAPhotoPickerActivity.getSelectedPhotos(data));
            }
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            mPhotosSnpl.setData(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
        }
    }
}
