package dakma.waplak.lk.utility;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by admin on 5/23/2017.
 */

public class DownloadedDataCenter {
    private final Context myContext;
    private static DownloadedDataCenter mdownloadedInstence;
    private ArrayList<String> loadAllTests = new ArrayList<String>();
    private ArrayList<String> loadTheoryTests = new ArrayList<String>();
    private ArrayList<String> loadRevisionTests = new ArrayList<String>();
    private ArrayList<String> loadModelPaperTests = new ArrayList<String>();
    private ArrayList<String> loadAllALYears = new ArrayList<String>();
    private ArrayList<String> selectedTestTests = new ArrayList<String>();
    private ArrayList<String> loadSchools = new ArrayList<String>();
    private ArrayList<PerformDetails> allResult = new ArrayList<PerformDetails>();
    private ArrayList<String> selectedTestsTestFromYear = new ArrayList<String>();
    public ArrayList<PerformDetails> getAllResult() {
        return allResult;
    }
    public ArrayList<String> getSelectedTestTests() {
        return selectedTestTests;
    }

    public void setSelectedTestTests(ArrayList<String> selectedTestTests) {
        this.selectedTestTests = selectedTestTests;
    }

    private DownloadedDataCenter(Context context) {
        this.myContext = context;
    }

    public static synchronized DownloadedDataCenter getInstance(Context context) {
        try{
            if (mdownloadedInstence == null) {
                mdownloadedInstence = new DownloadedDataCenter(context);
            }
            return mdownloadedInstence;
        } catch (Exception e) {
            throw new Error("Error  ");
        }
    }
    public ArrayList<String> getLoadRevisionTests() {
        return loadRevisionTests;
    }

    public ArrayList<String> getLoadTheoryTests() {
        return loadTheoryTests;
    }

    public ArrayList<String> getLoadAllTests() {
        return loadAllTests;
    }

    public ArrayList<String> getLoadAllALYears() {
        return loadAllALYears;
    }

    public ArrayList<String> getLoadModelPaperTests() {
        return loadModelPaperTests;
    }

    public ArrayList<String> getLoadSchools() {
        return loadSchools;
    }

    public ArrayList<String> getSelectedTestsTestFromYear() {
        return selectedTestsTestFromYear;
    }
    public void setLoadAllTests(String test) {
        this.loadAllTests.add(test);
    }
    public void setLoadTheoryTests(String test) {
        this.loadTheoryTests.add(test);
    }
    public void setLoadRevisionTests(String test) {
        this.loadRevisionTests.add(test);
    }
    public void setLoadAllALYears(String test) {
        this.loadAllALYears.add(test);
    }
    public void setLoadModelPaperTests(String test) {
        this.loadModelPaperTests.add(test);
    }
    public void setLoadSchools(String test) {
        this.loadSchools.add(test);
    }
    public void setResult(PerformDetails test) {
        this.allResult.add(test);
    }
    public void setselectedTestsTestFromYear(String test) {
        this.selectedTestsTestFromYear.add(test);
    }
}
