package com.github.lorenzopolidori.sqlbus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public interface SqlBusQuery {
    Cursor execute(SQLiteDatabase db);

    void onSuccess(Cursor c);

    void onFailure();

    class EmptySqlQuery implements SqlBusQuery {

        @Override
        public Cursor execute(SQLiteDatabase db) {
            return null;
        }

        @Override
        public void onSuccess(Cursor c) {

        }

        @Override
        public void onFailure() {

        }
    }
}
