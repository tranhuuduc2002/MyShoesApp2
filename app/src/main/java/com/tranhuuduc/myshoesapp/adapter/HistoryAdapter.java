package com.tranhuuduc.myshoesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.constant.Constants;
import com.tranhuuduc.myshoesapp.listener.IOnSingleClickListener;
import com.tranhuuduc.myshoesapp.model.HistoryModel;
import com.tranhuuduc.myshoesapp.utils.DateTimeUtils;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<HistoryModel> mListHistory;
    private final boolean mIsShowDate;
    private final IManagerHistoryListener iManagerHistoryListener;

    public interface IManagerHistoryListener {
        void editHistory(HistoryModel model);

        void deleteHistory(HistoryModel model);

        void onClickItemHistory(HistoryModel model);
    }

    public HistoryAdapter(List<HistoryModel> mListHistory, boolean mIsShowDate, IManagerHistoryListener iManagerHistoryListener) {
        this.mListHistory = mListHistory;
        this.mIsShowDate = mIsShowDate;
        this.iManagerHistoryListener = iManagerHistoryListener;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layoutItemHistory;
        private final TextView tvDeviceName;
        private final TextView tvPrice;
        private final TextView tvQuantity;
        private final TextView tvTotalPrice;
        private final ImageView imgEdit;
        private final ImageView imgDelete;
        private final TextView tvDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItemHistory = itemView.findViewById(R.id.layout_item_history);
            tvDeviceName = itemView.findViewById(R.id.tv_shoes_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_buy_or_used, parent, false);
        return new HistoryAdapter.HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryModel history = mListHistory.get(position);
        if (history == null) {
            return;
        }
        if (history.isAdd()) {
            holder.layoutItemHistory.setBackgroundResource(R.drawable.bg_white_corner_6_border_gray);
        } else {
            holder.layoutItemHistory.setBackgroundResource(R.drawable.bg_gray_corner_radius_6);
        }

        if (mIsShowDate) {
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(DateTimeUtils.convertTimeStampToDate(String.valueOf(history.getDate())));
            holder.layoutItemHistory.setOnClickListener(null);
        } else {
            holder.tvDate.setVisibility(View.GONE);
            holder.layoutItemHistory.setOnClickListener(new IOnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    iManagerHistoryListener.onClickItemHistory(history);
                }
            });
        }
        holder.tvDeviceName.setText(history.getshoesName());
        String strPrice = history.getPrice() + Constants.CURRENCY;
        holder.tvPrice.setText(strPrice);
        String strQuantity = history.getQuantity() + " " + history.getUnitName();
        holder.tvQuantity.setText(strQuantity);
        String strTotalPrice = history.getTotalPrice() + Constants.CURRENCY;
        holder.tvTotalPrice.setText(strTotalPrice);

        // Listener
        holder.imgEdit.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerHistoryListener.editHistory(history);
            }
        });

        holder.imgDelete.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerHistoryListener.deleteHistory(history);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListHistory != null)
            return mListHistory.size();
        return 0;
    }
}
