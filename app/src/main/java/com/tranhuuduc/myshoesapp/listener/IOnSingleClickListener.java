package com.tranhuuduc.myshoesapp.listener;

import android.os.SystemClock;
import android.view.View;

public abstract class IOnSingleClickListener implements View.OnClickListener{

    private static final long MIN_CLICK_INTERVAL = 600; // ms
    private long mLastClickTime;

    public abstract void onSingleClick(View view);

    @Override
    public void onClick(View view) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime =currentClickTime;
        if(elapsedTime <= MIN_CLICK_INTERVAL){
            return;
        }
        onSingleClick(view);
    }

}
