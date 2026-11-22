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
package cn.bingoogolapple.photopicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.baseadapter.BGAGridDivider;
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGARecyclerViewHolder;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;

/**
 * 浣滆€?鐜嬫旦 閭欢:bingoogolapple@gmail.com
 * 鍒涘缓鏃堕棿:16/7/8 涓嬪崍4:51
 * 鎻忚堪:鎷栨嫿鎺掑簭涔濆鏍煎浘鐗囨帶浠? */
public class BGASortableNinePhotoLayout extends RecyclerView implements BGAOnItemChildClickListener, BGAOnRVItemClickListener {
    private PhotoAdapter mPhotoAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private Delegate mDelegate;
    private GridLayoutManager mGridLayoutManager;

    private boolean mPlusEnable;
    private boolean mSortable;
    private int mDeleteDrawableResId;
    private boolean mDeleteDrawableOverlapQuarter;
    private int mPhotoTopRightMargin;
    private int mMaxItemCount;
    private int mItemSpanCount;
    private int mPlusDrawableResId;
    private int mItemCornerRadius;
    private int mItemWhiteSpacing;
    private int mOtherWhiteSpacing;
    private int mPlaceholderDrawableResId;
    private boolean mEditable;

    private int mItemWidth;

    public BGASortableNinePhotoLayout(Context context) {
        this(context, null);
    }

    public BGASortableNinePhotoLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BGASortableNinePhotoLayout(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDefaultAttrs();
        initCustomAttrs(context, attrs);
        afterInitDefaultAndCustomAttrs();
    }

    private void initDefaultAttrs() {
        mPlusEnable = true;
        mSortable = true;
        mEditable = true;
        mDeleteDrawableResId = R.mipmap.bga_pp_ic_delete;
        mDeleteDrawableOverlapQuarter = false;
        mMaxItemCount = 9;
        mItemSpanCount = 3;
        mItemWidth = 0;
        mItemCornerRadius = 0;
        mPlusDrawableResId = R.mipmap.bga_pp_ic_plus;
        mItemWhiteSpacing = BGABaseAdapterUtil.dp2px(4);
        mPlaceholderDrawableResId = R.mipmap.bga_pp_ic_holder_light;
        mOtherWhiteSpacing = BGABaseAdapterUtil.dp2px(100);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BGASortableNinePhotoLayout);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_plusEnable) {
            mPlusEnable = typedArray.getBoolean(attr, mPlusEnable);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_sortable) {
            mSortable = typedArray.getBoolean(attr, mSortable);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_deleteDrawable) {
            mDeleteDrawableResId = typedArray.getResourceId(attr, mDeleteDrawableResId);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_deleteDrawableOverlapQuarter) {
            mDeleteDrawableOverlapQuarter = typedArray.getBoolean(attr, mDeleteDrawableOverlapQuarter);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_maxItemCount) {
            mMaxItemCount = typedArray.getInteger(attr, mMaxItemCount);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_itemSpanCount) {
            mItemSpanCount = typedArray.getInteger(attr, mItemSpanCount);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_plusDrawable) {
            mPlusDrawableResId = typedArray.getResourceId(attr, mPlusDrawableResId);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_itemCornerRadius) {
            mItemCornerRadius = typedArray.getDimensionPixelSize(attr, 0);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_itemWhiteSpacing) {
            mItemWhiteSpacing = typedArray.getDimensionPixelSize(attr, mItemWhiteSpacing);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_otherWhiteSpacing) {
            mOtherWhiteSpacing = typedArray.getDimensionPixelOffset(attr, mOtherWhiteSpacing);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_placeholderDrawable) {
            mPlaceholderDrawableResId = typedArray.getResourceId(attr, mPlaceholderDrawableResId);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_editable) {
            mEditable = typedArray.getBoolean(attr, mEditable);
        } else if (attr == R.styleable.BGASortableNinePhotoLayout_bga_snpl_itemWidth) {
            mItemWidth = typedArray.getDimensionPixelSize(attr, mItemWidth);
        }
    }

    private void afterInitDefaultAndCustomAttrs() {
        if (mItemWidth == 0) {
            mItemWidth = (BGAPhotoPickerUtil.getScreenWidth() - mOtherWhiteSpacing) / mItemSpanCount;
        } else {
            mItemWidth += mItemWhiteSpacing;
        }

        setOverScrollMode(OVER_SCROLL_NEVER);
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback());
        mItemTouchHelper.attachToRecyclerView(this);

        mGridLayoutManager = new GridLayoutManager(getContext(), mItemSpanCount);
        setLayoutManager(mGridLayoutManager);
        addItemDecoration(BGAGridDivider.newInstanceWithSpacePx(mItemWhiteSpacing / 2));

        calculatePhotoTopRightMargin();

        mPhotoAdapter = new PhotoAdapter(this);
        mPhotoAdapter.setOnItemChildClickListener(this);
        mPhotoAdapter.setOnRVItemClickListener(this);
        setAdapter(mPhotoAdapter);
    }

    /**
     * 璁剧疆鏄惁鍙嫋鎷芥帓搴忥紝榛樿鍊间负 true
     *
     * @param sortable
     */
    public void setSortable(boolean sortable) {
        mSortable = sortable;
    }

    /**
     * 鑾峰彇鏄惁鍙嫋鎷芥帓搴?     *
     * @return
     */
    public boolean isSortable() {
        return mSortable;
    }

    /**
     * 璁剧疆鏄惁鍙紪杈戯紝榛樿鍊间负 true
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        mEditable = editable;
        mPhotoAdapter.notifyDataSetChanged();
    }

    /**
     * 鑾峰彇鏄惁鍙紪杈?     *
     * @return
     */
    public boolean isEditable() {
        return mEditable;
    }

    /**
     * 璁剧疆鍒犻櫎鎸夐挳鍥剧墖璧勬簮id锛岄粯璁ゅ€间负
     *
     * @param deleteDrawableResId
     */
    public void setDeleteDrawableResId(@DrawableRes int deleteDrawableResId) {
        mDeleteDrawableResId = deleteDrawableResId;
        calculatePhotoTopRightMargin();
    }

    /**
     * 璁剧疆鍒犻櫎鎸夐挳鏄惁閲嶅彔鍥涘垎涔嬩竴锛岄粯璁ゅ€间负 false
     *
     * @param deleteDrawableOverlapQuarter
     */
    public void setDeleteDrawableOverlapQuarter(boolean deleteDrawableOverlapQuarter) {
        mDeleteDrawableOverlapQuarter = deleteDrawableOverlapQuarter;
        calculatePhotoTopRightMargin();
    }

    /**
     * 璁＄畻鍥剧墖鍙充笂瑙?margin
     */
    private void calculatePhotoTopRightMargin() {
        if (mDeleteDrawableOverlapQuarter) {
            int deleteDrawableWidth = BitmapFactory.decodeResource(getResources(), mDeleteDrawableResId).getWidth();
            int deleteDrawablePadding = getResources().getDimensionPixelOffset(R.dimen.bga_pp_size_delete_padding);
            mPhotoTopRightMargin = deleteDrawablePadding + deleteDrawableWidth / 2;
        } else {
            mPhotoTopRightMargin = 0;
        }
    }

    /**
     * 璁剧疆鍙€夋嫨鍥剧墖鐨勬€诲紶鏁?榛樿鍊间负 9
     *
     * @param maxItemCount
     */
    public void setMaxItemCount(int maxItemCount) {
        mMaxItemCount = maxItemCount;
    }

    /**
     * 鑾峰彇閫夋嫨鐨勫浘鐗囩殑鏈€澶у紶鏁?     *
     * @return
     */
    public int getMaxItemCount() {
        return mMaxItemCount;
    }

    /**
     * 璁剧疆鍒楁暟,榛樿鍊间负 3
     *
     * @param itemSpanCount
     */
    public void setItemSpanCount(int itemSpanCount) {
        mItemSpanCount = itemSpanCount;
        mGridLayoutManager.setSpanCount(mItemSpanCount);
    }

    /**
     * 璁剧疆娣诲姞鎸夐挳鍥剧墖锛岄粯璁ゅ€间负 R.mipmap.bga_pp_ic_plus
     *
     * @param plusDrawableResId
     */
    public void setPlusDrawableResId(@DrawableRes int plusDrawableResId) {
        mPlusDrawableResId = plusDrawableResId;
    }

    /**
     * 璁剧疆 Item 鏉＄洰鍦嗚灏哄锛岄粯璁ゅ€间负 0dp
     *
     * @param itemCornerRadius
     */
    public void setItemCornerRadius(int itemCornerRadius) {
        mItemCornerRadius = itemCornerRadius;
    }

    /**
     * 璁剧疆鍥剧墖璺緞鏁版嵁闆嗗悎
     *
     * @param photos
     */
    public void setData(ArrayList<String> photos) {
        mPhotoAdapter.setData(photos);
    }

    /**
     * 鍦ㄩ泦鍚堝熬閮ㄦ坊鍔犳洿澶氭暟鎹泦鍚?     *
     * @param photos
     */
    public void addMoreData(ArrayList<String> photos) {
        if (photos != null) {
            mPhotoAdapter.getData().addAll(photos);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 鍦ㄩ泦鍚堢殑灏鹃儴娣诲姞涓€鏉℃暟鎹?     *
     * @param photo
     */
    public void addLastItem(String photo) {
        if (!TextUtils.isEmpty(photo)) {
            mPhotoAdapter.getData().add(photo);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int spanCount = mItemSpanCount;
        int itemCount = mPhotoAdapter.getItemCount();
        if (itemCount > 0 && itemCount < mItemSpanCount) {
            spanCount = itemCount;
        }
        mGridLayoutManager.setSpanCount(spanCount);

        int expectWidth = mItemWidth * spanCount;
        int expectHeight = 0;
        if (itemCount > 0) {
            int rowCount = (itemCount - 1) / spanCount + 1;
            expectHeight = mItemWidth * rowCount;
        }

        int width = resolveSize(expectWidth, widthMeasureSpec);
        int height = resolveSize(expectHeight, heightMeasureSpec);
        width = Math.min(width, expectWidth);
        height = Math.min(height, expectHeight);

        setMeasuredDimension(width, height);
    }

    /**
     * 鑾峰彇鍥剧墖璺緞鏁版嵁闆嗗悎
     *
     * @return
     */
    public ArrayList<String> getData() {
        return (ArrayList<String>) mPhotoAdapter.getData();
    }

    /**
     * 鍒犻櫎鎸囧畾绱㈠紩浣嶇疆鐨勫浘鐗?     *
     * @param position
     */
    public void removeItem(int position) {
        mPhotoAdapter.removeItem(position);
    }

    /**
     * 鑾峰彇鍥剧墖鎬绘暟
     *
     * @return
     */
    public int getItemCount() {
        return mPhotoAdapter.getData().size();
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if (mDelegate != null) {
            mDelegate.onClickDeleteNinePhotoItem(this, childView, position, mPhotoAdapter.getItem(position), getData());
        }
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        if (mPhotoAdapter.isPlusItem(position)) {
            if (mDelegate != null) {
                mDelegate.onClickAddNinePhotoItem(this, itemView, position, getData());
            }
        } else {
            if (mDelegate != null && ViewCompat.getScaleX(itemView) <= 1.0f) {
                mDelegate.onClickNinePhotoItem(this, itemView, position, mPhotoAdapter.getItem(position), getData());
            }
        }
    }

    /**
     * 璁剧疆鏄惁鏄剧ず鍔犲彿
     *
     * @param plusEnable
     */
    public void setPlusEnable(boolean plusEnable) {
        mPlusEnable = plusEnable;
        mPhotoAdapter.notifyDataSetChanged();
    }

    /**
     * 鏄惁鏄剧ず鍔犲彿鎸夐挳
     *
     * @return
     */
    public boolean isPlusEnable() {
        return mPlusEnable;
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    private class PhotoAdapter extends BGARecyclerViewAdapter<String> {
        private int mImageSize;

        public PhotoAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.bga_pp_item_nine_photo);
            mImageSize = BGAPhotoPickerUtil.getScreenWidth() / (mItemSpanCount > 3 ? 8 : 6);
        }

        @Override
        protected void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
            helper.setItemChildClickListener(R.id.iv_item_nine_photo_flag);
        }

        @Override
        public int getItemCount() {
            if (mEditable && mPlusEnable && super.getItemCount() < mMaxItemCount) {
                return super.getItemCount() + 1;
            }

            return super.getItemCount();
        }

        @Override
        public String getItem(int position) {
            if (isPlusItem(position)) {
                return null;
            }

            return super.getItem(position);
        }

        public boolean isPlusItem(int position) {
            return mEditable && mPlusEnable && super.getItemCount() < mMaxItemCount && position == getItemCount() - 1;
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, String model) {
            MarginLayoutParams params = (MarginLayoutParams) helper.getView(R.id.iv_item_nine_photo_photo).getLayoutParams();
            params.setMargins(0, mPhotoTopRightMargin, mPhotoTopRightMargin, 0);

            if (mItemCornerRadius > 0) {
                BGAImageView imageView = helper.getView(R.id.iv_item_nine_photo_photo);
                imageView.setCornerRadius(mItemCornerRadius);
            }

            if (isPlusItem(position)) {
                helper.setVisibility(R.id.iv_item_nine_photo_flag, View.GONE);
                helper.setImageResource(R.id.iv_item_nine_photo_photo, mPlusDrawableResId);
            } else {
                if (mEditable) {
                    helper.setVisibility(R.id.iv_item_nine_photo_flag, View.VISIBLE);
                    helper.setImageResource(R.id.iv_item_nine_photo_flag, mDeleteDrawableResId);
                } else {
                    helper.setVisibility(R.id.iv_item_nine_photo_flag, View.GONE);
                }
                BGAImage.display(helper.getImageView(R.id.iv_item_nine_photo_photo), mPlaceholderDrawableResId, model, mImageSize);
            }
        }
    }

    private class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

        @Override
        public boolean isLongPressDragEnabled() {
            return mEditable && mSortable && mPhotoAdapter.getData().size() > 1;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            if (mPhotoAdapter.isPlusItem(viewHolder.getAdapterPosition())) {
                dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
            }
            int swipeFlags = ItemTouchHelper.ACTION_STATE_IDLE;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType() || mPhotoAdapter.isPlusItem(target.getAdapterPosition())) {
                return false;
            }
            mPhotoAdapter.moveItem(source.getAdapterPosition(), target.getAdapterPosition());
            if (mDelegate != null) {
                mDelegate.onNinePhotoItemExchanged(BGASortableNinePhotoLayout.this, source.getAdapterPosition(), target.getAdapterPosition(), getData());
            }
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ACTION_STATE_IDLE) {
                ViewCompat.setScaleX(viewHolder.itemView, 1.2f);
                ViewCompat.setScaleY(viewHolder.itemView, 1.2f);
                ((BGARecyclerViewHolder) viewHolder).getViewHolderHelper().getImageView(R.id.iv_item_nine_photo_photo).setColorFilter(getResources().getColor(R.color.bga_pp_photo_selected_mask));
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            ViewCompat.setScaleX(viewHolder.itemView, 1.0f);
            ViewCompat.setScaleY(viewHolder.itemView, 1.0f);
            ((BGARecyclerViewHolder) viewHolder).getViewHolderHelper().getImageView(R.id.iv_item_nine_photo_photo).setColorFilter(null);
            super.clearView(recyclerView, viewHolder);
        }
    }

    public interface Delegate {
        void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models);

        void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models);

        void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models);

        void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout, int fromPosition, int toPosition, ArrayList<String> models);
    }
}
