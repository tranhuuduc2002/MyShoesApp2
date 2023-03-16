package com.tranhuuduc.myshoesapp.model;

import java.util.ArrayList;
import java.util.List;

public class Statistical {
    // Statistical: Thống Kê
    private long shoesId;
    private String shoesName;
    private long shoesUnitId;
    private String shoesUnitName;
    private List<HistoryModel> histories;

    public Statistical(){}

    public Statistical(long shoesId, String shoesName, long shoesUnitId, String shoesUnitName, List<HistoryModel> histories) {
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

    public int getQuantity() {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (HistoryModel history : histories) {
            result += history.getQuantity();
        }
        return result;
    }

    public int getTotalPrice() {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (HistoryModel history : histories) {
            result += history.getTotalPrice();
        }
        return result;
    }
}
