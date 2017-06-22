package cn.thecover.loaderdemo;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ListView;

import cn.thecover.loaderdemo.provider.LoaderContentProvider;

public class AsyncTaskActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static SimpleCursorAdapter mAdapter;
    private ListView mListView;
    private int mCursorType = 0;

    private static final int CURSOR_TYPE_CONTACTS = 0;//Contacts
    private static final int CURSOR_TYPE_PACKAGE_NAME = 1;//packageName

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);

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
        return new ContactsLoader(this);
    }

    private Loader<Cursor> getPackageCursor() {
        return new PackageNameLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private static class PackageNameLoader extends AsyncTaskLoader<Cursor> {

        private Context mContext;
        private ContentResolver mResolver;

        PackageNameLoader(Context context) {
            super(context);
            mContext = context;
            mResolver = mContext.getContentResolver();
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();

            forceLoad();
        }

        @Override
        public Cursor loadInBackground() {
            return mResolver.query(LoaderContentProvider.PACKAGE_NAME_CONTENT_URI,
                    new String[]{LoaderContentProvider.COLUMN_ID, LoaderContentProvider.COLUMN_PACKAGE_NAME},
                    null, null, null);
        }

        @Override
        public void deliverResult(Cursor data) {
            super.deliverResult(data);
            mAdapter.swapCursor(data);
        }
    }

    private static class ContactsLoader extends AsyncTaskLoader<Cursor> {

        private Context mContext;
        private ContentResolver mResolver;
        private String mCurFilter = "";

        static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
                Contacts._ID,
                Contacts.DISPLAY_NAME,
                Contacts.CONTACT_STATUS,
                Contacts.CONTACT_PRESENCE,
                Contacts.PHOTO_ID,
                Contacts.LOOKUP_KEY,
        };

        ContactsLoader(Context context) {
            super(context);
            mContext = context;
            mResolver = mContext.getContentResolver();
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();

            forceLoad();
        }

        @Override
        public Cursor loadInBackground() {
            Uri baseUri;
            if (!TextUtils.isEmpty(mCurFilter)) {
                baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
                        Uri.encode(mCurFilter));
            } else {
                baseUri = ContactsContract.Contacts.CONTENT_URI;
            }

            String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                    + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                    + Contacts.DISPLAY_NAME + " != '' ))";
            return mResolver.query(baseUri,
                    CONTACTS_SUMMARY_PROJECTION, select, null,
                    Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        }

        @Override
        public void deliverResult(Cursor data) {
            super.deliverResult(data);
            mAdapter.swapCursor(data);
        }
    }
}
