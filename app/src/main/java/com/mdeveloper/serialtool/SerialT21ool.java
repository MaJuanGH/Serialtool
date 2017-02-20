//package com.mdeveloper.serialtool;
//
//import android.app.Activity;
//import android.app.AlertDialog.Builder;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.method.NumberKeyListener;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//import android.widget.ToggleButton;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class SerialTool extends Activity implements View.OnClickListener {
//    private static final boolean DBGEN = true;
//    private Button mTtyButton;
//    private Button mBuardRateButton;
//    private Button mDataBitButton;
//    private Button mStopBitButton;
//    private Button mCheckBitButton;
//    private ToggleButton mOpenUartTogglleButton;
//    private ToggleButton charDisplay;
//    private ToggleButton charSend;
//    private Button send;
//    private EditText uartRx;
//    private EditText uartTx;
//    private final int buttonClickMSG = 16;
//    private Serial COMX = new Serial();
//    private int COMX_fd = 0;
//    private int clickIndex = 0;
//    private Button clickButton;
//    private String[] serialCfg = {"ttyS1", "9600", "8", "1", "None"};
//    private byte[] uartRxBuf = new byte['⠀'];
//    private final int uartRxData = 17;
//    private int uartRxLenth = 0;
//    private String uartTxChar = "Hello I'm Android Pad!\r\n";
//    private String uartTxHex = "31 32 33 34 35 36";
//
//    private Handler handler;
//    private boolean isStartReadThread = true;
//    private StringList mStringList = new StringList();
//    private Timer timer1;
//    private Message message;
//
//
//    static {
//        System.loadLibrary("native-lib");
//    }
//
//    private void DBG(String paramString) {
//        Log.d("irlearn", paramString);
//    }
//
//    private int DispList(String paramString, CharSequence[] paramArrayOfCharSequence) {
//        Builder localBuilder = new Builder(this);
//        localBuilder.setTitle(paramString).setIcon(R.drawable.ic_small).setCancelable(false).setItems(paramArrayOfCharSequence, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
//                clickIndex = paramAnonymousInt;
//                message = new Message();
//                message.what = 16;
//                handler.sendMessage(message);
//            }
//        });
//        localBuilder.create().show();
//        return clickIndex;
//    }
//
//    private void SerialTx() {
//        Object localObject = uartTx.getText().toString();
//        if (charSend.isChecked()) {
//            localObject = new DataConvert().Hex2byte((String) localObject);
//            COMX.Write((byte[]) localObject, ((byte[]) localObject).length);
//            return;
//        }
//        localObject = ((String) localObject).getBytes();
//        COMX.Write((byte[]) localObject, ((byte[]) localObject).length);
//    }
//
//    private void UpdateTitle() {
//        String str = serialCfg[0] + " ," + serialCfg[1] + " ," + serialCfg[2] + " ," + serialCfg[3] + " ," + serialCfg[4];
//        setTitle("SerialTool: " + str);
//    }
//
//    public void StartRxTimer() {
//        if (this.timer1 != null) {
//            timer1.cancel();
//            timer1 = null;
//        }
//        timer1 = new Timer();
//        TimerTask local2 = new TimerTask() {
//            public void run() {
//                if (COMX_fd > 0) {
//                    Object localObject = COMX.Read();
//                    if (localObject != null) {
//                        System.arraycopy(localObject, 0, uartRxBuf, uartRxLenth, ((byte[]) localObject).length);
//                        SerialTool localSerialTool = SerialTool.this;
//                        localSerialTool.uartRxLenth += ((byte[]) localObject).length;
//                        localObject = new Message();
//                        ((Message) localObject).what = 17;
//                        handler.sendMessage((Message) localObject);
//                    }
//                }
//            }
//        };
//        timer1.schedule(local2, 100L, 100L);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_serial_tool);
//        send = ((Button) findViewById(R.id.UartSend));
//        mTtyButton = ((Button) findViewById(R.id.ttyPort));
//        mBuardRateButton = ((Button) findViewById(R.id.BuardRate));
//        mDataBitButton = ((Button) findViewById(R.id.DataBit));
//        mStopBitButton = ((Button) findViewById(R.id.StopBit));
//        mCheckBitButton = ((Button) findViewById(R.id.CheckBit));
//        charDisplay = ((ToggleButton) findViewById(R.id.charhexDisplay));
//        charSend = ((ToggleButton) findViewById(R.id.charhexSend));
//        mOpenUartTogglleButton = ((ToggleButton) findViewById(R.id.OpenUart));
//        uartRx = ((EditText) findViewById(R.id.UartRx));
//        uartTx = ((EditText) findViewById(R.id.UartTx));
//        uartTx.setText(uartTxChar);
//        send.setOnClickListener(this);
//        mTtyButton.setOnClickListener(this);
//        mBuardRateButton.setOnClickListener(this);
//        mDataBitButton.setOnClickListener(this);
//        mStopBitButton.setOnClickListener(this);
//        mCheckBitButton.setOnClickListener(this);
//        charDisplay.setOnClickListener(this);
//        charSend.setOnClickListener(this);
//        mOpenUartTogglleButton.setOnClickListener(this);
//        handler = new Handler() {
//            public void handleMessage(Message message) {
//                super.handleMessage(message);
//                switch (message.what) {
//                    case 16:
//                        if (clickButton == mTtyButton) {
//                            mTtyButton.setText(mStringList.ttyList[clickIndex]);
//                            serialCfg[0] = ((String) mStringList.ttyList[clickIndex]);
//                        }
//                        if (clickButton == mBuardRateButton) {
//                            mBuardRateButton.setText(mStringList.BodList[clickIndex]);
//                            serialCfg[1] = ((String) mStringList.BodList[clickIndex]);
//                        }
//                        if (clickButton == mDataBitButton) {
//                            mDataBitButton.setText(mStringList.DataBit[clickIndex]);
//                            serialCfg[2] = ((String) mStringList.DataBit[clickIndex]);
//                        }
//                        if (clickButton == mStopBitButton) {
//                            mStopBitButton.setText(mStringList.StopBit[clickIndex]);
//                            serialCfg[3] = ((String) mStringList.StopBit[clickIndex]);
//                        }
//                        if (clickButton == mCheckBitButton) {
//                            mCheckBitButton.setText(mStringList.CheckBit[clickIndex]);
//                            serialCfg[4] = ((String) mStringList.CheckBit[clickIndex]);
//                        }
//                        isStartReadThread = false;
//                        COMX.Close();
//                        mOpenUartTogglleButton.setChecked(false);
//                        return;
//                    case 17:
//                        if (charDisplay.isChecked()) {
//                        }
//                    default:
//                        return;
//                }
////                message.getData().getIntArray("IrRawData");
//            }
//        };
//    }
//
//    public boolean onCreateOptionsMenu(Menu paramMenu) {
//        paramMenu.add(0, 2, 1, "Email Author");
//        return true;
//    }
//
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
//        return false;
//    }
//
//
//    @Override
//    public void onClick(View v) {
//
//        if (v == mOpenUartTogglleButton) {
//            if (!mOpenUartTogglleButton.isChecked()) {
//                Log.i("onClick", "" + !mOpenUartTogglleButton.isChecked());
//                return;
//            }
//            COMX_fd = COMX.OpenPort(serialCfg);
//            if (COMX_fd <= 0) {
//                Toast.makeText(getApplicationContext(), "Please Check " + serialCfg[0] + " Is Presence !!" + "\r\n" + "OR Permission is Correct:0666", 0).show();
//                mOpenUartTogglleButton.setChecked(false);
//                isStartReadThread = false;
//                timer1.cancel();
//            }
//        } else {
//            if (v == charSend) {
//                if (!charSend.isChecked()) {
//                    Log.i("onClick", "" + !charSend.isChecked());
//                }
//                uartTxChar = uartTx.getText().toString();
//                uartTx.setText(uartTxHex);
//                uartTx.setKeyListener(new NumberKeyListener() {
//                    protected char[] getAcceptedChars() {
//                        return new char[]{97, 98, 99, 100, 101, 102, 65, 66, 67, 68, 69, 70, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 32};
//                    }
//
//                    public int getInputType() {
//                        return 1;
//                    }
//                });
//            } else if (v == charDisplay) {
//                if (!charDisplay.isChecked()) {
//                    Log.i("onClick", "" + !charDisplay.isChecked());
//                    return;
//                }
//                new DataConvert();
//            } else if (v == mTtyButton) {
//                DispList("Select tty Port", mStringList.ttyList);
//                clickButton = mTtyButton;
//            } else if (v == mBuardRateButton) {
//                DispList("Select Buard Rate", mStringList.BodList);
//                clickButton = mBuardRateButton;
//            } else if (v == mDataBitButton) {
//                DispList("Select Data Bit", mStringList.DataBit);
//                clickButton = mDataBitButton;
//            } else if (v == mStopBitButton) {
//                DispList("Select Stop Bit", mStringList.StopBit);
//                clickButton = mStopBitButton;
//            } else if (v == mCheckBitButton) {
//                DispList("Select Check Bit Type", mStringList.CheckBit);
//                clickButton = mCheckBitButton;
//            } else if (v == send) {
//                String str = "";
//                int i = 0;
//                for (; ; ) {
//                    if (i >= uartRxLenth) {
//                        uartRx.setText(str);
//                        DBG("CharDisplay ---");
//                        break;
//                    }
//                    str = str + (char) uartRxBuf[i];
//                    i += 1;
//                }
//                uartRx.setText(str);
//                SerialTx();
//                isStartReadThread = true;
//                StartRxTimer();
//                DBG("Start RX");
//            }
//        }
//    }
//
//}
//
//
