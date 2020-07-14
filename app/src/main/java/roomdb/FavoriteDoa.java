package roomdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import models.Code;

@Dao
public interface FavoriteDoa {

    @Insert
    void addFav(Code code);

    @Query("DELETE FROM fav_table")
    void deleteAll();

    @Query("DELETE FROM fav_table WHERE id = :id")
    void removeFavById(String id);

    @Query("SELECT * FROM fav_table ORDER BY time ASC")
    LiveData<List<Code>> getFavorites();

    @Delete
    void removeFav(Code code);


}
