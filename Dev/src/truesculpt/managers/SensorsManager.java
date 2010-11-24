package truesculpt.managers;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import truesculpt.managers.PointOfViewManager.OnPointOfViewChangeListener;
import truesculpt.utils.MatrixUtils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class SensorsManager extends BaseManager implements SensorEventListener {

	boolean bOrigSet = false;
	float [] origAngles= new float[3];
	float [] lastAngles= new float[3];
	float [] diffAngles= new float[3];
	
	private SensorManager mSensorManager=null;
	
	public SensorsManager(Context baseContext) {
		super(baseContext);
		
	}
	@Override
	public void onCreate()
	{
		if (getManagers().getOptionsManager().getUseSensorsToChangePOV())
		{
			restart();
		}
	}
	@Override
	public void onDestroy()
	{
		stop();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		if (event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD )
		{
			if (!bOrigSet)
			{
				MatrixUtils.copy(event.values,origAngles);
				bOrigSet=true;
			}
			
			float fAngleThresold=30.0f;
			MatrixUtils.minus(event.values, lastAngles, diffAngles);
			
			//eliminate bas points
			if (diffAngles[0]<fAngleThresold &&
				diffAngles[1]<fAngleThresold)
			{
				float rotation=-(event.values[0]-origAngles[0]);
				float elevation=+(event.values[1]-origAngles[1]);
				float zoomDistance=event.values[2]-origAngles[2];
				
				MatrixUtils.copy(event.values,lastAngles);
				
				getManagers().getPointOfViewManager().setRotationAngle(rotation);
				getManagers().getPointOfViewManager().setElevationAngle(elevation);
				
				NotifyListeners();
			}
		}
	}
			
	public void start() {
		mSensorManager = (SensorManager) getbaseContext().getSystemService(	Context.SENSOR_SERVICE);		
		List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD );
		for (int i = 0; i < sensorList.size(); i++) {
			mSensorManager.registerListener(SensorsManager.this,sensorList.get(i), SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void stop() {
		if (mSensorManager!=null)
		{
			mSensorManager.unregisterListener(SensorsManager.this);
		}
	}	
	
	
	public interface OnSensorChangeListener
	{
		void onSensorChanged();
	}
	private Vector<OnSensorChangeListener> mListeners= new Vector<OnSensorChangeListener>();
	
	private void NotifyListeners()
	{
		getManagers().getPointOfViewManager().onSensorChanged();
		
		for (OnSensorChangeListener listener : mListeners) 
		{
			listener.onSensorChanged();		
		}	
	}
	
	public void registerOnSensorChangeListener(OnSensorChangeListener listener)
	{
		mListeners.add(listener);	
	}	
	public void unRegisterOnSensorChangeListener(OnSensorChangeListener listener)
	{
		mListeners.remove(listener);	
	}
	public void restart() {
		stop();
		start();
	}	

}
