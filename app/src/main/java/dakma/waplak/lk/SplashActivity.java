package dakma.waplak.lk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import dakma.waplak.lk.LoginActivity;
import dakma.waplak.lk.dakmapro.R;
import dakma.waplak.lk.utility.AndroidUtill;
import dakma.waplak.lk.utility.FileData;

/**
 * Created by admin on 5/24/2017.
 */

public class SplashActivity extends AppCompatActivity {
    private String isLogin="F";
    private String selectedCenter,selectedTestType,userType,userName,stdName;
    public static final String MyPREFERENCES = "MyPrefs";
    ProgressBar bar;
    TextView txt;
    int total = 0;
    private SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_splash);
        bar = (ProgressBar) findViewById(R.id.progressBar01);
        txt = (TextView) findViewById(R.id.txtrere);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final SpannableStringBuilder sb = new SpannableStringBuilder("Copyright © 2017 Dekma Institute. All Rights Reserved. | Powered by innosoft");
        TextView txtt = (TextView) findViewById(R.id.footerMark);
        // Span to set text color to some RGB value
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 255, 255));
        // Span to make text bold
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        // Set the text color for first 4 characters
        sb.setSpan(fcs, 0, 68, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        txtt.setText(sb);
        if(AndroidUtill.isNetworkConnected(SplashActivity.this)) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FileData newFile = AndroidUtill.deserializeObject();
                isLogin = newFile.getIsLogin();
                selectedCenter = newFile.getSelectedCenter();
                selectedTestType = newFile.getSelectedTestType();
                userType = newFile.getUserType();
                userName = newFile.getUserName();
                stdName = newFile.getStdName();
                if (isLogin != null && "T".equals(isLogin)) {
                    if (selectedCenter != null) {
                        NevigationActivity.ExamCenter = selectedCenter;
                    } else {
                        NevigationActivity.ExamCenter = "Matara";
                    }
                    if (selectedTestType != null) {
                        NevigationActivity.switchValue = selectedTestType;
                    } else {
                        NevigationActivity.switchValue = "ALL";
                    }
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("UserName", userName);
                    editor.putString("UserType", userType);
                    editor.putString("Name", stdName);
                    editor.commit();
                    bar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(SplashActivity.this, NevigationActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }else{
            Toast.makeText(
                    SplashActivity.this,
                    "No internet connection", Toast.LENGTH_LONG).show();
            finish();
        }
    }

}