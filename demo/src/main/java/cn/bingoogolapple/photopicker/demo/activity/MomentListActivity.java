package cn.bingoogolapple.photopicker.demo.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.baseadapter.BGAOnRVItemLongClickListener;
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.photopicker.demo.R;
import cn.bingoogolapple.photopicker.demo.model.Moment;
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener;
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 浣犺嚜宸遍」鐩噷銆屽彲浠ヤ笉缁ф壙 BGAPPToolbarActivity銆嶏紝鎴戝湪杩欓噷缁ф壙 BGAPPToolbarActivity 鍙槸涓轰簡鏂逛究鍐?Demo
 * BGAOnRVItemClickListener鍜孊GAOnRVItemLongClickListener杩欎袱涓帴鍙ｆ槸涓轰簡娴嬭瘯浜嬩欢浼犻€掓槸鍚︽纭紝浣犺嚜宸辩殑椤圭洰閲屽彲浠ヤ笉瀹炵幇杩欎袱涓帴鍙? */
public class MomentListActivity extends BGAPPToolbarActivity implements EasyPermissions.PermissionCallbacks, BGANinePhotoLayout.Delegate, BGAOnRVItemClickListener, BGAOnRVItemLongClickListener {
    private static final int PRC_PHOTO_PREVIEW = 1;

    private static final int RC_ADD_MOMENT = 1;

    private RecyclerView mMomentRv;
    private MomentAdapter mMomentAdapter;

    /**
     * 璁剧疆鍥剧墖棰勮鏃舵槸鍚﹀叿鏈変繚瀛樺浘鐗囧姛鑳姐€屾祴璇曟帴鍙ｇ敤鐨勩€?     */
    private CheckBox mDownLoadableCb;

    private BGANinePhotoLayout mCurrentClickNpl;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_moment_list);
        mDownLoadableCb = findViewById(R.id.cb_moment_list_downloadable);
        mMomentRv = findViewById(R.id.rv_moment_list_moments);
    }

    @Override
    protected void setListener() {
        mMomentAdapter = new MomentAdapter(mMomentRv);
        mMomentAdapter.setOnRVItemClickListener(this);
        mMomentAdapter.setOnRVItemLongClickListener(this);

        mMomentRv.addOnScrollListener(new BGARVOnScrollListener(this));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle("鏈嬪弸鍦堝垪琛?);

        mMomentRv.setLayoutManager(new LinearLayoutManager(this));
        mMomentRv.setAdapter(mMomentAdapter);

        addNetImageTestData();
    }

    /**
     * 娣诲姞缃戠粶鍥剧墖娴嬭瘯鏁版嵁
     */
    private void addNetImageTestData() {
        List<Moment> moments = new ArrayList<>();

        moments.add(new Moment("1寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered1.png"))));
        moments.add(new Moment("2寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered2.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered3.png"))));
        moments.add(new Moment("9寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered11.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered12.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered13.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered14.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered15.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered16.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered17.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered18.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered19.png"))));
        moments.add(new Moment("5寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered11.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered12.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered13.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered14.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered15.png"))));
        moments.add(new Moment("3寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered4.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered5.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered6.png"))));
        moments.add(new Moment("8寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered11.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered12.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered13.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered14.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered15.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered16.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered17.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered18.png"))));
        moments.add(new Moment("4寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered7.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered8.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered9.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered10.png"))));
        moments.add(new Moment("2寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered2.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered3.png"))));
        moments.add(new Moment("3寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered4.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered5.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered6.png"))));
        moments.add(new Moment("4寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered7.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered8.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered9.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered10.png"))));
        moments.add(new Moment("9寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered11.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered12.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered13.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered14.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered15.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered16.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered17.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered18.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered19.png"))));
        moments.add(new Moment("1寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered1.png"))));
        moments.add(new Moment("5寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered11.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered12.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered13.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered14.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered15.png"))));
        moments.add(new Moment("6寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered11.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered12.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered13.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered14.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered15.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered16.png"))));
        moments.add(new Moment("7寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered11.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered12.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered13.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered14.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered15.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered16.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered17.png"))));
        moments.add(new Moment("8寮犵綉缁滃浘鐗?, new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered11.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered12.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered13.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered14.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered15.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered16.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered17.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered18.png"))));

        mMomentAdapter.setData(moments);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tv_moment_list_add) {
            startActivityForResult(new Intent(this, MomentAddActivity.class), RC_ADD_MOMENT);
        } else if (v.getId() == R.id.tv_moment_list_system) {
            startActivity(new Intent(this, SystemGalleryActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_ADD_MOMENT) {
            mMomentAdapter.addFirstItem(MomentAddActivity.getMoment(data));
            mMomentRv.smoothScrollToPosition(0);
        }
    }

    /**
     * 鍥剧墖棰勮锛屽吋瀹?.0鍔ㄦ€佹潈闄?     */
    @AfterPermissionGranted(PRC_PHOTO_PREVIEW)
    private void photoPreviewWrapper() {
        if (mCurrentClickNpl == null) {
            return;
        }

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerDownload");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(this)
                    .saveImgDir(downloadDir); // 淇濆瓨鍥剧墖鐨勭洰褰曪紝濡傛灉浼?null锛屽垯娌℃湁淇濆瓨鍥剧墖鍔熻兘

            if (mCurrentClickNpl.getItemCount() == 1) {
                // 棰勮鍗曞紶鍥剧墖
                photoPreviewIntentBuilder.previewPhoto(mCurrentClickNpl.getCurrentClickItem());
            } else if (mCurrentClickNpl.getItemCount() > 1) {
                // 棰勮澶氬紶鍥剧墖
                photoPreviewIntentBuilder.previewPhotos(mCurrentClickNpl.getData())
                        .currentPosition(mCurrentClickNpl.getCurrentClickItemPosition()); // 褰撳墠棰勮鍥剧墖鐨勭储寮?            }
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "鍥剧墖棰勮闇€瑕佷互涓嬫潈闄?\n\n1.璁块棶璁惧涓婄殑鐓х墖", PRC_PHOTO_PREVIEW, perms);
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
        if (requestCode == PRC_PHOTO_PREVIEW) {
            Toast.makeText(this, "鎮ㄦ嫆缁濅簡銆屽浘鐗囬瑙堛€嶆墍闇€瑕佺殑鐩稿叧鏉冮檺!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
        mCurrentClickNpl = ninePhotoLayout;
        photoPreviewWrapper();
    }

    @Override
    public void onRVItemClick(ViewGroup viewGroup, View view, int position) {
        Toast.makeText(this, "鐐瑰嚮浜唅tem " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onRVItemLongClick(ViewGroup viewGroup, View view, int position) {
        Toast.makeText(this, "闀挎寜浜唅tem " + position, Toast.LENGTH_SHORT).show();
        return true;
    }

    private class MomentAdapter extends BGARecyclerViewAdapter<Moment> {

        public MomentAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_moment);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, Moment moment) {
            if (TextUtils.isEmpty(moment.content)) {
                helper.setVisibility(R.id.tv_item_moment_content, View.GONE);
            } else {
                helper.setVisibility(R.id.tv_item_moment_content, View.VISIBLE);
                helper.setText(R.id.tv_item_moment_content, moment.content);
            }

            BGANinePhotoLayout ninePhotoLayout = helper.getView(R.id.npl_item_moment_photos);
            ninePhotoLayout.setDelegate(MomentListActivity.this);
            ninePhotoLayout.setData(moment.photos);
        }
    }
}
