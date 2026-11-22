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
package cn.bingoogolapple.photopicker.pw;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;

/**
 * 浣滆€?鐜嬫旦 閭欢:bingoogolapple@gmail.com
 * 鍒涘缓鏃堕棿:16/6/24 涓嬪崍6:19
 * 鎻忚堪:
 */
public abstract class BGABasePopupWindow extends PopupWindow implements View.OnClickListener {
    protected Activity mActivity;
    protected View mWindowRootView;
    protected View mAnchorView;

    public BGABasePopupWindow(Activity activity, @LayoutRes int layoutId, View anchorView, int width, int height) {
        super(View.inflate(activity, layoutId, null), width, height, true);
        init(activity, anchorView);

        initView();
        setListener();
        processLogic();
    }

    private void init(Activity activity, View anchorView) {
        getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mAnchorView = anchorView;
        mActivity = activity;
        mWindowRootView = activity.getWindow().peekDecorView();
    }

    protected abstract void initView();

    protected abstract void setListener();

    protected abstract void processLogic();

    public abstract void show();

    @Override
    public void onClick(View view) {
    }

    /**
     * 鏌ユ壘View
     *
     * @param id   鎺т欢鐨刬d
     * @param <VT> View绫诲瀷
     * @return
     */
    protected <VT extends View> VT findViewById(@IdRes int id) {
        return getContentView().findViewById(id);
    }
}
