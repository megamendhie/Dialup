package holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.solojet.dialup.R;

public class BrandViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgLogo, imgAction;
    public TextView txtBrand;
    public BrandViewHolder(@NonNull View itemView) {
        super(itemView);

        imgLogo = itemView.findViewById(R.id.imgLogo);
        imgAction = itemView.findViewById(R.id.imgAction);
        txtBrand = itemView.findViewById(R.id.txtBrand);
    }
}
