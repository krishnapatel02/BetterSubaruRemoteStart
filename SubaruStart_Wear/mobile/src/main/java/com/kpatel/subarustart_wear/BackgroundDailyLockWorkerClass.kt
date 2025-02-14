package com.kpatel.subarustart_wear

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class BackgroundDailyLockWorkerClass(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val appContext = applicationContext;
        val datastore = DataStoreSingleton.getDataStoreRepo();
        execute(appContext, "lock",  datastore);
        return Result.success()
    }

}