package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Puspak Biswas on 26-07-2018.
 */

public class PetProvider extends ContentProvider {

    private PetDbHelper mPetDbHelper;

    public static final String LOG_TAG = PetContract.PetEntry.class.getSimpleName();

    public boolean onCreate(){
        mPetDbHelper = new PetDbHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder){
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues){
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        return 0;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs){
        return 0;
    }

    public String getType(Uri uri){
        return null;
    }
}
