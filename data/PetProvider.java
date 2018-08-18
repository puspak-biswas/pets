package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Puspak Biswas on 26-07-2018.
 */

public class PetProvider extends ContentProvider {

    private PetDbHelper mPetDbHelper;

    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;
    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PETS_ID = 101;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMather = new UriMatcher(UriMatcher.NO_MATCH);
    // Static initializer. This is run the first time anything is called from this class.
    static{
        sUriMather.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMather.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",PETS_ID);
    }
    public static final String LOG_TAG = PetContract.PetEntry.class.getSimpleName();


    public boolean onCreate(){
        mPetDbHelper = new PetDbHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder){
        // Get readable database
        SQLiteDatabase database = mPetDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMather.match(uri);

        switch(match){
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetContract.PetEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues contentValues){
        final int match = sUriMather.match(uri);
        switch(match){
            case PETS:
                return (insertPet(uri,contentValues));
            default:
                throw new IllegalArgumentException("Cannot insert unknown URI " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values){

        // Check that the name is not null
        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        if(name == null){
            throw new IllegalArgumentException("Pet name required");
        }

        // Check that the gender is valid
        Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        if(gender == null || !PetContract.PetEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Pet gender not valid");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if(weight != null && weight < 0){
            throw new IllegalArgumentException("Pet weight not valid");
        }

        // Get writeable database
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();
        // Insert the new pet with the given values
        long returnId = database.insert(PetContract.PetEntry.TABLE_NAME,null,values);

        if(returnId == -1){
            Log.e(LOG_TAG,"invalid insert in table"+uri);
            return null;
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri,returnId);
    }

    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        int match = sUriMather.match(uri);
        switch(match){
            case PETS:
                return updatePet(uri,contentValues,selection,selectionArgs);
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                long id = ContentUris.parseId(uri);
                selection = PetContract.PetEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(id)};
                return updatePet(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("The update URI is invalid");
        }
    }
    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)){
            String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
            if(name == null){
                throw new IllegalArgumentException("Pet name required");
            }
        }
        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_GENDER)){
            Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
            if(gender == null || !PetContract.PetEntry.isValidGender(gender)){
                throw new IllegalArgumentException("Pet gender not valid");
            }
        }
        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)){
            Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            if(weight != null && weight < 0){
                throw new IllegalArgumentException("Pet weight not valid");
            }
        }
        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if(values.size() == 0){
            return 0;
        }
        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rows = database.update(PetContract.PetEntry.TABLE_NAME,values,selection,selectionArgs);
        return rows;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs){
        // Get writeable database
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();
        int match = sUriMather.match(uri);
        switch(match){
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);
            case PETS_ID:
                // Delete a single row given by the ID in the URI
                long id = ContentUris.parseId(uri);
                selection = PetContract.PetEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(id)};
                return database.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Cant do delete due to invalid URI");
        }
    }

    public String getType(Uri uri){
        int match = sUriMather.match(uri);
        switch(match){
            case PETS:
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case PETS_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI "+uri+" with match "+match);
        }
    }
}
