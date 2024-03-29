package truesculpt.managers;

import android.content.Context;
import android.os.PowerManager;

public class SleepPowerManager extends BaseManager
{
	private PowerManager.WakeLock wl = null;

	public SleepPowerManager(Context baseContext)
	{
		super(baseContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate()
	{
		restart();
	}

	@Override
	public void onDestroy()
	{
		stop();
	}

	public void restart()
	{
		stop();
		start();
	}

	public void start()
	{
		// prevent sleep mode
		if (getManagers().getOptionsManager().getPreventSleepMode())
		{
			PowerManager pm = (PowerManager) getbaseContext().getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Truesculpt");
			wl.acquire();
		}
	}

	public void stop()
	{
		if (wl != null)
		{
			wl.release();
			wl = null;
		}
	}

}
