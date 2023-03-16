package com.tranhuuduc.myshoesapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;

import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.constant.GlobalFunction;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(this::gotoFeatureActivity, 2000);
    }

    private void gotoFeatureActivity(){
        GlobalFunction.startActivity(this, FeatureActivity.class);
        finish();
    }

}