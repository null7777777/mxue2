package model;

import java.util.Date;
import java.util.Map;

public class Tea {
    private Integer teaId;        // 饮品ID
    private Integer catalogId;    // 分类ID
    private String teaName;       // 饮品名称
    private Double price;         // 价格
    private String description;   // 描述
    private Integer imgId;        // 图片ID
    private Date addTime;         // 添加时间
    
    // 关联字段
    private String catalogName;   // 分类名称
    private String imgName;       // 图片名称
    private String imgSrc;        // 图片路径
    private String imgType;       // 图片类型

    public Tea() {}

    public Tea(Map<String, Object> map) {
        // 基本字段，使用安全的类型转换
        if (map.get("teaId") != null) {
            this.teaId = ((Number) map.get("teaId")).intValue();
        }
        if (map.get("catalogId") != null) {
            this.catalogId = ((Number) map.get("catalogId")).intValue();
        }
        this.teaName = (String) map.get("teaName");
        if (map.get("price") != null) {
            this.price = ((Number) map.get("price")).doubleValue();
        }
        this.description = (String) map.get("description");
        if (map.get("imgId") != null) {
            this.imgId = ((Number) map.get("imgId")).intValue();
        }
        this.addTime = (Date) map.get("addTime");
        
        // 关联字段
        this.catalogName = (String) map.get("catalogName");
        this.imgName = (String) map.get("imgName");
        this.imgSrc = (String) map.get("imgSrc");
        this.imgType = (String) map.get("imgType");
    }

    // Getters and Setters
    public Integer getTeaId() {
        return teaId;
    }

    public void setTeaId(Integer teaId) {
        this.teaId = teaId;
    }

    public Integer getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId) {
        this.catalogId = catalogId;
    }

    public String getTeaName() {
        return teaName;
    }

    public void setTeaName(String teaName) {
        this.teaName = teaName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getImgId() {
        return imgId;
    }

    public void setImgId(Integer imgId) {
        this.imgId = imgId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

    @Override
    public String toString() {
        return "Tea{" +
                "teaId=" + teaId +
                ", catalogId=" + catalogId +
                ", teaName='" + teaName + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imgId=" + imgId +
                ", addTime=" + addTime +
                ", catalogName='" + catalogName + '\'' +
                ", imgName='" + imgName + '\'' +
                ", imgSrc='" + imgSrc + '\'' +
                ", imgType='" + imgType + '\'' +
                '}';
    }
}