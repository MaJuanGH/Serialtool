package com.mdeveloper.serialtool;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

public class SerialTool extends Activity {
    private static final boolean DBGEN = true;
    Button BuardRate;
    final int ButtonClickMSG = 16;
    Serial COMX = new Serial();
    int COMX_fd = 0;
    ToggleButton CharDisplay;
    ToggleButton CharSend;
    Button CheckBit;
    private int ClickIndex = 0;
    Button DataBit;
    ToggleButton OpenUart;
    Button Send;
    private String[] SerialCfg = {"ttyS3", "9600", "8", "1", "None"};
    Button StopBit;
    Button Tty;
    EditText UartRx;
    byte[] UartRxBuf = new byte['⠀'];
    final int UartRxData = 17;
    int UartRxLenth = 0;
    EditText UartTx;
    String UartTxChar = "Hello I'm Android Pad!\r\n";
    String UartTxHex = "31 32 33 34 35 36";
    Button clickButton;
    Handler handler;
    private boolean isStartReadThread = true;
    StringList mStringList = new StringList();
    Timer timer1 ;
    Message message;
    private String paramAnonymousMessage;

    static {
        System.loadLibrary("SerialPort");
    }

    private void DBG(String paramString) {
        Log.d("irlearn", paramString);
    }

    private int DispList(String paramString, CharSequence[] paramArrayOfCharSequence) {
        Builder localBuilder = new Builder(this);
        localBuilder.setTitle(paramString).setIcon(R.drawable.ic_small).setCancelable(false).setItems(paramArrayOfCharSequence, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                SerialTool.this.ClickIndex = paramAnonymousInt;
                message = new Message();
                message.what = 16;
                SerialTool.this.handler.sendMessage(message);
            }
        });
        localBuilder.create().show();
        return this.ClickIndex;
    }

    private void SerialTx() {
        Object localObject = this.UartTx.getText().toString();
        if (this.CharSend.isChecked()) {
            localObject = new com.mdeveloper.serialtool.DataConvert().Hex2byte((String) localObject);
            this.COMX.Write((byte[]) localObject, ((byte[]) localObject).length);
            return;
        }
        localObject = ((String) localObject).getBytes();
        this.COMX.Write((byte[]) localObject, ((byte[]) localObject).length);
    }

    private void UpdateTitle() {
        String str = this.SerialCfg[0] + " ," + this.SerialCfg[1] + " ," + this.SerialCfg[2] + " ," + this.SerialCfg[3] + " ," + this.SerialCfg[4];
        setTitle("SerialTool: " + str);
    }

    public void StartRxTimer() {
        if (this.timer1 != null) {
            this.timer1.cancel();
            this.timer1 = null;
        }
        this.timer1 = new Timer();
        TimerTask local2 = new TimerTask() {
            public void run() {
                if (SerialTool.this.COMX_fd > 0) {
                    Object localObject = SerialTool.this.COMX.Read();
                    if (localObject != null) {
                        System.arraycopy(localObject, 0, SerialTool.this.UartRxBuf, SerialTool.this.UartRxLenth, ((byte[]) localObject).length);
                        SerialTool localSerialTool = SerialTool.this;
                        localSerialTool.UartRxLenth += ((byte[]) localObject).length;
                        localObject = new Message();
                        ((Message) localObject).what = 17;
                        SerialTool.this.handler.sendMessage((Message) localObject);
                    }
                }
            }
        };
        this.timer1.schedule(local2, 100L, 100L);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_serial_tool);
        this.Send = ((Button) findViewById(R.id.UartSend));
        this.Tty = ((Button) findViewById(R.id.ttyPort));
        this.BuardRate = ((Button) findViewById(R.id.BuardRate));
        this.DataBit = ((Button) findViewById(R.id.DataBit));
        this.StopBit = ((Button) findViewById(R.id.StopBit));
        this.CheckBit = ((Button) findViewById(R.id.CheckBit));
        this.CharDisplay = ((ToggleButton) findViewById(R.id.charhexDisplay));
        this.CharSend = ((ToggleButton) findViewById(R.id.charhexSend));
        this.OpenUart = ((ToggleButton) findViewById(R.id.OpenUart));
        this.UartRx = ((EditText) findViewById(R.id.UartRx));
        this.UartTx = ((EditText) findViewById(R.id.UartTx));
        this.UartTx.setText(this.UartTxChar);
        this.Send.setOnClickListener(new ButtonListener());
        this.Tty.setOnClickListener(new ButtonListener());
        this.BuardRate.setOnClickListener(new ButtonListener());
        this.DataBit.setOnClickListener(new ButtonListener());
        this.StopBit.setOnClickListener(new ButtonListener());
        this.CheckBit.setOnClickListener(new ButtonListener());
        this.CharDisplay.setOnClickListener(new ButtonListener());
        this.CharSend.setOnClickListener(new ButtonListener());
        this.OpenUart.setOnClickListener(new ButtonListener());
        this.handler = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                switch (message.what) {
                    default:
                        return;
                    case 16:
                        if (SerialTool.this.clickButton == SerialTool.this.Tty) {
                            SerialTool.this.Tty.setText(SerialTool.this.mStringList.ttyList[SerialTool.this.ClickIndex]);
                            SerialTool.this.SerialCfg[0] = ((String) SerialTool.this.mStringList.ttyList[SerialTool.this.ClickIndex]);
                        }
                        if (SerialTool.this.clickButton == SerialTool.this.BuardRate) {
                            SerialTool.this.BuardRate.setText(SerialTool.this.mStringList.BodList[SerialTool.this.ClickIndex]);
                            SerialTool.this.SerialCfg[1] = ((String) SerialTool.this.mStringList.BodList[SerialTool.this.ClickIndex]);
                        }
                        if (SerialTool.this.clickButton == SerialTool.this.DataBit) {
                            SerialTool.this.DataBit.setText(SerialTool.this.mStringList.DataBit[SerialTool.this.ClickIndex]);
                            SerialTool.this.SerialCfg[2] = ((String) SerialTool.this.mStringList.DataBit[SerialTool.this.ClickIndex]);
                        }
                        if (SerialTool.this.clickButton == SerialTool.this.StopBit) {
                            SerialTool.this.StopBit.setText(SerialTool.this.mStringList.StopBit[SerialTool.this.ClickIndex]);
                            SerialTool.this.SerialCfg[3] = ((String) SerialTool.this.mStringList.StopBit[SerialTool.this.ClickIndex]);
                        }
                        if (SerialTool.this.clickButton == SerialTool.this.CheckBit) {
                            SerialTool.this.CheckBit.setText(SerialTool.this.mStringList.CheckBit[SerialTool.this.ClickIndex]);
                            SerialTool.this.SerialCfg[4] = ((String) SerialTool.this.mStringList.CheckBit[SerialTool.this.ClickIndex]);
                        }
                        SerialTool.this.isStartReadThread = false;
                        SerialTool.this.COMX.Close();
                        SerialTool.this.OpenUart.setChecked(false);
                        return;
                    case 17:
                        if (SerialTool.this.CharDisplay.isChecked()) {
                            new com.mdeveloper.serialtool.DataConvert();
                            paramAnonymousMessage = "";
                            i = 0;
                            for (; ; ) {
                                if (i >= SerialTool.this.UartRxLenth) {
                                    SerialTool.this.UartRx.setText(paramAnonymousMessage);
                                    SerialTool.this.UartRx.setSelection(SerialTool.this.UartRx.getText().toString().length());
                                    return;
                                }
                                paramAnonymousMessage = paramAnonymousMessage + Integer.toHexString(SerialTool.this.UartRxBuf[i] & 0xFF | 0x100).substring(1) + " ";
                                i += 1;
                            }
                        }
                        paramAnonymousMessage = "";
                        int i = 0;
                        for (; ; ) {
                            if (i >= SerialTool.this.UartRxLenth) {
                                SerialTool.this.UartRx.setText(paramAnonymousMessage);
                                SerialTool.this.DBG("CharDisplay ---");
                                break;
                            }
                            paramAnonymousMessage = paramAnonymousMessage + (char) SerialTool.this.UartRxBuf[i];
                            i += 1;
                        }
                }
                message.getData().getIntArray("IrRawData");
            }
        };
    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        paramMenu.add(0, 2, 1, "Email Author");
        return true;
    }
    protected void onDestroy() {
        super.onDestroy();
    }


    class ButtonListener
            implements DialogInterface.OnClickListener, View.OnClickListener {
        ButtonListener() {
        }

        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
        }

        public void onClick(View paramView) {

            if (paramView == SerialTool.this.OpenUart) {
                if (!SerialTool.this.OpenUart.isChecked()) {
//                    break label551;
                    label551();
                }
                SerialTool.this.COMX_fd = SerialTool.this.COMX.OpenPort(SerialTool.this.SerialCfg);
                if (SerialTool.this.COMX_fd <= 0) {
                    Toast.makeText(SerialTool.this.getApplicationContext(), "Please Check " + SerialTool.this.SerialCfg[0] + " Is Presence !!" + "\r\n" + "OR Permission is Correct:0666", 0).show();
                    SerialTool.this.OpenUart.setChecked(false);
                    SerialTool.this.isStartReadThread = false;
                    SerialTool.this.timer1.cancel();
                }
            } else {
                if (paramView == SerialTool.this.CharSend) {
                    if (!SerialTool.this.CharSend.isChecked()) {
//                        break label573;
                        label573();
                    }
                    SerialTool.this.UartTxChar = SerialTool.this.UartTx.getText().toString();
                    SerialTool.this.UartTx.setText(SerialTool.this.UartTxHex);
                    SerialTool.this.UartTx.setKeyListener(new NumberKeyListener() {
                        protected char[] getAcceptedChars() {
                            return new char[]{97, 98, 99, 100, 101, 102, 65, 66, 67, 68, 69, 70, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 32};
                        }

                        public int getInputType() {
                            return 1;
                        }
                    });
                }
                label221:
                if (paramView == SerialTool.this.CharDisplay) {
                    if (!SerialTool.this.CharDisplay.isChecked()) {
//                        break label687;
                        label687();
                    }
                    new com.mdeveloper.serialtool.DataConvert();
                    str = "";
                    i = 0;
                }
            }
            for (; ; ) {
                if (i >= SerialTool.this.UartRxLenth) {
                    SerialTool.this.UartRx.setText(str);
                    if (paramView == SerialTool.this.Send) {
                        SerialTool.this.SerialTx();
                    }
                    if (paramView == SerialTool.this.Tty) {
                        SerialTool.this.DispList("Select tty Port", SerialTool.this.mStringList.ttyList);
                        SerialTool.this.clickButton = SerialTool.this.Tty;
                    }
                    if (paramView == SerialTool.this.BuardRate) {
                        SerialTool.this.DispList("Select Buard Rate", SerialTool.this.mStringList.BodList);
                        SerialTool.this.clickButton = SerialTool.this.BuardRate;
                    }
                    if (paramView == SerialTool.this.DataBit) {
                        SerialTool.this.DispList("Select Data Bit", SerialTool.this.mStringList.DataBit);
                        SerialTool.this.clickButton = SerialTool.this.DataBit;
                    }
                    if (paramView == SerialTool.this.StopBit) {
                        SerialTool.this.DispList("Select Stop Bit", SerialTool.this.mStringList.StopBit);
                        SerialTool.this.clickButton = SerialTool.this.StopBit;
                    }
                    if (paramView == SerialTool.this.CheckBit) {
                        SerialTool.this.DispList("Select Check Bit Type", SerialTool.this.mStringList.CheckBit);
                        SerialTool.this.clickButton = SerialTool.this.CheckBit;
                    }
                    SerialTool.this.isStartReadThread = true;
                    SerialTool.this.StartRxTimer();
                    SerialTool.this.DBG("Start RX");
                    break;
//                    label551:
//                    SerialTool.this.isStartReadThread = false;
//                    SerialTool.this.COMX.Close();
//                    break;
//                    label573:
//                    SerialTool.this.UartTxHex = SerialTool.this.UartTx.getText().toString();
//                    SerialTool.this.UartTx.setText(SerialTool.this.UartTxChar);
//                    SerialTool.this.UartTx.setKeyListener(new NumberKeyListener() {
//                        protected char[] getAcceptedChars() {
//                            return null;
//                        }
//
//                        public int getInputType() {
//                            return 1;
//                        }
//                    });
//                    break label221;
                }
                str = str + Integer.toHexString(SerialTool.this.UartRxBuf[i] & 0xFF | 0x100).substring(1) + " ";
                i += 1;
            }
//            label687:
//            for (; ; ) {
//                if (i >= SerialTool.this.UartRxLenth) {
//                    SerialTool.this.UartRx.setText(str);
//                    SerialTool.this.DBG("CharDisplay ---");
//                    break;
//                }
//                str = str + (char) SerialTool.this.UartRxBuf[i];
//                i += 1;
//            }
        }
    }
    public void label551(){
        SerialTool.this.isStartReadThread = false;
        SerialTool.this.COMX.Close();
    }

    public void 7label573(){
        SerialTool.this.UartTxHex = SerialTool.this.UartTx.getText().toString();
        SerialTool.this.UartTx.setText(SerialTool.this.UartTxChar);
        SerialTool.this.UartTx.setKeyListener(new NumberKeyListener() {
            protected char[] getAcceptedChars() {
                return null;
            }

            public int getInputType() {
                return 1;
            }
        });
    }
    public void label687(){
        for (; ; ) {
            if (i >= SerialTool.this.UartRxLenth) {
                SerialTool.this.UartRx.setText(str);
                SerialTool.this.DBG("CharDisplay ---");
                break;
            }
            str = str + (char) SerialTool.this.UartRxBuf[i];
            i += 1;
        }
    }
    public void label221(){

    }


    int i = 0;
    String str = "";
}

