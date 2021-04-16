package com.ktk.ktkwidget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException


// var data will hold the information received from the HTTP Request
var data = Data()

private const val TAG = "Widget"

class PriceWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

}

@SuppressLint("ResourceAsColor")
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

    // create remote view
    val views = RemoteViews(context.packageName, R.layout.price_widget)

    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val currency = prefs.getString("currency", "0")
    val switch = prefs.getBoolean("switch", false)



    if (!switch) {
        views.setTextColor(R.id.widget_text_price,ContextCompat.getColor(context, R.color.black))
        views.setTextColor(R.id.widget_text_price2,ContextCompat.getColor(context, R.color.black))
        views.setTextColor(R.id.widget_text_price3,ContextCompat.getColor(context, R.color.black))
        views.setTextColor(R.id.widget_text_price4,ContextCompat.getColor(context, R.color.black))
        views.setTextColor(R.id.kab1,ContextCompat.getColor(context, R.color.gray))
        views.setTextColor(R.id.kab2,ContextCompat.getColor(context, R.color.gray))
        views.setTextColor(R.id.kab3,ContextCompat.getColor(context, R.color.gray))
        views.setTextColor(R.id.kab4,ContextCompat.getColor(context, R.color.gray))
        views.setTextColor(R.id.time1,ContextCompat.getColor(context, R.color.gray))
        views.setTextColor(R.id.time2,ContextCompat.getColor(context, R.color.gray))
        views.setTextColor(R.id.time3,ContextCompat.getColor(context, R.color.gray))
        views.setTextColor(R.id.time4,ContextCompat.getColor(context, R.color.gray))
    } else {
        views.setTextColor(R.id.widget_text_price,ContextCompat.getColor(context, R.color.white))
        views.setTextColor(R.id.widget_text_price2,ContextCompat.getColor(context, R.color.white))
        views.setTextColor(R.id.widget_text_price3,ContextCompat.getColor(context, R.color.white))
        views.setTextColor(R.id.widget_text_price4,ContextCompat.getColor(context, R.color.white))
        views.setTextColor(R.id.kab1,ContextCompat.getColor(context, R.color.white))
        views.setTextColor(R.id.kab2,ContextCompat.getColor(context, R.color.white))
        views.setTextColor(R.id.kab3,ContextCompat.getColor(context, R.color.white))
        views.setTextColor(R.id.kab4,ContextCompat.getColor(context, R.color.white))
        views.setTextColor(R.id.time1,ContextCompat.getColor(context, R.color.white))
        views.setTextColor(R.id.time2,ContextCompat.getColor(context, R.color.white))
        views.setTextColor(R.id.time3,ContextCompat.getColor(context, R.color.white))
        views.setTextColor(R.id.time4,ContextCompat.getColor(context, R.color.white))
    }

    // set textview's text to loading string
    views.setTextViewText(R.id.widget_text_price, "Загрузка...")
    views.setTextViewText(R.id.widget_text_price2, "Загрузка...")
    views.setTextViewText(R.id.widget_text_price3, "Загрузка...")
    views.setTextViewText(R.id.widget_text_price4, "Загрузка...")
    views.setTextViewText(R.id.widget_status, "Загрузка...")
    views.setTextViewText(R.id.kab1, "Каб.")
    views.setTextViewText(R.id.kab2, "Каб.")
    views.setTextViewText(R.id.kab3, "Каб.")
    views.setTextViewText(R.id.kab4, "Каб.")
    views.setTextViewText(R.id.time1, "Время")
    views.setTextViewText(R.id.time2, "Время")
    views.setTextViewText(R.id.time3, "Время")
    views.setTextViewText(R.id.time4, "Время")

    // line 52 does not work
    // views.setTextColor(R.id.widget_day_change, R.attr.appWidgetTextColor)

    // first update call to set loading text
    appWidgetManager.updateAppWidget(appWidgetId, views) // continues after this

    // refresh button
    val intentUpdate = Intent(context, PriceWidget::class.java)
    intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

    val idArray = intArrayOf(appWidgetId)
    intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray)

    val pendingUpdate = PendingIntent.getBroadcast(
        context,
        appWidgetId,
        intentUpdate,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    Log.i(TAG, "Refresh button pressed.")
    views.setOnClickPendingIntent(R.id.widget_refresh_button, pendingUpdate)


    // function call to fetch data from HTTP GET request
    fetchData(appWidgetManager, appWidgetId, views, context, currency)
}



fun fetchData(
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    views: RemoteViews,
    context: Context,
    currency: String?
) {

    // url to send GET request
    val url = "https://onlypost.tk/rasp.php?id=$currency"

    // OkHttp

    val request = Request.Builder().url(url).build()

    val client = OkHttpClient()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            // successful GET request
            Log.i(TAG, "GET request successful.")

            // converts response into string
            val body = response.body?.string()

            // extracts object from JSON
            val tempList: Array<Data> = Gson().fromJson(body, Array<Data>::class.java)
            data = tempList[0]

            if (data.raspesanie1().contains("=")) {
                views.setViewVisibility(R.id.one, -5)
            } else {
                views.setViewVisibility(R.id.one, 1)
                views.setTextViewText(R.id.widget_text_price, data.raspesanie1())
                views.setTextViewText(R.id.time1, data.time1())
                views.setTextViewText(R.id.kab1, data.kab())
            }
            if (data.raspesanie2().contains("=")) {
                views.setViewVisibility(R.id.twoe, -5)
            } else {
                views.setViewVisibility(R.id.twoe, 1)
                views.setTextViewText(R.id.widget_text_price2, data.raspesanie2())
                views.setTextViewText(R.id.time2, data.time2())
                views.setTextViewText(R.id.kab2, data.kab2())
            }
            if (data.raspesanie3().contains("=")) {
                views.setViewVisibility(R.id.free, -5)
            } else {
                views.setViewVisibility(R.id.free, 1)
                views.setTextViewText(R.id.widget_text_price3, data.raspesanie3())
                views.setTextViewText(R.id.time3, data.time3())
                views.setTextViewText(R.id.kab3, data.kab3())
            }
            if (data.raspesanie4().contains("=")) {
                views.setViewVisibility(R.id.ezz, -5)
            } else {
                views.setViewVisibility(R.id.ezz, 1)
                views.setTextViewText(R.id.widget_text_price4, data.raspesanie4())
                views.setTextViewText(R.id.time4, data.time4())
                views.setTextViewText(R.id.kab4, data.kab4())
            }




            //ЗАМЕНА
            if (data.raspesanie1().contains("замена")) {
                // green color
                views.setTextColor(
                    R.id.widget_text_price,
                    ContextCompat.getColor(context, R.color.positive_green)
                )
            }
            if (data.raspesanie2().contains("замена")) {
                // green color
                views.setTextColor(
                    R.id.widget_text_price2,
                    ContextCompat.getColor(context, R.color.positive_green)
                )
            }
            if (data.raspesanie3().contains("замена")) {
                // green color
                views.setTextColor(
                    R.id.widget_text_price3,
                    ContextCompat.getColor(context, R.color.positive_green)
                )
            }
            if (data.raspesanie4().contains("замена")) {
                // green color
                views.setTextColor(
                    R.id.widget_text_price4,
                    ContextCompat.getColor(context, R.color.positive_green)
                )
            }

            // post execute here
            // update widget with new data

            views.setTextViewText(R.id.widget_status, data.status())

            if (data.status().contains("Работает")) {
                // green color
                views.setTextColor(
                    R.id.widget_status,
                    ContextCompat.getColor(context, R.color.positive_green)
                )
            } else {
                // red color
                views.setTextColor(
                    R.id.widget_status,
                    ContextCompat.getColor(context, R.color.negative_red)
                )
            }

            // makes final call to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)

        }

        override fun onFailure(call: Call, e: IOException) {
            // failed GET request
            Log.i(TAG, "Failed to execute GET request.")
        }
    })
}