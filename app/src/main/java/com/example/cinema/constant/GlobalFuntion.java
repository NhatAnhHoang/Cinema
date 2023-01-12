package com.example.cinema.constant;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.cinema.activity.MainActivity;
import com.example.cinema.activity.admin.AdminMainActivity;
import com.example.cinema.listener.IGetDateListener;
import com.example.cinema.model.RoomFirebase;
import com.example.cinema.model.Seat;
import com.example.cinema.model.TimeFirebase;
import com.example.cinema.prefs.DataStoreManager;
import com.example.cinema.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GlobalFuntion {

    public static void startActivity(Context context, Class<?> clz) {
        Intent intent = new Intent(context, clz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(context, clz);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void showSoftKeyboard(Activity activity, EditText editText) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void gotoMainActivity(Context context) {
        if (DataStoreManager.getUser().isAdmin()) {
            GlobalFuntion.startActivity(context, AdminMainActivity.class);
        } else {
            GlobalFuntion.startActivity(context, MainActivity.class);
        }
    }

    public static List<RoomFirebase> getListRooms() {
        List<RoomFirebase> list = new ArrayList<>();
        list.add(new RoomFirebase(1, "Phòng 1", getListTimes()));
        list.add(new RoomFirebase(2, "Phòng 2", getListTimes()));
        list.add(new RoomFirebase(3, "Phòng 3", getListTimes()));
        list.add(new RoomFirebase(4, "Phòng 4", getListTimes()));
        list.add(new RoomFirebase(5, "Phòng 5", getListTimes()));
        list.add(new RoomFirebase(6, "Phòng 6", getListTimes()));
        return list;
    }

    public static List<TimeFirebase> getListTimes() {
        List<TimeFirebase> list = new ArrayList<>();
        list.add(new TimeFirebase(1, "7AM - 8AM", getListSeats()));
        list.add(new TimeFirebase(2, "8AM - 9AM", getListSeats()));
        list.add(new TimeFirebase(3, "9AM - 10AM", getListSeats()));
        list.add(new TimeFirebase(4, "10AM - 11AM", getListSeats()));
        list.add(new TimeFirebase(5, "1PM - 2PM", getListSeats()));
        list.add(new TimeFirebase(6, "2PM - 3PM", getListSeats()));
        return list;
    }

    public static List<Seat> getListSeats() {
        List<Seat> list = new ArrayList<>();
        list.add(new Seat(1, "1", false));
        list.add(new Seat(2, "2", false));
        list.add(new Seat(3, "3", false));
        list.add(new Seat(4, "4", false));
        list.add(new Seat(5, "5", false));
        list.add(new Seat(6, "6", false));
        list.add(new Seat(7, "7", false));
        list.add(new Seat(8, "8", false));
        list.add(new Seat(9, "9", false));
        list.add(new Seat(10, "10", false));
        list.add(new Seat(11, "11", false));
        list.add(new Seat(12, "12", false));
        list.add(new Seat(13, "13", false));
        list.add(new Seat(14, "14", false));
        list.add(new Seat(15, "15", false));
        list.add(new Seat(16, "16", false));
        list.add(new Seat(17, "17", false));
        list.add(new Seat(18, "18", false));
        return list;
    }

    public static void showDatePicker(Context context, final IGetDateListener getDateListener) {
        Calendar mCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener callBack = (view, year, monthOfYear, dayOfMonth) -> {
            String date = StringUtil.getDoubleNumber(dayOfMonth) + "-" + StringUtil.getDoubleNumber(monthOfYear + 1) + "-" + year;
            getDateListener.getDate(date);
        };
        DatePickerDialog datePicker = new DatePickerDialog(context,
                callBack, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DATE));
        datePicker.show();
    }

    public static String getStringEmailUser() {
        return DataStoreManager.getUser().getEmail()
                .replace("@", "aa")
                .replace(".", "dot");
    }
}
