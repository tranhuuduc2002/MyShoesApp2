package com.tranhuuduc.myshoesapp.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tranhuuduc.myshoesapp.MyShoesApplication;
import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.adapter.HistoryAdapter;
import com.tranhuuduc.myshoesapp.adapter.SelectShoesAdapter;
import com.tranhuuduc.myshoesapp.constant.Constants;
import com.tranhuuduc.myshoesapp.constant.GlobalFunction;
import com.tranhuuduc.myshoesapp.listener.IOnSingleClickListener;
import com.tranhuuduc.myshoesapp.model.ShoesModel;
import com.tranhuuduc.myshoesapp.model.HistoryModel;
import com.tranhuuduc.myshoesapp.utils.DateTimeUtils;
import com.tranhuuduc.myshoesapp.utils.StringUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryActivity extends BaseActivity {

    private TextView mTvDateSelected;
    private TextView tvTotalPrice;

    private List<ShoesModel> mListShoes;

    private List<HistoryModel> mListHistory;
    private HistoryAdapter mHistoryAdapter;

    private ShoesModel mShoesSelected;
    private boolean isShoesUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getDataIntent();
        initToolbar();
        initUi();
        getListShoess();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        isShoesUsed = bundle.getBoolean(Constants.KEY_INTENT_SHOES_USED);
    }

    private void initToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        if (isShoesUsed) {
            getSupportActionBar().setTitle(getString(R.string.feature_shoes_used));
        } else {
            getSupportActionBar().setTitle(getString(R.string.feature_add_shoes));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        TextView tvListTitle = findViewById(R.id.tv_list_title);
        if (isShoesUsed) {
            tvListTitle.setText(getString(R.string.list_shoes_used));
        } else {
            tvListTitle.setText(getString(R.string.list_shoes_buy));
        }

        tvTotalPrice = findViewById(R.id.tv_total_price);

        mTvDateSelected = findViewById(R.id.tv_date_selected);
        String currentDate = new SimpleDateFormat(DateTimeUtils.DEFAULT_FORMAT_DATE, Locale.ENGLISH).format(new Date());
        mTvDateSelected.setText(currentDate);
        getListHistoryShoesOfDate(currentDate);

        RelativeLayout layoutSelectDate = findViewById(R.id.layout_select_date);
        layoutSelectDate.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(HistoryActivity.this, mTvDateSelected.getText().toString(), date -> {
                    mTvDateSelected.setText(date);
                    getListHistoryShoesOfDate(date);
                });
            }
        });

        FloatingActionButton fabAddData = findViewById(R.id.fab_add_data);
        fabAddData.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onClickAddOrEditHistory(null);
            }
        });

        RecyclerView rcvHistory = findViewById(R.id.rcv_history);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvHistory.setLayoutManager(linearLayoutManager);

        mListShoes = new ArrayList<>();
        mListHistory = new ArrayList<>();

        mHistoryAdapter = new HistoryAdapter(mListHistory, false,
                new HistoryAdapter.IManagerHistoryListener() {
                    @Override
                    public void editHistory(HistoryModel history) {
                        onClickAddOrEditHistory(history);
                    }

                    @Override
                    public void deleteHistory(HistoryModel history) {
                        onClickDeleteHistory(history);
                    }

                    @Override
                    public void onClickItemHistory(HistoryModel history) {
                        ShoesModel Shoes = new ShoesModel(history.getshoesId(), history.getshoesName(),
                                history.getUnitId(), history.getUnitName());
                        GlobalFunction.goToShoesDetailActivity(HistoryActivity.this, Shoes);
                    }
                });
        rcvHistory.setAdapter(mHistoryAdapter);
        rcvHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAddData.hide();
                } else {
                    fabAddData.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getListShoess() {
        MyShoesApplication.get(this).getShoesDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListShoes != null) mListShoes.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ShoesModel Shoes = dataSnapshot.getValue(ShoesModel.class);
                    mListShoes.add(0, Shoes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast(getString(R.string.msg_get_data_error));
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListHistoryShoesOfDate(@NonNull String date) {
        long longDate = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(date));
        MyShoesApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mListHistory != null) mListHistory.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            HistoryModel history = dataSnapshot.getValue(HistoryModel.class);
                            if (history != null) {
                                if (longDate == history.getDate()) {
                                    addHistoryToList(history);
                                }
                            }
                        }
                        mHistoryAdapter.notifyDataSetChanged();

                        // Calculator price
                        String strTotalPrice = getTotalPrice() + Constants.CURRENCY;
                        tvTotalPrice.setText(strTotalPrice);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showToast(getString(R.string.msg_get_data_error));
                    }
                });
    }

    private void addHistoryToList(HistoryModel history) {
        if (history == null) {
            return;
        }
        if (isShoesUsed) {
            if (!history.isAdd()) {
                mListHistory.add(0, history);
            }
        } else {
            if (history.isAdd()) {
                mListHistory.add(0, history);
            }
        }
    }

    private void onClickAddOrEditHistory(HistoryModel history) {
        if (mListShoes == null || mListShoes.isEmpty()) {
            showToast(getString(R.string.msg_list_shoes_require));
            return;
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_history);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final Spinner spnShoes = dialog.findViewById(R.id.spinner_shoes);
        final EditText edtQuantity = dialog.findViewById(R.id.edt_quantity);
        final TextView tvUnitName = dialog.findViewById(R.id.tv_unit_name);
        final EditText edtPrice = dialog.findViewById(R.id.edt_price);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAdd = dialog.findViewById(R.id.tv_dialog_add);

        // Set data
        if (isShoesUsed) {
            tvTitleDialog.setText(getString(R.string.feature_shoes_used));
        } else {
            tvTitleDialog.setText(getString(R.string.feature_add_shoes));
        }

        SelectShoesAdapter selectShoesAdapter = new SelectShoesAdapter(this, R.layout.item_choose_option, mListShoes);
        spnShoes.setAdapter(selectShoesAdapter);
        spnShoes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mShoesSelected = selectShoesAdapter.getItem(position);
                tvUnitName.setText(mShoesSelected.getUnitName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (history != null) {
            if (isShoesUsed) {
                tvTitleDialog.setText(getString(R.string.edit_history_used));
            } else {
                tvTitleDialog.setText(getString(R.string.edit_history_add));
            }
            spnShoes.setSelection(getPositionShoesUpdate(history));
            edtQuantity.setText(String.valueOf(history.getQuantity()));
            edtPrice.setText(String.valueOf(history.getPrice()));
        }

        // Listener
        tvDialogCancel.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialog.dismiss();
            }
        });

        tvDialogAdd.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String strQuantity = edtQuantity.getText().toString().trim();
                String strPrice = edtPrice.getText().toString().trim();
                if (StringUtil.isEmpty(strQuantity) || StringUtil.isEmpty(strPrice)) {
                    showToast(getString(R.string.msg_enter_full_infor));
                    return;
                }

                if (history == null) {
                    HistoryModel history = new HistoryModel();
                    history.setId(System.currentTimeMillis());
                    history.setshoesId(mShoesSelected.getId());
                    history.setshoesName(mShoesSelected.getName());
                    history.setUnitId(mShoesSelected.getUnitId());
                    history.setUnitName(mShoesSelected.getUnitName());
                    history.setQuantity(Integer.parseInt(strQuantity));
                    history.setPrice(Integer.parseInt(strPrice));
                    history.setTotalPrice(history.getQuantity() * history.getPrice());
                    history.setAdd(!isShoesUsed);
                    String strDate = DateTimeUtils.convertDateToTimeStamp(mTvDateSelected.getText().toString());
                    history.setDate(Long.parseLong(strDate));

                    MyShoesApplication.get(HistoryActivity.this).getHistoryDatabaseReference()
                            .child(String.valueOf(history.getId()))
                            .setValue(history, (error, ref) -> {
                                if (isShoesUsed) {
                                    showToast(getString(R.string.msg_used_shoes_success));
                                } else {
                                    showToast(getString(R.string.msg_add_shoes_success));
                                }
                                changeQuantity(history.getshoesId(), history.getQuantity(), !isShoesUsed);
                                GlobalFunction.hideSoftKeyboard(HistoryActivity.this);
                                dialog.dismiss();
                            });
                    return;
                }

                // Edit history
                Map<String, Object> map = new HashMap<>();
                map.put("ShoesId", mShoesSelected.getId());
                map.put("ShoesName", mShoesSelected.getName());
                map.put("unitId", mShoesSelected.getUnitId());
                map.put("unitName", mShoesSelected.getUnitName());
                map.put("quantity", Integer.parseInt(strQuantity));
                map.put("price", Integer.parseInt(strPrice));
                map.put("totalPrice", Integer.parseInt(strQuantity) * Integer.parseInt(strPrice));

                MyShoesApplication.get(HistoryActivity.this).getHistoryDatabaseReference()
                        .child(String.valueOf(history.getId()))
                        .updateChildren(map, (error, ref) -> {
                            GlobalFunction.hideSoftKeyboard(HistoryActivity.this);
                            if (isShoesUsed) {
                                showToast(getString(R.string.msg_edit_used_history_success));
                            } else {
                                showToast(getString(R.string.msg_edit_add_history_success));
                            }
                            changeQuantity(history.getshoesId(), Integer.parseInt(strQuantity) - history.getQuantity(), !isShoesUsed);

                            dialog.dismiss();
                        });
            }
        });

        dialog.show();
    }

    private void changeQuantity(long ShoesId, int quantity, boolean isAdd) {
        MyShoesApplication.get(HistoryActivity.this).getQuantityDatabaseReference(ShoesId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer currentQuantity = snapshot.getValue(Integer.class);
                        if (currentQuantity != null) {
                            int totalQuantity;
                            if (isAdd) {
                                totalQuantity = currentQuantity + quantity;
                            } else {
                                totalQuantity = currentQuantity - quantity;
                            }
                            MyShoesApplication.get(HistoryActivity.this).getQuantityDatabaseReference(ShoesId).removeEventListener(this);
                            updateQuantityToFirebase(ShoesId, totalQuantity);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void updateQuantityToFirebase(long ShoesId, int quantity) {
        MyShoesApplication.get(HistoryActivity.this).getQuantityDatabaseReference(ShoesId)
                .setValue(quantity);
    }

    private int getPositionShoesUpdate(HistoryModel history) {
        if (mListShoes == null || mListShoes.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < mListShoes.size(); i++) {
            if (history.getshoesId() == mListShoes.get(i).getId()) {
                return i;
            }
        }
        return 0;
    }

    private int getTotalPrice() {
        if (mListHistory == null || mListHistory.isEmpty()) {
            return 0;
        }

        int totalPrice = 0;
        for (HistoryModel history : mListHistory) {
            totalPrice += history.getTotalPrice();
        }
        return totalPrice;
    }

    private void onClickDeleteHistory(HistoryModel history) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_delete), (dialogInterface, i)
                        -> MyShoesApplication.get(HistoryActivity.this).getHistoryDatabaseReference()
                        .child(String.valueOf(history.getId()))
                        .removeValue((error, ref) -> {
                            if (isShoesUsed) {
                                showToast(getString(R.string.msg_delete_used_history_success));
                            } else {
                                showToast(getString(R.string.msg_delete_add_history_success));

                            }
                            changeQuantity(history.getshoesId(), history.getQuantity(), isShoesUsed);
                            GlobalFunction.hideSoftKeyboard(HistoryActivity.this);
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }
}