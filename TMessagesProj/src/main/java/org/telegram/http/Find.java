package org.telegram.http;

import java.util.List;

/**
 * Created by sa on 2018/4/11.
 */

public class Find {
    public class Banner {
        private String id;

        private String type;

        private String title;

        private String image;

        private String link;

        private String sort;

        private String ctime;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setTitle(String title){
            this.title = title;
        }
        public String getTitle(){
            return this.title;
        }
        public void setImage(String image){
            this.image = image;
        }
        public String getImage(){
            return this.image;
        }
        public void setLink(String link){
            this.link = link;
        }
        public String getLink(){
            return this.link;
        }
        public void setSort(String sort){
            this.sort = sort;
        }
        public String getSort(){
            return this.sort;
        }
        public void setCtime(String ctime){
            this.ctime = ctime;
        }
        public String getCtime(){
            return this.ctime;
        }

    }
    public class Zhon {
        private String id;

        private String img;

        private String title;

        private String url;

        private String sort;

        private String ctime;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setImg(String img){
            this.img = img;
        }
        public String getImg(){
            return this.img;
        }
        public void setTitle(String title){
            this.title = title;
        }
        public String getTitle(){
            return this.title;
        }
        public void setUrl(String url){
            this.url = url;
        }
        public String getUrl(){
            return this.url;
        }
        public void setSort(String sort){
            this.sort = sort;
        }
        public String getSort(){
            return this.sort;
        }
        public void setCtime(String ctime){
            this.ctime = ctime;
        }
        public String getCtime(){
            return this.ctime;
        }

    }

    public class Qunzu {
        private String id;

        private String qun;

        private String img;

        private String title;

        private String introduction;

        private String is_mo;

        private String is_re;

        private String ren_num;

        private String sort;

        private String ctime;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setQun(String qun){
            this.qun = qun;
        }
        public String getQun(){
            return this.qun;
        }
        public void setImg(String img){
            this.img = img;
        }
        public String getImg(){
            return this.img;
        }
        public void setTitle(String title){
            this.title = title;
        }
        public String getTitle(){
            return this.title;
        }
        public void setIntroduction(String introduction){
            this.introduction = introduction;
        }
        public String getIntroduction(){
            return this.introduction;
        }
        public void setIs_mo(String is_mo){
            this.is_mo = is_mo;
        }
        public String getIs_mo(){
            return this.is_mo;
        }
        public void setIs_re(String is_re){
            this.is_re = is_re;
        }
        public String getIs_re(){
            return this.is_re;
        }
        public void setRen_num(String ren_num){
            this.ren_num = ren_num;
        }
        public String getRen_num(){
            return this.ren_num;
        }
        public void setSort(String sort){
            this.sort = sort;
        }
        public String getSort(){
            return this.sort;
        }
        public void setCtime(String ctime){
            this.ctime = ctime;
        }
        public String getCtime(){
            return this.ctime;
        }

    }
    public class Data {
        private List<Banner> banner ;

        private List<Zhon> zhon ;

        private List<Qunzu> qunzu ;

        public void setBanner(List<Banner> banner){
            this.banner = banner;
        }
        public List<Banner> getBanner(){
            return this.banner;
        }
        public void setZhon(List<Zhon> zhon){
            this.zhon = zhon;
        }
        public List<Zhon> getZhon(){
            return this.zhon;
        }
        public void setQunzu(List<Qunzu> qunzu){
            this.qunzu = qunzu;
        }
        public List<Qunzu> getQunzu(){
            return this.qunzu;
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
        public void setString(List<String> wy_yum){
            this.wy_yum = wy_yum;
        }
        public List<String> getString(){
            return this.wy_yum;
        }

    }
}
