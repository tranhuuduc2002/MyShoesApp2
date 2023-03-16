package com.tranhuuduc.myshoesapp.adapter;

import android.content.Context;
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
import com.tranhuuduc.myshoesapp.model.ProfitModel;

import java.util.List;

public class ProfitAdapter extends RecyclerView.Adapter<ProfitAdapter.ProfitViewHolder> {

    private Context mContext;
    private final List<ProfitModel> mListProfit;
    private final IManagerProfitListener iManagerProfitListener;

    public void release() {
        mContext = null;
    }

    public interface IManagerProfitListener {
        void onClickItem(ProfitModel model);
    }

    public ProfitAdapter(Context mContext, List<ProfitModel> mListProfit, IManagerProfitListener iManagerProfitListener) {
        this.mContext = mContext;
        this.mListProfit = mListProfit;
        this.iManagerProfitListener = iManagerProfitListener;
    }

    public static class ProfitViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvStt;
        private final TextView tvShoesName;
        private final TextView tvCurrentQuantity;
        private final TextView tvProfit;
        private final LinearLayout layoutItem;

        public ProfitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tv_stt);
            tvShoesName = itemView.findViewById(R.id.tv_shoes_name);
            tvCurrentQuantity = itemView.findViewById(R.id.tv_current_quantity);
            tvProfit = itemView.findViewById(R.id.tv_profit);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }

    @NonNull
    @Override
    public ProfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profit, parent, false);
        return new ProfitAdapter.ProfitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfitViewHolder holder, int position) {
        ProfitModel profit = mListProfit.get(position);
        if (profit == null) {
            return;
        }
        holder.tvStt.setText(String.valueOf(position + 1));
        holder.tvShoesName.setText(profit.getshoesName());
        String strQuantity = profit.getCurrentQuantity() + "";
        holder.tvCurrentQuantity.setText(strQuantity);
        String strProfit = profit.getshoesUnitName();
        holder.tvProfit.setText(strProfit);

        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerProfitListener.onClickItem(profit);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListProfit != null)
            return mListProfit.size();
        return 0;
    }
}
