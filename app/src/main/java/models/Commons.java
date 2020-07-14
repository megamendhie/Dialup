package models;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class Commons {
    public static String NAME = "name";
    public static String CODES = "Codes";
    public static String BRANDS = "Brands";
    public static String SECTOR = "sector";
    public static String PRIORITY = "priority";
    public static String LOGO = "logo";
    public static String INFO = "info";
    public static String CLICKS = "clicks";
    public static String TELCO = "telco";
    public static String TV = "tv";
    public static String BANKING = "banking";
    public static String HEALTH = "health";
    public static String SECURITY = "law";
    public static String SPORTS = "sports";
    public static String BRANDTAG = "brandTag";
    public static String COUNTRY = "country";
    public static String LAST_FRAGMENT = "";
    public static final String FRAG_HOME = "fragmentHome";
    public static final String FRAG_FAVORITE = "fragmentFav";
    public static final String FRAG_HISTORY = "fragmentHistory";

    private static FirebaseFirestore database;
    private static FirebaseStorage storage;

    private static CollectionReference brandReference;
    private static CollectionReference codeReference;

    public static void share(String code, String purpose, Context context){

        String output = purpose + ": "+code + "\n\nDownload Dailup app for all USSD codes: https://play.google.com/store/apps/details?https://play.google.com/store/apps/details?com.solojet.dialup" ;

        Intent share = new Intent(Intent.ACTION_SEND);share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, output);
        share.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(Intent.createChooser(share, "Share via:"));
    }

    public static void actionButtonOnClick(Context context, Code model){
        switch (model.getAction()){
            case "call":
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", model.getCode(), null));
                context.startActivity(intent);
                break;
            case "sms":
                Intent intentMessage = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", model.getTo(), null));
                intentMessage.putExtra("sms_body",model.getMessage());
                context.startActivity(intentMessage);
                break;
        }
    }

    public static void copyToClipboard(Context context, String code){
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied code", code);
        manager.setPrimaryClip(clip);
    }

    public static FirebaseFirestore getDatabase() {
        if(database==null)
            database = FirebaseFirestore.getInstance();
        return database;
    }

    public static FirebaseStorage getStorage() {
        if(storage==null)
            storage = FirebaseStorage.getInstance();
        return storage;
    }

    public  static  boolean getNetworkAvailability(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public static CollectionReference getBrandReference(String country) {
        brandReference = getDatabase().collection(country+"_"+BRANDS);
        return brandReference;
    }

    public static CollectionReference getCodeReference(String country) {
        codeReference = getDatabase().collection(country+"_"+CODES);
        return codeReference;
    }
}
