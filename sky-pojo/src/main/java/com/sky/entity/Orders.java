package com.sky.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName orders
 */
@TableName(value ="orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable {
    private Long id;

    private String number;

    private Integer status;

    private Long userId;

    private Long addressBookId;

    private LocalDateTime orderTime;

    private LocalDateTime checkoutTime;

    private Integer payMethod;

    private Integer payStatus;

    private BigDecimal amount;

    private String remark;

    private String phone;

    private String address;

    private String userName;

    private String consignee;

    private String cancelReason;

    private String rejectionReason;

    private LocalDateTime cancelTime;

    private LocalDateTime estimatedDeliveryTime;

    private Integer deliveryStatus;

    private LocalDateTime deliveryTime;

    private Integer packAmount;

    private Integer tablewareNumber;

    private Integer tablewareStatus;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Orders other = (Orders) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getNumber() == null ? other.getNumber() == null : this.getNumber().equals(other.getNumber()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getAddressBookId() == null ? other.getAddressBookId() == null : this.getAddressBookId().equals(other.getAddressBookId()))
            && (this.getOrderTime() == null ? other.getOrderTime() == null : this.getOrderTime().equals(other.getOrderTime()))
            && (this.getCheckoutTime() == null ? other.getCheckoutTime() == null : this.getCheckoutTime().equals(other.getCheckoutTime()))
            && (this.getPayMethod() == null ? other.getPayMethod() == null : this.getPayMethod().equals(other.getPayMethod()))
            && (this.getPayStatus() == null ? other.getPayStatus() == null : this.getPayStatus().equals(other.getPayStatus()))
            && (this.getAmount() == null ? other.getAmount() == null : this.getAmount().equals(other.getAmount()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
            && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
            && (this.getConsignee() == null ? other.getConsignee() == null : this.getConsignee().equals(other.getConsignee()))
            && (this.getCancelReason() == null ? other.getCancelReason() == null : this.getCancelReason().equals(other.getCancelReason()))
            && (this.getRejectionReason() == null ? other.getRejectionReason() == null : this.getRejectionReason().equals(other.getRejectionReason()))
            && (this.getCancelTime() == null ? other.getCancelTime() == null : this.getCancelTime().equals(other.getCancelTime()))
            && (this.getEstimatedDeliveryTime() == null ? other.getEstimatedDeliveryTime() == null : this.getEstimatedDeliveryTime().equals(other.getEstimatedDeliveryTime()))
            && (this.getDeliveryStatus() == null ? other.getDeliveryStatus() == null : this.getDeliveryStatus().equals(other.getDeliveryStatus()))
            && (this.getDeliveryTime() == null ? other.getDeliveryTime() == null : this.getDeliveryTime().equals(other.getDeliveryTime()))
            && (this.getPackAmount() == null ? other.getPackAmount() == null : this.getPackAmount().equals(other.getPackAmount()))
            && (this.getTablewareNumber() == null ? other.getTablewareNumber() == null : this.getTablewareNumber().equals(other.getTablewareNumber()))
            && (this.getTablewareStatus() == null ? other.getTablewareStatus() == null : this.getTablewareStatus().equals(other.getTablewareStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getNumber() == null) ? 0 : getNumber().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getAddressBookId() == null) ? 0 : getAddressBookId().hashCode());
        result = prime * result + ((getOrderTime() == null) ? 0 : getOrderTime().hashCode());
        result = prime * result + ((getCheckoutTime() == null) ? 0 : getCheckoutTime().hashCode());
        result = prime * result + ((getPayMethod() == null) ? 0 : getPayMethod().hashCode());
        result = prime * result + ((getPayStatus() == null) ? 0 : getPayStatus().hashCode());
        result = prime * result + ((getAmount() == null) ? 0 : getAmount().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getConsignee() == null) ? 0 : getConsignee().hashCode());
        result = prime * result + ((getCancelReason() == null) ? 0 : getCancelReason().hashCode());
        result = prime * result + ((getRejectionReason() == null) ? 0 : getRejectionReason().hashCode());
        result = prime * result + ((getCancelTime() == null) ? 0 : getCancelTime().hashCode());
        result = prime * result + ((getEstimatedDeliveryTime() == null) ? 0 : getEstimatedDeliveryTime().hashCode());
        result = prime * result + ((getDeliveryStatus() == null) ? 0 : getDeliveryStatus().hashCode());
        result = prime * result + ((getDeliveryTime() == null) ? 0 : getDeliveryTime().hashCode());
        result = prime * result + ((getPackAmount() == null) ? 0 : getPackAmount().hashCode());
        result = prime * result + ((getTablewareNumber() == null) ? 0 : getTablewareNumber().hashCode());
        result = prime * result + ((getTablewareStatus() == null) ? 0 : getTablewareStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", number=").append(number);
        sb.append(", status=").append(status);
        sb.append(", userId=").append(userId);
        sb.append(", addressBookId=").append(addressBookId);
        sb.append(", orderTime=").append(orderTime);
        sb.append(", checkoutTime=").append(checkoutTime);
        sb.append(", payMethod=").append(payMethod);
        sb.append(", payStatus=").append(payStatus);
        sb.append(", amount=").append(amount);
        sb.append(", remark=").append(remark);
        sb.append(", phone=").append(phone);
        sb.append(", address=").append(address);
        sb.append(", userName=").append(userName);
        sb.append(", consignee=").append(consignee);
        sb.append(", cancelReason=").append(cancelReason);
        sb.append(", rejectionReason=").append(rejectionReason);
        sb.append(", cancelTime=").append(cancelTime);
        sb.append(", estimatedDeliveryTime=").append(estimatedDeliveryTime);
        sb.append(", deliveryStatus=").append(deliveryStatus);
        sb.append(", deliveryTime=").append(deliveryTime);
        sb.append(", packAmount=").append(packAmount);
        sb.append(", tablewareNumber=").append(tablewareNumber);
        sb.append(", tablewareStatus=").append(tablewareStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}