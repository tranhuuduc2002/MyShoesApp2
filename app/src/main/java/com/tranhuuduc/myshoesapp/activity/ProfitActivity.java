package com.tranhuuduc.myshoesapp.activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tranhuuduc.myshoesapp.MyShoesApplication;
import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.adapter.ProfitAdapter;
import com.tranhuuduc.myshoesapp.constant.Constants;
import com.tranhuuduc.myshoesapp.constant.GlobalFunction;
import com.tranhuuduc.myshoesapp.listener.IOnSingleClickListener;
import com.tranhuuduc.myshoesapp.model.ShoesModel;
import com.tranhuuduc.myshoesapp.model.HistoryModel;
import com.tranhuuduc.myshoesapp.model.ProfitModel;
import com.tranhuuduc.myshoesapp.utils.DateTimeUtils;
import com.tranhuuduc.myshoesapp.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfitActivity extends BaseActivity {

    private TextView tvDateFrom, tvDateTo;
    private RecyclerView rcvData;

    private List<ProfitModel> mListProfit;
    private ProfitAdapter mProfitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);

        initToolbar();
        initUi();
        getListProfit();
    }

    private void initToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().setTitle(getString(R.string.feature_profit));
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
        tvDateFrom = findViewById(R.id.tv_date_from);
        tvDateTo = findViewById(R.id.tv_date_to);
        rcvData = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);

        tvDateFrom.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(ProfitActivity.this, tvDateFrom.getText().toString(), date -> {
                    tvDateFrom.setText(date);
                    getListProfit();
                });
            }
        });

        tvDateTo.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(ProfitActivity.this, tvDateTo.getText().toString(), date -> {
                    tvDateTo.setText(date);
                    getListProfit();
                });
            }
        });
    }

    private void getListProfit() {
        MyShoesApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<HistoryModel> list = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            HistoryModel history = dataSnapshot.getValue(HistoryModel.class);
                            if (canAddHistory(history)) {
                                list.add(history);
                            }
                        }
                        handleDataHistories(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showToast(getString(R.string.msg_get_data_error));
                    }
                });
    }

    private boolean canAddHistory(@Nullable HistoryModel history) {
        if (history == null) {
            return false;
        }
        String strDateFrom = tvDateFrom.getText().toString();
        String strDateTo = tvDateTo.getText().toString();
        if (StringUtil.isEmpty(strDateFrom) && StringUtil.isEmpty(strDateTo)) {
            return true;
        }
        if (StringUtil.isEmpty(strDateFrom) && !StringUtil.isEmpty(strDateTo)) {
            long longDateTo = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(strDateTo));
            return history.getDate() <= longDateTo;
        }
        if (!StringUtil.isEmpty(strDateFrom) && StringUtil.isEmpty(strDateTo)) {
            long longDateFrom = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(strDateFrom));
            return history.getDate() >= longDateFrom;
        }
        long longDateTo = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(strDateTo));
        long longDateFrom = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(strDateFrom));
        return history.getDate() >= longDateFrom && history.getDate() <= longDateTo;
    }

    private void handleDataHistories(List<HistoryModel> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (mListProfit != null) {
            mListProfit.clear();
        } else {
            mListProfit = new ArrayList<>();
        }
        for (HistoryModel history : list) {
            long ShoesId = history.getshoesId();
            if (checkProfitExist(ShoesId)) {
                getProfitFromShoesId(ShoesId).getHistories().add(history);
            } else {
                ProfitModel profit = new ProfitModel();
                profit.setshoesId(history.getshoesId());
                profit.setshoesName(history.getshoesName());
                profit.setshoesUnitId(history.getUnitId());
                profit.setshoesUnitName(history.getUnitName());
                profit.getHistories().add(history);
                mListProfit.add(profit);
            }
        }
        mProfitAdapter = new ProfitAdapter(this, mListProfit, profit -> {
            ShoesModel Shoes = new ShoesModel(profit.getshoesId(), profit.getshoesName(),
                    profit.getshoesUnitId(), profit.getshoesUnitName());
            GlobalFunction.goToShoesDetailActivity(this, Shoes);
        });
        rcvData.setAdapter(mProfitAdapter);


    }

    private boolean checkProfitExist(long ShoesId) {
        if (mListProfit == null || mListProfit.isEmpty()) {
            return false;
        }
        boolean result = false;
        for (ProfitModel profit : mListProfit) {
            if (ShoesId == profit.getshoesId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private ProfitModel getProfitFromShoesId(long ShoesId) {
        ProfitModel result = null;
        for (ProfitModel profit : mListProfit) {
            if (ShoesId == profit.getshoesId()) {
                result = profit;
                break;
            }
        }
        return result;
    }

    private int getTotalProfit() {
        if (mListProfit == null || mListProfit.isEmpty()) {
            return 0;
        }

        int total = 0;
        for (ProfitModel profit : mListProfit) {
            total += profit.getProfit();
        }
        return total;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProfitAdapter != null) {
            mProfitAdapter.release();
        }
    }
}