package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StockDetailedActivity extends AppCompatActivity {

    @BindView(R.id.tv_symbol)
    TextView mSymbol;

    @BindView(R.id.chart)
    LineChart mChart;

    String symbol;
    String history = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detailed);
        ButterKnife.bind(this);

        this.symbol = getIntent().getStringExtra("symbol");

        mSymbol.setText(symbol);
        getHistory();

    }

    private void getHistory() {
        Cursor cursor = getContentResolver().query(Contract.Quote.makeUriForStock(symbol), null, null, null,null);

        if(cursor.moveToFirst()) {
            history = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
            cursor.close();
        }

        List<Entry> entries = new ArrayList<Entry>();

        CSVReader reader = new CSVReader(new StringReader(history));
        String[] nextLine;
        final List<Long> xAxisValues = new ArrayList<>();
        int xAxisPosition = 0;

        try {
            while((nextLine = reader.readNext()) != null) {
                xAxisValues.add(Long.valueOf(nextLine[0]));

                entries.add(new Entry(
                        xAxisPosition,
                        Float.valueOf(nextLine[1])
                ));
                xAxisPosition++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        LineData lineData = new LineData(new LineDataSet(entries, symbol));
        mChart.setData(lineData);

        XAxis x = mChart.getXAxis();

        x.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Date date = new Date( xAxisValues.get(xAxisValues.size() - (int) value - 1));
                return new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH).format(date);
            }
        });

        YAxis yLeft = mChart.getAxisLeft();
        YAxis yRight = mChart.getAxisRight();

        yLeft.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value + "$";
            }
        });

        yRight.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value + "$";
            }
        });


    }
}
