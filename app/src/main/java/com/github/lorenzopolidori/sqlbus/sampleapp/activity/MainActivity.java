package com.github.lorenzopolidori.sqlbus.sampleapp.activity;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.github.lorenzopolidori.sqlbus.SqlBus;
import com.github.lorenzopolidori.sqlbus.SqlBusCommand;
import com.github.lorenzopolidori.sqlbus.SqlBusQuery;
import com.github.lorenzopolidori.sqlbus.sampleapp.R;
import com.github.lorenzopolidori.sqlbus.sampleapp.database.DatabaseHelper;
import com.github.lorenzopolidori.sqlbus.sampleapp.database.TodoTable;

import java.util.Random;


public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);
        SqlBus.create(new DatabaseHelper(this));

        this.getListView().setDividerHeight(2);
        fillData();
        registerForContextMenu(getListView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    private void fillData() {
        String[] from = new String[]{TodoTable.COLUMN_SUMMARY};
        int[] to = new int[]{R.id.label};

        getLoaderManager().initLoader(0, null, this);

        adapter = new SimpleCursorAdapter(this, R.layout.todo_row, null, from, to, 0);

        setListAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return SqlBus.get().createLoader(this, new SqlBusQuery.EmptySqlQuery() {
            @Override
            public Cursor execute(SQLiteDatabase db) {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(TodoTable.TABLE_TODO);

                return builder.query(db,
                        new String[]{TodoTable.COLUMN_ID, TodoTable.COLUMN_SUMMARY},
                        null, null, null, null, null);
            }
        });
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public void onClick(View view) {
        SqlBus.get().insertAsync(this, new SqlBusCommand.EmptySqlBusCommand() {
            @Override
            public int execute(SQLiteDatabase db) {
                ContentValues values = new ContentValues();
                values.put(TodoTable.COLUMN_CATEGORY, "Nice");
                values.put(TodoTable.COLUMN_SUMMARY, "Summary " + randInt(0, 1000));
                values.put(TodoTable.COLUMN_DESCRIPTION, "Description");
                return (int) db.insert(TodoTable.TABLE_TODO, null, values);
            }
        });
    }

    private static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}
