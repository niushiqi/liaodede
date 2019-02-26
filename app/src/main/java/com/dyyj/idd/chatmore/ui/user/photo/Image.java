package com.dyyj.idd.chatmore.ui.user.photo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *图片实体类
 */
public class Image implements Parcelable {

    private int type;
    private String path;
    private long time;
    private String name;
    private int ori;

    public int getOri() {
        return ori;
    }

    public void setOri(int ori) {
        this.ori = ori;
    }

    public Image(String path, long time, String name, int ori) {
        this.path = path;
        this.time = time;
        this.name = name;
        this.type = 0;
        this.ori = ori;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeLong(this.time);
        dest.writeString(this.name);
        dest.writeInt(this.type);
    }

    protected Image(Parcel in) {
        this.path = in.readString();
        this.time = in.readLong();
        this.name = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
