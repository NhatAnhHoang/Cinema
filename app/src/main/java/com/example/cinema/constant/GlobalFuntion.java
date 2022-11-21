package com.example.cinema.constant;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cinema.listener.IGetDateListener;
import com.example.cinema.model.Room;
import com.example.cinema.model.SlotTime;
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

    public static void hideSoftKeyboard(Activity activity, TextView textView) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static List<Room> getListRooms() {
        List<Room> list = new ArrayList<>();
        list.add(new Room(1, "Phòng 1", true));
        list.add(new Room(2, "Phòng 2", false));
        list.add(new Room(3, "Phòng 3", false));
        list.add(new Room(4, "Phòng 4", false));
        list.add(new Room(5, "Phòng 5", false));
        list.add(new Room(6, "Phòng 6", false));
        list.add(new Room(7, "Phòng 7", false));
        list.add(new Room(8, "Phòng 8", false));
        list.add(new Room(9, "Phòng 9", false));
        list.add(new Room(10, "Phòng 10", false));
        list.add(new Room(11, "Phòng 11", false));
        list.add(new Room(12, "Phòng 12", false));
        return list;
    }

    public static List<SlotTime> getListSlotTimes() {
        List<SlotTime> list = new ArrayList<>();
        list.add(new SlotTime(1, "7AM - 8AM", true));
        list.add(new SlotTime(2, "8AM - 9AM", false));
        list.add(new SlotTime(3, "9AM - 10AM", false));
        list.add(new SlotTime(4, "10AM - 11AM", false));
        list.add(new SlotTime(5, "1PM - 2PM", false));
        list.add(new SlotTime(6, "2PM - 3PM", false));
        list.add(new SlotTime(7, "3PM - 4PM", false));
        list.add(new SlotTime(8, "4PM - 5PM", false));
        list.add(new SlotTime(9, "5PM - 6PM", false));
        list.add(new SlotTime(10, "6PM - 7PM", false));
        list.add(new SlotTime(11, "7PM - 8PM", false));
        list.add(new SlotTime(12, "8PM - 9PM", false));
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
                .replace("@","aa")
                .replace(".", "dot");
    }
}
