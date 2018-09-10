package test.spider.pojo;

import java.sql.Blob;
import java.util.List;

public class Brand {
    
    //type:"mb",activity:null,id:92,name:"°¢¶û·¨¡¤ÂÞÃÜÅ·",url:"/tree_chexing/mb_92/",cur:0,num:5
    
    private String type;
    
    private String activity;
    
    private Integer id;
    
    private Integer cur;
    
    private Integer num;
    
    private String url;
    
    private String name;
    
    private byte[] image;
    
    private Blob image1;
    
    private List<Series> seriesList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCur() {
        return cur;
    }

    public void setCur(Integer cur) {
        this.cur = cur;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Series> getSeriesList() {
        return seriesList;
    }

    public void setSeriesList(List<Series> seriesList) {
        this.seriesList = seriesList;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Blob getImage1() {
        return image1;
    }

    public void setImage1(Blob image1) {
        this.image1 = image1;
    }

}
