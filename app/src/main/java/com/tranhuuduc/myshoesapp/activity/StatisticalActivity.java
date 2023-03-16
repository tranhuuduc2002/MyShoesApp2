package com.tranhuuduc.myshoesapp.activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tranhuuduc.myshoesapp.MyShoesApplication;
import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.adapter.StatisticalAdapter;
import com.tranhuuduc.myshoesapp.constant.Constants;
import com.tranhuuduc.myshoesapp.constant.GlobalFunction;
import com.tranhuuduc.myshoesapp.listener.IOnSingleClickListener;
import com.tranhuuduc.myshoesapp.model.ShoesModel;
import com.tranhuuduc.myshoesapp.model.HistoryModel;
import com.tranhuuduc.myshoesapp.model.Statistical;
import com.tranhuuduc.myshoesapp.utils.DateTimeUtils;
import com.tranhuuduc.myshoesapp.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StatisticalActivity extends BaseActivity {

    private TextView tvTotalValue;
    private TextView tvDateFrom, tvDateTo;
    private RecyclerView rcvData;

    private int mType;
    private boolean isShoesPopular;
    private List<Statistical> mListStatistical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistical);

        getDataIntent();
        initToolbar();
        initUi();
        getListStatistical();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        mType = bundle.getInt(Constants.KEY_TYPE_STATISTICAL);
        isShoesPopular = bundle.getBoolean(Constants.KEY_SHOES_POPULAR);
    }

    private void initToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        switch (mType) {
            case Constants.TYPE_REVENUE:
                getSupportActionBar().setTitle(getString(R.string.feature_revenue));
                break;

            case Constants.TYPE_COST:
                getSupportActionBar().setTitle(getString(R.string.feature_cost));
                break;
        }
        if (isShoesPopular) {
            getSupportActionBar().setTitle(getString(R.string.feature_shoes_popular));
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
        tvDateFrom = findViewById(R.id.tv_date_from);
        tvDateTo = findViewById(R.id.tv_date_to);
        tvTotalValue = findViewById(R.id.tv_total_value);
        rcvData = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);

        LinearLayout layoutFilter = findViewById(R.id.layout_filter);
        View viewDivider = findViewById(R.id.view_divider);
        RelativeLayout layoutBottom = findViewById(R.id.layout_bottom);
        if (isShoesPopular) {
            layoutFilter.setVisibility(View.GONE);
            viewDivider.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
        } else {
            layoutFilter.setVisibility(View.VISIBLE);
            viewDivider.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
        }

        TextView labelTotalValue = findViewById(R.id.label_total_value);
        switch (mType) {
            case Constants.TYPE_REVENUE:
                labelTotalValue.setText(getString(R.string.label_total_revenue));
                break;

            case Constants.TYPE_COST:
                labelTotalValue.setText(getString(R.string.label_total_cost));
                break;
        }

        tvDateFrom.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(StatisticalActivity.this, tvDateFrom.getText().toString(), date -> {
                    tvDateFrom.setText(date);
                    getListStatistical();
                });
            }
        });

        tvDateTo.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(StatisticalActivity.this, tvDateTo.getText().toString(), date -> {
                    tvDateTo.setText(date);
                    getListStatistical();
                });
            }
        });
    }

    private void getListStatistical() {
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
        if (Constants.TYPE_REVENUE == mType) {
            if (history.isAdd()) {
                return false;
            }
        } else {
            if (!history.isAdd()) {
                return false;
            }
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
        if (mListStatistical != null) {
            mListStatistical.clear();
        } else {
            mListStatistical = new ArrayList<>();
        }
        for (HistoryModel history : list) {
            long shoesId = history.getshoesId();
            if (checkStatisticalExist(shoesId)) {
                getStatisticalFromshoesId(shoesId).getHistories().add(history);
            } else {
                Statistical statistical = new Statistical();
                statistical.setshoesId(history.getshoesId());
                statistical.setshoesName(history.getshoesName());
                statistical.setshoesUnitId(history.getUnitId());
                statistical.setshoesUnitName(history.getUnitName());
                statistical.getHistories().add(history);
                mListStatistical.add(statistical);
            }
        }
        if (isShoesPopular) {
            List<Statistical> listPopular = new ArrayList<>(mListStatistical);
            listPopular.sort((statistical1, statistical2)
                    -> statistical2.getTotalPrice() - statistical1.getTotalPrice());
            StatisticalAdapter statisticalAdapter = new StatisticalAdapter(listPopular, statistical -> {
                ShoesModel shoes = new ShoesModel(statistical.getshoesId(), statistical.getshoesName(),
                        statistical.getshoesUnitId(), statistical.getshoesUnitName());
                GlobalFunction.goToShoesDetailActivity(this, shoes);
            });
            rcvData.setAdapter(statisticalAdapter);
        } else {
            StatisticalAdapter statisticalAdapter = new StatisticalAdapter(mListStatistical, statistical -> {
                ShoesModel shoes = new ShoesModel(statistical.getshoesId(), statistical.getshoesName(),
                        statistical.getshoesUnitId(), statistical.getshoesUnitName());
                GlobalFunction.goToShoesDetailActivity(this, shoes);
            });
            rcvData.setAdapter(statisticalAdapter);
        }

        // Calculate total
        String strTotalValue = getTotalValues() + Constants.CURRENCY;
        tvTotalValue.setText(strTotalValue);
    }

    private boolean checkStatisticalExist(long shoesId) {
        if (mListStatistical == null || mListStatistical.isEmpty()) {
            return false;
        }
        boolean result = false;
        for (Statistical statistical : mListStatistical) {
            if (shoesId == statistical.getshoesId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private Statistical getStatisticalFromshoesId(long shoesId) {
        Statistical result = null;
        for (Statistical statistical : mListStatistical) {
            if (shoesId == statistical.getshoesId()) {
                result = statistical;
                break;
            }
        }
        return result;
    }

    private int getTotalValues() {
        if (mListStatistical == null || mListStatistical.isEmpty()) {
            return 0;
        }

        int total = 0;
        for (Statistical statistical : mListStatistical) {
            total += statistical.getTotalPrice();
        }
        return total;
    }
}