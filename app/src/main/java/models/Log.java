package models;

import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "log_table")
public class Log {

    @PrimaryKey
    @NonNull
    private long time;
    private String id;
    private String code;
    private String country;
    private String purpose;
    private String action;
    private String brandRef;
    private String message;
    private String to;
    private String date;

    public Log(Code code){
        this.id = code.getId();
        this.code = code.getCode();
        this.country = code.getCountry();
        this.purpose = code.getPurpose();
        this.action = code.getAction();
        this.brandRef = code.getBrandRef();
        this.message = code.getMessage();
        this.to = code.getTo();
        this.time = new Date().getTime();
        this.date = DateFormat.format("dd/MM/yyyy", time).toString();
    }

    public Log(Log log) {
        this.id = log.getId();
        this.code = log.getCode();
        this.country = log.getCountry();
        this.purpose = log.getPurpose();
        this.action = log.getAction();
        this.brandRef = log.getBrandRef();
        this.message = log.getMessage();
        this.to = log.getTo();
        this.time = log.getTime();
        this.date = log.getDate();

    }

    public Log() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBrandRef() {
        return brandRef;
    }

    public void setBrandRef(String brandRef) {
        this.brandRef = brandRef;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
