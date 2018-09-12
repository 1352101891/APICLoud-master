package com.yunbao.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by cxf on 2017/7/14.
 */

public class CommentBean implements Parcelable {

    /**
     * id : 1
     * uid : 11163
     * touid : 8209
     * videoid : 9
     * commentid : 0
     * parentid : 0
     * content : dddd
     * addtime : 1501924416
     * userinfo : {"id":"11163","user_nicename":"张小凡","avatar":"http://q.qlogo.cn/qqapp/100371282/D9337345784F5FD6BCD8073480BA1F85/40","coin":"60","avatar_thumb":"http://q.qlogo.cn/qqapp/100371282/D9337345784F5FD6BCD8073480BA1F85/40","sex":"2","signature":"这家伙很懒，什么都没留下","consumption":"0","votestotal":"0","province":"","city":"泰安市","birthday":"","issuper":"0","level":"1","level_anchor":"1","vip":{"type":"0"},"liang":{"name":"0"}}
     * datetime : 2分钟前
     */



    private String id;
    private String uid;
    private String touid;
    private String videoid;
    private String commentid;
    private String parentid;
    private String content;
    private String addtime;
    private UserBean userinfo;
    private String likes;
    private int replys;
    private String datetime;
    private UserBean touserinfo;
    private ToCommentInfo tocommentinfo;
    private int islike;

    private String dialectid;
    private String user_nicename;
    private String avatar;
    private String avatar_thumb;

    public String getDialectid() {
        return dialectid;
    }

    public void setDialectid(String dialectid) {
        this.dialectid = dialectid;
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

    public CommentBean(){

    }

    public int getIslike() {
        return islike;
    }

    public void setIslike(int islike) {
        this.islike = islike;
    }

    public ToCommentInfo getTocommentinfo() {
        return tocommentinfo;
    }

    public void setTocommentinfo(ToCommentInfo tocommentinfo) {
        this.tocommentinfo = tocommentinfo;
    }

    public int getReplys() {
        return replys;
    }

    public void setReplys(int replys) {
        this.replys = replys;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTouid() {
        return touid;
    }

    public void setTouid(String touid) {
        this.touid = touid;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public UserBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserBean userinfo) {
        this.userinfo = userinfo;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public UserBean getTouserinfo() {
        return touserinfo;
    }

    public void setTouserinfo(UserBean touserinfo) {
        this.touserinfo = touserinfo;
    }


    public static  class ToCommentInfo implements Parcelable{
        public ToCommentInfo(){

        }
        private String content;

        protected ToCommentInfo(Parcel in) {
            content = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(content);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ToCommentInfo> CREATOR = new Creator<ToCommentInfo>() {
            @Override
            public ToCommentInfo createFromParcel(Parcel in) {
                return new ToCommentInfo(in);
            }

            @Override
            public ToCommentInfo[] newArray(int size) {
                return new ToCommentInfo[size];
            }
        };

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.touid);
        dest.writeString(this.videoid);
        dest.writeString(this.commentid);
        dest.writeString(this.parentid);
        dest.writeString(this.content);
        dest.writeString(this.addtime);
        dest.writeParcelable(this.userinfo, flags);
        dest.writeString(this.likes);
        dest.writeInt(this.replys);
        dest.writeString(this.datetime);
        dest.writeParcelable(this.touserinfo, flags);
        dest.writeParcelable(this.tocommentinfo, flags);
        dest.writeInt(this.islike);
        dest.writeString(this.dialectid);
        dest.writeString(this.user_nicename);
        dest.writeString(this.avatar);
        dest.writeString(this.avatar_thumb);
    }



    protected CommentBean(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.touid = in.readString();
        this.videoid = in.readString();
        this.commentid = in.readString();
        this.parentid = in.readString();
        this.content = in.readString();
        this.addtime = in.readString();
        this.userinfo = in.readParcelable(UserBean.class.getClassLoader());
        this.likes = in.readString();
        this.replys = in.readInt();
        this.datetime = in.readString();
        this.touserinfo = in.readParcelable(UserBean.class.getClassLoader());
        this.tocommentinfo = in.readParcelable(ToCommentInfo.class.getClassLoader());
        this.islike = in.readInt();
        this.dialectid = in.readString();
        this.user_nicename = in.readString();
        this.avatar = in.readString();
        this.avatar_thumb = in.readString();
    }

    public static final Creator<CommentBean> CREATOR = new Creator<CommentBean>() {
        @Override
        public CommentBean createFromParcel(Parcel source) {
            return new CommentBean(source);
        }

        @Override
        public CommentBean[] newArray(int size) {
            return new CommentBean[size];
        }
    };
}
