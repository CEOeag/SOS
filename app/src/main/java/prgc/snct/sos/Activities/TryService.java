package prgc.snct.sos.Activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import prgc.snct.sos.R;

//
//
public class TryService extends Service {

    private final static String TAG = "TryService#";
    int oldscount=0;
    int flag=0;
    Context con =this;
    int scount;
    double lat,lng,Latr,Lngr;

    private int mCount = 0;


    private Handler mHandler = new Handler();


    private boolean mThreadActive = true;
    protected Runnable mTask = new Runnable() {

        @Override
        public void run() {
            final GetSOS d=new GetSOS(con);
            lat=d.lat;
            lng=d.lng;
            Latr=d.Latr;
            Lngr=d.Lngr;

            while (mThreadActive) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }


                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if(mThreadActive==true) {
                            //mCount++;
                            //showText("Service was bound.");


                                scount=d.geter(lat,lng,Latr,Lngr);


                            if(scount>oldscount&&flag==1) {
                                showNotification(TryService.this);
                            }
                            flag=1;

                            if(oldscount<scount)
                                oldscount=scount;


                        }
                    }
                });
            }


            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    showText("Thread end");
                }
            });
        }
    };
    private Thread mThread;

    // ______________________________________________________________________________
    /**

     */
    private void showText(Context ctx, final String text) {
        Toast.makeText(this, TAG + text, Toast.LENGTH_SHORT).show();
    }

    // ______________________________________________________________________________
    /**

     */
    private void showText(final String text) {
        showText(this, text);
    }


    // ______________________________________________________________________________
    @Override
    public IBinder onBind(Intent intent) {
        this.showText("Service was bound.");
        return null;
    }

    // ______________________________________________________________________________
    @Override   // onStartCommand:
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        this.showText("onStartCommand");
        this.mThread = new Thread(null, mTask, "NortifyingService");
        this.mThread.start();


        //showNotification(this);


        return START_STICKY;
    }

    // ______________________________________________________________________________
    @Override
    public void onCreate() {
        this.showText("Service has been begun.");
        super.onCreate();
    }


    // ______________________________________________________________________________
    @Override   // onDestroy:
    public void onDestroy() {
        this.showText("Service has been ended.");


        this.mThread.interrupt();
        this.mThreadActive = false;

        this.stopNotification(this);
        super.onDestroy();
    }

    // ______________________________________________________________________________

    public static void stopNotification(final Context ctx) {
        NotificationManager mgr = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.cancel(R.layout.activity_service);
    }

    // ______________________________________________________________________________

    private void showNotification(final Context ctx) {

        NotificationManager mgr = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(ctx, ActivityService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);


        Notification n = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("There is a rescue request person..")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("SOS")
                .setContentText("When a tap is done, the location of the linchpin savior is indicated.")
                .setContentIntent(contentIntent)
                .build();
        n.defaults |= Notification.DEFAULT_ALL;
        n.flags = Notification.FLAG_NO_CLEAR;

        mgr.notify(R.layout.activity_service, n);

    }

    //This bar, please choose "service end" behind the tap.
}