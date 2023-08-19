package com.project.chatgpt.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AppUtility {
    public static final int APP_VERSION=1;
    public static final int getScreenResolusion(Activity activity,String choice){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height;
        if (choice.toLowerCase().contentEquals("h")){
            height = size.y;
        }else {
            height = size.x;
        }
        return height;
    }
    public static String changeDateFormat(String inputFormat, String outputFormat, String inputDate) {

        try {
            DateFormat iFormat, oFormat = null;
            Date date = null;
            try {
                iFormat = new SimpleDateFormat(inputFormat);
                oFormat = new SimpleDateFormat(outputFormat);
                date = iFormat.parse(inputDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return oFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static final Bitmap ConvertPic(String picPath) {
        byte[] pics = Base64.decode(picPath, Base64.DEFAULT);
        Bitmap memPic = BitmapFactory.decodeByteArray(pics, 0, pics.length);
        return memPic;
    }
    public static int getAge(Date dateOfBirth) {
        int age = 0;
        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        if(dateOfBirth!= null) {
            now.setTime(new Date());
            born.setTime(dateOfBirth);
            if(born.after(now)) {
                throw new IllegalArgumentException("Can't be born in the future");
            }
            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
            if(now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR))  {
                age-=1;
            }
        }
        return age;
    }

    public static String convertTime(String milis){
        long mil=Long.parseLong(milis);
        Date d=new Date(mil);
        DateFormat f=new SimpleDateFormat("hh:mm a");
        return f.format(d);
    }

    public static String convertDate(String milis){
        long mil=Long.parseLong(milis);
        Date d=new Date(mil);
        DateFormat f=new SimpleDateFormat("dd/MM/yyyy");
        return f.format(d);
    }

    public static String setDate(String milis){
        DateFormat f=new SimpleDateFormat("dd/MM/yyyy");
        long mil=Long.parseLong(milis);
        Date d= new Date(mil);
        Date cDate=new Date();
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,-1);
        if (f.format(d).equals(f.format(cDate))){
            return "Today";
        }else if (f.format(d).equals(f.format(cal.getTime()))){
            return "Yesterday";
        }else return f.format(d);
    }

    public static void SnacView(String msg, View main_layout){
        Snackbar mSnackbar = Snackbar.make(main_layout, msg, Snackbar.LENGTH_LONG);
        View mView = mSnackbar.getView();
        TextView mTextView = (TextView) mView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mSnackbar.show();
    }

}
