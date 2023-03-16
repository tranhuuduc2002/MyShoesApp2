package com.tranhuuduc.myshoesapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.tranhuuduc.myshoesapp.fragment.ShoesDetailAddedFragment;
import com.tranhuuduc.myshoesapp.fragment.ShoesDetailUsedFragment;
import com.tranhuuduc.myshoesapp.model.ShoesModel;

public class MyPagerAdapter extends FragmentStateAdapter {

    private final ShoesModel mShoes;

    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity, ShoesModel mShoes) {
        super(fragmentActivity);
        this.mShoes = mShoes;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new ShoesDetailUsedFragment(mShoes);
        }
        return new ShoesDetailAddedFragment(mShoes);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
