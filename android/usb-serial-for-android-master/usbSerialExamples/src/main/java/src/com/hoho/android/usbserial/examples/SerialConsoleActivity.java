/* Copyright 2011-2013 Google Inc.
 * Copyright 2013 mike wakerly <opensource@hoho.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: https://github.com/mik3y/usb-serial-for-android
 */

package com.hoho.android.usbserial.examples;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import src.com.hoho.android.usbserial.examples.gauge.GaugeView;

import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import src.com.hoho.android.usbserial.examples.DtoDataMessage;

import static java.lang.Thread.sleep;

/**
 * Monitors a single {@link UsbSerialPort} instance, showing all data
 * received.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
public class SerialConsoleActivity extends Activity {

    private static final int MAX_QUEUE_SIZE = 5;
    private final String TAG = SerialConsoleActivity.class.getSimpleName();

    /**
     * Driver instance, passed in statically via
     * {@link #show(Context, UsbSerialPort)}.
     * <p/>
     * <p/>
     * This is a devious hack; it'd be cleaner to re-create the driver using
     * arguments passed in with the {@link #startActivity(Intent)} intent. We
     * can get away with it because both activities will run in the same
     * process, and this is a simple demo.
     */
    private static UsbSerialPort sPort = null;

    private TextView mDumpTextView;
    private ScrollView mScrollView;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;

    private LinkedBlockingQueue<Byte> queue;

    private LinkedBlockingQueue<List<Byte>> processedMessages;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    SerialConsoleActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < data.length; i++) {
                                try {
                                    queue.put(data[i]);
                                } catch (InterruptedException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }
                    });
                }
            };
    private boolean iterar = false;
    private GaugeView mGaugeRPM;
    private GaugeView mGaugeBNK1;
    private GaugeView mGaugeBNK2;
    private GaugeView mGaugeAFM;
    private GaugeView mGaugeCTS;
    private GaugeView mGaugeAIT;
    private GaugeView mGaugeSPD;
    private GaugeView mGaugeO2;
    private GaugeView mGaugeVOLT;
    private TextView mTextPARTIAL;
    private TextView mTextIDLE;
    private TextView mTextMalfunction;
    private TextView mTextVOLT;
    private TextView mTextSPD;
    private TextView mTextCTS;
    private TextView mTextBNK2;
    private TextView mTextRPM;
    private TextView mTextBNK1;
    private TextView mTextAFM;
    private TextView mTextAIT;
    private TextView mTextO2;
    private TextView mTextWOT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serial_console);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);
        queue = new LinkedBlockingQueue<>();
        processedMessages = new LinkedBlockingQueue<>();

        //gauges
        mGaugeRPM = (GaugeView) findViewById(R.id.gauge_rpm);
        mGaugeBNK1 = (GaugeView) findViewById(R.id.gauge_inj_timing_b1);
        mGaugeBNK2 = (GaugeView) findViewById(R.id.gauge_inj_timing_b2);
        mGaugeAFM = (GaugeView) findViewById(R.id.gauge_afm);
        mGaugeCTS = (GaugeView) findViewById(R.id.gauge_cts);
        mGaugeAIT = (GaugeView) findViewById(R.id.gauge_ait);
        mGaugeSPD = (GaugeView) findViewById(R.id.gauge_spd);
        mGaugeO2 = (GaugeView) findViewById(R.id.gauge_o2);
        mGaugeVOLT = (GaugeView) findViewById(R.id.gauge_volt);

        // texts
        mTextRPM = (TextView) findViewById(R.id.rpmText);
        mTextBNK1 = (TextView) findViewById(R.id.bnk1Text);
        mTextBNK2 = (TextView) findViewById(R.id.bnk2Text);
        mTextAFM = (TextView) findViewById(R.id.afmText);
        mTextCTS = (TextView) findViewById(R.id.ctsText);
        mTextAIT = (TextView) findViewById(R.id.aitText);
        mTextSPD = (TextView) findViewById(R.id.spdText);
        mTextO2 = (TextView) findViewById(R.id.o2Text);
        mTextVOLT = (TextView) findViewById(R.id.voltText);
        mTextWOT = (TextView) findViewById(R.id.wotText);
        mTextIDLE = (TextView) findViewById(R.id.idleText);
        mTextPARTIAL = (TextView) findViewById(R.id.partialText);
        mTextMalfunction = (TextView) findViewById(R.id.malfunctionText);

        mGaugeRPM.setTargetValue(0);
        mGaugeBNK1.setTargetValue(0);
        mGaugeBNK2.setTargetValue(0);
        mGaugeAFM.setTargetValue(0);
        mGaugeCTS.setTargetValue(0);
        mGaugeAIT.setTargetValue(0);
        mGaugeSPD.setTargetValue(0);
        mGaugeO2.setTargetValue(0);
        mGaugeVOLT.setTargetValue(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed, port=" + sPort);
        if (sPort == null) {
        } else {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            if (connection == null) {
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
        }
        onDeviceStateChange();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
        iterar = false;
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
        iterar = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // acá vamos a iterar
                while (iterar) {
                    try {
                        sleep(100);
                        while (!(new String(new byte[]{queue.take()}).equals("#"))) {
                        }
                        List<Byte> byteList = new ArrayList<>();
                        int counter = 0;
                        Byte b;
                        while (!(new String(new byte[]{b = queue.take()}).equals("#"))) {
                            if (counter < 1024) {
                                byteList.add(b);
                                counter++;
                            } else {
                                break;
                            }
                        }
                        while(processedMessages.size() > MAX_QUEUE_SIZE){
                            processedMessages.take();
                        }
                        processedMessages.put(byteList);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateReceivedData();
                            }
                        });
                    } catch (final InterruptedException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateReceivedData(e.getMessage());
                            }
                        });
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }).start();

    }

    private void updateReceivedData() {
        try {
            while(processedMessages.size() > MAX_QUEUE_SIZE){
                processedMessages.take();
            }
            List<Byte> data = processedMessages.take();
            byte[] data2 = new byte[data.size()];
            int counter = 0;
            for (Byte b : data) {
                data2[counter] = b;
                counter++;
            }
            DtoDataMessage dto = DtoDataMessage.parseReceivedValues(HexDump.dumpHexString(data2, 0, data.size()), this);
            updateReceivedData(HexDump.dumpHexString(data2, 0, data.size()));
            updateReceivedData(dto);
            if (dto != null) {
//                updateGaugesData(dto);
                updateTextsData(dto);
            }
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void updateTextsData(DtoDataMessage dto) {
        if (mTextRPM != null) {
            mTextRPM.setText(dto.getRpm() + getString(R.string.RPM));
        }
        if (mTextBNK1 != null) {
            mTextBNK1.setText(dto.getBank1() + getString(R.string.MS) + " " + getString(R.string.BANK1));
        }
        if (mTextBNK2 != null) {
            mTextBNK2.setText(dto.getBank2() + getString(R.string.MS) + " " + getString(R.string.BANK2));
        }
        if (mTextAFM != null) {
            mTextAFM.setText(dto.getAfm() + getString(R.string.Voltage) + " " + getString(R.string.AFM));
        }
        if (mTextCTS != null) {
            mTextCTS.setText(dto.getCts() + getString(R.string.Voltage) + " " + getString(R.string.CTS));
        }
        if (mTextAIT != null) {
            mTextAIT.setText(dto.getAit() + getString(R.string.Voltage) + " " + getString(R.string.AIT));
        }
        if (mTextSPD != null) {
            mTextSPD.setText(dto.getSpeedInKph() + " " + getString(R.string.SPD));
        }
        if (mTextO2 != null) {
            mTextO2.setText(dto.getO2() + getString(R.string.Voltage) + " " + getString(R.string.O2));
        }
        if (mTextVOLT != null) {
            mTextVOLT.setText(dto.getVolt() + getString(R.string.Voltage) + " " + getString(R.string.BatVolt));
        }
        if (mTextWOT != null && mTextPARTIAL != null && mTextIDLE != null && dto.getThrottleStatus() != null) {
            switch (dto.getThrottleStatus()) {
                case IDLE:
                    mTextWOT.setVisibility(View.INVISIBLE);
                    mTextPARTIAL.setVisibility(View.INVISIBLE);
                    mTextIDLE.setVisibility(View.VISIBLE);
                    mTextMalfunction.setVisibility(View.INVISIBLE);
                    break;
                case PARTIAL:
                    mTextWOT.setVisibility(View.INVISIBLE);
                    mTextPARTIAL.setVisibility(View.VISIBLE);
                    mTextIDLE.setVisibility(View.INVISIBLE);
                    mTextMalfunction.setVisibility(View.INVISIBLE);
                    break;
                case WOT:
                    mTextWOT.setVisibility(View.VISIBLE);
                    mTextPARTIAL.setVisibility(View.INVISIBLE);
                    mTextIDLE.setVisibility(View.INVISIBLE);
                    mTextMalfunction.setVisibility(View.INVISIBLE);
                    break;
                default:
                    mTextWOT.setVisibility(View.INVISIBLE);
                    mTextPARTIAL.setVisibility(View.INVISIBLE);
                    mTextIDLE.setVisibility(View.INVISIBLE);
                    mTextMalfunction.setVisibility(View.VISIBLE);
            }
        } else {
            mTextWOT.setVisibility(View.INVISIBLE);
            mTextPARTIAL.setVisibility(View.INVISIBLE);
            mTextIDLE.setVisibility(View.INVISIBLE);
            if (mTextMalfunction.getVisibility() == View.VISIBLE) {
                mTextMalfunction.setVisibility(View.INVISIBLE);
            } else {
                mTextMalfunction.setVisibility(View.VISIBLE);
            }
        }


    }

    private void updateGaugesData(DtoDataMessage dto) {
        mGaugeAFM.setTargetValue(dto.getAfm());
        mGaugeAIT.setTargetValue(dto.getAit());
        mGaugeCTS.setTargetValue(dto.getCts());
        mGaugeO2.setTargetValue(dto.getO2());
        mGaugeVOLT.setTargetValue(dto.getVolt());
        mGaugeBNK1.setTargetValue(dto.getBank1());
        mGaugeBNK2.setTargetValue(dto.getBank2());
        mGaugeSPD.setTargetValue(dto.getSpeedInKph());
        mGaugeRPM.setTargetValue(dto.getRpm() / 100);
    }

    private void updateReceivedData(DtoDataMessage dto) {
        if (dto != null) {
            mDumpTextView.append(dto.toString());
        } else {
            mDumpTextView.append("Null Dto Received\n");
        }
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data, int size) {
//        final String message = "Read " + data.length + " bytes: \n"
//                + HexDump.dumpHexString(data) + "\n\n";
        final String message = //"Read " + data.length + " bytes: \n"
                HexDump.dumpHexString(data, 0, size) + "\n\n";
        mDumpTextView.append(message);
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
    }

    private void updateReceivedData(List<Byte> data, int size) {
//        final String message = "Read " + data.length + " bytes: \n"
//                + HexDump.dumpHexString(data) + "\n\n";
        byte[] data2 = new byte[size];
        int counter = 0;
        for (Byte b : data) {
            data2[counter] = b;
            counter++;
        }
        final String message = //"Read " + data.length + " bytes: \n"
                HexDump.dumpHexString(data2, 0, size) + "\n\n";
        mDumpTextView.append(message);
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
    }

    public void updateReceivedData(String data) {
//        final String message = "Read " + data.length + " bytes: \n"
//                + HexDump.dumpHexString(data) + "\n\n";
        mDumpTextView.append("\n" + data.replace("\n", ""));
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
    }

    /**
     * Starts the activity, using the supplied driver instance.
     *
     * @param context
     * @param driver
     */
    static void show(Context context, UsbSerialPort port) {
        sPort = port;
        final Intent intent = new Intent(context, SerialConsoleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

}
