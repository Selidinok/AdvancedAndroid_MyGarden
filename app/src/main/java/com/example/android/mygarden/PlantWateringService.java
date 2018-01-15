package com.example.android.mygarden;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;

import java.sql.Time;

/**
 * Created by User on 14:58 15.01.2018.
 */

public class PlantWateringService extends IntentService {
    public static final String ACTION_WATER_PLANTS =
            "com.example.android.mygarden.action.water_plants";
    private static final String TAG = PlantWateringService.class.getSimpleName();

    public PlantWateringService() {
        super("PlantWateringService");
    }

    public static void startActionWaterPlants(Context context) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_WATER_PLANTS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final String action = intent.getAction();
        if (ACTION_WATER_PLANTS.equals(action))  handleActionWaterPlants();
    }

    private void handleActionWaterPlants() {
        ContentValues cv = new ContentValues();
        long timeNow = System.currentTimeMillis();
        cv.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);

        getContentResolver().update(
                PlantContract.PlantEntry.CONTENT_URI,
                cv,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME+">?",
                new String[]{String.valueOf(timeNow - PlantUtils.MAX_AGE_WITHOUT_WATER)});

        Log.d(TAG, "Plants is watering");
    }
}
