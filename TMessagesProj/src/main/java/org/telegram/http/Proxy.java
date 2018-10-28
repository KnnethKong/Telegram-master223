package org.telegram.http;

import java.util.List;

/**
 * Created by sa on 2018/4/8.
 */

public class Proxy{
    public class Yum {
        private String zyum;

        private String byum;

        public void setZyum(String zyum){
            this.zyum = zyum;
        }
        public String getZyum(){
            return this.zyum;
        }
        public void setByum(String byum){
            this.byum = byum;
        }
        public String getByum(){
            return this.byum;
        }

    }
    public class Data {
        private String id;

        private String ip;

        private String address;

        private String port;

        private String type;

        private String is_zh;

        private String account;

        private String pwd;

        private String key;

        private String is_mo;

        private String sort;

        private String ctime;

        private Yum yum;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setIp(String ip){
            this.ip = ip;
        }
        public String getIp(){
            return this.ip;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
        public void setPort(String port){
            this.port = port;
        }
        public String getPort(){
            return this.port;
        }
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setIs_zh(String is_zh){
            this.is_zh = is_zh;
        }
        public String getIs_zh(){
            return this.is_zh;
        }
        public void setAccount(String account){
            this.account = account;
        }
        public String getAccount(){
            return this.account;
        }
        public void setPwd(String pwd){
            this.pwd = pwd;
        }
        public String getPwd(){
            return this.pwd;
        }
        public void setKey(String key){
            this.key = key;
        }
        public String getKey(){
            return this.key;
        }
        public void setIs_mo(String is_mo){
            this.is_mo = is_mo;
        }
        public String getIs_mo(){
            return this.is_mo;
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
        public void setYum(Yum yum){
            this.yum = yum;
        }
        public Yum getYum(){
            return this.yum;
        }

    }

    public class Wy_yum {

    }

    public class Root {
        private int status;

        private Data data;

        private List<String> wy_yum;

        public void setStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return this.status;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Data getData() {
            return this.data;
        }

        public void setWy_yum(List<String> wy_yum) {
            this.wy_yum = wy_yum;
        }

        public List<String> getWy_yum() {
            return this.wy_yum;
        }

    }
}