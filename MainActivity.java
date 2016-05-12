package com.example.yooyj.hw3;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;
/*
* This program is role to send a message.
* Matching each situation, give a toast message.
 * ex) if success -> 전송완료
 *     if fail -> 전송실패..
* */
public class MainActivity extends Activity {
    Context mContext;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        webView=(WebView)findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavaScriptInterface(), "Android");

        webView.loadUrl("file:///android_asset/Hw3.html");
    }
/*
* This class makes html file interact with android app.
* Using @JavascriptInterface method,
* Html's information can be got and app's information can be sent
* */
    public class JavaScriptInterface {
        String number = "";

    /*
    * Get html button's information, send processing data to web page.
    * */
        @JavascriptInterface
        public String makeNumber(String tmp, String str) {
            number = tmp + str;
            return number;
        }
    /*
    * Getting html's message and phone number, android can send a message.
    * But if phone number's length is 0 and message's length is 0,
    * give toast message.
    * */
        @JavascriptInterface
        public void sendSMS(String smsNumber,String smsText){

            if (smsNumber.length()>0 && smsText.length()>0){
                sendMessage(smsNumber, smsText);
            }else{
                Toast.makeText(getApplicationContext(), "모두 입력해 주세요", Toast.LENGTH_SHORT).show();
            }
        }
/*
* This method can make it send a message.
* Matching each situation, give a toast message.
* */
        public void sendMessage(String smsNumber, String smsText){
            PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT_ACTION"), 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED_ACTION"), 0);

            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch(getResultCode()){
                        case Activity.RESULT_OK:
                            // 전송 성공
                            Toast.makeText(getApplicationContext(), "전송 완료", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            // 전송 실패
                            Toast.makeText(getApplicationContext(), "전송 실패", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            // 서비스 지역 아님
                            Toast.makeText(getApplicationContext(), "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            // 무선 꺼짐
                            Toast.makeText(getApplicationContext(), "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            // PDU 실패
                            Toast.makeText(getApplicationContext(), "PDU Null", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter("SMS_SENT_ACTION"));

            /**
             * SMS가 도착했을때 실행
             * When the SMS massage has been delivered
             */
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()){
                        case Activity.RESULT_OK:
                            // 도착 완료
                            Toast.makeText(mContext, "SMS 도착 완료", Toast.LENGTH_SHORT).show();
                            break;
                        case Activity.RESULT_CANCELED:
                            // 도착 안됨
                            Toast.makeText(mContext, "SMS 도착 실패", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter("SMS_DELIVERED_ACTION"));

            SmsManager mSmsManager = SmsManager.getDefault();
            mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);
        }
    }
}
