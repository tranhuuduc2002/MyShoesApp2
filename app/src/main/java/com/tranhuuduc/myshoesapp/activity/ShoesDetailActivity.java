package com.tranhuuduc.myshoesapp.activity;


import android.os.Bundle;
import android.view.MenuItem;

import androidx.viewpager2.widget.ViewPager2;

import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.adapter.MyPagerAdapter;
import com.tranhuuduc.myshoesapp.constant.Constants;
import com.tranhuuduc.myshoesapp.model.ShoesModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ShoesDetailActivity extends BaseActivity {
    private ShoesModel mShoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoes_detail);

        getDataIntent();
        initToolbar();
        initView();
    }

    public void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        mShoes = (ShoesModel) bundle.get(Constants.KEY_INTENT_SHOES_OBJECT);
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mShoes.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager_2);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(this, mShoes);
        viewPager2.setAdapter(myPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText(getString(R.string.label_added));
            } else {
                tab.setText(getString(R.string.label_used));
            }
        }).attach();
    }
}