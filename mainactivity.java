//Manifest kýsmý için gerekli kodlar:

<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.BLUETOOTH"/>

//Main Activity kýsmý için gerekli kodlar:

package com.example.oem.sulamasistemi;


        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.util.ArrayList;
        import java.util.Set;
        import java.util.UUID;

        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothSocket;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;



public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    Button btnOn;
    Button btnOff;
    TextView text;
    String address=null;
    									8
    ListView liste;
    public  static final int baglanti=0;
    public static final int mesajoku=1;
    OutputStream outStream;
    InputStream instream;
    ArrayAdapter<String> adaptorlist;
    ArrayList<String > eslesen;
    ArrayList<BluetoothDevice> aygitlar;
    IntentFilter filtre;
    private BroadcastReceiver receiver;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket =null;
    private  boolean isBtConnected=false;
    Set<BluetoothDevice> devicearray;
    static  final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch(msg.what){
                case baglanti:

                    ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
                    Toast.makeText(getApplicationContext(), "Baglandi",Toast.LENGTH_LONG).show();
                    String s = "successfully connected";
                    break;
                case mesajoku:
                    byte[] readBuf = (byte[])msg.obj;
                    String string = new String(readBuf);
                    Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        liste=(ListView)findViewById(R.id.lv);
        liste.setOnItemClickListener(this);
        adaptorlist=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,0);
									9        
        liste.setAdapter(adaptorlist);
        myBluetooth=BluetoothAdapter.getDefaultAdapter();
        eslesen=new ArrayList<String>();
        filtre=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        aygitlar = new ArrayList<BluetoothDevice>();

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    BluetoothDevice aygit = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    aygitlar.add(aygit);

                    String s="";
                    for (int i=0;i<eslesen.size();i++)
                    {
                        if (aygit.getName().equals(eslesen.get(i))){
                            s="(Eslesti)";
                            break;
                        }
                    }
                    adaptorlist.add(aygit.getName()+""+s+"\n"+aygit.getAddress());
                }else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
                {
                    if (myBluetooth.getState()==myBluetooth.STATE_OFF){
                        bltac();
                    }
                }
            }
        };
        registerReceiver(receiver,filtre);
        filtre = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver,filtre);
        if (myBluetooth==null)
        {
            text.setText("Bluetooth yok");
            Toast.makeText(getApplicationContext(),"Bluetooth yok",Toast.LENGTH_SHORT).show();
        }else
        {
            if (myBluetooth.isEnabled())
            {
                bltac();
            }
            secim();
									10            
            startDiscovery();
            btnOn=(Button)findViewById(R.id.button1);
            btnOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        sensor();
                    } catch (IOException e) {

                    }
                }
            });
            btnOff=(Button)findViewById(R.id.button2);
            btnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        sula();
                    }catch (IOException e)
                    {

                    }
                }
            });
        }

    }

    private void  bltac(){
        Intent acintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(acintent, 1);
    }
    private  void  secim(){
        devicearray=myBluetooth.getBondedDevices();
        if(devicearray.size()>0)
        {
            for (BluetoothDevice aygit:devicearray){
                eslesen.add(aygit.getName());
            }
        }
    }

    private  void startDiscovery(){
        myBluetooth.cancelDiscovery();
        myBluetooth.startDiscovery();
    }


									11    
   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)           
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_CANCELED)
        {
            Toast.makeText(getApplicationContext(),"Bluetoothu aciniz.",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void onItemClick(AdapterView<?> arg0,View arg1, int arg2, long arg3)
    {
        if (myBluetooth.isDiscovering())
        {
            myBluetooth.cancelDiscovery();
        }
        if (adaptorlist.getItem(arg2).contains("Eslesti"))
        {
            BluetoothDevice secilenaygit = aygitlar.get(arg2);
            ConnectThread connect= new ConnectThread(secilenaygit);
            connect.start();
        }else{
            Toast.makeText(getApplicationContext(),"Bluetootha baglanamadi.",Toast.LENGTH_SHORT).show();
            text.setText("Baglanamadi");
        }
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {

            BluetoothSocket tmp = null;
            mmDevice = device;

            try {

                tmp = device.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {

            }
            mmSocket = tmp;
        }

	  								12        
            public void run() {
            myBluetooth.cancelDiscovery();

            try {

                mmSocket.connect();

            } catch (IOException connectException) {

                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            mHandler.obtainMessage(baglanti, mmSocket).sendToTarget();
        }


        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            outStream = tmpOut;
        }

        public void run() {
            byte[] buffer;
            
									13
            int bytes;
            while (true) {
            try {

                    buffer = new byte[1024];
                    bytes = mmInStream.read(buffer);

                    mHandler.obtainMessage(mesajoku, bytes, -1, buffer)
                            .sendToTarget();

                } catch (IOException e) {
                    break;
                }
            }
        }


        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public  void sensor() throws IOException
    {
        outStream.write("1".getBytes());
    }


    public  void sula() throws IOException
    {
        outStream.write("0".getBytes());
    }

}
