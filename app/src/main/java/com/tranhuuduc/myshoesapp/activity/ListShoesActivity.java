package com.tranhuuduc.myshoesapp.activity;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tranhuuduc.myshoesapp.MyShoesApplication;
import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.adapter.ShoesAdapter;
import com.tranhuuduc.myshoesapp.adapter.SelectUnitAdapter;
import com.tranhuuduc.myshoesapp.constant.GlobalFunction;
import com.tranhuuduc.myshoesapp.listener.IOnSingleClickListener;
import com.tranhuuduc.myshoesapp.model.ShoesModel;
import com.tranhuuduc.myshoesapp.model.HistoryModel;
import com.tranhuuduc.myshoesapp.model.UnitObjectModel;
import com.tranhuuduc.myshoesapp.utils.StringUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListShoesActivity extends BaseActivity {

    private List<ShoesModel> mListShoes;
    private ShoesAdapter mShoesAdapter;

    private List<UnitObjectModel> mListUnit;
    private UnitObjectModel mUnitSelected;

    private EditText edtSearchName;
    private String mKeySearch;
    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            ShoesModel Shoes = dataSnapshot.getValue(ShoesModel.class);
            if (Shoes == null || mListShoes == null || mShoesAdapter == null) {
                return;
            }
            if (StringUtil.isEmpty(mKeySearch)) {
                mListShoes.add(0, Shoes);
            } else {
                if (GlobalFunction.getTextSearch(Shoes.getName().toLowerCase())
                        .contains(GlobalFunction.getTextSearch(mKeySearch).toLowerCase())) {
                    mListShoes.add(0, Shoes);
                }
            }
            mShoesAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            ShoesModel Shoes = dataSnapshot.getValue(ShoesModel.class);
            if (Shoes == null || mListShoes == null || mListShoes.isEmpty() || mShoesAdapter == null) {
                return;
            }
            for (int i = 0; i < mListShoes.size(); i++) {
                if (Shoes.getId() == mListShoes.get(i).getId()) {
                    mListShoes.set(i, Shoes);
                    break;
                }
            }
            mShoesAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            ShoesModel Shoes = dataSnapshot.getValue(ShoesModel.class);
            if (Shoes == null || mListShoes == null || mListShoes.isEmpty() || mShoesAdapter == null) {
                return;
            }
            for (ShoesModel ShoesObject : mListShoes) {
                if (Shoes.getId() == ShoesObject.getId()) {
                    mListShoes.remove(ShoesObject);
                    break;
                }
            }
            mShoesAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_list_shoes);

        initToolbar();
        initUi();
        getListUnit();
        getListShoes();
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.feature_list_menu));
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
                    GlobalFunction.hideSoftKeyboard(ListShoesActivity.this);
                }
            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fab_add_data);
        fabAdd.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onClickAddOrEditShoes(null);
            }
        });

        LinearLayout layoutDeleteAll = findViewById(R.id.layout_delete_all);
        layoutDeleteAll.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mListShoes == null || mListShoes.isEmpty()) {
                    return;
                }
                onClickDeleteAllShoes();
            }
        });

        RecyclerView rcvShoes = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvShoes.setLayoutManager(linearLayoutManager);

        mListUnit = new ArrayList<>();
        mListShoes = new ArrayList<>();

        mShoesAdapter = new ShoesAdapter(mListShoes, new ShoesAdapter.IManagerShoesListener() {
            @Override
            public void editShoes(ShoesModel Shoes) {
                onClickAddOrEditShoes(Shoes);
            }

            @Override
            public void deleteShoes(ShoesModel Shoes) {
                onClickDeleteShoes(Shoes);
            }

            @Override
            public void onClickItemShoes(ShoesModel Shoes) {
                GlobalFunction.goToShoesDetailActivity(ListShoesActivity.this, Shoes);
            }
        });
        rcvShoes.setAdapter(mShoesAdapter);
        rcvShoes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAdd.hide();
                } else {
                    fabAdd.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getListUnit() {
        MyShoesApplication.get(this).getUnitDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListUnit != null) mListUnit.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UnitObjectModel unitObject = dataSnapshot.getValue(UnitObjectModel.class);
                    mListUnit.add(0, unitObject);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast(getString(R.string.msg_get_data_error));
            }
        });
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

    private void onClickAddOrEditShoes(ShoesModel Shoes) {
        if (mListUnit == null || mListUnit.isEmpty()) {
            showToast(getString(R.string.msg_list_unit_require));
            return;
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_and_edit_shoes);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final EditText edtShoesName = dialog.findViewById(R.id.edt_shoes_name);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAction = dialog.findViewById(R.id.tv_dialog_action);
        final Spinner spnUnit = dialog.findViewById(R.id.spinner_unit);

        SelectUnitAdapter selectUnitAdapter = new SelectUnitAdapter(this, R.layout.item_choose_option, mListUnit);
        spnUnit.setAdapter(selectUnitAdapter);
        spnUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUnitSelected = selectUnitAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set data
        if (Shoes == null) {
            tvTitleDialog.setText(getString(R.string.add_shoes_name));
            tvDialogAction.setText(getString(R.string.action_add));
        } else {
            tvTitleDialog.setText(getString(R.string.edit_shoes_name));
            tvDialogAction.setText(getString(R.string.action_edit));
            edtShoesName.setText(Shoes.getName());
            spnUnit.setSelection(getPositionUnitUpdate(Shoes));
        }

        // Set listener
        tvDialogCancel.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialog.dismiss();
            }
        });

        tvDialogAction.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String strShoesName = edtShoesName.getText().toString().trim();
                if (StringUtil.isEmpty(strShoesName)) {
                    showToast(getString(R.string.msg_shoes_name_require));
                    return;
                }

                if (isShoesExist(strShoesName)) {
                    showToast(getString(R.string.msg_shoes_exist));
                    return;
                }

                if (Shoes == null) {
                    long id = System.currentTimeMillis();
                    ShoesModel ShoesObject = new ShoesModel();
                    ShoesObject.setId(id);
                    ShoesObject.setName(strShoesName);
                    ShoesObject.setUnitId(mUnitSelected.getId());
                    ShoesObject.setUnitName(mUnitSelected.getName());

                    MyShoesApplication.get(ListShoesActivity.this).getShoesDatabaseReference()
                            .child(String.valueOf(id)).setValue(ShoesObject, (error, ref) -> {
                                GlobalFunction.hideSoftKeyboard(ListShoesActivity.this, edtShoesName);
                                showToast(getString(R.string.msg_add_shoes_success));
                                dialog.dismiss();
                                GlobalFunction.hideSoftKeyboard(ListShoesActivity.this);
                            });
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", strShoesName);
                    map.put("unitId", mUnitSelected.getId());
                    map.put("unitName", mUnitSelected.getName());

                    MyShoesApplication.get(ListShoesActivity.this).getShoesDatabaseReference()
                            .child(String.valueOf(Shoes.getId())).updateChildren(map, (error, ref) -> {
                                GlobalFunction.hideSoftKeyboard(ListShoesActivity.this, edtShoesName);
                                showToast(getString(R.string.msg_edit_shoes_success));
                                dialog.dismiss();
                                GlobalFunction.hideSoftKeyboard(ListShoesActivity.this);
                                updateShoesInHistory(new ShoesModel(Shoes.getId(), strShoesName,
                                        mUnitSelected.getId(), mUnitSelected.getName()));
                            });
                }
            }
        });

        dialog.show();
    }

    private int getPositionUnitUpdate(ShoesModel Shoes) {
        if (mListUnit == null || mListUnit.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < mListUnit.size(); i++) {
            if (Shoes.getUnitId() == mListUnit.get(i).getId()) {
                return i;
            }
        }
        return 0;
    }

    private boolean isShoesExist(String ShoesName) {
        if (mListShoes == null || mListShoes.isEmpty()) {
            return false;
        }

        for (ShoesModel Shoes : mListShoes) {
            if (ShoesName.equals(Shoes.getName())) {
                return true;
            }
        }

        return false;
    }

    private void onClickDeleteShoes(ShoesModel Shoes) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_delete), (dialogInterface, i)
                        -> MyShoesApplication.get(ListShoesActivity.this).getShoesDatabaseReference()
                        .child(String.valueOf(Shoes.getId())).removeValue((error, ref) -> {
                            showToast(getString(R.string.msg_delete_shoes_success));
                            GlobalFunction.hideSoftKeyboard(ListShoesActivity.this);
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void onClickDeleteAllShoes() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete_all))
                .setPositiveButton(getString(R.string.delete_all), (dialogInterface, i)
                        -> MyShoesApplication.get(ListShoesActivity.this).getShoesDatabaseReference()
                        .removeValue((error, ref) -> {
                            showToast(getString(R.string.msg_delete_all_shoes_success));
                            GlobalFunction.hideSoftKeyboard(ListShoesActivity.this);
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void updateShoesInHistory(ShoesModel Shoes) {
        MyShoesApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<HistoryModel> list = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            HistoryModel history = dataSnapshot.getValue(HistoryModel.class);
                            if (history != null && history.getshoesId() == Shoes.getId()) {
                                list.add(history);
                            }
                        }
                        MyShoesApplication.get(ListShoesActivity.this).getHistoryDatabaseReference()
                                .removeEventListener(this);
                        if (list.isEmpty()) {
                            return;
                        }
                        for (HistoryModel history : list) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("ShoesName", Shoes.getName());
                            map.put("unitId", Shoes.getUnitId());
                            map.put("unitName", Shoes.getUnitName());

                            MyShoesApplication.get(ListShoesActivity.this).getHistoryDatabaseReference()
                                    .child(String.valueOf(history.getId()))
                                    .updateChildren(map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}