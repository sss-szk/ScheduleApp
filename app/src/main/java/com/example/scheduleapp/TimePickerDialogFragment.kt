package com.example.scheduleapp

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerDialogFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    // Activity や Fragment に選択結果を渡すためのリスナー
    interface OnSelectedTimeListener {
        fun selectedTime(hour: Int, minute: Int)
    }

    private lateinit var listener: OnSelectedTimeListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSelectedTimeListener) {
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = Date().time
        val context = context // smart cast
        return when {
            context != null -> {
                TimePickerDialog(
                    context,
                    this, // ここでは TimePickerDialog の リスナーを渡す
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true)
            }
            else -> super.onCreateDialog(savedInstanceState)
        }
    }
    // TimePickerDialog から選択結果が渡される。
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener.selectedTime(hourOfDay, minute)
    }

    companion object {
        @Suppress("unused")
        private val TAG = TimePickerDialogFragment::class.java.simpleName
    }

}