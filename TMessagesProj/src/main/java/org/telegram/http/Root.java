package org.telegram.http;

import java.util.List;

/**
 * Created by sa on 2018/4/9.
 */

public class Root {
    private int status;

    private List<String> data ;

    private List<String> wy_yum;

    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setString(List<String> data){
        this.data = data;
    }
    public List<String> getString(){
        return this.data;
    }
    public void setWy_yum(List<String> wy_yum){
        this.wy_yum = wy_yum;
    }
    public List<String> getWy_yum(){
        return this.wy_yum;
    }
}
