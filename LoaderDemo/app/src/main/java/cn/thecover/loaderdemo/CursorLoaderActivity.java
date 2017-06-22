package cn.thecover.loaderdemo;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import cn.thecover.loaderdemo.provider.LoaderContentProvider;

public class CursorLoaderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter;
    private ListView mListView;
    private int mCursorType = 1;

    private static final int CURSOR_TYPE_CONTACTS = 0;//Contacts
    private static final int CURSOR_TYPE_PACKAGE_NAME = 1;//packageName

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursor_loader);
        mListView = (ListView) findViewById(R.id.lv_contacts);
        initAdapter();

        initLoader();
    }

    private void initAdapter() {
        switch (mCursorType) {
            case CURSOR_TYPE_CONTACTS:
                mAdapter = new SimpleCursorAdapter(this,
                        android.R.layout.simple_list_item_2, null,
                        new String[]{Contacts.DISPLAY_NAME, Contacts.CONTACT_STATUS},
                        new int[]{android.R.id.text1, android.R.id.text2}, 0);
                break;
            case CURSOR_TYPE_PACKAGE_NAME:
                mAdapter = new SimpleCursorAdapter(this,
                        android.R.layout.simple_list_item_2, null,
                        new String[]{LoaderContentProvider.COLUMN_PACKAGE_NAME},
                        new int[]{android.R.id.text1}, 0);
                break;
        }
        mListView.setAdapter(mAdapter);
    }

    private void initLoader() {
        getLoaderManager().initLoader(mCursorType, null, this);
    }


    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            Contacts._ID,
            Contacts.DISPLAY_NAME,
            Contacts.CONTACT_STATUS,
            Contacts.CONTACT_PRESENCE,
            Contacts.PHOTO_ID,
            Contacts.LOOKUP_KEY,
    };

    String mCurFilter;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case CURSOR_TYPE_CONTACTS:
                return getContactsCursor();
            case CURSOR_TYPE_PACKAGE_NAME:
                return getPackageCursor();
            default:
                return getContactsCursor();
        }
    }

    private Loader<Cursor> getContactsCursor() {
        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
                    Uri.encode(mCurFilter));
        } else {
            baseUri = Contacts.CONTENT_URI;
        }

        String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + Contacts.DISPLAY_NAME + " != '' ))";
        return new CursorLoader(this, baseUri,
                CONTACTS_SUMMARY_PROJECTION, select, null,
                Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
    }

    private Loader<Cursor> getPackageCursor() {
        Uri baseUri = LoaderContentProvider.PACKAGE_NAME_CONTENT_URI;
        return new CursorLoader(this, baseUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
