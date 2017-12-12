package com.xiaolian.amigo.data.network.model.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.xiaolian.amigo.data.network.model.bonus.Bonus;

import java.io.Serializable;

import lombok.Data;

/**
 * 订单预备信息
 * <p>
 * Created by zcd on 10/13/17.
 */
@Data
public class OrderPreInfoDTO implements Parcelable {

    /**
     * 代金券
     */
    Bonus bonus;
    /*Order*
     * 预付金额
     */
    Double prepay;
    /**
     * 最小预付金额
     */
    Double minPrepay;
    /**
     * 余额
     */
    Double balance;
    /**
     * 客服电话
     */
    String csMobile;

    public OrderPreInfoDTO() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.bonus, flags);
        dest.writeValue(this.prepay);
        dest.writeValue(this.minPrepay);
        dest.writeValue(this.balance);
        dest.writeString(this.csMobile);
    }

    protected OrderPreInfoDTO(Parcel in) {
        this.bonus = in.readParcelable(Bonus.class.getClassLoader());
        this.prepay = (Double) in.readValue(Double.class.getClassLoader());
        this.minPrepay = (Double) in.readValue(Double.class.getClassLoader());
        this.balance = (Double) in.readValue(Double.class.getClassLoader());
        this.csMobile = in.readString();
    }

    public static final Parcelable.Creator<OrderPreInfoDTO> CREATOR = new Parcelable.Creator<OrderPreInfoDTO>() {
        @Override
        public OrderPreInfoDTO createFromParcel(Parcel source) {
            return new OrderPreInfoDTO(source);
        }

        @Override
        public OrderPreInfoDTO[] newArray(int size) {
            return new OrderPreInfoDTO[size];
        }
    };
}
