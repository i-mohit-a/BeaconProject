package com.example.dell.beaconproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;

import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ufobeaconsdk.callback.OnConnectSuccessListener;
import com.ufobeaconsdk.callback.OnFailureListener;
import com.ufobeaconsdk.callback.OnScanSuccessListener;
import com.ufobeaconsdk.callback.OnSuccessListener;
import com.ufobeaconsdk.main.EddystoneType;
import com.ufobeaconsdk.main.UFOBeaconManager;
import com.ufobeaconsdk.main.UFODevice;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8;
    private Button bt1,bt2;

    private UFOBeaconManager ufoBeaconManager;
    final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    UFODevice device;

    GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);
        bt1 = (Button) findViewById(R.id.b1);
        bt2 = (Button) findViewById(R.id.b2);

        ufoBeaconManager = new UFOBeaconManager(getApplicationContext());

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);


        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ufoBeaconManager.isBluetoothEnabled(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(boolean isSuccess) {

                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(int code, String message) {
                                AlertDialog.Builder altdil = new AlertDialog.Builder(MainActivity.this);
                                altdil.setMessage(null).setCancelable(false)
                                        .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                                mBluetoothAdapter.enable();
                                            }
                                        })
                                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                                Toast.makeText(getApplication(),"Bluetooth Should be on for the apps functioning",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                AlertDialog alertDialog = altdil.create();
                                alertDialog.setTitle("Turn Bluetooth On");
                                alertDialog.show();
                            }
                        }
                );

                ufoBeaconManager.isLocationServiceEnabled(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(boolean isSuccess) {

                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(int code, String message) {
                                AlertDialog.Builder altdil = new AlertDialog.Builder(MainActivity.this);
                                altdil.setMessage(null).setCancelable(false)
                                        .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                startActivity(intent1);
                                                LocationManager locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                                                boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                                            }
                                        })
                                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                                Toast.makeText(getApplication(),"Bluetooth Should be on for the apps functioning",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                AlertDialog alertDialog = altdil.create();
                                alertDialog.setTitle("Turn Location On");
                                alertDialog.show();
                            }
                        }
                );


                ufoBeaconManager.startScan(

                    new OnScanSuccessListener() {
                        @Override
                        public void onSuccess(final UFODevice ufodevice) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(ufodevice!=null){
                                        device = ufodevice;
                                        tv1.setText( "ID : "+device.getModelId() +" ");
                                        tv2.setText( "Mac Add. : "+device.getBtdevice() +" ");
                                        tv3.setText( "Type : "+device.getDeviceType() +" ");
                                        tv4.setText( "Power : "+device.txPower +" ");
                                        tv5.setText( "RSSI : "+device.getRssi() +" ");
                                        double tx = device.getRssiAt1meter();
                                        double rssi = device.getRssi();
                                        double d = Math.pow(10, ((tx - rssi)/20) );
                                        d= (double)Math.round(d*100.0)/100.0;
                                        tv6.setText( "Instant Dist.: "+d +" m");
                                        tv7.setText( "Calculated Dist.: "+device.distance +" m");
                                        tv8.setText( "Set URL : "+device.getEddystoneURL() +" ");
                                        if(device.distance >= 0.9){noti();}
                                        getDeviceLocation(device.distance);
                                    }

                                }
                            });
                        }
                    },

                    new OnFailureListener() {
                        @Override
                        public void onFailure(final int code, final String message) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                );

            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ufoBeaconManager.stopScan(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(boolean isStop) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(final int code, final String message) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }

                        }
                );
            }
        });




    }

    @Override
    protected void onDestroy() {
        Process.killProcess(Process.myPid());
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover Beacons.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                    builder.show();
                }
                return;
            }}}


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void getDeviceLocation(final double dist) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task location = fusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Location currentLocation = (Location) task.getResult();
                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 25f));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                        .radius(dist)
                        .fillColor(0x4000ff00)
                        .strokeColor(Color.BLUE)
                        .strokeWidth(2)
                );


            }
        });
    }

    public void noti(){
        NotificationCompat.Builder newBuilder = new NotificationCompat.Builder(this);
        newBuilder.setContentTitle("BeaconProject");
        newBuilder.setContentText("Your Beacon is out of 10m radius");
        newBuilder.setSmallIcon(R.drawable.bea);
        Intent i = new Intent(this, NotifyDist.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotifyDist.class);
        stackBuilder.addNextIntent(i);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        newBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, newBuilder.build());

    }
}
