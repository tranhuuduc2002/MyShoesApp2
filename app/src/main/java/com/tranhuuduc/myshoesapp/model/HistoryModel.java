package com.tranhuuduc.myshoesapp.model;

public class HistoryModel {

    private long id;
    private long shoesId;
    private String shoesName;
    private long unitId;
    private String unitName;
    private int quantity;
    private int price;
    private int totalPrice;
    private long date;
    private boolean add;

    public HistoryModel(){}

    public HistoryModel(long id, long shoesId, String shoesName, long unitId, String unitName, int quantity, int price, int totalPrice, long date, boolean add) {
        this.id = id;
        this.shoesId = shoesId;
        this.shoesName = shoesName;
        this.unitId = unitId;
        this.unitName = unitName;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
        this.date = date;
        this.add = add;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }
}
