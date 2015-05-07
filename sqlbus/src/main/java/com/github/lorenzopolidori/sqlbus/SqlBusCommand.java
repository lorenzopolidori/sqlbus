package com.github.lorenzopolidori.sqlbus;

import android.database.sqlite.SQLiteDatabase;

public interface SqlBusCommand {
    int execute(SQLiteDatabase db);

    void onSuccess(int rowCount);

    void onFailure();

    class EmptySqlBusCommand implements SqlBusCommand {

        @Override
        public int execute(SQLiteDatabase db) {
            return 0;
        }

        @Override
        public void onSuccess(int rowCount) {

        }

        @Override
        public void onFailure() {

        }
    }
}
