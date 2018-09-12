package com.yunbao.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cxf on 2017/8/14.
 */

public class UserBean implements Parcelable {
    private String id;
    private String user_nicename;
    private String avatar;
    private String avatar_thumb;
    private int sex;
    private String signature;
    private int coin;
    private String votes;
    private String consumption;
    private String votestotal;
    private String province;
    private String city;
    private String birthday;
    private int level;
    private int level_anchor;
    private String lives;
    private String follows;
    private String fans;
    private Vip vip;
    private Liang liang;
    private Car car;
    private String mobile;
    private String qqnum;
    private String hometown;
    private String nowaddress;
    private String age;
    private String level_icon;
    private String level_anchor_icon;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getConsumption() {
        return consumption;
    }

    public void setConsumption(String consumption) {
        this.consumption = consumption;
    }

    public String getVotestotal() {
        return votestotal;
    }

    public void setVotestotal(String votestotal) {
        this.votestotal = votestotal;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel_anchor() {
        return level_anchor;
    }

    public void setLevel_anchor(int level_anchor) {
        this.level_anchor = level_anchor;
    }

    public String getLives() {
        return lives;
    }

    public void setLives(String lives) {
        this.lives = lives;
    }

    public String getFollows() {
        return follows;
    }

    public void setFollows(String follows) {
        this.follows = follows;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public Vip getVip() {
        return vip;
    }

    public void setVip(Vip vip) {
        this.vip = vip;
    }

    public Liang getLiang() {
        return liang;
    }

    public void setLiang(Liang liang) {
        this.liang = liang;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getQqnum() {
        return qqnum;
    }

    public void setQqnum(String qqnum) {
        this.qqnum = qqnum;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getNowaddress() {
        return nowaddress;
    }

    public void setNowaddress(String nowaddress) {
        this.nowaddress = nowaddress;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLevel_icon() {
        return level_icon;
    }

    public void setLevel_icon(String level_icon) {
        this.level_icon = level_icon;
    }

    public String getLevel_anchor_icon() {
        return level_anchor_icon;
    }

    public void setLevel_anchor_icon(String level_anchor_icon) {
        this.level_anchor_icon = level_anchor_icon;
    }

    public UserBean() {
    }

    public UserBean(Parcel in) {
        this.id = in.readString();
        this.user_nicename = in.readString();
        this.avatar = in.readString();
        this.avatar_thumb = in.readString();
        this.sex = in.readInt();
        this.signature = in.readString();
        this.coin = in.readInt();
        this.votes = in.readString();
        this.consumption = in.readString();
        this.votestotal = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.birthday = in.readString();
        this.level = in.readInt();
        this.level_anchor = in.readInt();
        this.lives = in.readString();
        this.follows = in.readString();
        this.fans = in.readString();
        this.vip = in.readParcelable(Vip.class.getClassLoader());
        this.liang = in.readParcelable(Liang.class.getClassLoader());
        this.car = in.readParcelable(Car.class.getClassLoader());
        this.mobile=in.readString();
        this.qqnum=in.readString();
        this.hometown=in.readString();
        this.nowaddress=in.readString();
        this.age=in.readString();
        this.level_icon=in.readString();
        this.level_anchor_icon=in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.user_nicename);
        dest.writeString(this.avatar);
        dest.writeString(this.avatar_thumb);
        dest.writeInt(this.sex);
        dest.writeString(this.signature);
        dest.writeInt(this.coin);
        dest.writeString(this.votes);
        dest.writeString(this.consumption);
        dest.writeString(this.votestotal);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.birthday);
        dest.writeInt(this.level);
        dest.writeInt(this.level_anchor);
        dest.writeString(this.lives);
        dest.writeString(this.follows);
        dest.writeString(this.fans);
        dest.writeParcelable(this.vip, flags);
        dest.writeParcelable(this.liang, flags);
        dest.writeParcelable(this.car, flags);
        dest.writeString(this.mobile);
        dest.writeString(this.qqnum);
        dest.writeString(this.hometown);
        dest.writeString(this.nowaddress);
        dest.writeString(this.age);
        dest.writeString(this.level_icon);
        dest.writeString(this.level_anchor_icon);
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }

        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }
    };


    public static class Vip implements Parcelable {
        private int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public Vip() {

        }

        public Vip(Parcel in) {
            this.type = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.type);
        }

        public static final Creator<Vip> CREATOR = new Creator<Vip>() {
            @Override
            public Vip[] newArray(int size) {
                return new Vip[size];
            }

            @Override
            public Vip createFromParcel(Parcel in) {
                return new Vip(in);
            }
        };
    }

    public static class Liang implements Parcelable {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Liang() {

        }

        public Liang(Parcel in) {
            this.name = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
        }

        public static final Creator<Liang> CREATOR = new Creator<Liang>() {

            @Override
            public Liang createFromParcel(Parcel in) {
                return new Liang(in);
            }

            @Override
            public Liang[] newArray(int size) {
                return new Liang[size];
            }
        };

    }

    public static class Car implements Parcelable {
        private int id;
        private String swf;
        private float swftime;
        private String words;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSwf() {
            return swf;
        }

        public void setSwf(String swf) {
            this.swf = swf;
        }

        public float getSwftime() {
            return swftime;
        }

        public void setSwftime(float swftime) {
            this.swftime = swftime;
        }

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }

        public Car() {

        }

        public Car(Parcel in) {
            this.id = in.readInt();
            this.swf = in.readString();
            this.swftime = in.readFloat();
            this.words = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.swf);
            dest.writeFloat(this.swftime);
            dest.writeString(this.words);
        }


        public static final Creator<Car> CREATOR = new Creator<Car>() {
            @Override
            public Car[] newArray(int size) {
                return new Car[size];
            }

            @Override
            public Car createFromParcel(Parcel in) {
                return new Car(in);
            }
        };

    }


}
