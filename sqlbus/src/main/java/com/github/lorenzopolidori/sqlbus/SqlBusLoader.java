package com.github.lorenzopolidori.sqlbus;

import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import static com.github.lorenzopolidori.sqlbus.SqlBus.SQLBUS_INTENT_FILTER;
import static com.github.lorenzopolidori.sqlbus.SqlBus.SQLBUS_INTENT_FILTER_CATEGORY_DELETE;
import static com.github.lorenzopolidori.sqlbus.SqlBus.SQLBUS_INTENT_FILTER_CATEGORY_INSERT;
import static com.github.lorenzopolidori.sqlbus.SqlBus.SQLBUS_INTENT_FILTER_CATEGORY_UPDATE;


public class SqlBusLoader extends AsyncTaskLoader<Cursor> {
    private final SqlBus mSqlBus;
    private final SqlBusQuery mQuery;
    private final SqlBusBroadcastReceiver mReceiver;
    private final IntentFilter mFilter;


    public SqlBusLoader(Context context, SqlBus sqlBus, SqlBusQuery query) {
        super(context);
        mSqlBus = sqlBus;
        mQuery = query;
        mReceiver = new SqlBusBroadcastReceiver();

        mFilter = new IntentFilter(SQLBUS_INTENT_FILTER);
        mFilter.addCategory(SQLBUS_INTENT_FILTER_CATEGORY_INSERT);
        mFilter.addCategory(SQLBUS_INTENT_FILTER_CATEGORY_UPDATE);
        mFilter.addCategory(SQLBUS_INTENT_FILTER_CATEGORY_DELETE);
    }

    @Override
    public Cursor loadInBackground() {
        return mSqlBus.query(mQuery);
    }

    @Override
    protected void onStartLoading() {
        Log.d("SqlBusLoader", "onStartLoading");
        super.onStartLoading();
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(mReceiver, mFilter);
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        Log.d("SqlBusLoader", "onStopLoading");
        super.onStopLoading();
        LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onAbandon() {
        Log.d("SqlBusLoader", "onAbandon");
        super.onAbandon();
        LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver(mReceiver);
    }

    private class SqlBusBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            forceLoad();
            Log.d("SqlBusBroadcastReceiver", "Received broadcast " + intent.getAction());
        }
    }
}
