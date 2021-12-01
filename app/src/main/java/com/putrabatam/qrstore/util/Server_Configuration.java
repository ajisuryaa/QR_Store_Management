package com.putrabatam.qrstore.util;

public class Server_Configuration {
    //local_server
    public static final String address_server = "http://192.168.163.58/API_STORE_MANAGEMENT/view/";
    public static final String address_image = "http://192.168.163.58/API_STORE_MANAGEMENT/";

    //hosting_server
//    public static final String address_server = "https://qrstoreapps.000webhostapp.com/view/";
//    public static final String address_image = "https://qrstoreapps.000webhostapp.com/";

    public static final String address_login = address_server + "login.php";

    public static final String address_add_new_employee = address_server + "add_new_employee.php";
    public static final String address_update_employee = address_server + "update_employee.php";
    public static final String address_delete_employee = address_server + "delete_employee.php";
    public static final String address_get_list_employee = address_server + "show_list_employee.php";

    public static final String address_add_new_material = address_server + "add_new_material.php";
    public static final String address_update_material = address_server + "update_material.php";
    public static final String address_delete_new_material = address_server + "delete_material.php";
    public static final String address_get_list_material = address_server + "show_list_material.php";
    public static final String address_scan_in_material = address_server + "scan_in_material.php";
    public static final String address_scan_out_material = address_server + "scan_out_material.php";

    public static final String address_get_list_log_activity = address_server + "show_log_activity_employee.php";
}
