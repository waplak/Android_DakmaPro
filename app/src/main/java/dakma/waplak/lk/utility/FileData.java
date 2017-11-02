package dakma.waplak.lk.utility;

import java.io.Serializable;

/**
 * Created by admin on 5/27/2017.
 */

public class FileData implements Serializable {
    private String userName,isLogin,selectedCenter,selectedTestType,userType,stdName;
    public FileData(){}

    public String getSelectedCenter() {
        return selectedCenter;
    }

    public void setSelectedCenter(String selectedCenter) {
        this.selectedCenter = selectedCenter;
    }

    public String getSelectedTestType() {
        return selectedTestType;
    }

    public void setSelectedTestType(String selectedTestType) {
        this.selectedTestType = selectedTestType;
    }

    public FileData(String userName, String isLogin, String selectedCenter, String selectedTestType,String userType,String stdName){
        super();
        this.userName = userName;
        this.isLogin = isLogin;
        this.selectedCenter = selectedCenter;
        this.selectedTestType = selectedTestType;
        this.userType = userType;
        this.stdName=stdName;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }

    public String getStdName() {
        return stdName;
    }

    public void setStdName(String stdName) {
        this.stdName = stdName;
    }
}
