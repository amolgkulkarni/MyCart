package eci.officeshopper.model;

public class CartListItem {
    private static final String TAG = "OfficeShopper";
    private String logoUrl;
    private String itemsNums;
    private String itemDesc;
    private String totalPrice;
    private String comp;
    private String sku;
    private String price;
    private String itemsPerPack;
    private String cartAction;

    public String getLogoUrl () {
      return logoUrl;
    }
    public void setLogoUrl (String value) {
        this.logoUrl = value;
    }

    public String getItemsNums () {
        return itemsNums;
    }
    public void setItemsNums (String value) {
        this.itemsNums = value;
    }

    public String getItemDesc () {
        return itemDesc;
    }
    public void setItemDesc (String value) {
        this.itemDesc = value;
    }

    public String getTotalPrice () {
        return totalPrice;
    }
    public void setTotalPrice (String value) {
        this.totalPrice = value;
    }

    public String getComp () {
        return comp;
    }
    public void setComp (String value) {
        this.comp = value;
    }

    public String getSKU () {
        return sku;
    }
    public void setSKU (String value) {
        this.sku = value;
    }

    public String getPrice () {
        return price;
    }
    public void setPrice (String value) {
        this.price = value;
    }

    public String getItemsPerPack () {
        return itemsPerPack;
    }
    public void setItemsPerPack (String value) {
        this.itemsPerPack = value;
    }

    public String getCartAction () {
        return cartAction;
    }
    public void setCartAction (String value) {
        this.cartAction = value;
    }

}
