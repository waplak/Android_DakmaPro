package dakma.waplak.lk.dakmapro;

/**
 * Created by admin on 5/17/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;
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

import dakma.waplak.lk.NevigationActivity;
import dakma.waplak.lk.utility.AndroidUtill;
import dakma.waplak.lk.utility.DownloadedDataCenter;
import dakma.waplak.lk.utility.PerformDetails;

public class Performance extends Fragment{
    private Spinner spTestId;
    private String testID;
    private TextView selectedCenter;
    private TableLayout markTable;
    public static final String MyPREFERENCES = "MyPrefs";
    private SharedPreferences sharedpreferences;
    private String name,studId;
    private int maxRow = 50;
    private int startValue = 0;
    private boolean blockNext = false;
    private boolean blockBack = false;
    private ScrollView scrl;
    private boolean isLastSet=false;
    private EditText search;
    private ArrayList<PerformDetails> allResult;
    private TableLayout tb;
    private TextView averageText,averageValue;
    private double studenCount=0.0;
    private double allMarks=0.0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.performance_fragment, container, false);
        Button bt=(Button)rootView.findViewById(R.id.btView);
        spTestId = (Spinner) rootView.findViewById(R.id.spTestID);
        search = (EditText) rootView.findViewById(R.id.serachName);
        selectedCenter=(TextView)rootView.findViewById(R.id.selectedCenter);
        selectedCenter.setText(NevigationActivity.ExamCenter);
        markTable=(TableLayout) rootView.findViewById(R.id.markTable);
        scrl=(ScrollView) rootView.findViewById(R.id.scrollView1);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        tb=(TableLayout) rootView.findViewById(R.id.serachTable);
        tb.setVisibility(View.GONE);
        averageText=(TextView)rootView.findViewById(R.id.avText);
        averageText.setVisibility(View.GONE);
        averageValue=(TextView)rootView.findViewById(R.id.avTextResul);
        averageValue.setVisibility(View.GONE);
        name=sharedpreferences.getString("Name",null);
        studId=sharedpreferences.getString("UserName",null).trim();
        if(NevigationActivity.switchValue.equals("ALL")){
            DownloadedDataCenter.getInstance(getActivity()).setSelectedTestTests(DownloadedDataCenter.getInstance(getActivity()).getLoadAllTests());
        }else if(NevigationActivity.switchValue.equals("THEORY")){
            DownloadedDataCenter.getInstance(getActivity()).setSelectedTestTests(DownloadedDataCenter.getInstance(getActivity()).getLoadTheoryTests());
        }else if(NevigationActivity.switchValue.equals("REVISION")){
            DownloadedDataCenter.getInstance(getActivity()).setSelectedTestTests(DownloadedDataCenter.getInstance(getActivity()).getLoadRevisionTests());
        }else if(NevigationActivity.switchValue.equals("MODEL")){
            DownloadedDataCenter.getInstance(getActivity()).setSelectedTestTests(DownloadedDataCenter.getInstance(getActivity()).getLoadModelPaperTests());
        }
        if(NevigationActivity.ALYear==null || NevigationActivity.ALYear.equals("")){
            DownloadedDataCenter.getInstance(getActivity()).getSelectedTestsTestFromYear().clear();
            if(DownloadedDataCenter.getInstance(getActivity()).getSelectedTestTests().size()>0) {
                for (int i = 0; i < DownloadedDataCenter.getInstance(getActivity()).getSelectedTestTests().size(); i++) {
                    if (DownloadedDataCenter.getInstance(getActivity()).getSelectedTestTests().get(i).contains(DownloadedDataCenter.getInstance(getActivity()).getLoadAllALYears().get(0))) {
                        DownloadedDataCenter.getInstance(getActivity()).setselectedTestsTestFromYear(DownloadedDataCenter.getInstance(getActivity()).getSelectedTestTests().get(i));

                    }
                }
            }
        }else{
            DownloadedDataCenter.getInstance(getActivity()).getSelectedTestsTestFromYear().clear();
            if(DownloadedDataCenter.getInstance(getActivity()).getSelectedTestTests().size()>0) {
                for (int i = 0; i < DownloadedDataCenter.getInstance(getActivity()).getSelectedTestTests().size(); i++) {
                    if (DownloadedDataCenter.getInstance(getActivity()).getSelectedTestTests().get(i).contains(NevigationActivity.ALYear)) {
                        DownloadedDataCenter.getInstance(getActivity()).setselectedTestsTestFromYear(DownloadedDataCenter.getInstance(getActivity()).getSelectedTestTests().get(i));

                    }
                }
            }
        }
        loadSpinners();
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                filterTableByName(s.toString() );
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
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
        ArrayAdapter<String> dataAdapterTest = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,DownloadedDataCenter.getInstance(getActivity()).getSelectedTestsTestFromYear());
        dataAdapterTest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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

    }
    public void filterTableByName(String enterdValue){
        if(allResult!=null && allResult.size()>0){
            ArrayList<PerformDetails> allResultTemp=new ArrayList<>() ;
           for(int i=0;i<allResult.size();i++){
//               if(allResult.get(i).getName().substring(0,enterdValue.length()).equalsIgnoreCase(enterdValue)){
//                   allResultTemp.add(allResult.get(i));
//               }
               if(allResult.get(i).getName().toUpperCase().contains(enterdValue.toUpperCase())){
                   allResultTemp.add(allResult.get(i));
               }
           }
            blockNext = false;
            blockBack = false;
            isLastSet=false;
            startValue = 0;
            markTable.removeAllViews();
            loadTableLayout(allResultTemp);
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Performance");
    }
    private class SendfeedbackJob extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(
                getActivity(),ProgressDialog.THEME_HOLO_DARK);
        @Override
        protected void onPreExecute() {
            try {
                this.dialog.setMessage("Loading Data....");
                this.dialog.setCanceledOnTouchOutside(false);
                this.dialog.show();
            } catch (Exception e) {
                Toast.makeText(
                        getActivity(),
                        "There are Some error in Loading Data : "
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
                int timeoutSocket = 1200000; // in milliseconds which is the timeout
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpGet request = new HttpGet();
                String url = "http://api.dekma.edu.lk/api/Performance/LoadStudentPerformance?TestId="+testID+"&Centre="+NevigationActivity.ExamCenter;
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
                    allResult = new ArrayList<PerformDetails>();
                    if (jsonarray != null && jsonarray.length() > 0) {
                        DownloadedDataCenter.getInstance(getActivity()).getAllResult().clear();
                        studenCount=0;
                        allMarks=0;
                        for (int i = 0; i < jsonarray.length(); i++) {
                            ++studenCount;
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            PerformDetails newResult=new PerformDetails();
                            newResult.setRank(jsonobject.getString("Rank").trim());
                            newResult.setName(jsonobject.getString("Name").trim());
                            newResult.setSchool(jsonobject.getString("School").trim());
                            newResult.setMark(jsonobject.getString("Marks").trim());
                            allMarks=allMarks+Integer.parseInt(jsonobject.getString("Marks").trim());
                            newResult.setStudentId(jsonobject.getString("StudentId").trim());
                            allResult.add(newResult);
                        }
                        blockNext = false;
                        blockBack = false;
                        isLastSet=false;
                        startValue = 0;
                        markTable.removeAllViews();
                        loadTableLayout(allResult);
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "Data Not found", Toast.LENGTH_LONG).show();
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
                    markTable.removeAllViews();
                    tb.setVisibility(View.GONE);
                    averageText.setVisibility(View.GONE);
                    averageValue.setVisibility(View.GONE);
                    Toast.makeText(
                            getActivity(),
                            "Data Not found ", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
                markTable.removeAllViews();
                tb.setVisibility(View.GONE);
                averageText.setVisibility(View.GONE);
                averageValue.setVisibility(View.GONE);
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
    private void loadTableLayout(final ArrayList<PerformDetails> allResult){
        int pagin = (int) (getResources().getDimension(R.dimen.rcellPgination) / getResources().getDisplayMetrics().density);
        int texthead = (int) (getResources().getDimension(R.dimen.boldtext) / getResources().getDisplayMetrics().density);
        int textRow = (int) (getResources().getDimension(R.dimen.normalText) / getResources().getDisplayMetrics().density);
        int pcellDpW = (int) (getResources().getDimension(R.dimen.rcellSecW) / getResources().getDisplayMetrics().density);

        TableLayout.LayoutParams rowLp = new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,1.0f);
        TableLayout.LayoutParams rowLpTemp = new TableLayout.LayoutParams(pcellDpW, LayoutParams.WRAP_CONTENT,1.0f);
        TableRow.LayoutParams cellLp = new TableRow.LayoutParams( pcellDpW,LayoutParams.WRAP_CONTENT,1.0f);
        TableRow.LayoutParams cellPagin = new TableRow.LayoutParams( pagin,LayoutParams.WRAP_CONTENT,1.0f);
        TableRow.LayoutParams cellSec = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1.0f);

        TableRow th = new TableRow(getActivity());
        TextView thCode = new TextView(getActivity());
        thCode.setTextSize(texthead);
        thCode.setTextColor(Color.WHITE);
        thCode.setText("Rank");
        thCode.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
        thCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.raw_border));
        thCode.setPadding(8, 15, 8, 15);
        th.addView(thCode,cellLp);

        TextView thDmgStk = new TextView(getActivity());
        thDmgStk.setTextSize(texthead);
        thDmgStk.setTextColor(Color.WHITE);
        thDmgStk.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
        thDmgStk.setText("Name");
        thDmgStk.setBackgroundDrawable(getResources().getDrawable(R.drawable.raw_border));
        thDmgStk.setPadding(8, 15, 8, 15);
        th.addView(thDmgStk,cellSec);

        TextView thInvDate = new TextView(getActivity());
        thInvDate.setTextSize(texthead);
        thInvDate.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
        thInvDate.setTextColor(Color.WHITE);
        thInvDate.setText("Maks");
        thInvDate.setBackgroundDrawable(getResources().getDrawable(R.drawable.raw_border));
        thInvDate.setPadding(8, 15, 8, 15);
        th.addView(thInvDate,cellLp);
        TextView thInvVal = new TextView(getActivity());
        thInvVal.setTextSize(texthead);
        thInvVal.setTextColor(Color.WHITE);
        thInvVal.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
        thInvVal.setText("School");
        thInvVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.raw_border));
        thInvVal.setPadding(8, 15, 8, 15);
        th.addView(thInvVal,cellSec);




        th.setBackgroundResource(R.color.candidate_other);
        markTable.addView(th,rowLp);

        if(allResult.size() > 0 && (startValue+maxRow) <= allResult.size() && startValue >= 0){
            isLastSet=false;
            for (int i=startValue;i<startValue+maxRow;i++) {

                final TableRow tr = new TableRow(getActivity());

                final TextView retNum = new TextView(getActivity());
                retNum.setTextSize(textRow);
                retNum.setTypeface(Typeface.SANS_SERIF);
                retNum.setTextColor(Color.BLACK);
                retNum.setText(allResult.get(i).getRank());
                retNum.setBackgroundDrawable(getResources().getDrawable(R.drawable.data_border));
                retNum.setPadding(8, 15, 8, 15);
                tr.addView(retNum,cellLp);

                final TextView txtReName = new TextView(getActivity());
                txtReName.setTextSize(textRow);
                txtReName.setTypeface(Typeface.SANS_SERIF);
                txtReName.setTextColor(Color.BLACK);
                txtReName.setBackgroundDrawable(getResources().getDrawable(R.drawable.data_border));
                txtReName.setPadding(8, 15, 8, 15);
                txtReName.setText(allResult.get(i).getName());
                tr.addView(txtReName,cellSec);

                final TextView txtTraDate = new TextView(getActivity());
                txtTraDate.setTextSize(textRow);
                txtTraDate.setTypeface(Typeface.SANS_SERIF);
                txtTraDate.setTextColor(Color.BLACK);
                txtTraDate.setText(allResult.get(i).getMark());
                txtTraDate.setBackgroundDrawable(getResources().getDrawable(R.drawable.data_border));
                txtTraDate.setPadding(8, 15, 8, 15);
                tr.addView(txtTraDate,cellLp);

                TextView txtInValue = new TextView(getActivity());
                txtInValue.setTextSize(textRow);
                txtInValue.setTypeface(Typeface.SANS_SERIF);
                txtInValue.setTextColor(Color.BLACK);
                txtInValue.setText(allResult.get(i).getSchool());
                txtInValue.setBackgroundDrawable(getResources().getDrawable(R.drawable.data_border));
                txtInValue.setPadding(8, 15, 8, 15);
                tr.addView(txtInValue,cellSec);



                final TextView stdId = new TextView(getActivity());
                stdId.setTextSize(textRow);
                stdId.setTypeface(Typeface.SANS_SERIF);
                stdId.setTextColor(Color.BLACK);
                stdId.setVisibility(View.GONE);
                stdId.setText(allResult.get(i).getStudentId());
                tr.addView(stdId,cellLp);

                tr.setBackgroundDrawable(getResources().getDrawable(R.drawable.table_shape));
                tr.setVisibility(View.VISIBLE);
                markTable.addView(tr,rowLp);

                if(studId.equals(allResult.get(i).getStudentId())){
                    tr.getChildAt(0).setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_pink));
                    tr.getChildAt(0).setPadding(8, 15, 8, 15);
                    tr.getChildAt(1).setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_pink));
                    tr.getChildAt(1).setPadding(8, 15, 8, 15);
                    tr.getChildAt(2).setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_pink));
                    tr.getChildAt(2).setPadding(8, 15, 8, 15);
                    tr.getChildAt(3).setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_pink));
                    tr.getChildAt(3).setPadding(8, 15, 8, 15);
                }
            }
        }else{
           if(allResult.size()-startValue >0){
               isLastSet=true;
               for (int i=startValue;i<allResult.size();i++) {

                   final TableRow tr = new TableRow(getActivity());

                   final TextView retNum = new TextView(getActivity());
                   retNum.setTextSize(textRow);
                   retNum.setTypeface(Typeface.SANS_SERIF);
                   retNum.setTextColor(Color.BLACK);
                   retNum.setText(allResult.get(i).getRank());
                   retNum.setBackgroundDrawable(getResources().getDrawable(R.drawable.data_border));
                   retNum.setPadding(8, 15, 8, 15);
                   tr.addView(retNum,cellLp);

                   final TextView txtReName = new TextView(getActivity());
                   txtReName.setTextSize(textRow);
                   txtReName.setTypeface(Typeface.SANS_SERIF);
                   txtReName.setTextColor(Color.BLACK);
                   txtReName.setBackgroundDrawable(getResources().getDrawable(R.drawable.data_border));
                   txtReName.setPadding(8, 15, 8, 15);
                   txtReName.setText(allResult.get(i).getName());
                   tr.addView(txtReName,cellSec);

                   final TextView txtTraDate = new TextView(getActivity());
                   txtTraDate.setTextSize(textRow);
                   txtTraDate.setTypeface(Typeface.SANS_SERIF);
                   txtTraDate.setTextColor(Color.BLACK);
                   txtTraDate.setText(allResult.get(i).getMark());
                   txtTraDate.setBackgroundDrawable(getResources().getDrawable(R.drawable.data_border));
                   txtTraDate.setPadding(8, 15, 8, 15);
                   tr.addView(txtTraDate,cellLp);

                   TextView txtInValue = new TextView(getActivity());
                   txtInValue.setTextSize(textRow);
                   txtInValue.setTypeface(Typeface.SANS_SERIF);
                   txtInValue.setTextColor(Color.BLACK);
                   txtInValue.setText(allResult.get(i).getSchool());
                   txtInValue.setBackgroundDrawable(getResources().getDrawable(R.drawable.data_border));
                   txtInValue.setPadding(8, 15, 8, 15);
                   tr.addView(txtInValue,cellSec);




                   tr.setBackgroundDrawable(getResources().getDrawable(R.drawable.table_shape));
                   tr.setVisibility(View.VISIBLE);
                   markTable.addView(tr,rowLp);

                   if(studId.equals(allResult.get(i).getStudentId())){
                       tr.getChildAt(0).setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_pink));
                       tr.getChildAt(0).setPadding(8, 15, 8, 15);
                       tr.getChildAt(1).setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_pink));
                       tr.getChildAt(1).setPadding(8, 15, 8, 15);
                       tr.getChildAt(2).setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_pink));
                       tr.getChildAt(2).setPadding(8, 15, 8, 15);
                       tr.getChildAt(3).setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_pink));
                       tr.getChildAt(3).setPadding(8, 15, 8, 15);
                   }
               }
           }
        }
        TableRow Pagination = new TableRow(getActivity());

        Button cl2 = new Button(getActivity());
        cl2.setTextSize(texthead);
        cl2.setTextColor(Color.BLACK);
        cl2.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
        cl2.setText("<<<<");
        cl2.setPadding(8, 15, 8, 15);
        Pagination.addView(cl2,cellPagin);
        cl2.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                    if (!isLastSet){
                        if (!blockBack) {
                            startValue = startValue - maxRow;
                            if (startValue >= 0) {
                                blockBack = false;
                                blockNext = false;
                                markTable.removeAllViews();
                                loadTableLayout(allResult);
                                scrl.fullScroll(ScrollView.FOCUS_UP);
                            } else {
                                blockBack = true;
                            }
                        }
                    }else{
                        if (!blockBack) {
                            startValue = startValue - maxRow;
                            isLastSet=false;
                            if (startValue >= 0) {
                                blockBack = false;
                                blockNext = false;
                                markTable.removeAllViews();
                                loadTableLayout(allResult);
                                scrl.fullScroll(ScrollView.FOCUS_UP);
                            } else {
                                blockBack = true;
                            }
                        }
                }
            }
        });
        Button cl3 = new Button(getActivity());
        cl3.setTextSize(texthead);
        cl3.setTextColor(Color.BLACK);
        cl3.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
        cl3.setText(">>>>");
        cl3.setPadding(8, 15, 8, 15);
        Pagination.addView(cl3,cellPagin);
        cl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!blockNext) {
                    if(startValue<0){
                        startValue=0;
                    }
                    startValue = startValue + maxRow;
                    if ((startValue + maxRow) <= allResult.size()) {
                        blockNext = false;
                        blockBack = false;
                        markTable.removeAllViews();
                        loadTableLayout(allResult);
                        scrl.fullScroll(ScrollView.FOCUS_UP);
                    }else{
                        if(allResult.size()-startValue > 0){
                            blockNext = false;
                            blockBack = false;
                            markTable.removeAllViews();
                            loadTableLayout(allResult);
                            scrl.fullScroll(ScrollView.FOCUS_UP);
                        }else {
                            blockNext = true;
                        }
                    }
                }
            }
        });
//        Button cl8 = new Button(getActivity());
//        cl8.setTextSize(texthead);
//        cl8.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
//        cl8.setTextColor(Color.WHITE);
//        cl8.setPadding(8, 15, 8, 15);
//        Pagination.addView(cl8,cellPagin);
//
//        Button cl9 = new Button(getActivity());
//        cl9.setTextSize(texthead);
//        cl9.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
//        cl9.setTextColor(Color.WHITE);
//        cl9.setPadding(8, 15, 8, 15);
//        Pagination.addView(cl9,cellPagin);

        Button cl4 = new Button(getActivity());
        cl4.setTextSize(texthead);
        cl4.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
        cl4.setTextColor(Color.WHITE);
        cl4.setPadding(8, 15, 8, 15);
        Pagination.addView(cl4,cellPagin);



        Pagination.setBackgroundResource(R.color.white);
        markTable.addView(Pagination,rowLpTemp);
        tb.setVisibility(View.VISIBLE);
        averageText.setVisibility(View.VISIBLE);
        averageValue.setVisibility(View.VISIBLE);
        double average=allMarks/studenCount;
        averageValue.setText(""+Math.round(average*100.0)/100.0);
    }
}