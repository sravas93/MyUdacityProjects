package com.example.sravanthy.habittrackerapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import com.example.sravanthy.habittrackerapp.data.CodeContract;
import com.example.sravanthy.habittrackerapp.data.CodeContract.CodeEntry;
import com.example.sravanthy.habittrackerapp.data.CodeDbHelper;

public class HabitActivity extends AppCompatActivity {


        private CodeDbHelper mCodeDbHelper;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.habit_activity);

            // Setup FAB to open EditorActivity
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HabitActivity.this, EditorActivity.class);
                    startActivity(intent);
                }
            });
            readCode();
            mCodeDbHelper = new CodeDbHelper(this);
        }

    public Cursor readCode(){
        CodeDbHelper mCodeDbHelper = new CodeDbHelper(this);
        SQLiteDatabase db = mCodeDbHelper.getReadableDatabase();
                Cursor cursor = db.query(CodeEntry.TABLE_NAME, new String[]{CodeEntry._ID, CodeEntry.COLUMN_LANGUAGE,
                                CodeEntry.COLUMN_PRACTICE_HOURS, CodeEntry.COLUMN_FEELING, CodeEntry.COLUMN_MODE},
                        null, null,
                        null, null, null);
                TextView displayView = (TextView) findViewById(R.id.text_view_code_record);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // habittracker table in the database).`
            displayView.setText("Code database contains: " + cursor.getCount() + " code history \n\n");
            displayView.append(CodeEntry._ID + "-" + CodeEntry.COLUMN_LANGUAGE + "-" + CodeEntry.COLUMN_PRACTICE_HOURS + "-" +
                    CodeEntry.COLUMN_FEELING + "-" + CodeEntry.COLUMN_MODE + "\n");
            int idColumnIndex = cursor.getColumnIndex(CodeEntry._ID);
            int languageNameColumnIndex = cursor.getColumnIndex(CodeEntry.COLUMN_LANGUAGE);
            int practiceHoursColumnIndex = cursor.getColumnIndex(CodeEntry.COLUMN_PRACTICE_HOURS);
            int feelingColumnIndex = cursor.getColumnIndex(CodeEntry.COLUMN_FEELING);
            int modeColumnIndex = cursor.getColumnIndex(CodeEntry.COLUMN_MODE);
            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(idColumnIndex);
                String currentLanguageName = cursor.getString(languageNameColumnIndex);
                String currentPracticeHours = cursor.getString(practiceHoursColumnIndex);
                String currentFeeling = String.valueOf(cursor.getInt(feelingColumnIndex));
                String currentMode = String.valueOf(cursor.getInt(modeColumnIndex));
                displayView.append("\n" + currentId + "-" + currentLanguageName + "-" + currentPracticeHours + "-" +
                        currentFeeling + "-" + currentMode);
            }

        } finally {
            cursor.close();
            db.close();
        }
                return cursor;
    }


        private void insertCode() {
            // Gets the data repository in write mode
            SQLiteDatabase db = mCodeDbHelper.getWritableDatabase();
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(CodeEntry.COLUMN_LANGUAGE, "RUNNING");
            values.put(CodeEntry.COLUMN_PRACTICE_HOURS, "1");
            values.put(CodeEntry.COLUMN_FEELING, CodeEntry.FEELING_DUBIOUS);
            values.put(CodeEntry.COLUMN_MODE, CodeEntry.MODE_OFFLINE);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(CodeEntry.TABLE_NAME, null, values);
        }

        private void deleteDataBase() {
            Context context = HabitActivity.this;
        /*then delete the database*/
            context.deleteDatabase("code.db");
            mCodeDbHelper = new CodeDbHelper(this);
        }

        @Override
        protected void onStart() {
            super.onStart();
            readCode();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu options from the res/menu/menu_catalog.xml file.
            // This adds menu items to the app bar.
            getMenuInflater().inflate(R.menu.menu_habit, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // User clicked on a menu option in the app bar overflow menu
            switch (item.getItemId()) {
                // Respond to a click on the "Insert dummy data" menu option
                case R.id.action_insert_dummy_data:
                    insertCode();
                    readCode();
                    return true;
                // Respond to a click on the "Delete all entries" menu option
                case R.id.action_delete_all_entries:
                    deleteDataBase();
                    readCode();
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }