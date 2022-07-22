package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI

class BlurWorker(ctx : Context, params: WorkerParameters): Worker(ctx, params) {
    override fun doWork(): Result {
        val appContext = applicationContext

        val inputDataUri = inputData.getString(KEY_IMAGE_URI)

        sleep()

        makeStatusNotification("Blurring Image", appContext)
        return try {

            if(TextUtils.isEmpty(inputDataUri)){
                Log.e("errorUriResource", "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val picture = BitmapFactory.decodeStream(
                appContext.contentResolver.openInputStream(Uri.parse(inputDataUri))
            )

            val output = blurBitmap(picture, appContext)

            val outputUri = writeBitmapToFile(appContext, output)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            Result.success(outputData)
        } catch (throwable: Throwable){

            Log.e("errorBlurUri", "Error applying blur")
            throwable.printStackTrace()
            Result.failure()

        }
    }
}