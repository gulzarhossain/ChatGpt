package com.project.chatgpt.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    private static final  String USER_OTP="OTP";
    private static final  String Booking_Month="lastLogin";
    private static final  String USER_NAME="name";
    private static final  String USER_MOB="mob";
    private static final  String USER_PAN="PAN";
    private static final  String USER_ADHAAR="ADHAAR";
    private static final  String USER_STATE="STATE";
    private static final  String USER_CITY="CITY";
    private static final  String USER_PIN="PIN";
    private static final  String USER_DOB="DOB";
    private static final  String USER_Address="ADDRS";
    private static final  String USER_SCORE="SCORE";
    private static final  String USER_URL="URL";
    private static final  String USER_TYPE="TYPE";
    private static final  String USER_INCOME="INCOME";
    private static final  String USER_ACCOUNT="ACOUNT";
    private static final  String USER_IFSC="IFSC";
    private static final  String USER_LOANTYPE="LTYPE";
    private static final  String USER_LOANAMOUNT="LAMOUNT";
    private static final  String USER_TERM="TERM";
    private static final  String USER_ROI="ROI";
    private static final  String USER_TOTALAMOUNT="TAMNT";
    private static final  String USER_STATESHORT="STATESHORT";
    private static final  String USER_IMG="IMG";
    private static final  String USER_CODE="CODE";
    private static final  String USER_BRANCH="BRANCH";

    private static final String USER_LOGIN_STATUS="status";
    private static final String USER_LOGIN_TYPE="LOGTY";

    public static void PutBoolean(Context context, String key, boolean val){
        SharedPreferences preferences=context.getSharedPreferences("SharedPrefarence",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,val);
        editor.commit();
    }
    public static boolean GetBoolean(Context context,String key){
        SharedPreferences preferences=context.getSharedPreferences("SharedPrefarence",Context.MODE_PRIVATE);
        return preferences.getBoolean(key,false);
    }

    public static void PutInteger(Context context, String key, int val){
        SharedPreferences preferences=context.getSharedPreferences("SharedPrefarence",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,val);
        editor.commit();
    }
    public static int GetInteger(Context context, String key){
        SharedPreferences preferences=context.getSharedPreferences("SharedPrefarence",Context.MODE_PRIVATE);
        return preferences.getInt(key,0);
    }

    public static void setUserOTP(Context context,int otp){
        PutInteger(context,USER_OTP,otp);
    }

    public static Integer getUserOTP(Context context){
        return GetInteger(context,USER_OTP);
    }


    public static void setUserLoginStatus(Context context,boolean status){
        PutBoolean(context,USER_LOGIN_STATUS,status);
    }

    public static boolean getUserLoginStatus(Context context){
        return GetBoolean(context,USER_LOGIN_STATUS);
    }

    public static void PutString(Context context,String key,String val){
        SharedPreferences preferences=context.getSharedPreferences("SharedPrefarence",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,val);
        editor.commit();
    }

    public static String GetString(Context context,String key){
        SharedPreferences preferences=context.getSharedPreferences("SharedPrefarence",Context.MODE_PRIVATE);
        return preferences.getString(key,"00/00/0000 00:00:00");
    }
    public static void setUserImg(Context context,String name){
        PutString(context,USER_IMG,name);
    }

    public static String getUserImg(Context context){
        return GetString(context,USER_IMG);
    }

    public static void setUserName(Context context,String name){
        PutString(context,USER_NAME,name);
    }

    public static String getUserName(Context context){
        return GetString(context,USER_NAME);
    }

    public static void setUserCode(Context context,String name){
        PutString(context,USER_CODE,name);
    }

    public static String getUserCode(Context context){
        return GetString(context,USER_CODE);
    }

    public static void setUserBranch(Context context,String name){
        PutString(context,USER_BRANCH,name);
    }

    public static String getUserBranch(Context context){
        return GetString(context,USER_BRANCH);
    }

    public static void setUserMob(Context context,String name){
        PutString(context,USER_MOB,name);
    }

    public static String getUserMob(Context context){
        return GetString(context,USER_MOB);
    }

    public static void setUserPan(Context context,String name){
        PutString(context,USER_PAN,name);
    }

    public static String getUserPan(Context context){
        return GetString(context,USER_PAN);
    }

    public static void setUserAdhaar(Context context,String name){
        PutString(context,USER_ADHAAR,name);
    }

    public static String getUserAdhaar(Context context){
        return GetString(context,USER_ADHAAR);
    }

    public static void setUserState(Context context,String name){
        PutString(context,USER_STATE,name);
    }

    public static String getUserState(Context context){
        return GetString(context,USER_STATE);
    }

    public static void setUserStateshort(Context context,String name){
        PutString(context,USER_STATESHORT,name);
    }

    public static String getUserStateshort(Context context){
        return GetString(context,USER_STATESHORT);
    }

    public static void setUserCity(Context context,String name){
        PutString(context,USER_CITY,name);
    }

    public static String getUserCity(Context context){
        return GetString(context,USER_CITY);
    }

    public static void setUserPin(Context context,String name){
        PutString(context,USER_PIN,name);
    }

    public static String getUserPin(Context context){
        return GetString(context,USER_PIN);
    }

    public static void setUserDob(Context context,String name){
        PutString(context,USER_DOB,name);
    }

    public static String getUserDob(Context context){
        return GetString(context,USER_DOB);
    }

    public static void setUSER_Address(Context context,String name){
        PutString(context,USER_Address,name);
    }

    public static String getUSER_Address(Context context){
        return GetString(context,USER_Address);
    }

    public static void setUserScore(Context context,String name){
        PutString(context,USER_SCORE,name);
    }

    public static String getUserScore(Context context){
        return GetString(context,USER_SCORE);
    }


    public static void setUserUrl(Context context,String name){
        PutString(context,USER_URL,name);
    }

    public static String getUserUrl(Context context){
        return GetString(context,USER_URL);
    }

    public static void setUserType(Context context,String name){
        PutString(context,USER_TYPE,name);
    }

    public static String getUserType(Context context){
        return GetString(context,USER_TYPE);
    }

    public static void setUserIncome(Context context,String name){
        PutString(context,USER_INCOME,name);
    }

    public static String getUserIncome(Context context){
        return GetString(context,USER_INCOME);
    }

    public static void setUserAccount(Context context,String name){
        PutString(context,USER_ACCOUNT,name);
    }

    public static String getUserAccount(Context context){
        return GetString(context,USER_ACCOUNT);
    }

    public static void setUserLoginType(Context context,String name){
        PutString(context,USER_LOGIN_TYPE,name);
    }

    public static String getUserLoginType(Context context){
        return GetString(context,USER_LOGIN_TYPE);
    }


    public static void setLastLoginDate(Context context,String LlginDt){
        PutString(context,Booking_Month,LlginDt);
    }

    public static String getLastLoginDate(Context context){
        return GetString(context,Booking_Month);
    }
}
