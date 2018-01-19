package com.example.android.mygarden;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.ui.PlantDetailActivity;
import com.example.android.mygarden.utils.PlantUtils;

import static com.example.android.mygarden.provider.PlantContract.INVALID_PLANT_ID;

/**
 * Created by User on 14:35 19.01.2018.
 */
public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridWidgetFactory(this.getApplicationContext());
    }
}

class GridWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;

    public GridWidgetFactory(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Uri uri = PlantContract.PlantEntry.CONTENT_URI;
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_CREATION_TIME
        );
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            return 0;
        } else return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null && mCursor.getCount() == 0) return null;

        mCursor.moveToPosition(position);
        long plantId;
        int imgRes;

        int idIndex = mCursor.getColumnIndex(PlantContract.PlantEntry._ID);
        int createTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int plantTypeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

        plantId = mCursor.getLong(idIndex);
        long timeNow = System.currentTimeMillis();
        long wateredAt = mCursor.getLong(waterTimeIndex);
        long createdAt = mCursor.getLong(createTimeIndex);
        int plantType = mCursor.getInt(plantTypeIndex);
        imgRes = PlantUtils.getPlantImageRes(mContext, timeNow - createdAt,
                timeNow - wateredAt, plantType);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.plant_widget);

        views.setImageViewResource(R.id.widget_plant_image, imgRes);
        views.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));
        views.setViewVisibility(R.id.widget_water_button, View.GONE);

        Bundle extras = new Bundle();
        extras.putLong(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        Intent intent = new Intent();
        intent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_plant_image, intent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
