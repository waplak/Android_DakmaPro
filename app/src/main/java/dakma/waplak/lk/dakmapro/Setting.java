package dakma.waplak.lk.dakmapro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import dakma.waplak.lk.NevigationActivity;
import dakma.waplak.lk.dakmapro.R;
import dakma.waplak.lk.utility.AndroidUtill;
import dakma.waplak.lk.utility.DownloadedDataCenter;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by admin on 5/26/2017.
 */

public class Setting extends Fragment {
    public static final String MyPREFERENCES = "MyPrefs";
    private String userName,userType,stdName;
    private Switch all,theory,revision,model;
    private Switch matara,galle,hambantota;
    private Spinner spinnerAlYear;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.setting_layout, container, false);
        prefs = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        userName=prefs.getString("UserName",null).toString().trim();
        userType=prefs.getString("UserType",null).toString().trim();
        stdName=prefs.getString("Name",null).toString().trim();
        if(NevigationActivity.ALYear!=null && !NevigationActivity.ALYear.equals("")) {
            NevigationActivity.ALYear = prefs.getString("SelectedYear", null).toString().trim();
        }
        all = (Switch) rootView.findViewById(R.id.all);
        theory = (Switch) rootView.findViewById(R.id.theory);
        revision = (Switch) rootView.findViewById(R.id.revision);
        model = (Switch) rootView.findViewById(R.id.model);

        spinnerAlYear = (Spinner) rootView.findViewById(R.id.spinnerAlYears);

        matara = (Switch) rootView.findViewById(R.id.matara);
        galle = (Switch) rootView.findViewById(R.id.galle);
        hambantota = (Switch) rootView.findViewById(R.id.hambantota);

        if(NevigationActivity.switchValue.equals("ALL")){
            all.setChecked(true);
        }else if(NevigationActivity.switchValue.equals("THEORY")){
            theory.setChecked(true);
        }else if(NevigationActivity.switchValue.equals("REVISION")){
            revision.setChecked(true);
        }else if(NevigationActivity.switchValue.equals("MODEL")){
            model.setChecked(true);
        }

        if(NevigationActivity.ExamCenter.equals("Matara")){
            matara.setChecked(true);
        }else if(NevigationActivity.ExamCenter.equals("Galle")){
            galle.setChecked(true);
        }else if(NevigationActivity.ExamCenter.equals("Hambantota")){
            hambantota.setChecked(true);
        }
        all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    revision.setChecked(false);
                    model.setChecked(false);
                    theory.setChecked(false);
                    NevigationActivity.switchValue="ALL";
                    DownloadedDataCenter.getInstance(getActivity()).setSelectedTestTests(DownloadedDataCenter.getInstance(getActivity()).getLoadAllTests());
                    AndroidUtill.serializeObject(userName,"T",NevigationActivity.ExamCenter,NevigationActivity.switchValue,userType,stdName);
                }
            }
        });
        theory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    model.setChecked(false);
                    all.setChecked(false);
                    revision.setChecked(false);
                    NevigationActivity.switchValue="THEORY";
                    DownloadedDataCenter.getInstance(getActivity()).setSelectedTestTests(DownloadedDataCenter.getInstance(getActivity()).getLoadTheoryTests());
                    AndroidUtill.serializeObject(userName,"T",NevigationActivity.ExamCenter,NevigationActivity.switchValue,userType,stdName);
                }
            }
        });
        revision.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    theory.setChecked(false);
                    model.setChecked(false);
                    all.setChecked(false);
                    NevigationActivity.switchValue="REVISION";
                    DownloadedDataCenter.getInstance(getActivity()).setSelectedTestTests(DownloadedDataCenter.getInstance(getActivity()).getLoadRevisionTests());
                    AndroidUtill.serializeObject(userName,"T",NevigationActivity.ExamCenter,NevigationActivity.switchValue,userType,stdName);
                }
            }
        });
        model.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    theory.setChecked(false);
                    revision.setChecked(false);
                    all.setChecked(false);
                    NevigationActivity.switchValue="MODEL";
                    DownloadedDataCenter.getInstance(getActivity()).setSelectedTestTests(DownloadedDataCenter.getInstance(getActivity()).getLoadModelPaperTests());
                    AndroidUtill.serializeObject(userName,"T",NevigationActivity.ExamCenter,NevigationActivity.switchValue,userType,stdName);
                }
            }
        });

        matara.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    hambantota.setChecked(false);
                    galle.setChecked(false);
                    NevigationActivity.ExamCenter="Matara";
                    AndroidUtill.serializeObject(userName,"T",NevigationActivity.ExamCenter,NevigationActivity.switchValue,userType,stdName);
                }
            }
        });
        galle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    hambantota.setChecked(false);
                    matara.setChecked(false);
                    NevigationActivity.ExamCenter="Galle";
                    AndroidUtill.serializeObject(userName,"T",NevigationActivity.ExamCenter,NevigationActivity.switchValue,userType,stdName);
                }
            }
        });
        hambantota.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    galle.setChecked(false);
                    matara.setChecked(false);
                    NevigationActivity.ExamCenter="Hambantota";
                    AndroidUtill.serializeObject(userName,"T",NevigationActivity.ExamCenter,NevigationActivity.switchValue,userType,stdName);
                }
            }
        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, DownloadedDataCenter.getInstance(getActivity()).getLoadAllALYears());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAlYear.setAdapter(dataAdapter);
        spinnerAlYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NevigationActivity.ALYear = (String) parent.getItemAtPosition(position);
                DownloadedDataCenter.getInstance(getActivity()).getSelectedTestsTestFromYear().clear();
                if(NevigationActivity.ALYear!=null && !NevigationActivity.ALYear.equals("")){
                    if(DownloadedDataCenter.getInstance(getActivity()).getSelectedTestTests().size()>0) {
                        for (int i = 0; i < DownloadedDataCenter.getInstance(getActivity()).getSelectedTestTests().size(); i++) {
                            if (DownloadedDataCenter.getInstance(getActivity()).getSelectedTestTests().get(i).contains(NevigationActivity.ALYear)) {
                                DownloadedDataCenter.getInstance(getActivity()).setselectedTestsTestFromYear(DownloadedDataCenter.getInstance(getActivity()).getSelectedTestTests().get(i));

                            }
                        }
                    }
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("SelectedYear", NevigationActivity.ALYear);
                    editor.commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if(NevigationActivity.ALYear!=null && !NevigationActivity.ALYear.equals("")) {
            spinnerAlYear.setSelection(dataAdapter.getPosition(NevigationActivity.ALYear));
        }
        return rootView;

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Setting");
    }
}