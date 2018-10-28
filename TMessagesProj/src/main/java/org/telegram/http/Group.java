package org.telegram.http;

import java.util.List;

/**
 * Created by sa on 2018/4/12.
 */

public class Group {
    public class Data {
        private String share_url;

        public void setShare_url(String share_url){
            this.share_url = share_url;
        }
        public String getShare_url(){
            return this.share_url;
        }

    }
    public class Root {
        private int status;

        private Data data;

        private List<String> wy_yum ;

        public void setStatus(int status){
            this.status = status;
        }
        public int getStatus(){
            return this.status;
        }
        public void setData(Data data){
            this.data = data;
        }
        public Data getData(){
            return this.data;
        }
        public void setWy_yum(List<String> wy_yum){
            this.wy_yum = wy_yum;
        }
        public List<String> getWy_yum(){
            return this.wy_yum;
        }

    }
}
