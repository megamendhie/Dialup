package roomdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import models.Log;

@Dao
public interface LogDoa {

    @Insert
    void addLog(Log log);

    @Delete
    void deleteLog(Log log);

    @Query("SELECT * FROM log_table")
    LiveData<List<Log>> getLogs();

    @Query("SELECT * FROM log_table WHERE id = :id AND date = :date")
    LiveData<List<Log>> getParticularLog(String id, String date);

    @Query("DELETE FROM log_table WHERE time =:time")
    void deleteLogByTime(String time);
}
