package cn.thecover.loaderdemo.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class LoaderContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.futureagent.loader.content.provider";

    public static final Uri PACKAGE_NAME_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/pkgName");

    public static final String COLUMN_PACKAGE_NAME = "packageName"; // app的应用名
    public static final String COLUMN_ID = "_id"; // app的应用名

    String[] COLUMNS = {
            COLUMN_ID,
            COLUMN_PACKAGE_NAME,
    };

    public LoaderContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO: Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return queryAllPackageName();
    }

    private Cursor queryAllPackageName() {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        List<PackageNameEntity> packageList = getLocalPackageList();
        for (int index = 0; index < packageList.size(); index++) {
            cursor.addRow(convertEntityToColumns(index, packageList.get(index)));
        }
        return cursor;
    }

    private List<PackageNameEntity> getLocalPackageList() {
        List<PackageNameEntity> packageNameEntityList = new ArrayList<>();
        packageNameEntityList.add(new PackageNameEntity("com.tencent.mm"));
        packageNameEntityList.add(new PackageNameEntity("com.tencent.mobileqq"));
        packageNameEntityList.add(new PackageNameEntity("com.baidu.searchbox"));
        packageNameEntityList.add(new PackageNameEntity("com.qihoo360.mobilesafe"));
        packageNameEntityList.add(new PackageNameEntity("com.eg.android.AlipayGphone"));
        packageNameEntityList.add(new PackageNameEntity("com.ss.android.article.news"));
        return packageNameEntityList;
    }

    private String[] convertEntityToColumns(int index, PackageNameEntity entity) {
        String[] columns = new String[COLUMNS.length];
        columns[0] = String.valueOf(index);
        columns[1] = entity.getPkgName();
        return columns;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class PackageNameEntity {
        private String pkgName;

        public PackageNameEntity(String pkgName) {
            this.pkgName = pkgName;
        }

        public String getPkgName() {
            return pkgName;
        }

        public void setPkgName(String pkgName) {
            this.pkgName = pkgName;
        }
    }
}
