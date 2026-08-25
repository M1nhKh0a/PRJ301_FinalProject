package model;

import java.util.Date;

public class Order {
    private String orderID;
    private Date orderDate;
    private String customer;
    private String address;
    private double totalAmount;
    private int status; // 0: Newly created, 1: Pending (Order in delivery), 2: Delivered, 3: Rejected

    public Order() {
    }

    public Order(String orderID, Date orderDate, String customer, String address, double totalAmount, int status) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.customer = customer;
        this.address = address;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusString() {
        switch (status) {
            case 0:
                return "Newly created";
            case 1:
                return "Pending (Order in delivery)";
            case 2:
                return "Delivered";
            case 3:
                return "Rejected";
            default:
                return "Unknown";
        }
    }
}
