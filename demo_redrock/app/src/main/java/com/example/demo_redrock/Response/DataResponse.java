package com.example.demo_redrock.Response;

//接口返回数据解析类
public class DataResponse {
    private int errorCode;
    private String errorMsg;
    private Data data;

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int getErrorCode() { return errorCode; }
    public String getErrorMsg() { return errorMsg; }
    public Data getData() { return data; }

    public static class Data {
        private int id;
        private String username;
        public int getId() { return id; }
        public String getUsername() { return username; }
    }
}