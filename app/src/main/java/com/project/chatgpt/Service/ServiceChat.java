package com.project.chatgpt.Service;

import android.app.Service;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.project.chatgpt.R;
import com.project.chatgpt.Utils.Notification;

public class ServiceChat extends Service {
    int i=0;
    FirebaseDatabase firebase= FirebaseDatabase.getInstance();
    DatabaseReference reference= firebase.getReferenceFromUrl("https://chatgpt-5941d-default-rtdb.firebaseio.com/");
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CountDownTimer countDownTimer=new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long l) {
                Log.e("siut",l+"");
            }

            @Override
            public void onFinish() {

            }
        }.start();
        return START_STICKY;
    }
}
