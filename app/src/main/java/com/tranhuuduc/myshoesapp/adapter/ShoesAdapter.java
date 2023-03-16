package com.tranhuuduc.myshoesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.listener.IOnSingleClickListener;
import com.tranhuuduc.myshoesapp.model.ShoesModel;

import java.util.List;

public class ShoesAdapter extends RecyclerView.Adapter<ShoesAdapter.ShoesViewHolder> {

    private final List<ShoesModel> mListShoes;
    private final IManagerShoesListener iManagerShoesListener;

    public interface IManagerShoesListener{
        void editShoes(ShoesModel model);
        void deleteShoes(ShoesModel model);
        void onClickItemShoes(ShoesModel model);
    }

    public ShoesAdapter(List<ShoesModel> mListShoes, IManagerShoesListener iManagerShoesListener) {
        this.mListShoes = mListShoes;
        this.iManagerShoesListener = iManagerShoesListener;
    }

    public static class ShoesViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvName;
        private final TextView tvUnitName;
        private final ImageView imgEdit;
        private final ImageView imgDelete;
        private final RelativeLayout layoutItem;

        public ShoesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvUnitName = itemView.findViewById(R.id.tv_unit_name);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }

    @NonNull
    @Override
    public ShoesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shoes, parent, false);
        return new ShoesAdapter.ShoesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoesViewHolder holder, int position) {
        ShoesModel Shoes = mListShoes.get(position);
        if (Shoes == null) {
            return;
        }
        holder.tvName.setText(Shoes.getName());
        holder.tvUnitName.setText(Shoes.getUnitName());

        // Listener
        holder.imgEdit.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerShoesListener.editShoes(Shoes);
            }
        });

        holder.imgDelete.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerShoesListener.deleteShoes(Shoes);
            }
        });

        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerShoesListener.onClickItemShoes(Shoes);
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
