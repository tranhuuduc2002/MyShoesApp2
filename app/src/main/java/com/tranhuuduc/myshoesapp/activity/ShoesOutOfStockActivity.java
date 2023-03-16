package com.tranhuuduc.myshoesapp.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tranhuuduc.myshoesapp.MyShoesApplication;
import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.adapter.ManageShoesAdapter;
import com.tranhuuduc.myshoesapp.constant.GlobalFunction;
import com.tranhuuduc.myshoesapp.listener.IOnSingleClickListener;
import com.tranhuuduc.myshoesapp.model.ShoesModel;
import com.tranhuuduc.myshoesapp.utils.StringUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class ShoesOutOfStockActivity extends BaseActivity {

    private List<ShoesModel> mListShoes;
    private ManageShoesAdapter mManageShoesAdapter;

    private EditText edtSearchName;
    private String mKeySearch;
    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            ShoesModel Shoes = dataSnapshot.getValue(ShoesModel.class);
            if (Shoes == null || mListShoes == null || mManageShoesAdapter == null) {
                return;
            }
            if (Shoes.getQuantity() <= 0) {
                if (StringUtil.isEmpty(mKeySearch)) {
                    mListShoes.add(0, Shoes);
                } else {
                    if (GlobalFunction.getTextSearch(Shoes.getName().toLowerCase())
                            .contains(GlobalFunction.getTextSearch(mKeySearch).toLowerCase())) {
                        mListShoes.add(0, Shoes);
                    }
                }
            }
            mManageShoesAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            ShoesModel Shoes = dataSnapshot.getValue(ShoesModel.class);
            if (Shoes == null || mListShoes == null || mListShoes.isEmpty() || mManageShoesAdapter == null) {
                return;
            }
            if (Shoes.getQuantity() > 0) {
                for (ShoesModel ShoesObject : mListShoes) {
                    if (Shoes.getId() == ShoesObject.getId()) {
                        mListShoes.remove(ShoesObject);
                        break;
                    }
                }
            } else {
                for (int i = 0; i < mListShoes.size(); i++) {
                    if (Shoes.getId() == mListShoes.get(i).getId()) {
                        mListShoes.set(i, Shoes);
                        break;
                    }
                }
            }
            mManageShoesAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            ShoesModel Shoes = dataSnapshot.getValue(ShoesModel.class);
            if (Shoes == null || mListShoes == null || mListShoes.isEmpty() || mManageShoesAdapter == null) {
                return;
            }
            for (ShoesModel ShoesObject : mListShoes) {
                if (Shoes.getId() == ShoesObject.getId()) {
                    mListShoes.remove(ShoesObject);
                    break;
                }
            }
            mManageShoesAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            showToast(getString(R.string.msg_get_data_error));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoes_out_of_stock);

        initToolbar();
        initUi();
        getListShoes();
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.feature_shoes_out_of_stock));
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

    private void initUi() {
        edtSearchName = findViewById(R.id.edt_search_name);
        ImageView imgSearch = findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                searchShoes();
            }
        });

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchShoes();
                return true;
            }
            return false;
        });

        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    mKeySearch = "";
                    getListShoes();
                    GlobalFunction.hideSoftKeyboard(ShoesOutOfStockActivity.this);
                }
            }
        });

        RecyclerView rcvShoes = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvShoes.setLayoutManager(linearLayoutManager);

        mListShoes = new ArrayList<>();
        mManageShoesAdapter = new ManageShoesAdapter(mListShoes, Shoes
                -> GlobalFunction.goToShoesDetailActivity(this, Shoes));
        rcvShoes.setAdapter(mManageShoesAdapter);
    }

    public void getListShoes() {
        if (mListShoes != null) {
            mListShoes.clear();
            MyShoesApplication.get(this).getShoesDatabaseReference().removeEventListener(mChildEventListener);
        }
        MyShoesApplication.get(this).getShoesDatabaseReference().addChildEventListener(mChildEventListener);
    }

    private void searchShoes() {
        if (mListShoes == null || mListShoes.isEmpty()) {
            GlobalFunction.hideSoftKeyboard(this);
            return;
        }
        mKeySearch = edtSearchName.getText().toString().trim();
        getListShoes();
        GlobalFunction.hideSoftKeyboard(this);
    }
}