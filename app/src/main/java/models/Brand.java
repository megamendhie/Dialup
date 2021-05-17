package models;

import android.os.Parcel;
import android.os.Parcelable;

public class Brand implements Parcelable{
    private String id;
    private String logo;
    private String name;
    private String brandTag;
    private String sector;
    private String info;
    private long clicks;

    public Brand(){}

    protected Brand(Parcel in) {
        id = in.readString();
        logo = in.readString();
        name = in.readString();
        brandTag = in.readString();
        sector = in.readString();
        info = in.readString();
        clicks = in.readLong();
    }

    public static final Creator<Brand> CREATOR = new Creator<Brand>() {
        @Override
        public Brand createFromParcel(Parcel in) {
            return new Brand(in);
        }

        @Override
        public Brand[] newArray(int size) {
            return new Brand[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public long getClicks() {
        return clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrandTag() {
        return brandTag;
    }

    public void setBrandTag(String brandTag) {
        this.brandTag = brandTag;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(logo);
        dest.writeString(name);
        dest.writeString(brandTag);
        dest.writeString(sector);
        dest.writeString(info);
        dest.writeLong(clicks);
    }
}
