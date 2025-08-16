package com.example.audiobookshelfwatch.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream

class FileReceiverWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val dataClient by lazy { Wearable.getDataClient(appContext) }

    override suspend fun doWork(): Result {
        // Get the DataItem URI and file name passed from the service
        val dataItemUriString = inputData.getString("data_item_uri") ?: return Result.failure()
        val destinationFileName = inputData.getString("file_name") ?: return Result.failure()

        Log.d("FileReceiverWorker", "Worker started for file: $destinationFileName")

        return try {
            // Fetch the full DataItem using the URI to get a valid Asset
            val dataItem = dataClient.getDataItem(Uri.parse(dataItemUriString)).await()
            if (dataItem == null) {
                Log.e("FileReceiverWorker", "DataItem not found for URI: $dataItemUriString")
                return Result.failure()
            }

            val dataMapItem = DataMapItem.fromDataItem(dataItem)
            val asset = dataMapItem.dataMap.getAsset("audio_file")

            if (asset == null) {
                Log.e("FileReceiverWorker", "Failed to retrieve asset from DataItem.")
                return Result.failure()
            }

            val destinationFile = File(applicationContext.filesDir, destinationFileName)
            val outputStream = FileOutputStream(destinationFile)
            val inputStream = dataClient.getFdForAsset(asset).await()?.inputStream

            if (inputStream == null) {
                Log.e("FileReceiverWorker", "Failed to get InputStream for asset.")
                return Result.failure()
            }

            Log.d("FileReceiverWorker", "Receiving file to: ${destinationFile.absolutePath}")
            inputStream.use { stream ->
                stream.copyTo(outputStream)
            }
            Log.d("FileReceiverWorker", "SUCCESS: File received and saved successfully!")
            Result.success()
        } catch (e: Exception) {
            Log.e("FileReceiverWorker", "Failed to receive file", e)
            Result.failure()
        }
    }
}
