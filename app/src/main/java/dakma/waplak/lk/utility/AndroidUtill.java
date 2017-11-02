package dakma.waplak.lk.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by admin on 5/21/2017.
 */

public class AndroidUtill {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public static String LoadAllTests = "http://api.dekma.edu.lk/api/Test/LoadAllTests";
    public static String LoadTheoryTests  = "http://api.dekma.edu.lk/api/Test/LoadTheoryTests";
    public static String LoadRevisionTests = "http://api.dekma.edu.lk/api/Test/LoadRevisionTests";
    public static String LoadModelPaperTests = "http://api.dekma.edu.lk/api/Test/LoadModelPaperTests";
    public static String LoadAllALYears = "http://api.dekma.edu.lk/api/Test/LoadAllALYears";
    public static String LoadSchools = "http://api.dekma.edu.lk/api/School/LoadSchools";

    public static final String FILE_PATH ="/data/data/dakma.waplak.lk.dakmapro/log.txt";

    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)
                ctx.getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
    public static int getConnectivityStatus(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)
                ctx.getSystemService (Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (null != ni) {
            if(ni.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(ni.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }
    public static boolean isInternetAvailable(String host) {
        //String host = "www.google.com";
        int port = 80;
        Socket socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(host, port), 2000);
            socket.close();
            return true;
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException es) {}
            return false;
        }
    }

    public static void serializeObject(String userName , String isLogin,String center,String testType ,String userType, String stdName) {

        try {
            FileData object1 = new FileData(userName, isLogin,center,testType,userType,stdName);
            FileOutputStream fos = new FileOutputStream(FILE_PATH);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object1);
            oos.flush();
            oos.close();
        }catch(Exception e) {
            Log.i("TAG","Exception during serialization:"+ e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }
    public static FileData deserializeObject() {
        File file = new File(FILE_PATH);
        FileData object2 = new FileData();

        if (file.exists()) {


            try {
                FileInputStream fis = new FileInputStream(FILE_PATH);
                ObjectInputStream ois = new ObjectInputStream(fis);
                object2 = (FileData) ois.readObject();
                ois.close();
                return object2;
            } catch (Exception e) {

                Log.i("TAG","Exception during deserialization:"+ e.getMessage());
                e.printStackTrace();
                System.exit(0);

            }
        }
        return object2;
    }
}
