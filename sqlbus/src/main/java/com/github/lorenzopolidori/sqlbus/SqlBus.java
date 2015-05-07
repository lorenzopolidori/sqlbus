package com.github.lorenzopolidori.sqlbus;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

public class SqlBus {

    final static String SQLBUS_INTENT_FILTER = SqlBus.class.getName() + ".intent-filter";
    final static String SQLBUS_INTENT_FILTER_CATEGORY_INSERT = SQLBUS_INTENT_FILTER + ".category.insert";
    final static String SQLBUS_INTENT_FILTER_CATEGORY_UPDATE = SQLBUS_INTENT_FILTER + ".category.update";
    final static String SQLBUS_INTENT_FILTER_CATEGORY_DELETE = SQLBUS_INTENT_FILTER + ".category.delete";

    private static SqlBus sInstance = null;

    private final SQLiteOpenHelper mSQLiteHelper;

    public static SqlBus create(SQLiteOpenHelper helper) {
        sInstance = new SqlBus(helper);
        return sInstance;
    }

    public static SqlBus get() {
        return sInstance;
    }

    private SqlBus(SQLiteOpenHelper helper) {
        mSQLiteHelper = helper;
    }

    public void insertAsync(final Context context, final SqlBusCommand command) {
        new SqlCommandAsyncTask(mSQLiteHelper, new SqlBusCommand() {
            @Override
            public int execute(SQLiteDatabase db) {
                return command.execute(db);
            }

            @Override
            public void onSuccess(int rowCount) {
                sendLocalBroadcast(context, SQLBUS_INTENT_FILTER_CATEGORY_INSERT);
                command.onSuccess(rowCount);
            }

            @Override
            public void onFailure() {
                command.onFailure();
            }
        }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public void updateAsync(final Context context, final SqlBusCommand command) {
        new SqlCommandAsyncTask(mSQLiteHelper, new SqlBusCommand() {
            @Override
            public int execute(SQLiteDatabase db) {
                return command.execute(db);
            }

            @Override
            public void onSuccess(int rowCount) {
                sendLocalBroadcast(context, SQLBUS_INTENT_FILTER_CATEGORY_INSERT);
                command.onSuccess(rowCount);
            }

            @Override
            public void onFailure() {
                command.onFailure();
            }
        }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public void queryAsync(SqlBusQuery query) {
        new SqlQueryAsyncTask(mSQLiteHelper, query).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public Cursor query(SqlBusQuery query) {
        return query.execute(mSQLiteHelper.getReadableDatabase());
    }

    public SqlBusLoader createLoader(Context context, SqlBusQuery query) {
        return new SqlBusLoader(context, this, query);
    }

    private void sendLocalBroadcast(Context context, String category) {
        Intent intent = new Intent(SQLBUS_INTENT_FILTER);
        intent.addCategory(category);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    private class SqlQueryAsyncTask extends AsyncTask<Void, Void, Cursor> {
        private SQLiteOpenHelper mSQLiteHelper = null;
        private SqlBusQuery mQuery = null;

        public SqlQueryAsyncTask(SQLiteOpenHelper helper, SqlBusQuery query) {
            mSQLiteHelper = helper;
            mQuery = query;
        }

        @Override
        protected Cursor doInBackground(Void... sqlQueries) {
            return mQuery.execute(mSQLiteHelper.getReadableDatabase());
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mQuery.onSuccess(cursor);
        }
    }

    private class SqlCommandAsyncTask extends AsyncTask<Void, Void, Integer> {
        private SQLiteOpenHelper mSQLiteHelper = null;
        private SqlBusCommand mCommand = null;

        public SqlCommandAsyncTask(SQLiteOpenHelper helper, SqlBusCommand command) {
            mSQLiteHelper = helper;
            mCommand = command;
        }

        @Override
        protected Integer doInBackground(Void... sqlQueries) {
            return mCommand.execute(mSQLiteHelper.getWritableDatabase());
        }

        @Override
        protected void onPostExecute(Integer rowCount) {
            mCommand.onSuccess(rowCount);
        }
    }
}
