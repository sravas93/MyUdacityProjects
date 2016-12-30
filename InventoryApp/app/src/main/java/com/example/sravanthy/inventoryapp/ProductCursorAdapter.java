package com.example.sravanthy.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sravanthy.inventoryapp.ProductProvider.ProductEntry;

/**
 * Created by sandeep on 12/22/2016.
 */
public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context,Cursor c){
        super(context,c,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        int itemIdIndex = cursor.getColumnIndex(ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
       final int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);


        final int itemId = cursor.getInt(itemIdIndex);

        String productName = cursor.getString(nameColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);

//        if (TextUtils.isEmpty(productName)){
//         productName = context.getString(R.string.list_item_unknown_name);
//        }

        if (TextUtils.isEmpty(productQuantity)){
            productQuantity = context.getString(R.string.list_item_unknown_quantity);
        }

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                if (currentQuantity>0) {
                    currentQuantity--;
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, String.valueOf(currentQuantity));
                    Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, itemId);
                    context.getContentResolver().update(currentProductUri, values, null, null);
                }else{
                    Toast.makeText(context, "Nothing to sell", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nameTextView.setText(context.getString(R.string.string_extension,productName));
        quantityTextView.setText(context.getString(R.string.quantity_extension,productQuantity));
        priceTextView.setText(context.getString(R.string.price_formatted_text,productPrice));
    }
}
