package com.example.audiobookshelfwatch.presentation

import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService

class DataLayerListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        Log.d("DataLayerListener", "onDataChanged received ${dataEvents.count} events.")

        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path?.startsWith("/audiobook") == true) {
                Log.d("DataLayerListener", "Found audiobook data item: ${event.dataItem.uri}")

                val bookId = event.dataItem.uri.lastPathSegment
                if (bookId.isNullOrEmpty()) {
                    Log.e("DataLayerListener", "Book ID is missing from URI.")
                    return@forEach
                }

                // Pass the URI of the entire DataItem, not just the asset.
                val workRequest = OneTimeWorkRequestBuilder<FileReceiverWorker>()
                    .setInputData(workDataOf(
                        "data_item_uri" to event.dataItem.uri.toString(),
                        "file_name" to "$bookId.m4a"
                    ))
                    .build()

                WorkManager.getInstance(applicationContext).enqueue(workRequest)
                Log.d("DataLayerListener", "WorkManager request enqueued for bookId: $bookId")
            }
        }
    }
}
