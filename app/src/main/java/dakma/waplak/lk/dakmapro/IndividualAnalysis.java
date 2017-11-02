package dakma.waplak.lk.dakmapro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dakma.waplak.lk.NevigationActivity;
import dakma.waplak.lk.dakmapro.indi.GradePercentagesFragment;
import dakma.waplak.lk.dakmapro.indi.IndividualPerformanceFragment;
import dakma.waplak.lk.utility.DownloadedDataCenter;

import static android.content.ContentValues.TAG;

/**
 * Created by admin on 5/17/2017.
 */

public class IndividualAnalysis extends Fragment {

    public static String getTAG() {
        return "IndividualAnalysis";
    }
    private  ViewPager mViewPager;
    private Adapter viewPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = null;
        try {
            rootView = inflater.inflate(R.layout.individual_analysis_fragment, container, false);
            mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

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
            Log.d(TAG, "onCreateView");
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: " + e.toString());
        }
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        setupViewPager(mViewPager);
        getActivity().setTitle("Individual Analysis");
    }



    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new Adapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new IndividualPerformanceFragment(), "Individual Performance");
        viewPagerAdapter.addFragment(new GradePercentagesFragment(), "Grade Percentages");
        viewPager.setAdapter(viewPagerAdapter);
    }

    private static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}