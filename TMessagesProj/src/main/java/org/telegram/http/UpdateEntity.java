package org.telegram.http;

import java.util.List;

/**
 * Created by Administrator on 2018/4/9.
 */

public class UpdateEntity {
    public class Data {
        private String id;

        private String title;

        private String edition;

        private String type;

        private String is_qian;

        private String content;

        private String url;

        private String ctime;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setTitle(String title){
            this.title = title;
        }
        public String getTitle(){
            return this.title;
        }
        public void setEdition(String edition){
            this.edition = edition;
        }
        public String getEdition(){
            return this.edition;
        }
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setIs_qian(String is_qian){
            this.is_qian = is_qian;
        }
        public String getIs_qian(){
            return this.is_qian;
        }
        public void setContent(String content){
            this.content = content;
        }
        public String getContent(){
            return this.content;
        }
        public void setUrl(String url){
            this.url = url;
        }
        public String getUrl(){
            return this.url;
        }
        public void setCtime(String ctime){
            this.ctime = ctime;
        }
        public String getCtime(){
            return this.ctime;
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
