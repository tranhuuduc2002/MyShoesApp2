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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tranhuuduc.myshoesapp.MyShoesApplication;
import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.adapter.UnitAdapter;
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

public class UnitActivity extends BaseActivity {

    private List<UnitObjectModel> mListUnit;
    private UnitAdapter mUnitAdapter;

    private EditText edtSearchName;
    private String mKeySearch;
    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            UnitObjectModel unitObject = dataSnapshot.getValue(UnitObjectModel.class);
            if (unitObject == null || mListUnit == null || mUnitAdapter == null) {
                return;
            }
            if (StringUtil.isEmpty(mKeySearch)) {
                mListUnit.add(0, unitObject);
            } else {
                if (GlobalFunction.getTextSearch(unitObject.getName().toLowerCase())
                        .contains(GlobalFunction.getTextSearch(mKeySearch).toLowerCase())) {
                    mListUnit.add(0, unitObject);
                }
            }
            mUnitAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            UnitObjectModel unitObject = dataSnapshot.getValue(UnitObjectModel.class);
            if (unitObject == null || mListUnit == null || mListUnit.isEmpty() || mUnitAdapter == null) {
                return;
            }
            for (int i = 0; i < mListUnit.size(); i++) {
                if (unitObject.getId() == mListUnit.get(i).getId()) {
                    mListUnit.set(i, unitObject);
                    break;
                }
            }
            mUnitAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            UnitObjectModel unitObject = dataSnapshot.getValue(UnitObjectModel.class);
            if (unitObject == null || mListUnit == null || mListUnit.isEmpty() || mUnitAdapter == null) {
                return;
            }
            for (UnitObjectModel unit : mListUnit) {
                if (unitObject.getId() == unit.getId()) {
                    mListUnit.remove(unit);
                    break;
                }
            }
            mUnitAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_unit);

        initToolbar();
        initUi();
        getListUnit();
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.feature_manage_unit));
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
                searchUnit();
            }
        });

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchUnit();
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
                    getListUnit();
                    GlobalFunction.hideSoftKeyboard(UnitActivity.this);
                }
            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fab_add_data);
        fabAdd.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onClickAddOrEditUnit(null);
            }
        });

        LinearLayout layoutDeleteAll = findViewById(R.id.layout_delete_all);
        layoutDeleteAll.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mListUnit == null || mListUnit.isEmpty()) {
                    return;
                }
                onClickDeleteAllUnit();
            }
        });

        RecyclerView rcvUnit = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvUnit.setLayoutManager(linearLayoutManager);

        mListUnit = new ArrayList<>();
        mUnitAdapter = new UnitAdapter(mListUnit, new UnitAdapter.IManagerUnitListener() {
            @Override
            public void editUnit(UnitObjectModel unitObject) {
                onClickAddOrEditUnit(unitObject);
            }

            @Override
            public void deleteUnit(UnitObjectModel unitObject) {
                onClickDeleteUnit(unitObject);
            }
        });
        rcvUnit.setAdapter(mUnitAdapter);
        rcvUnit.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    public void getListUnit() {
        if (mListUnit != null) {
            mListUnit.clear();
            MyShoesApplication.get(this).getUnitDatabaseReference().removeEventListener(mChildEventListener);
        }
        MyShoesApplication.get(this).getUnitDatabaseReference().addChildEventListener(mChildEventListener);
    }

    private void searchUnit() {
        if (mListUnit == null || mListUnit.isEmpty()) {
            GlobalFunction.hideSoftKeyboard(this);
            return;
        }
        mKeySearch = edtSearchName.getText().toString().trim();
        getListUnit();
        GlobalFunction.hideSoftKeyboard(this);
    }

    private void onClickAddOrEditUnit(UnitObjectModel unitObject) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_and_edit_unit);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final EditText edtUnitName = dialog.findViewById(R.id.edt_unit_name);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAction = dialog.findViewById(R.id.tv_dialog_action);

        // Set data
        if (unitObject == null) {
            tvTitleDialog.setText(getString(R.string.add_unit_name));
            tvDialogAction.setText(getString(R.string.action_add));
        } else {
            tvTitleDialog.setText(getString(R.string.edit_unit_name));
            tvDialogAction.setText(getString(R.string.action_edit));
            edtUnitName.setText(unitObject.getName());
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
                String strUnitName = edtUnitName.getText().toString().trim();
                if (StringUtil.isEmpty(strUnitName)) {
                    showToast(getString(R.string.msg_unit_name_require));
                    return;
                }

                if (isUnitExist(strUnitName)) {
                    showToast(getString(R.string.msg_unit_exist));
                    return;
                }

                if (unitObject == null) {
                    long id = System.currentTimeMillis();
                    UnitObjectModel unit = new UnitObjectModel();
                    unit.setId(id);
                    unit.setName(strUnitName);

                    MyShoesApplication.get(UnitActivity.this).getUnitDatabaseReference()
                            .child(String.valueOf(id)).setValue(unit, (error, ref) -> {
                                GlobalFunction.hideSoftKeyboard(UnitActivity.this, edtUnitName);
                                showToast(getString(R.string.msg_add_unit_success));
                                dialog.dismiss();
                                GlobalFunction.hideSoftKeyboard(UnitActivity.this);
                            });
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", strUnitName);

                    MyShoesApplication.get(UnitActivity.this).getUnitDatabaseReference()
                            .child(String.valueOf(unitObject.getId())).updateChildren(map, (error, ref) -> {
                                GlobalFunction.hideSoftKeyboard(UnitActivity.this, edtUnitName);
                                showToast(getString(R.string.msg_edit_unit_success));
                                dialog.dismiss();
                                GlobalFunction.hideSoftKeyboard(UnitActivity.this);
                                updateUnitInShoes(new UnitObjectModel(unitObject.getId(), strUnitName));
                                updateUnitInHistory(new UnitObjectModel(unitObject.getId(), strUnitName));
                            });
                }
            }
        });

        dialog.show();
    }

    private boolean isUnitExist(String unitName) {
        if (mListUnit == null || mListUnit.isEmpty()) {
            return false;
        }

        for (UnitObjectModel unitObject : mListUnit) {
            if (unitName.equals(unitObject.getName())) {
                return true;
            }
        }

        return false;
    }

    private void onClickDeleteUnit(UnitObjectModel unitObject) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_delete), (dialogInterface, i)
                        -> MyShoesApplication.get(UnitActivity.this).getUnitDatabaseReference()
                        .child(String.valueOf(unitObject.getId())).removeValue((error, ref) -> {
                            showToast(getString(R.string.msg_delete_unit_success));
                            GlobalFunction.hideSoftKeyboard(UnitActivity.this);
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void onClickDeleteAllUnit() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete_all))
                .setPositiveButton(getString(R.string.delete_all), (dialogInterface, i)
                        -> MyShoesApplication.get(UnitActivity.this).getUnitDatabaseReference()
                        .removeValue((error, ref) -> {
                            showToast(getString(R.string.msg_delete_all_unit_success));
                            GlobalFunction.hideSoftKeyboard(UnitActivity.this);
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void updateUnitInShoes(UnitObjectModel unitObject) {
        MyShoesApplication.get(this).getShoesDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ShoesModel> list = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ShoesModel Shoes = dataSnapshot.getValue(ShoesModel.class);
                            if (Shoes != null && Shoes.getUnitId() == unitObject.getId()) {
                                list.add(Shoes);
                            }
                        }
                        MyShoesApplication.get(UnitActivity.this).getShoesDatabaseReference()
                                .removeEventListener(this);
                        if (list.isEmpty()) {
                            return;
                        }
                        for (ShoesModel Shoes : list) {
                            MyShoesApplication.get(UnitActivity.this).getShoesDatabaseReference()
                                    .child(String.valueOf(Shoes.getId()))
                                    .child("unitName").setValue(unitObject.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void updateUnitInHistory(UnitObjectModel unitObject) {
        MyShoesApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<HistoryModel> list = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            HistoryModel history = dataSnapshot.getValue(HistoryModel.class);
                            if (history != null && history.getUnitId() == unitObject.getId()) {
                                list.add(history);
                            }
                        }
                        MyShoesApplication.get(UnitActivity.this).getHistoryDatabaseReference()
                                .removeEventListener(this);
                        if (list.isEmpty()) {
                            return;
                        }
                        for (HistoryModel history : list) {
                            MyShoesApplication.get(UnitActivity.this).getHistoryDatabaseReference()
                                    .child(String.valueOf(history.getId()))
                                    .child("unitName").setValue(unitObject.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}