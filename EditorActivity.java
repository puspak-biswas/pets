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
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.content.Loader;
import android.support.v4.content.ContentResolverCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

import java.net.URI;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    private Uri mUri;

    private static final int EXISTING_PET_LOADER = 0;

    //define a variable to track whether user has made any changes to any of the input fields
    private boolean mPetHasChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
// the view, and we change the mPetHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        //attach the on touch listener to each field so that if any field is edited it can be detected
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();

        Intent intent = getIntent();
        mUri = intent.getData();
        if(mUri == null){
            // This is a new pet, so change the app bar to say "Add a Pet"
            setTitle("Add a pet");
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        }else{
            setTitle("Edit a pet");
            getLoaderManager().initLoader(EXISTING_PET_LOADER,null,this);
        }

    }

    //onPrepareOptionsMenu will get called and you can modify the Menu object by hiding the delete menu option if it’s a new pet
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    //If back button is pressed then check if user changes have been saved
    public void onBackPressed(){
        //Check if input fields have been changed.If not then continue
        if(!mPetHasChanged){
            super.onBackPressed();
            return;
        }

        //If input fields have been changed, warn user that there are unsaved changes.
        //Create a click listener to handle user's response to discard changes
        //DialogInterface.OnClickListener discardButtonClickListener =
        //        new DialogInterface.OnClickListener(){
        //            @Override
        //            public void onClick(DialogInterface dialogInterface, int i){
        //                //User clicked "discard" button so finish activity
        //                finish();
        //            }
        //       };
        // Show dialog that there are unsaved changes
        //showUnsavedChangesDialog(discardButtonClickListener);
        //If input fields have been changed, warn user that there are unsaved changes.
        showUnsavedChangesDialog();
    }
//DialogInterface.OnClickListener discardButtonClickListener
    private void showUnsavedChangesDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener(){
            //Create a click listener to handle user's response to discard changes
            public void onClick(DialogInterface dialogInterface, int i){
                finish();
            }
        });
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
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
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    public void savePet(){
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();
        //check whether input fields are empty. If yes and if it is a new pet insertion then just finish the activity without inserting a new pet
        if (mUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString)
            && TextUtils.isEmpty(weightString) && mGender == PetEntry.GENDER_UNKNOWN){
            return;
        }
        //check if the WEIGHT field is non integer. If yes then default it to zero
        int weight = 0;
        if(!TextUtils.isEmpty(weightString)){
            weight = Integer.parseInt(weightString);
        }
        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, nameString);
        values.put(PetEntry.COLUMN_PET_BREED, breedString);
        values.put(PetEntry.COLUMN_PET_GENDER, mGender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, weightString);

        if(mUri == null) {
            //inserting a new pet
            // Insert a new pet into the provider, returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.insert_failed, Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.insert_success, Toast.LENGTH_SHORT).show();
            }
        }else{
            //updating an existing pet
            int rowsUpdated = getContentResolver().update(mUri,values,null,null);
            if (rowsUpdated == 0){
                //Error while updating
                Toast.makeText(this,"Error while updating pet", Toast.LENGTH_SHORT).show();
            }else{
                //successful update
                Toast.makeText(this,"Pet updated successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                savePet();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                //DialogInterface.OnClickListener discardButtonClickListener =
                //        new DialogInterface.OnClickListener() {
                //            @Override
                //            public void onClick(DialogInterface dialogInterface, int i) {
                //                // User clicked "Discard" button, navigate to parent activity.
                //                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                //            }
                //        };

                // Show a dialog that notifies the user they have unsaved changes
                //showUnsavedChangesDialog(discardButtonClickListener);
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                showUnsavedChangesDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        String[] projection = {PetEntry.COLUMN_PET_NAME,PetEntry.COLUMN_PET_BREED,PetEntry.COLUMN_PET_WEIGHT,PetEntry.COLUMN_PET_GENDER};
        return new CursorLoader(this,mUri,projection,null,null,null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
        if(cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            mNameEditText.setText(cursor.getString(nameColumnIndex));
            mBreedEditText.setText(cursor.getString(breedColumnIndex));
            mWeightEditText.setText(cursor.getString(weightColumnIndex));
            mGenderSpinner.setSelection(cursor.getInt(genderColumnIndex));
        }
    }

    public void onLoaderReset(Loader<Cursor> loader){
        mNameEditText.setText(null);
        mBreedEditText.setText(null);
        mWeightEditText.setText(null);
        mGenderSpinner.setSelection(0);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deletePet(){
        int numberOfRowsDeleted = getContentResolver().delete(mUri,null,null);
        if(numberOfRowsDeleted == 0){
            Toast.makeText(this,"Delete was not successful",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Deleted successfully",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
