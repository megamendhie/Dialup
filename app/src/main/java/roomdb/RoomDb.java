package roomdb;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.Code;
import models.Log;

@Database(entities = {Code.class, Log.class}, version = 1, exportSchema = false)
public abstract class RoomDb extends RoomDatabase {
    abstract FavoriteDoa favDoa();
    abstract LogDoa logDoa();

    private static volatile RoomDb INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static RoomDb getDatabase(final Context context){

        if(INSTANCE==null){
            synchronized (RoomDb.class){
                if(INSTANCE==null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RoomDb.class, "code_database").addCallback(databaseCallback).build();
                }
            }
        }

        return INSTANCE;
    }

    private static Callback databaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };
}
