package com.example.junaid.smslistview;


/**
 * Created by Junaid on 5/15/2016.
 */
public class Messages  {

    private String cellNo;
    private String messageBody;
    private String time;

    public Messages(String cellNo, String messageBody, String time) {
        this.cellNo = cellNo;
        this.messageBody = messageBody;
        this.time = time;
    }

    public void setCellNo(String cellNo) {
        this.cellNo = cellNo;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCellNo() {
        return cellNo;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getTime() {
        return time;
    }
}
