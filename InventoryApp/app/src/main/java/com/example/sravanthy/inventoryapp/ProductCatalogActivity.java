package com.example.sravanthy.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sravanthy.inventoryapp.ProductProvider.ProductEntry;

public class ProductCatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ProductCatalogActivity.class.getSimpleName();

    private static final int PRODUCT_LOADER=0;

    private ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_product);

       //  Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductCatalogActivity.this, ProductEditorActivity.class);
                startActivity(intent);
            }
        });

        ListView productListView = (ListView) findViewById(R.id.listview);

        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        mCursorAdapter = new ProductCursorAdapter(this,null);
        productListView.setAdapter(mCursorAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(ProductCatalogActivity.this,ProductEditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                Log.i(LOG_TAG, "The content URI being passed is " + currentPetUri.toString());

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_product_catalog, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                showDeleteAllPetsConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertProduct() {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, getString(R.string.name_dummy));
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, getString(R.string.quantity_dummy));
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, getString(R.string.price_dummy));
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE,getDrawableResourceUri(R.drawable.pecel).toString());
        Log.i(LOG_TAG, values.toString());
        Uri newRowUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
       if (loaderId==PRODUCT_LOADER){
           String[] projection = {
                   ProductEntry._ID,
                   ProductEntry.COLUMN_PRODUCT_NAME,
                   ProductEntry.COLUMN_PRODUCT_QUANTITY,
                   ProductEntry.COLUMN_PRODUCT_PRICE,
                   ProductEntry.COLUMN_PRODUCT_IMAGE
           };

           // Create the CursorLoader with the Content URI of the products table and a projection.
           return new CursorLoader(
                   this,                       // Parent activity context
                   ProductEntry.CONTENT_URI,       // Content URI of table to query
                   projection,                 // Columns to include in the resulting Cursor
                   null,                       // No selection clause
                   null,                       // No selection arguments
                   null                        // Default sort order
           );

       }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
    private void showDeleteAllPetsConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_products_dialog_msg);

        builder.setPositiveButton(R.string.delete_all_products, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Delete" button, so delete all products
                deleteAllPets();
            }
        });

        builder.setNegativeButton(R.string.cancel_delete_all_products, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Deletes all the products in the products table of the database.
     */
    private void deleteAllPets() {
        // Call the ContentResolver to delete all the products. The ProductEntry.CONTENT_URI
        // specifies content://com.example.android.products/products
        // Pass in null for the selection and selection args because we want to delete every
        // row in the products table.e
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);

        // If more than one row was deleted,
        if (rowsDeleted > 0) {
            // Show a toast explaining that every pet in the table was deleted
            Toast.makeText(this, R.string.catalog_delete_products_successful, Toast.LENGTH_SHORT).show();
        } else {
            // Show a toast explaining that the delete operation was unsuccessful
            Toast.makeText(this, R.string.catalog_delete_products_failed, Toast.LENGTH_SHORT).show();
        }
    }
    private Uri getDrawableResourceUri(int resource) {
        Uri result = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                        getResources().getResourcePackageName(resource) + "/" +
                        getResources().getResourceTypeName(resource) + "/" +
                        getResources().getResourceEntryName(resource));
        return result;
    }
}
