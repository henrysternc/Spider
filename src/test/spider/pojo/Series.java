package test.spider.pojo;

import java.util.List;

public class Series {
    
    private Integer id;
    
    private Integer brandId;
    
    private String brandName;//Ʒ������
    
    private String seriesName;//ϵ�����ƣ�
    
    private String seriesUrl;
    
    private List<Model> models;//�ͺ��б�
    
    private List<String> unsaleList;
    
    private boolean status;//�Ƿ���ȡ��

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getSeriesUrl() {
        return seriesUrl;
    }

    public void setSeriesUrl(String seriesUrl) {
        this.seriesUrl = seriesUrl;
    }

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<String> getUnsaleList() {
        return unsaleList;
    }

    public void setUnsaleList(List<String> unsaleList) {
        this.unsaleList = unsaleList;
    }

}
