package fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.solojet.dialup.AboutActivity;
import com.solojet.dialup.BrandActivity;
import com.solojet.dialup.R;

import static models.Commons.BANKING;
import static models.Commons.HEALTH;
import static models.Commons.SECURITY;
import static models.Commons.SECTOR;
import static models.Commons.SPORTS;
import static models.Commons.TELCO;
import static models.Commons.TV;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FrameLayout crdTelco = view.findViewById(R.id.crdTelco);
        crdTelco.setOnClickListener(this);
        FrameLayout crdTv = view.findViewById(R.id.crdTv);
        crdTv.setOnClickListener(this);
        FrameLayout crdBanking = view.findViewById(R.id.crdBanking);
        crdBanking.setOnClickListener(this);
        FrameLayout crdHealth = view.findViewById(R.id.crdHealth);
        crdHealth.setOnClickListener(this);
        FrameLayout crdLaw = view.findViewById(R.id.crdSecurity);
        crdLaw.setOnClickListener(this);
        FrameLayout crdSports = view.findViewById(R.id.crdSports);
        crdSports.setOnClickListener(this);

        LinearLayout lnrAbout = view.findViewById(R.id.lnrAbout);
        lnrAbout.setOnClickListener(this);
        LinearLayout lnrRate = view.findViewById(R.id.lnrRate);
        lnrRate.setOnClickListener(this);
        LinearLayout lnrShare = view.findViewById(R.id.lnrShare);
        lnrShare.setOnClickListener(this);
        LinearLayout lnrSettings = view.findViewById(R.id.lnrSettings);
        lnrSettings.setOnClickListener(view1 -> showPrompt());

        return view;
    }

    private void showPrompt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),
                R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setMessage("Glad you clicked here. The settings option would be added in the next version. Also, the app would expand from Nigeria only to 4 new countries. You will be notified." +
                "\n\nIn case you have not rated the app yet, kindly do so.")
                .setPositiveButton("Okay", (dialogInterface, i) -> { })
                .show();
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch (view.getId()){
            case R.id.crdTelco:
                intent = new Intent(getContext(), BrandActivity.class);
                intent.putExtra(SECTOR, TELCO);
                break;
            case R.id.crdTv:
                intent = new Intent(getContext(), BrandActivity.class);
                intent.putExtra(SECTOR, TV);
                break;
            case R.id.crdBanking:
                intent = new Intent(getContext(), BrandActivity.class);
                intent.putExtra(SECTOR, BANKING);
                break;
            case R.id.crdHealth:
                intent = new Intent(getContext(), BrandActivity.class);
                intent.putExtra(SECTOR, HEALTH);
                break;
            case R.id.crdSecurity:
                intent = new Intent(getContext(), BrandActivity.class);
                intent.putExtra(SECTOR, SECURITY);
                break;
            case R.id.crdSports:
                intent = new Intent(getContext(), BrandActivity.class);
                intent.putExtra(SECTOR, SPORTS);
                break;
            case R.id.lnrAbout:
                intent = new Intent(getContext(), AboutActivity.class);
                break;
            case R.id.lnrRate:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.solojet.dialup"));
                break;
            case R.id.lnrShare:
                String output = "Download Dialup app to quickly dial any USSD code or emergency number:\n\n https://play.google.com/store/apps/details?id=com.solojet.dialup" ;
                intent = new Intent(Intent.ACTION_SEND); intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, output);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                break;
        }
        startActivity(intent);
    }

}
