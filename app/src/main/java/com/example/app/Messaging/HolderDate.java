package com.example.app.Messaging;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;

/**
 * Created by Dytstudio.
 */

public class HolderDate extends RecyclerView.ViewHolder {

    private TextView date;

    public HolderDate(View v) {
        super(v);
        date = (TextView) v.findViewById(R.id.otherOptions);
    }

    public TextView getDate() {
        return date;
    }

    public void setDate(TextView date) {
        this.date = date;
    }
}
