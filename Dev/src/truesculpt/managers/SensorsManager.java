package truesculpt.managers;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import truesculpt.managers.PointOfViewManager.OnPointOfViewChangeListener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class SensorsManager extends BaseManager implements SensorEventListener {

	boolean bOrigSet = false;
	
	private SensorManager mSensorManager;
	
	public SensorsManager(Context baseContext) {
		super(baseContext);
		
	}
	@Override
	public void onCreate()
	{
		super.onCreate();
		Start();
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Stop();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			NotifyListeners();
		}
	}
		
	private String getUniqueDeviceID() {
		WifiManager wm = (WifiManager) getbaseContext().getSystemService(
				Context.WIFI_SERVICE);
		String macAddr = wm.getConnectionInfo().getMacAddress();

		final TelephonyManager tm = (TelephonyManager) getbaseContext().getSystemService(Context.TELEPHONY_SERVICE);

		String tmDevice = tm.getDeviceId();
		String tmSerial = tm.getSimSerialNumber();
		String androidId = android.provider.Settings.Secure.getString(
				getbaseContext().getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);

		String msg = macAddr + "\n" + tmDevice + "\n" + tmSerial + "\n"
				+ androidId + "\n";
		
		return msg;	
	}

	public void Start() {
		mSensorManager = (SensorManager) getbaseContext().getSystemService(	Context.SENSOR_SERVICE);		
		List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		for (int i = 0; i < sensorList.size(); i++) {
			mSensorManager.registerListener(SensorsManager.this,sensorList.get(i), SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void Stop() {
		mSensorManager.unregisterListener(SensorsManager.this);
	}	
	
	
	public interface OnSensorChangeListener
	{
		void onSensorChanged();
	}
	private Vector<OnSensorChangeListener> mListeners= new Vector<OnSensorChangeListener>();
	
	private void NotifyListeners()
	{
		for (OnSensorChangeListener listener : mListeners) 
		{
			listener.onSensorChanged();		
		}	
	}
	
	public void registerOnSensorChangeListener(OnSensorChangeListener listener)
	{
		mListeners.add(listener);	
	}	

}
