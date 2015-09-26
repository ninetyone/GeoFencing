package com.example.akshaygoyal.geofencing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by akshaygoyal on 9/26/15.
 */
public class OrderListAdapter extends ArrayAdapter<Order> {


    public OrderListAdapter(Context context, int resource, List orderList) {
        super(context, resource, orderList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Order order = getItem(position);

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.order_list_row_item, null);
        }
        TextView mOrderNumberItem = (TextView) view.findViewById(R.id.order_number_list_item);
        TextView mOrderAmountItem = (TextView) view.findViewById(R.id.order_amount_list_item);
        mOrderNumberItem.setText(order.getOrderNumber());
        mOrderAmountItem.setText(String.valueOf(order.getOrderAmount()));

        return view;
    }

}
