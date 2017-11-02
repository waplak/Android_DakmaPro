package dakma.waplak.lk.dakmapro.indi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import dakma.waplak.lk.NevigationActivity;
import dakma.waplak.lk.dakmapro.R;
import dakma.waplak.lk.utility.AndroidUtill;
import dakma.waplak.lk.utility.AutocompleteAdapter;
import dakma.waplak.lk.utility.DownloadedDataCenter;
import dakma.waplak.lk.utility.StudentNameCode;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by chamith_d on 5/17/2017.
 */

public class GradePercentagesFragment extends Fragment{
    private PieChart mChart;
    public static final String MyPREFERENCES = "MyPrefs";
    private String userName,fromTestID,toTestID,stdName,userType;
    private Spinner spinnerFrom,spinnerTo;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.grade_percentages_fragment, container, false);
        SharedPreferences prefs = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        userName=prefs.getString("UserName",null).toString().trim();
        userType=prefs.getString("UserType",null).toString().trim();
        spinnerFrom = (Spinner) rootView.findViewById(R.id.spinnerFrom);
        spinnerTo = (Spinner) rootView.findViewById(R.id.spinnerTo);
        mChart = (PieChart) rootView.findViewById(R.id.chart);
        Button btView = (Button) rootView.findViewById(R.id.btView);
        final AutoCompleteTextView nameSearch = (AutoCompleteTextView)rootView.findViewById(R.id.autoName);
        TextView nameView=(TextView) rootView.findViewById(R.id.txtName);
        nameSearch.setThreshold(1);
        if("Student".equals(userType)){
            nameSearch.setVisibility(View.GONE);
            nameView.setVisibility(View.GONE);
        }
        final AutocompleteAdapter adapter = new AutocompleteAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line);
        nameSearch.setAdapter(adapter);
        nameSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String countryName = adapter.getItem(position).getStdName();

                String [] strStdDetals =countryName.split("\\|");
                if(strStdDetals.length > 0) {
                    userName = strStdDetals[0].trim();
                    stdName = strStdDetals[1].trim();
                    nameSearch.setText(stdName);
                }
            }
        });
        loadSpinners();

        btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AndroidUtill.isNetworkConnected(getActivity())) {
                    new SendfeedbackJob().execute();
                }else{
                    Toast.makeText(
                            getActivity(),
                            "No internet connection", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        return rootView;
    }

    public void loadSpinners(){

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, DownloadedDataCenter.getInstance(getActivity()).getSelectedTestsTestFromYear());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(dataAdapter);
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromTestID = (String) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTo.setAdapter(dataAdapter);
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toTestID = (String) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private class SendfeedbackJob extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(
                getActivity(),ProgressDialog.THEME_HOLO_DARK);
        @Override
        protected void onPreExecute() {
            try {
                this.dialog.setMessage("Loading Chart....");
                this.dialog.setCanceledOnTouchOutside(false);
                this.dialog.show();
            } catch (Exception e) {
                Toast.makeText(
                        getActivity(),
                        "There are Some error in Loading Chart : "
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
                HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);
                int timeoutSocket = 120000; // in milliseconds which is the timeout
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpGet request = new HttpGet();
                String url = "http://api.dekma.edu.lk/api/Dashboard/LoadIndividualPieChartDetails?StudentId="+userName+"&FromTestId="+fromTestID+"&ToTestId="+toTestID;
                request.setURI(new URI(url.replace(" ", "%20")));
                response = httpclient.execute(request);
                if(response!= null && !response.equals("")) {

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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("log_tag", "Error in http connection " + e.toString());
            }
            return result;

        }

        @Override
        protected void onPostExecute(String message) {
            try {
                if(message!=null) {
                    JSONArray jsonarray = new JSONArray(message);
                    ArrayList<Entry> entries = new ArrayList<>();
                    ArrayList<String> PieEntryLabels = new ArrayList<String>();
                    PieDataSet pieDataSet ;
                    PieData pieData ;
                    if (jsonarray != null && jsonarray.length() > 0) {
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            float marks = Float.parseFloat(jsonobject.getString("Value"));
                            if(marks>0) {
                                entries.add(new BarEntry(marks, i + 1));
                                PieEntryLabels.add(jsonobject.getString("Key"));
                            }

                        }
                        pieDataSet = new PieDataSet(entries,"Grade Percentages :"+stdName==null ? userName :stdName);
                        pieData = new PieData(PieEntryLabels, pieDataSet);
                        pieData.setValueTextColor(Color.WHITE);
                        pieData.setValueTextSize(20);
                        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                        mChart.setData(pieData);
                        // enable hole and configure
                        mChart.setDrawHoleEnabled(true);
                        mChart.setRotationEnabled(false);
                        mChart.setHoleColorTransparent(true);
                        mChart.setDescription("Grade Percentages :"+stdName==null ? userName :stdName);
                        mChart.setHoleRadius(0);
                        mChart.setTransparentCircleRadius(0);

                        mChart.animateY(3000);
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "Data Not found ", Toast.LENGTH_LONG).show();
                        if (this.dialog.isShowing()) {
                            this.dialog.dismiss();
                        }
                    }
                    if (this.dialog.isShowing()) {
                        this.dialog.dismiss();
                    }
                }else {
                    if (this.dialog.isShowing()) {
                        this.dialog.dismiss();
                    }
                    Toast.makeText(
                            getActivity(),
                            "Data Not found : ", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
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