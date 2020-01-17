package ua.makovskyi.androidnotification

import android.app.Application
import android.util.Log

import com.google.firebase.iid.FirebaseInstanceId

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->  
                if (task.isSuccessful) {
                    task.result?.let { result ->
                        Log.v("Firebase device token", result.token)
                    }
                }
            }
    }
}