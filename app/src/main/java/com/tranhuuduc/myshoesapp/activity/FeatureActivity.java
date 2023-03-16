package com.tranhuuduc.myshoesapp.activity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;

import com.tranhuuduc.myshoesapp.R;
import com.tranhuuduc.myshoesapp.adapter.FeatureAdapter;
import com.tranhuuduc.myshoesapp.constant.Constants;
import com.tranhuuduc.myshoesapp.constant.GlobalFunction;
import com.tranhuuduc.myshoesapp.model.FeatureModel;

import java.util.ArrayList;
import java.util.List;

public class FeatureActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);
        initUi();
    }

    private void initUi() {
        RecyclerView rcvFeature = findViewById(R.id.rcv_feature);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvFeature.setLayoutManager(gridLayoutManager);

        FeatureAdapter featureAdapter = new FeatureAdapter(getListFeatures(), this::onClickItemFeatureModel);
        rcvFeature.setAdapter(featureAdapter);
    }

    private List<FeatureModel> getListFeatures() {
        List<FeatureModel> list = new ArrayList<>();
        list.add(new FeatureModel(FeatureModel.FEATURE_MANAGE_UNIT, R.drawable.ic_manage_unit, getString(R.string.feature_manage_unit)));
        list.add(new FeatureModel(FeatureModel.FEATURE_LIST_MENU, R.drawable.ic_list_shoes, getString(R.string.feature_list_menu)));
        list.add(new FeatureModel(FeatureModel.FEATURE_ADD_SHOES, R.drawable.ic_add_shoes, getString(R.string.feature_add_shoes)));
        list.add(new FeatureModel(FeatureModel.FEATURE_MANAGE_SHOES, R.drawable.ic_mange_shoes_quan_ly, getString(R.string.feature_manage_shoes)));
        list.add(new FeatureModel(FeatureModel.FEATURE_SHOES_OUT_OF_STOCK, R.drawable.ic_shoes_kho_trong, getString(R.string.feature_shoes_out_of_stock)));
        list.add(new FeatureModel(FeatureModel.FEATURE_COST, R.drawable.ic_cost, getString(R.string.feature_cost)));
        list.add(new FeatureModel(FeatureModel.FEATURE_PROFIT, R.drawable.ic_profit, getString(R.string.feature_profit)));

        return list;
    }

    @Override
    public void onBackPressed() {
        showDialogExitApp();
    }

    private void showDialogExitApp() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.msg_confirm_exit_app))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> finishAffinity())
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    public void onClickItemFeatureModel(FeatureModel feature) {
        switch (feature.getId()) {
            case FeatureModel.FEATURE_LIST_MENU:
                GlobalFunction.startActivity(this, ListShoesActivity.class);
                break;

            case FeatureModel.FEATURE_MANAGE_UNIT:
                GlobalFunction.startActivity(this, UnitActivity.class);
                break;

            case FeatureModel.FEATURE_ADD_SHOES:
                GlobalFunction.startActivity(this, HistoryActivity.class);
                break;

            case FeatureModel.FEATURE_SHOES_USED:
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.KEY_INTENT_SHOES_USED, true);
                GlobalFunction.startActivity(this, HistoryActivity.class, bundle);
                break;

            case FeatureModel.FEATURE_MANAGE_SHOES:
                GlobalFunction.startActivity(this, ManageShoesActivity.class);
                break;

            case FeatureModel.FEATURE_SHOES_OUT_OF_STOCK:
                GlobalFunction.startActivity(this, ShoesOutOfStockActivity.class);
                break;

            case FeatureModel.FEATURE_REVENUE:
                goToStatisticalActivity(Constants.TYPE_REVENUE);
                break;

            case FeatureModel.FEATURE_COST:
                goToStatisticalActivity(Constants.TYPE_COST);
                break;

            case FeatureModel.FEATURE_PROFIT:
                GlobalFunction.startActivity(this, ProfitActivity.class);
                break;

            case FeatureModel.FEATURE_SHOES_POPULAR:
                goToListShoesPopular();
                break;

            default:
                break;
        }
    }

    private void goToStatisticalActivity(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_TYPE_STATISTICAL, type);
        GlobalFunction.startActivity(this, StatisticalActivity.class, bundle);
    }

    private void goToListShoesPopular() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_TYPE_STATISTICAL, Constants.TYPE_REVENUE);
        bundle.putBoolean(Constants.KEY_SHOES_POPULAR, true);
        GlobalFunction.startActivity(this, StatisticalActivity.class, bundle);
    }
}