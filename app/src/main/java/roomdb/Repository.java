package roomdb;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import models.Code;
import models.Log;

class Repository {
    private FavoriteDoa favDoa;
    private LogDoa logDoa;

    private LiveData<List<Code>> favorites;
    private LiveData<List<Log>> history;

    Repository(Application application) {
        RoomDb roomDb = RoomDb.getDatabase(application);
        favDoa = roomDb.favDoa();
        logDoa = roomDb.logDoa();
    }

    LiveData<List<Code>> getFavorites() {
        favorites = favDoa.getFavorites();
        return favorites;
    }

    void deleteAllFavorites(){
        favDoa.deleteAll();
    }

    void addFavorite(Code code){
        RoomDb.databaseWriteExecutor.execute(() -> {
            favDoa.addFav(code);
        });
    }

    void removeFavoriteById(String id){
        RoomDb.databaseWriteExecutor.execute(() -> {
            favDoa.removeFavById(id);
        });
    }

    LiveData<List<Log>> getHistory() {
        history = logDoa.getLogs();
        return history;
    }

    LiveData<List<Log>> getParticularHistory(String id, String date){
        history = logDoa.getParticularLog(id, date);
        return history;
    }

    void deleteHistoryByDate(String id, String date){
        history = logDoa.getParticularLog(id, date);

        RoomDb.databaseWriteExecutor.execute(() -> {
            for(Log log: history.getValue()){
                logDoa.deleteLog(log);
            }
        });
    }

    void addHistory(Log log){
        RoomDb.databaseWriteExecutor.execute(() -> {
            logDoa.addLog(log);
        });
    }
}
