package models;

import androidx.annotation.NonNull;

public class CustomLog extends Log implements Comparable{

    private int count;

    public CustomLog(Log log) {
        super(log);
        this.count = 1;
    }
    public int getCount() {
        return count;
    }

    public void setCount() {
        this.count += 1;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Log log = (Log) o;
        if(this.getTime()==log.getTime())
            return 0;
        else if (this.getTime()>log.getTime())
            return -1;
        else
            return 1;
    }
}
