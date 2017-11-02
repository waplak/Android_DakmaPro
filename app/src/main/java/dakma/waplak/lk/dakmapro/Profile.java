package dakma.waplak.lk.dakmapro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import dakma.waplak.lk.NevigationActivity;

/**
 * Created by admin on 5/17/2017.
 */

public class Profile extends Fragment {
    private TextView stdId,name,home,street,city,gender,bday,nic,tel_Land,mobile,stream,
            ex_center,t_r,alYear,school,gudianName,guardian_Occupation,guardian_TP;
    public static final String MyPREFERENCES = "MyPrefs";
    private SharedPreferences sharedpreferences;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = null;
        rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        stdId=(TextView)rootView.findViewById(R.id.stdId);
        stdId.setText(sharedpreferences.getString("Student_ID",null));
        name=(TextView)rootView.findViewById(R.id.name);
        name.setText(sharedpreferences.getString("Name",null));
        home=(TextView)rootView.findViewById(R.id.home);
        home.setText(sharedpreferences.getString("House",null));
        street=(TextView)rootView.findViewById(R.id.street);
        street.setText(sharedpreferences.getString("street",null));
        city=(TextView)rootView.findViewById(R.id.city);
        city.setText(sharedpreferences.getString("City",null));
        gender=(TextView)rootView.findViewById(R.id.gender);
        gender.setText(sharedpreferences.getString("Gender",null));
        //bday=(TextView)rootView.findViewById(R.id.bday);
        //bday.setText(sharedpreferences.getString("Birth_day",null));
        //nic=(TextView)rootView.findViewById(R.id.nic);
        //nic.setText(sharedpreferences.getString("NIC",null));
        ex_center=(TextView)rootView.findViewById(R.id.ex_center);
        ex_center.setText(sharedpreferences.getString("Ex_Center",null));
        //t_r=(TextView)rootView.findViewById(R.id.t_r);
        //t_r.setText(sharedpreferences.getString("T_R",null));
        alYear=(TextView)rootView.findViewById(R.id.alYear);
        alYear.setText(sharedpreferences.getString("AL_Year",null));
        school=(TextView)rootView.findViewById(R.id.school);
        school.setText(sharedpreferences.getString("School_Name",null));
        gudianName=(TextView)rootView.findViewById(R.id.gudianName);
        gudianName.setText(sharedpreferences.getString("Guardian_Name",null));
        //guardian_Occupation=(TextView)rootView.findViewById(R.id.guardian_Occupation);
        //guardian_Occupation.setText(sharedpreferences.getString("Guardian_Occupation",null));
        guardian_TP=(TextView)rootView.findViewById(R.id.guardian_TP);
        guardian_TP.setText(sharedpreferences.getString("Guardian_TP",null));
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Profile Details");
    }



}
