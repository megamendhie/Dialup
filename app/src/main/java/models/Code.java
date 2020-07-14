package models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fav_table")
public class Code {

    @PrimaryKey
    @NonNull
    private String id;
    private String action;
    private String brandTag;
    private String brandRef;
    private String code;
    private String country;
    private String message;
    private String purpose;
    private String to;
    private long clicks;
    private long priority;
    private long time;

    public Code(){
    }

    public Code(CustomLog log) {
        this.id = log.getId();
        this.code = log.getCode();
        this.country = log.getCountry();
        this.purpose = log.getPurpose();
        this.action = log.getAction();
        this.brandRef = log.getBrandRef();
        this.message = log.getMessage();
        this.to = log.getTo();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getClicks() {
        return clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public String getBrandTag() {
        return brandTag;
    }

    public void setBrandTag(String brandTag) {
        this.brandTag = brandTag;
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

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Code){
            Code code = (Code) obj;
            return (this.getId().equals(code.getId()) && this.getTime()==code.getTime());
        }
        return false;
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
