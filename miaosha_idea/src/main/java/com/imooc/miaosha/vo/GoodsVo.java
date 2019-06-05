package com.imooc.miaosha.vo;

import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

//这里的类都是用来组成返回结果的,跟domain类要区分开
public class GoodsVo extends Goods {
    private Double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Double getMiaoshaPrice() {
        return miaoshaPrice;
    }

    public void setMiaoshaPrice(Double miaoshaPrice) {
        this.miaoshaPrice = miaoshaPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
