package com.tranhuuduc.myshoesapp.model;

import java.util.ArrayList;
import java.util.List;

public class ProfitModel {

    private long shoesId;
    private String shoesName;
    private long shoesUnitId;
    private String shoesUnitName;
    private List<HistoryModel> histories;

    public ProfitModel(){}

    public ProfitModel(long shoesId, String shoesName, long shoesUnitId, String shoesUnitName, List<HistoryModel> histories) {
        this.shoesId = shoesId;
        this.shoesName = shoesName;
        this.shoesUnitId = shoesUnitId;
        this.shoesUnitName = shoesUnitName;
        this.histories = histories;
    }

    public long getshoesId() {
        return shoesId;
    }

    public void setshoesId(long shoesId) {
        this.shoesId = shoesId;
    }

    public String getshoesName() {
        return shoesName;
    }

    public void setshoesName(String shoesName) {
        this.shoesName = shoesName;
    }

    public long getshoesUnitId() {
        return shoesUnitId;
    }

    public void setshoesUnitId(long shoesUnitId) {
        this.shoesUnitId = shoesUnitId;
    }

    public String getshoesUnitName() {
        return shoesUnitName;
    }

    public void setshoesUnitName(String shoesUnitName) {
        this.shoesUnitName = shoesUnitName;
    }

    public List<HistoryModel> getHistories() {
        if (histories == null) {
            histories = new ArrayList<>();
        }
        return histories;
    }

    public void setHistories(List<HistoryModel> histories) {
        this.histories = histories;
    }

    public int getCurrentQuantity() {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (HistoryModel history : histories) {
            if (history.isAdd()) {
                result += history.getQuantity();
            } else {
                result -= history.getQuantity();
            }
        }
        return result;
    }

    public int getProfit() {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (HistoryModel history : histories) {
            if (history.isAdd()) {
                result -= history.getTotalPrice();
            } else {
                result += history.getTotalPrice();
            }
        }
        return result;
    }

}
