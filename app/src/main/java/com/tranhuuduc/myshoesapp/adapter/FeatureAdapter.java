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
import com.tranhuuduc.myshoesapp.listener.IOnSingleClickListener;
import com.tranhuuduc.myshoesapp.model.FeatureModel;

import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder>{

    private final List<FeatureModel> mListFeature;
    private final IManagerFeatureListener iManagerFeatureListener;

    public interface IManagerFeatureListener{
        void clickFeatureItem(FeatureModel model);
    }

    public FeatureAdapter(List<FeatureModel> mListFeature, IManagerFeatureListener iManagerFeatureListener) {
        this.mListFeature = mListFeature;
        this.iManagerFeatureListener = iManagerFeatureListener;
    }

    public static class FeatureViewHolder extends RecyclerView.ViewHolder{
        private final LinearLayout layoutItem;
        private final ImageView imgFeature;
        private final TextView tvFeature;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            imgFeature = itemView.findViewById(R.id.img_feature);
            tvFeature = itemView.findViewById(R.id.tv_feature);
        }
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feature, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        FeatureModel feature = mListFeature.get(position);
        if (feature == null) {
            return;
        }
        holder.imgFeature.setImageResource(feature.getResource());
        holder.tvFeature.setText(feature.getTitle());

        // Listener
        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerFeatureListener.clickFeatureItem(feature);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListFeature != null)
            return mListFeature.size();
        return 0;
    }
}
