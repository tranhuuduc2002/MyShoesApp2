package com.tranhuuduc.myshoesapp.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tranhuuduc.myshoesapp.MyShoesApplication;
import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.adapter.HistoryAdapter;
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

public class ShoesDetailUsedFragment extends Fragment {

    private View mView;
    private TextView tvTotalPrice;
    private TextView tvTotalQuantity;
    private final ShoesModel mShoes;
    private List<HistoryModel> mListHistory;
    private HistoryAdapter mHistoryAdapter;

    public ShoesDetailUsedFragment(ShoesModel Shoes) {
        this.mShoes = Shoes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_shoes_detail_used, container, false);
        initUi();
        return mView;
    }

    private void initUi() {
        tvTotalQuantity = mView.findViewById(R.id.tv_total_quantity);
        tvTotalPrice = mView.findViewById(R.id.tv_total_price);
        RecyclerView rcvHistory = mView.findViewById(R.id.rcv_history);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvHistory.setLayoutManager(linearLayoutManager);

        mListHistory = new ArrayList<>();
        mHistoryAdapter = new HistoryAdapter(mListHistory, true,
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
                    }
                });
        rcvHistory.setAdapter(mHistoryAdapter);

        FloatingActionButton fabAddData = mView.findViewById(R.id.fab_add_data);
        fabAddData.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onClickAddOrEditHistory(null);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListHistoryAdded() {
        if (getActivity() == null) {
            return;
        }
        MyShoesApplication.get(getActivity()).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mListHistory != null) {
                            mListHistory.clear();
                        }

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            HistoryModel history = dataSnapshot.getValue(HistoryModel.class);
                            if (history != null) {
                                if (mShoes.getId() == history.getshoesId() && !history.isAdd()) {
                                    mListHistory.add(0, history);
                                }
                            }
                        }
                        mHistoryAdapter.notifyDataSetChanged();

                        displayLayoutBottomInfor();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), getString(R.string.msg_get_data_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayLayoutBottomInfor() {
        // Calculator quantity
        String strTotalQuantity = getTotalQuantity() + " " + mShoes.getUnitName();
        tvTotalQuantity.setText(strTotalQuantity);
        // Calculator price
        String strTotalPrice = getTotalPrice() + Constants.CURRENCY;
        tvTotalPrice.setText(strTotalPrice);
    }

    private int getTotalQuantity() {
        if (mListHistory == null || mListHistory.isEmpty()) {
            return 0;
        }

        int totalQuantity = 0;
        for (HistoryModel history : mListHistory) {
            totalQuantity += history.getQuantity();
        }
        return totalQuantity;
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

    private void onClickAddOrEditHistory(@Nullable HistoryModel history) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_detail_shoes_edit);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final TextView tvShoesName = dialog.findViewById(R.id.tv_shoes_name);
        final EditText edtQuantity = dialog.findViewById(R.id.edt_quantity);
        final TextView tvUnitName = dialog.findViewById(R.id.tv_unit_name);
        final EditText edtPrice = dialog.findViewById(R.id.edt_price);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAdd = dialog.findViewById(R.id.tv_dialog_add);

        // Set data
        if (history == null) {
            tvTitleDialog.setText(getString(R.string.feature_shoes_used));
            tvShoesName.setText(mShoes.getName());
            tvUnitName.setText(mShoes.getUnitName());
        } else {
            tvTitleDialog.setText(getString(R.string.edit_history_used));
            tvShoesName.setText(history.getshoesName());
            tvUnitName.setText(history.getUnitName());
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
                    GlobalFunction.showToast(getActivity(), getString(R.string.msg_enter_full_infor));
                    return;
                }

                if (history == null) {
                    HistoryModel history = new HistoryModel();
                    history.setId(System.currentTimeMillis());
                    history.setshoesId(mShoes.getId());
                    history.setshoesName(mShoes.getName());
                    history.setUnitId(mShoes.getUnitId());
                    history.setUnitName(mShoes.getUnitName());
                    history.setQuantity(Integer.parseInt(strQuantity));
                    history.setPrice(Integer.parseInt(strPrice));
                    history.setTotalPrice(history.getQuantity() * history.getPrice());
                    history.setAdd(false);

                    String currentDate = new SimpleDateFormat(DateTimeUtils.DEFAULT_FORMAT_DATE, Locale.ENGLISH).format(new Date());
                    String strDate = DateTimeUtils.convertDateToTimeStamp(currentDate);
                    history.setDate(Long.parseLong(strDate));

                    if (getActivity() != null) {
                        MyShoesApplication.get(getActivity()).getHistoryDatabaseReference()
                                .child(String.valueOf(history.getId()))
                                .setValue(history, (error, ref) -> {
                                    GlobalFunction.showToast(getActivity(), getString(R.string.msg_used_shoes_success));
                                    changeQuantity(history.getshoesId(), history.getQuantity(), false);
                                    GlobalFunction.hideSoftKeyboard(getActivity());
                                    dialog.dismiss();
                                });
                    }
                } else {
                    // Edit history
                    Map<String, Object> map = new HashMap<>();
                    map.put("quantity", Integer.parseInt(strQuantity));
                    map.put("price", Integer.parseInt(strPrice));
                    map.put("totalPrice", Integer.parseInt(strQuantity) * Integer.parseInt(strPrice));

                    if (getActivity() != null) {
                        MyShoesApplication.get(getActivity()).getHistoryDatabaseReference()
                                .child(String.valueOf(history.getId()))
                                .updateChildren(map, (error, ref) -> {
                                    GlobalFunction.hideSoftKeyboard(getActivity());
                                    GlobalFunction.showToast(getActivity(), getString(R.string.msg_edit_used_history_success));
                                    changeQuantity(history.getshoesId(), Integer.parseInt(strQuantity) - history.getQuantity(), false);

                                    dialog.dismiss();
                                });
                    }
                }
            }
        });

        dialog.show();
    }

    private void changeQuantity(long ShoesId, int quantity, boolean isAdd) {
        if (getActivity() == null) {
            return;
        }
        MyShoesApplication.get(getActivity()).getQuantityDatabaseReference(ShoesId)
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
                            if (getActivity() != null) {
                                MyShoesApplication.get(getActivity()).getQuantityDatabaseReference(ShoesId).removeEventListener(this);
                                MyShoesApplication.get(getActivity()).getQuantityDatabaseReference(ShoesId).setValue(totalQuantity);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void onClickDeleteHistory(HistoryModel history) {
        if (getActivity() == null) {
            return;
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_delete), (dialogInterface, i)
                        -> MyShoesApplication.get(getActivity()).getHistoryDatabaseReference()
                        .child(String.valueOf(history.getId()))
                        .removeValue((error, ref) -> {
                            GlobalFunction.showToast(getActivity(), getString(R.string.msg_delete_used_history_success));
                            changeQuantity(history.getshoesId(), history.getQuantity(), true);
                            GlobalFunction.hideSoftKeyboard(getActivity());
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    public View getmView() {
        return mView;
    }

    public void setmView(View mView) {
        this.mView = mView;
    }

}