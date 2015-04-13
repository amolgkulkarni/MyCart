package eci.officeshopper.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eci.officeshopper.R;
import eci.officeshopper.model.CartListItem;
import eci.officeshopper.util.ImageDownloader;

public class CartAdapter extends BaseAdapter {
    private Context mContext;
    private List<CartListItem> mCartItems;

    public CartAdapter(Context context, List<CartListItem> cartItems) {
        this.mContext = context;
        this.mCartItems= cartItems;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Using Holder to improve performance
        ViewHolder vh = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cart_list_item, null);
            vh = new ViewHolder();

            vh.imgLogo = (ImageView) convertView.findViewById(R.id.item_logo);
            vh.txtNums = (TextView) convertView.findViewById(R.id.item_nums);
            vh.txtDesc = (TextView) convertView.findViewById(R.id.item_desc);
            vh.txtTotal = (TextView) convertView.findViewById(R.id.item_total);
            vh.txtComp = (TextView) convertView.findViewById(R.id.item_comp);
            vh.txtSKU = (TextView) convertView.findViewById(R.id.item_sku);
            vh.txtPrice = (TextView) convertView.findViewById(R.id.item_price);
            vh.txtPack = (TextView) convertView.findViewById(R.id.items_pack);
            vh.btnAction = (Button) convertView.findViewById(R.id.cart_action);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        // Download and cache Image
        // Pass position so that we update iff position matched
        ImageDownloader imageDownloader = new ImageDownloader(mContext, vh.imgLogo,
                mCartItems.get(position).getLogoUrl().replace("/",""), R.drawable.star, position);
        imageDownloader.execute(mCartItems.get(position).getLogoUrl());
        vh.txtNums.setText(mCartItems.get(position).getItemsNums());
        vh.txtDesc.setText(mCartItems.get(position).getItemDesc());
        vh.txtTotal.setText(mCartItems.get(position).getTotalPrice());
        vh.txtComp.setText(mCartItems.get(position).getComp());
        vh.txtSKU.setText(mCartItems.get(position).getSKU());
        vh.txtPrice.setText(mCartItems.get(position).getPrice());
        vh.txtPack.setText(mCartItems.get(position).getItemsPerPack());
        vh.btnAction.setText(mCartItems.get(position).getCartAction());

        return convertView;
    }

    @Override
    public int getCount() {
        return (null != mCartItems ? mCartItems.size() : 0);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Holder class for holding item views.
    class ViewHolder{
        ImageView imgLogo;
        TextView txtNums;
        TextView txtDesc;
        TextView txtTotal;
        TextView txtComp;
        TextView txtSKU;
        TextView txtPrice;
        TextView txtPack;
        Button btnAction;
    }

}
