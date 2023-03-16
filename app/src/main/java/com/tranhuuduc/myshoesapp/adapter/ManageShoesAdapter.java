package com.tranhuuduc.myshoesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.listener.IOnSingleClickListener;
import com.tranhuuduc.myshoesapp.model.ShoesModel;

import java.util.List;

public class ManageShoesAdapter extends RecyclerView.Adapter<ManageShoesAdapter.ManageShoesViewHolder> {


    private final List<ShoesModel> mListShoes;
    private final IManageShoesListener iManageShoesListener;

    public interface IManageShoesListener {
        void clickItem(ShoesModel model);
    }

    public ManageShoesAdapter(List<ShoesModel> mListShoes, IManageShoesListener iManageShoesListener) {
        this.mListShoes = mListShoes;
        this.iManageShoesListener = iManageShoesListener;
    }

    public static class ManageShoesViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvName;
        private final TextView tvCurrentQuantity;
        private final RelativeLayout layoutItem;

        public ManageShoesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCurrentQuantity = itemView.findViewById(R.id.tv_current_quantity);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }

    @NonNull
    @Override
    public ManageShoesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_shoes, parent, false);
        return new ManageShoesAdapter.ManageShoesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageShoesViewHolder holder, int position) {
        ShoesModel Shoes = mListShoes.get(position);
        if (Shoes == null) {
            return;
        }
        holder.tvName.setText(Shoes.getName());
        String strCurrentQuantity = Shoes.getQuantity() + " " + Shoes.getUnitName();
        holder.tvCurrentQuantity.setText(strCurrentQuantity);

        // Listener
        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManageShoesListener.clickItem(Shoes);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListShoes != null)
            return mListShoes.size();
        return 0;
    }
}
