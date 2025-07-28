package com.alliance.palletkvalproject.Model;

public class lineItem{
    public String mo;
    public String lineNumber;
    public String doorSize;
    public String orderNumber;
    public String customer;
    int quantity;

    public lineItem(String mo, String lineNumber, String doorSize, String orderNumber, String customer, int quantity) {
        this.mo = mo;
        this.lineNumber = lineNumber;
        this.doorSize = doorSize;
        this.quantity = quantity;
        this.orderNumber = orderNumber;
        this.customer = customer;
    }
    public String getMo() {
        return mo;
    }
    public String getLineNumber() {
        return lineNumber;
    }
    public String getOrderNumber() {
        return orderNumber;
    }
    public String getDoorSize() {
        return doorSize;
    }

    public int getQuantity() {
        return quantity;
    }

    public String itemToString(){
        return "\nMO: " + this.mo + " \nLINE: " + this.lineNumber + " \nDOOR: " + this.doorSize + " \nQUANTITY: " + this.quantity +  "\nORDER NUMBER: " + this.orderNumber + " \nCUSTOMER: " + this.customer;
    }

    public String itemGetCustomer(){
        return this.customer;
    }
}