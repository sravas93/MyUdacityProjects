package com.example.sravanthy.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sravanthy.inventoryapp.ProductProvider.ProductEntry;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by sandeep on 12/22/2016.
 */
public class ProductEditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 1;

    private static final String LOG_TAG = ProductEditorActivity.class.getSimpleName();

    private Uri mCurrentProductUri;

    private EditText mNameEditText;

    private Button mMinusButton;

    int productQuantity;

    Uri productImageUri;

    private Button mPlusButton;

    private EditText mQuantityEditText;

    private EditText mPriceEditText;

    private Button mOrderButton;

    private ImageView mImageView;

    private Button mImageButton;
    public static final int PICK_IMAGE_REQUEST = 1;

    private boolean mProductHasChanged = false;

    private String imageUri;

    private Uri uri;
   // private static final int GET_IMAGE_ACTIVITY = 1234;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_product);

        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mMinusButton = (Button) findViewById(R.id.minus_quantity_button);
        mPlusButton = (Button) findViewById(R.id.plus_quantity_button);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_Quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_Price);
        mOrderButton = (Button) findViewById(R.id.order_more_button);
        mImageButton = (Button) findViewById(R.id.product_image_button);
        mImageView = (ImageView) findViewById(R.id.image_view);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle("Add Product");

            invalidateOptionsMenu();
        } else {
            setTitle("Edit Product");

            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
        mNameEditText.setOnTouchListener(mTouchListener);
        mMinusButton.setOnTouchListener(mTouchListener);
        mPlusButton.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mImageButton.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(mTouchListener);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"we_sale_things@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "We need more " + mNameEditText.getText().toString() + " right now!");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ProductEditorActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantity = mQuantityEditText.getText().toString().trim();
                if (!currentQuantity.isEmpty()) {
                    int quantity = Integer.parseInt(currentQuantity);
                    mQuantityEditText.setText(String.valueOf(++quantity));
                } else {
                    mQuantityEditText.setText(String.valueOf(0));
                }
                mProductHasChanged = true;
            }
        });

        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantity = mQuantityEditText.getText().toString().trim();
                if (!currentQuantity.isEmpty()) {
                    int quantity = Integer.parseInt(currentQuantity);
                    if ( quantity > 0){
                        mQuantityEditText.setText(String.valueOf(--quantity));
                } else if(quantity==0) {
                    mQuantityEditText.setText(String.valueOf(0));
                        Toast.makeText(ProductEditorActivity.this,"Can not go below 0",Toast.LENGTH_SHORT).show();
                }
                }
                mProductHasChanged = true;
            }
        });
    }


       @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_product_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                // Exit activity
                // finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Display a dialog for the user to confirm deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(ProductEditorActivity.this);
                    return true;
                }


                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ProductEditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }



    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ProductEditorActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    mImageView.setImageBitmap(bitmap);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

                mImageView.setImageBitmap(thumbnail);
            }
        }
    }



    private void saveProduct() {
        ContentValues values = new ContentValues();
        if (mNameEditText.getText().toString().length() == 0) {
            Toast.makeText(ProductEditorActivity.this, "Product requires a name", Toast.LENGTH_SHORT).show();
        } else if (mQuantityEditText.getText().toString().length() == 0) {
            Toast.makeText(ProductEditorActivity.this, "Product requires a quantity", Toast.LENGTH_SHORT).show();
        } else if (mPriceEditText.getText().toString().length() == 0) {
            Toast.makeText(ProductEditorActivity.this, "product requires a price", Toast.LENGTH_SHORT).show();
        }
        else {
            String nameString = mNameEditText.getText().toString().trim();
            String quantityString = mQuantityEditText.getText().toString().trim();
            String priceString = mPriceEditText.getText().toString().trim();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] img = bos.toByteArray();




            if (mCurrentProductUri == null &&
                    TextUtils.isEmpty(nameString) && TextUtils.isEmpty(quantityString) &&
                    TextUtils.isEmpty(priceString)) {
                return;
            }
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
            values.put(ProductEntry.COLUMN_PRODUCT_IMAGE,img);

            int quantity = 0;
            if (!TextUtils.isEmpty(quantityString)) {
                quantity = Integer.parseInt(quantityString);
            }
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

            int price = 0;
            if (!TextUtils.isEmpty(priceString)) {
                price = Integer.parseInt(priceString);
            }
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);


            // Determine if this is a new or existing pet by checking if mCurrentProductUri is null or not
            if (mCurrentProductUri == null) {
                // This is a NEW product, so insert a new product into the provider.
                // Receive the content URI of the new row.
                Uri newRowUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

                // If the row URI is null, this means a new row was not successfully inserted into the table
                if (newRowUri == null) {
                    // If a new row was not successfully added, display a Toast to that effect
                    Toast.makeText(this, R.string.editor_insert_product_failed, Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, display a toast saying "Product saved"
                    Toast.makeText(this, R.string.editor_insert_product_successful, Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsUpdated = getContentResolver().update(mCurrentProductUri, values, null, null);

                // If the number of rows updated is 0, no rows were updated, i.e. the product's values
                // were not updated
                if (rowsUpdated == 0) {
                    // Display a Toast explaining that there was an error with the update
                    Toast.makeText(this, R.string.editor_update_product_failed, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Otherwise, display a toast saying "Product updated"
                    Toast.makeText(this, R.string.editor_update_product_successful, Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_IMAGE
        };


        return new CursorLoader(
                this,                       // Parent activity context
                mCurrentProductUri,             // The content URI of the table being queried
                projection,                 // The columns to be returned
                null,                       // The selection clause
                null,                       // The selection arguments
                null
          );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            int price = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
          //  byte[] blob = cursor.getBlob(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE));

//            String productImageUriStr = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE));
//            productImageUri = Uri.parse(productImageUriStr);
            mNameEditText.setText(name);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Integer.toString(price));
           // mImageView.setImageURI(productImageUri);


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mImageView.setImageURI(null);

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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
     * Perform the deletion of the product in the database.
     */
    private void deletePet() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete. Display a toast
                // explaining that deletion failed.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast to that effect.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}

