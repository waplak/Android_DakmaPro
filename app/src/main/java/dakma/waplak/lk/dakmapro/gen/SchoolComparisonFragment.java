package dakma.waplak.lk.dakmapro.gen;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
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
import dakma.waplak.lk.utility.DownloadedDataCenter;

/**
 * Created by admin on 5/21/2017.
 */

public class SchoolComparisonFragment extends Fragment{
    private HorizontalBarChart barChartChart;
    private Spinner spTestId,spMarks;
    private List<String> marksRange;
    private String testID,spMarksAbove;
    private TextView selectedCenter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.school_comparison_fragment, container, false);
        // spCenter= (Spinner) rootView.findViewById(R.id.spCenter);
        spTestId = (Spinner) rootView.findViewById(R.id.spTestID);
        spMarks = (Spinner) rootView.findViewById(R.id.spMarks);
        Button btView = (Button) rootView.findViewById(R.id.btView);
        barChartChart = (HorizontalBarChart) rootView.findViewById(R.id.chart);
        selectedCenter=(TextView)rootView.findViewById(R.id.selectedCenter);
        selectedCenter.setText(NevigationActivity.ExamCenter);
        marksRange = new ArrayList<String>();
        marksRange.clear();
        marksRange.add("00");
        marksRange.add("10");
        marksRange.add("20");
        marksRange.add("30");
        marksRange.add("40");
        marksRange.add("50");
        marksRange.add("60");
        marksRange.add("70");
        marksRange.add("80");
        marksRange.add("90");

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
        ArrayAdapter<String> dataAdapterTest = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,  DownloadedDataCenter.getInstance(getActivity()).getSelectedTestsTestFromYear());
        dataAdapterTest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> dataAdapterMarks = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, marksRange);
        dataAdapterMarks.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner

        spTestId.setAdapter(dataAdapterTest);
        spTestId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                testID = (String) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spMarks.setAdapter(dataAdapterMarks);
        spMarks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spMarksAbove = (String) parent.getItemAtPosition(position);

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
                String url = "http://api.dekma.edu.lk/api/Dashboard/LoadSchoolComparisonPieChartDetails?Centre="+ NevigationActivity.ExamCenter+"&TestId="+testID+"&MarksAbove="+spMarksAbove;
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
                    ArrayList<BarDataSet> dataSets = null;
                    ArrayList<BarEntry> valueSet1 = new ArrayList<>();
                    ArrayList<String> xAxis = new ArrayList<>();
                    if (jsonarray != null && jsonarray.length() > 0) {
                        int fullCount=0;
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            fullCount=fullCount+Integer.parseInt(jsonobject.getString("Count"));
                        }
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            float marks = Float.parseFloat(jsonobject.getString("Count"));
                            float pieValue=((float)marks/(float)fullCount)*100;
                            BarEntry v1e1 = new BarEntry(pieValue, i );
                            valueSet1.add(v1e1);
                            xAxis.add(jsonobject.getString("Key"));

                        }
                        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Student Percentages");
                        barDataSet1.setColor(Color.rgb( 29, 149, 213));
                        dataSets = new ArrayList<>();
                        dataSets.add(barDataSet1);

                        XAxis xAxis1=barChartChart.getXAxis();
                        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis1.setDrawAxisLine(true);
                        xAxis1.setDrawGridLines(false);

                        BarData data = new BarData(xAxis, dataSets);
                        barChartChart.setData(data);
                        barChartChart.setDrawBarShadow(false);
                        barChartChart.setDrawValueAboveBar(true);
                        barChartChart.setDrawGridBackground(false);
                        barChartChart.setDescription("");
                        barChartChart.animateX(2500);
                        YAxis yAxis = barChartChart.getAxisLeft();
                        yAxis.setDrawGridLines(false);

                        yAxis = barChartChart.getAxisRight();
                        yAxis.setDrawGridLines(false);

                        barChartChart.invalidate();
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
