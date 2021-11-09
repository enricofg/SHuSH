package com.example.shush

import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


class MyBarDataSet(yVals: List<BarEntry?>?, label: String?) :
    BarDataSet(yVals, label) {
    override fun getEntryIndex(e: BarEntry?): Int {
        TODO("Not yet implemented")
    }

    override fun getColor(index: Int): Int {
        return if (getEntryForXIndex(index).getVal() < 65) // less than 95 green
            mColors[0]
        else if (getEntryForXIndex(index).getVal() < 86) // less than 100 orange
            mColors[1]
        else  // greater or equal than 100 red
            mColors[2]
    }
}