package com.example.sravanthy.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.sravanthy.inventoryapp.data.ProductDbHelper;

import java.io.ByteArrayOutputStream;

/**
 * Created by sandeep on 12/22/2016.
 */
public class ProductProvider extends ContentProvider {
    public static final String CONTENT_AUTHORITY = "com.example.sravanthy.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";

  public static final class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_NAME ="name";

        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        public final static String COLUMN_PRODUCT_PRICE = "price";

        public final static String COLUMN_PRODUCT_IMAGE = "image";

    }



    private static final int PRODUCTS = 100;


    private static final int PRODUCTS_ID= 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {

        sUriMatcher.addURI(ProductProvider.CONTENT_AUTHORITY, ProductProvider.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductProvider.CONTENT_AUTHORITY, ProductProvider.PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    /** Tag for the log messages */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /** The {@link ProductDbHelper} which helps the app create, version, access the SQLite database */
    private ProductDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {

        mDbHelper = new ProductDbHelper(getContext());

        return true;
    }


@Override
public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                    String sortOrder) {
    // Get readable database
    SQLiteDatabase database = mDbHelper.getReadableDatabase();

    // This cursor will hold the result of the query
    Cursor cursor;

    // Figure out if the URI matcher can match the URI to a specific code
    int match = sUriMatcher.match(uri);
    switch (match) {
        case PRODUCTS:

                    cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                    null, null, sortOrder);
            break;
        case PRODUCTS_ID:
            selection = ProductEntry._ID + "=?";
            selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

            // This will perform a query on the pets table where the _id equals 3 to return a
            // Cursor containing that row of the table.
            cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                    null, null, sortOrder);
            break;
        default:
            throw new IllegalArgumentException("Cannot query unknown URI " + uri);
    }

    // Set notification URI on the Cursor, so we know what content URI the
    // Cursor was created for.
    // If the data at this URI changes, then we know we need to update the Cursor.
    cursor.setNotificationUri(getContext().getContentResolver(), uri);

    return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    public Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires valid price");
        }

        // If the price is provided, check that it's greater than or equal to 0
        Integer price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product requires valid price");
        }


        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();


        long id = database.insert(ProductEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the ContentValues object is empty (i.e. 0), then there's no point in proceeding
        // further, because there's no values to update.
        // The number of updated rows will be 0, so return that value.
        if (values.size() == 0) {
            return 0;
        }

        // INPUT VALIDATION
        // Check if the user is attempting to update the "name" column for X number of rows
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            // Check that the user's desired value for the name is not null.
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity  < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        // No need to check the breed. Any value is valid (including null).

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Update the database with the given values.
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // If rows were updated, notify all listeners that the content URI has changed,
        // and the current Cursor is stale.
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement.
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Get the integer code produced by the URI matcher
        final int match = sUriMatcher.match(uri);

        int rowsDeleted;


        switch (match) {
            // Delete all rows that match the selection and selection args
            case PRODUCTS:
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            // Delete a single row given by the ID in the URI
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If rows were deleted, notify all listeners that the content URI has changed, and the
        // current Cursor is stale.
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the deletion statement.
        return rowsDeleted;
    }

        /**
         * Returns the MIME type of data for the content URI.
         */
    @Override
    public String getType(Uri uri) {
        // Get the integer code produced by the URI matcher
        final int match = sUriMatcher.match(uri);


        switch (match) {

            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;

            case PRODUCTS_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
