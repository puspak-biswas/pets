/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.CursorAdapter;

import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private PetDbHelper mDbHelper;

    private static final int PET_LOADER = 0;

    private PetCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Log.i("CatalogActivity", "onCreate: Has been called ");

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = null;
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        mCursorAdapter = new PetCursorAdapter(this,null);
        petListView.setAdapter(mCursorAdapter);
        mDbHelper = new PetDbHelper(this);

        getLoaderManager().initLoader(PET_LOADER,null,this);

        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri uri = ContentUris.withAppendedId(PetEntry.CONTENT_URI,id);
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        //displayDatabaseInfo();
    }

    //protected void onStart(){
     //   super.onStart();
        //displayDatabaseInfo();
    //}

   // private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.

    //    String[] projection = {PetEntry._ID,PetEntry.COLUMN_PET_NAME,PetEntry.COLUMN_PET_BREED,PetEntry.COLUMN_PET_GENDER,
    //    PetEntry.COLUMN_PET_WEIGHT};

     //   Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI,projection,null,null,null);
        //TextView displayView = (TextView) findViewById(R.id.text_view_pet);

      //  ListView list = (ListView) findViewById(R.id.list);
       // PetCursorAdapter adapter = new PetCursorAdapter(this,cursor);
       // list.setAdapter(adapter);

       // cursor.close();

        //try {
            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - name - breed - gender - weight
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
        //    displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
        //    displayView.append(PetEntry._ID + " - " +
        //            PetEntry.COLUMN_PET_NAME + "-" + PetEntry.COLUMN_PET_BREED + "-" +
        //            PetEntry.COLUMN_PET_GENDER + "-" + PetEntry.COLUMN_PET_WEIGHT + "\n");

            // Figure out the index of each column
         //   int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
         //   int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
         //   int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
         //   int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
         //   int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            // Iterate through all the returned rows in the cursor
         //   while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
         //       int currentID = cursor.getInt(idColumnIndex);
         //       String currentName = cursor.getString(nameColumnIndex);
         //       String currentBreed = cursor.getString(breedColumnIndex);
         //       int currentGender = cursor.getInt(genderColumnIndex);
         //       int currentWeight = cursor.getInt(weightColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
         //       displayView.append(("\n" + currentID + " - " +
         //               currentName + "-" + currentBreed + "-" + currentGender + "-" + currentWeight));
          //  }
        //} finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
         //   cursor.close();
        //}
    //}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertRow();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllRows();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteAllRows(){
        int numberOfRowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI,null,null);
    }

    public void insertRow(){
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME,"Toto");
        values.put(PetEntry.COLUMN_PET_BREED,"Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER,1);
        values.put(PetEntry.COLUMN_PET_WEIGHT,14);
        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI,values);
        if(newUri == null){
            Log.e("InsertResult","not correct");
        }
        //displayDatabaseInfo();
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle bundle){
        String[] projection = {PetEntry._ID,PetEntry.COLUMN_PET_NAME,PetEntry.COLUMN_PET_BREED};
        return new CursorLoader(this,PetEntry.CONTENT_URI,projection,null,null,null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
        mCursorAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> loader){
        mCursorAdapter.swapCursor(null);
    }
}
