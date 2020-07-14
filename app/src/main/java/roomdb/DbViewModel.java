package roomdb;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import models.Code;
import models.Log;

public class DbViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<List<Log>> allHistory;
    private LiveData<List<Code>> allFavorite;

    public DbViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        allHistory = repository.getHistory();
        allFavorite = repository.getFavorites();
    }

    public LiveData<List<Code>> getAllFavorite() {
        return allFavorite;
    }

    public void deleteAllFavorites(){
        repository.deleteAllFavorites();
    }

    public void addFavorite(Code code){
        repository.addFavorite(code);
    }

    public void removeFavoriteById(String id){
        repository.removeFavoriteById(id);
    }

    public LiveData<List<Log>> getAllHistory() {
        return allHistory;
    }

    public LiveData<List<Log>> getParticularHistory(String id, String date){
        return repository.getParticularHistory(id, date);
    }

    public void deleteHistoryByDate(String id, String date){
        repository.deleteHistoryByDate(id, date);
    }

    public void addHistory(Log log){
        repository.addHistory(log);
    }
}
