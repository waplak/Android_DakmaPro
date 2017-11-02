package dakma.waplak.lk.utility;

/**
 * Created by admin on 6/4/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.ArrayList;

import dakma.waplak.lk.dakmapro.R;

public class AutocompleteAdapter extends ArrayAdapter implements Filterable {
    private ArrayList<StudentNameCode> mName;

    public AutocompleteAdapter(Context context, int resource) {
        super(context, resource);
        mName = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mName.size();
    }

    @Override
    public StudentNameCode getItem(int position) {
        return mName.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null){
                    try{
                        //get data from the web
                        String term = constraint.toString();
                        mName = new SendfeedbackJob().execute(term).get();
                    }catch (Exception e){
                        Log.d("HUS","EXCEPTION "+e);
                    }
                    filterResults.values = mName;
                    filterResults.count = mName.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0){
                    notifyDataSetChanged();
                }else{
                    notifyDataSetInvalidated();
                }
            }
        };

        return myFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.auto_complete_layout,parent,false);

        StudentNameCode stdEntity = mName.get(position);

        TextView stdName = (TextView) view.findViewById(R.id.stdName);

        stdName.setText(stdEntity.getStdName());

        return view;
    }

    private class SendfeedbackJob extends AsyncTask<String, Void, ArrayList> {;
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected ArrayList doInBackground(String[] params) {
            HttpResponse response = null;
            String result = null;
            ArrayList stdList=new ArrayList<>();
            try {
                HttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = 60000;
                HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);
                int timeoutSocket = 120000; // in milliseconds which is the timeout
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpGet request = new HttpGet();
                String url = "http://api.dekma.edu.lk/api/Student/SearchStudentByStudentIdOrName?StudentIdOrName="+params[0];
                request.setURI(new URI(url.replace(" ", "%20")));
                response = httpclient.execute(request);
                if(response!= null && !response.equals("")) {

                    HttpEntity entity = response.getEntity();
                    if (entity != null) {

                        // A Simple JSON Response Read
                        InputStream instream = entity.getContent();
                        result = convertStreamToString(instream);
                        try {
                            if(result!=null) {
                                JSONArray jsonarray = new JSONArray(result);
                                for (int i = 0; i < jsonarray.length(); i++) {
                                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                                    StudentNameCode std = new StudentNameCode();
                                    std.setStdName(jsonobject.getString("StudentNameWithId").trim());
                                    stdList.add(std);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        instream.close();


                    }
//                    org.apache.http.Header[] headers = response.getAllHeaders();
//                    for (int i = 0; i < headers.length; i++) {
//                        System.out.println(headers[i]);
//                    }
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("log_tag", "Error in http connection " + e.toString());
            }
            return stdList;

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
