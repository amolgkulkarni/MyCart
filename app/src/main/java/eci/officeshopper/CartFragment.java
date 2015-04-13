package eci.officeshopper;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eci.officeshopper.adapter.CartAdapter;
import eci.officeshopper.model.CartListItem;
import eci.officeshopper.util.AsyncRestTask;
import eci.officeshopper.util.Config;

public class CartFragment extends Fragment {
    private static final String TAG = "OfficeShopper";
    private Context mContext;
    private ListView mCartList;
    private TextView mDeptView;
    private TextView mItemNumsView;
    private TextView mTotalPriceView;
    private Integer mRemainingItems = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        this.mCartList = (ListView)rootView.findViewById(R.id.cart_list);
        this.mDeptView = (TextView)rootView.findViewById(R.id.department);
        this.mTotalPriceView = (TextView)rootView.findViewById(R.id.total_price);
        this.mItemNumsView = (TextView)rootView.findViewById(R.id.item_nums);
        getContents();
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        ((DrawerFragment.OnItemSelectedListener) activity).onMessage("CmdInfo", "12");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    private void getContents() {
        // Get BaseUrl
        SharedPreferences preferences = mContext.getSharedPreferences(Config.PREF_MODULE, mContext.MODE_PRIVATE);
        String baseUrl = preferences.getString(Config.DEALER_BASE_URL, null);
        if (null != baseUrl) {
            AsyncRestTask loginUser = new AsyncRestTask(mContext, "GET", null){
                @Override
                public void onExecute(String result) {
                    onCartExecute(result);
                }
            };
            loginUser.execute("http://" + baseUrl + Config.CART_API);
        }
    }

    private void onCartExecute (String result) {
        if (null != result) {
            List <CartListItem> cartItems = new ArrayList<CartListItem>();
            String totalCartPrice;
            Integer totalCartItems = 0;
            try {
                JSONObject json = new JSONObject(result);
                boolean success = json.getBoolean("Success");
                if (success) {
                    mRemainingItems = json.getInt("Count");
                    totalCartPrice = json.getString("Subtotal");
                    JSONArray items = json.getJSONArray("Items");
                    Integer itemLen = items.length();
                    for (Integer idx = 0; idx < itemLen; idx++) {
                        JSONObject item = items.getJSONObject(idx);
                        CartListItem cartItem = new CartListItem();
                        cartItem.setLogoUrl(item.getString("ImageUrl"));
                        String quantity = item.getString("Quantity");
                        cartItem.setItemsNums(quantity);
                        cartItem.setItemDesc(item.getString("Description"));
                        cartItem.setComp(item.getString("Company"));
                        cartItem.setSKU(item.getString("SKU"));
                        cartItem.setPrice(item.getString("SellPrice"));
                        cartItem.setItemsPerPack(item.getString("Packaging"));
                        cartItem.setCartAction("Remove");
                        String extendedPrice = item.getString("ExtendedPrice");
                        Integer quantityInt;
                        Float extendedPriceInt;
                        try {
                            quantityInt = Integer.parseInt(quantity);
                            char currency = extendedPrice.charAt(0);
                            extendedPriceInt = Float.parseFloat(extendedPrice.replaceAll("[^0-9.]+",""));
                            Float totalPrice = quantityInt*extendedPriceInt;
                            totalCartItems += quantityInt;
                            cartItem.setTotalPrice(currency + totalPrice.toString());
                        } catch (NumberFormatException  nfe) {
                            Log.e(TAG, "Could not parse" + nfe);
                        }
                        totalCartItems += Integer.parseInt(quantity);
                        cartItems.add(cartItem);
                    }
                    mCartList.setAdapter(new CartAdapter(mContext, cartItems));
                    mDeptView.setText("BLANK DEPT"); // Todo: Get Actual Data
                    mItemNumsView.setText(totalCartItems.toString());
                    mTotalPriceView.setText(totalCartPrice);

                    ((DrawerFragment.OnItemSelectedListener) mContext).onMessage("CmdInfo", totalCartItems.toString());
                }
            } catch (JSONException e){
            e.printStackTrace();
        }

        }
    }
}
