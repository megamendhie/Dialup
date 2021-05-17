package models;

import androidx.annotation.NonNull;

import java.util.List;

public class OrganizedLog implements Comparable{
    private long timeStamp;
    private List<CustomLog> listOfCalls;

    public OrganizedLog(long timeStamp, List<CustomLog> listOfCalls) {
        this.timeStamp = timeStamp;
        this.listOfCalls = listOfCalls;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        OrganizedLog log = (OrganizedLog) o;
        if(this.getTimeStamp()==log.getTimeStamp())
            return 0;
        else if (this.getTimeStamp()>log.getTimeStamp())
            return -1;
        else
            return 1;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<CustomLog> getListOfCalls() {
        return listOfCalls;
    }

    public void setListOfCalls(List<CustomLog> listOfCalls) {
        this.listOfCalls = listOfCalls;
    }
}
