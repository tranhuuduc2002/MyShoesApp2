package com.tranhuuduc.myshoesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.constant.Constants;
import com.tranhuuduc.myshoesapp.listener.IOnSingleClickListener;
import com.tranhuuduc.myshoesapp.model.Statistical;

import java.util.List;

public class StatisticalAdapter extends RecyclerView.Adapter<StatisticalAdapter.StatisticalViewHolder> {

    private final List<Statistical> mListStatistical;
    private final IManagerStatisticalListener iManagerStatisticalListener;

    public interface IManagerStatisticalListener {
        void onClickItem (Statistical statistical);
    }

    public StatisticalAdapter(List<Statistical> mListStatistical, IManagerStatisticalListener iManagerStatisticalListener) {
        this.mListStatistical = mListStatistical;
        this.iManagerStatisticalListener = iManagerStatisticalListener;
    }

    public static class StatisticalViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvStt;
        private final TextView tvDrinkName;
        private final TextView tvQuantity;
        private final TextView tvTotalPrice;
        private final LinearLayout layoutItem;

        public StatisticalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tv_stt);
            tvDrinkName = itemView.findViewById(R.id.tv_shoes_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }

    @NonNull
    @Override
    public StatisticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistical, parent, false);
        return new StatisticalAdapter.StatisticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticalViewHolder holder, int position) {
        Statistical statistical = mListStatistical.get(position);
        if (statistical == null) {
            return;
        }
        holder.tvStt.setText(String.valueOf(position + 1));
        holder.tvDrinkName.setText(statistical.getshoesName());
        String strQuantity = statistical.getQuantity() + " ";
        holder.tvQuantity.setText(strQuantity);
        String strTotalPrice = statistical.getTotalPrice() + Constants.CURRENCY;
        holder.tvTotalPrice.setText(strTotalPrice);

        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerStatisticalListener.onClickItem(statistical);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListStatistical != null)
            return mListStatistical.size();
        return 0;
    }
}
