package dakma.waplak.lk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.utils.ColorTemplate;

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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import dakma.waplak.lk.dakmapro.GeneralAnalysis;
import dakma.waplak.lk.dakmapro.IndividualAnalysis;
import dakma.waplak.lk.dakmapro.Performance;
import dakma.waplak.lk.dakmapro.Profile;
import dakma.waplak.lk.dakmapro.R;
import dakma.waplak.lk.dakmapro.home.HorizontalPagerFragment;
import dakma.waplak.lk.dakmapro.Setting;
import dakma.waplak.lk.utility.AndroidUtill;
import dakma.waplak.lk.utility.DownloadedDataCenter;

public class NevigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MyPREFERENCES = "MyPrefs";
    private String userName,userType,name,selectedYear;
    public static String switchValue="ALL";
    public static String ExamCenter="Matara";
    public static String ALYear="";
    private SharedPreferences sharedpreferences;
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //DownloadedDataCenter.getInstance(NevigationActivity.this).setSelectedTestTests(DownloadedDataCenter.getInstance(NevigationActivity.this).getLoadAllTests());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userName=sharedpreferences.getString("UserName",null).trim();
        name=sharedpreferences.getString("Name",null);
        userType=sharedpreferences.getString("UserType",null);
        selectedYear=sharedpreferences.getString("SelectedYear",null);
        if(selectedYear!=null && !selectedYear.equals("")) {
            ALYear=selectedYear;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.nav_home);
        if(AndroidUtill.isNetworkConnected(NevigationActivity.this)) {
            new SendfeedbackJob().execute(AndroidUtill.LoadAllTests,"1");
        }else{
            Toast.makeText(
                    NevigationActivity.this,
                    "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        TextView txtName=(TextView)findViewById(R.id.name);
        txtName.setText(name);
        TextView txtCode=(TextView)findViewById(R.id.code);
        txtCode.setText(userName);
       // getMenuInflater().inflate(R.menu.nevigation, menu);
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }
    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                fragment = new HorizontalPagerFragment();
                break;
            case R.id.nav_individ:
                fragment = new IndividualAnalysis();
                break;
            case R.id.nav_general:
                fragment = new GeneralAnalysis();
                break;
            case R.id.nav_performanse:
                fragment = new Performance();
                break;
            case R.id.nav_prof:
                fragment = new Profile();
                break;
            case R.id.nav_setting:
                fragment = new Setting();
                break;
            case R.id.nav_logout:
                AlertDialog.Builder creditConf = new AlertDialog.Builder(NevigationActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                creditConf.setMessage("Do you want to logout? ");
                creditConf.setCancelable(false);
                creditConf.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.dismiss();
                                AndroidUtill.serializeObject(null,"F",null,null,null,null);
                                File file = new File(AndroidUtill.FILE_PATH);
                                file.delete(); // Remove File Path
                                Intent intent = new Intent(NevigationActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }

                        });
                creditConf.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                creditConf.setTitle("Logout..");
                creditConf.show();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    private class SendfeedbackJob extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(
                NevigationActivity.this, ProgressDialog.THEME_HOLO_DARK);
        String msg="Loading....";
        private String cycleNo="0";
        @Override
        protected void onPreExecute() {
            try {

//                SpannableString ss2=  new SpannableString(msg);
//                ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
//                ss2.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, ss2.length(), 0);
                this.dialog.setMessage(msg);
                this.dialog.setCanceledOnTouchOutside(false);
                this.dialog.show();
            } catch (Exception e) {
                Toast.makeText(
                        NevigationActivity.this,
                        "There are Some error in login page : "
                                + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String[] params) {
            HttpResponse response = null;
            String result = null;
            try {
                HttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = 60000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                int timeoutSocket = 120000; // in milliseconds which is the timeout
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpGet request = new HttpGet();
                //String url = ;
                request.setURI(new URI(params[0].replace(" ", "%20")));
                cycleNo=params[1];
                response = httpclient.execute(request);
                if (response != null && !response.equals("")) {

                    HttpEntity entity = response.getEntity();
                    if (entity != null) {

                        // A Simple JSON Response Read
                        InputStream instream = entity.getContent();
                        result = convertStreamToString(instream);
                        // now you have the string representation of the HTML request
                        System.out.println("RESPONSE: " + result);
                        instream.close();


                    }
                    org.apache.http.Header[] headers = response.getAllHeaders();
                    for (int i = 0; i < headers.length; i++) {
                        System.out.println(headers[i]);
                    }
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("log_tag", "Error in http connection " + e.toString());
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
            }
            return result;

        }
        @Override
        protected void onPostExecute(String message) {
            try {
                //JSONObject myObject = new JSONObject(message);
                //JSONArray arr=myObject.getJSONArray();
                if(message!=null) {
                    JSONArray jsonarray = new JSONArray(message);
                    if("1".equals(cycleNo)) {
                        DownloadedDataCenter.getInstance(NevigationActivity.this).getLoadAllTests().clear();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            DownloadedDataCenter.getInstance(NevigationActivity.this).setLoadAllTests(jsonobject.getString("TestID"));
                         }
                        new SendfeedbackJob().execute(AndroidUtill.LoadTheoryTests,"2");
                    }else if("2".equals(cycleNo)){
                        DownloadedDataCenter.getInstance(NevigationActivity.this).getLoadTheoryTests().clear();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            DownloadedDataCenter.getInstance(NevigationActivity.this).setLoadTheoryTests(jsonobject.getString("Test_ID"));
                        }
                        new SendfeedbackJob().execute(AndroidUtill.LoadRevisionTests,"3");
                    }else if("3".equals(cycleNo)){
                        DownloadedDataCenter.getInstance(NevigationActivity.this).getLoadRevisionTests().clear();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            DownloadedDataCenter.getInstance(NevigationActivity.this).setLoadRevisionTests(jsonobject.getString("Test_ID"));
                        }
                        new SendfeedbackJob().execute(AndroidUtill.LoadModelPaperTests,"4");
                    }else if("4".equals(cycleNo)){
                        DownloadedDataCenter.getInstance(NevigationActivity.this).getLoadModelPaperTests().clear();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            DownloadedDataCenter.getInstance(NevigationActivity.this).setLoadModelPaperTests(jsonobject.getString("Test_ID"));
                        }
                        new SendfeedbackJob().execute(AndroidUtill.LoadAllALYears,"5");
                    }else if("5".equals(cycleNo)){
                        DownloadedDataCenter.getInstance(NevigationActivity.this).getLoadAllALYears().clear();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            DownloadedDataCenter.getInstance(NevigationActivity.this).setLoadAllALYears(jsonobject.getString("AL_Year"));
                        }
                        new SendfeedbackJob().execute(AndroidUtill.LoadSchools,"6");
                    }else if("6".equals(cycleNo)){
                        DownloadedDataCenter.getInstance(NevigationActivity.this).getLoadSchools().clear();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            DownloadedDataCenter.getInstance(NevigationActivity.this).setLoadSchools(jsonobject.getString("Name").trim()+","+jsonobject.getString("District").trim());
                        }
                        new SendfeedbackJob().execute("http://api.dekma.edu.lk/api/Student/LoadStudentByStudentId?StudentId="+userName,"7");

                    }else if("7".equals(cycleNo)){
                        if (jsonarray != null && jsonarray.length() > 0) {


                            JSONObject jsonobject = jsonarray.getJSONObject(0);

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("Student_ID",jsonobject.getString("Student_ID").trim());
                            editor.putString("Name",jsonobject.getString("Name").trim());
                            editor.putString("House",jsonobject.getString("House").trim());
                            editor.putString("street",jsonobject.getString("street").trim());
                            editor.putString("City",jsonobject.getString("City").trim());
                            editor.putString("Gender",jsonobject.getString("Gender").trim());
                            editor.putString("Birth_day",jsonobject.getString("Birth_day").trim());
                            editor.putString("NIC",jsonobject.getString("NIC").trim());
                            editor.putString("Stream",jsonobject.getString("Stream").trim());
                            editor.putString("Ex_Center",jsonobject.getString("Ex_Center").trim());
                            editor.putString("T_R",jsonobject.getString("T_R").trim());
                            editor.putString("AL_Year",jsonobject.getString("AL_Year").trim());
                            editor.putString("School_Name",jsonobject.getString("School_Name").trim());
                            editor.putString("Guardian_Name",jsonobject.getString("Guardian_Name").trim());
                            editor.putString("Guardian_Occupation",jsonobject.getString("Guardian_Occupation").trim());
                            editor.putString("Guardian_TP",jsonobject.getString("Guardian_TP").trim());
                            editor.commit();



                        } else {
                            Toast.makeText(
                                    NevigationActivity.this,
                                    "Data Not found ", Toast.LENGTH_LONG).show();
                        }
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.openDrawer(Gravity.LEFT);
                    }
                }
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
            } catch (JSONException e) {
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
                e.printStackTrace();
            }
        }
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
