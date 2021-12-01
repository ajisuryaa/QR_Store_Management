package com.putrabatam.qrstore.controller;

public class Log_Activity {
    String id_material, scan_type, datetime;
    String quantity;

    public Log_Activity(String id_material, String scan_type, String quantity, String datetime){
        this.id_material = id_material;
        this.scan_type = scan_type;
        this.quantity = quantity;
        this.datetime = datetime;
    }

    public String getId_material() {
        return id_material;
    }

    public String getScan_type() {
        return scan_type;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDatetime() {
        return datetime;
    }
}
