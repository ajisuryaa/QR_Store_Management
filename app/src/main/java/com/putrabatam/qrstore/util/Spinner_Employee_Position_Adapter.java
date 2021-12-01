package com.putrabatam.qrstore.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.putrabatam.qrstore.R;

public class Spinner_Employee_Position_Adapter extends BaseAdapter {
    Context context;
    String[] PositionNames;
    LayoutInflater inflter;

    public Spinner_Employee_Position_Adapter(Context applicationContext, String[] countryNames) {
        this.context = applicationContext;
        this.PositionNames = countryNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return PositionNames.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_spinner_value_position_employee, null);
        TextView names = (TextView) view.findViewById(R.id.spv_position_employee_name);
        names.setText(PositionNames[i]);
        return view;
    }
}
