package truesculpt.ui.panels;

import truesculpt.main.Managers;
import truesculpt.main.TrueSculptApp;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

/**
 * <h3>Application that displays the values of the acceleration sensor graphically.</h3>
 * 
 * <p>
 * This demonstrates the {@link android.hardware.SensorManager android.hardware.SensorManager} class.
 * 
 * <h4>Demo</h4>
 * OS / Sensors
 * 
 * <h4>Source files</h4>
 * <table class="LinkTable">
 * <tr>
 * <td >src/com.example.android.apis/os/Sensors.java</td>
 * <td >Sensors</td>
 * </tr>
 * </table>
 */
public class DebugSensorsPanel extends Activity
{
	private class GraphView extends View implements SensorListener
	{
		private Bitmap mBitmap;
		private final Canvas mCanvas = new Canvas();
		private final int mColors[] = new int[3 * 2];
		private float mHeight;
		private final float mLastValues[] = new float[3 * 2];
		private float mLastX;
		private float mMaxX;
		private final float mOrientationValues[] = new float[3];
		private final Paint mPaint = new Paint();
		private final Path mPath = new Path();
		private final RectF mRect = new RectF();
		private final float mScale[] = new float[2];
		private final float mSpeed = 1.0f;
		private float mWidth;
		private float mYOffset;

		public GraphView(Context context)
		{
			super(context);
			mColors[0] = Color.argb(192, 255, 64, 64);
			mColors[1] = Color.argb(192, 64, 128, 64);
			mColors[2] = Color.argb(192, 64, 64, 255);
			mColors[3] = Color.argb(192, 64, 255, 255);
			mColors[4] = Color.argb(192, 128, 64, 128);
			mColors[5] = Color.argb(192, 255, 255, 64);

			mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
			mRect.set(-0.5f, -0.5f, 0.5f, 0.5f);
			mPath.arcTo(mRect, 0, 180);
		}

		@Override
		public void onAccuracyChanged(int sensor, int accuracy)
		{

		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			synchronized (this)
			{
				if (mBitmap != null)
				{
					final Paint paint = mPaint;
					final Path path = mPath;
					final int outer = 0xFFC0C0C0;
					final int inner = 0xFFff7010;

					if (mLastX >= mMaxX)
					{
						mLastX = 0;
						final Canvas cavas = mCanvas;
						final float yoffset = mYOffset;
						final float maxx = mMaxX;
						final float oneG = SensorManager.STANDARD_GRAVITY * mScale[0];
						paint.setColor(0xFFAAAAAA);
						cavas.drawColor(0xFFFFFFFF);
						cavas.drawLine(0, yoffset, maxx, yoffset, paint);
						cavas.drawLine(0, yoffset + oneG, maxx, yoffset + oneG, paint);
						cavas.drawLine(0, yoffset - oneG, maxx, yoffset - oneG, paint);
					}
					canvas.drawBitmap(mBitmap, 0, 0, null);

					float[] values = mOrientationValues;
					if (mWidth < mHeight)
					{
						float w0 = mWidth * 0.333333f;
						float w = w0 - 32;
						float x = w0 * 0.5f;
						for (int i = 0; i < 3; i++)
						{
							canvas.save(Canvas.MATRIX_SAVE_FLAG);
							canvas.translate(x, w * 0.5f + 4.0f);
							canvas.save(Canvas.MATRIX_SAVE_FLAG);
							paint.setColor(outer);
							canvas.scale(w, w);
							canvas.drawOval(mRect, paint);
							canvas.restore();
							canvas.scale(w - 5, w - 5);
							paint.setColor(inner);
							canvas.rotate(-values[i]);
							canvas.drawPath(path, paint);
							canvas.restore();
							x += w0;
						}
					}
					else
					{
						float h0 = mHeight * 0.333333f;
						float h = h0 - 32;
						float y = h0 * 0.5f;
						for (int i = 0; i < 3; i++)
						{
							canvas.save(Canvas.MATRIX_SAVE_FLAG);
							canvas.translate(mWidth - (h * 0.5f + 4.0f), y);
							canvas.save(Canvas.MATRIX_SAVE_FLAG);
							paint.setColor(outer);
							canvas.scale(h, h);
							canvas.drawOval(mRect, paint);
							canvas.restore();
							canvas.scale(h - 5, h - 5);
							paint.setColor(inner);
							canvas.rotate(-values[i]);
							canvas.drawPath(path, paint);
							canvas.restore();
							y += h0;
						}
					}

				}
			}
		}

		@Override
		public void onSensorChanged(int sensor, float[] values)
		{
			// Log.d(TAG, "sensor: " + sensor + ", x: " + values[0] + ", y: " +
			// values[1] + ", z: " + values[2]);
			synchronized (this)
			{
				if (mBitmap != null)
				{
					final Canvas canvas = mCanvas;
					final Paint paint = mPaint;
					if (sensor == SensorManager.SENSOR_ORIENTATION)
					{
						for (int i = 0; i < 3; i++)
						{
							mOrientationValues[i] = values[i];
						}
					}
					else
					{
						float deltaX = mSpeed;
						float newX = mLastX + deltaX;

						int j = sensor == SensorManager.SENSOR_MAGNETIC_FIELD ? 1 : 0;
						for (int i = 0; i < 3; i++)
						{
							int k = i + j * 3;
							final float v = mYOffset + values[i] * mScale[j];
							paint.setColor(mColors[k]);
							canvas.drawLine(mLastX, mLastValues[k], newX, v, paint);
							mLastValues[k] = v;
						}
						if (sensor == SensorManager.SENSOR_MAGNETIC_FIELD)
						{
							mLastX += mSpeed;
						}
					}
					invalidate();
				}
			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{
			mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
			mCanvas.setBitmap(mBitmap);
			mCanvas.drawColor(0xFFFFFFFF);
			mYOffset = h * 0.5f;
			mScale[0] = -(h * 0.5f * 1.0f / (SensorManager.STANDARD_GRAVITY * 2));
			mScale[1] = -(h * 0.5f * 1.0f / SensorManager.MAGNETIC_FIELD_EARTH_MAX);
			mWidth = w;
			mHeight = h;
			if (mWidth < mHeight)
			{
				mMaxX = w;
			}
			else
			{
				mMaxX = w - 50;
			}
			mLastX = mMaxX;
			super.onSizeChanged(w, h, oldw, oldh);
		}
	}

	private GraphView mGraphView;

	private SensorManager mSensorManager;

	/**
	 * Initialization of the Activity after it is first created. Must at least call {@link android.app.Activity#setContentView setContentView()} to describe what is to be displayed in the screen.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Be sure to call the super class.
		super.onCreate(savedInstanceState);

		getManagers().getUtilsManager().updateFullscreenWindowStatus(getWindow());

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mGraphView = new GraphView(this);
		setContentView(mGraphView);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mSensorManager.registerListener(mGraphView, SensorManager.SENSOR_ACCELEROMETER | SensorManager.SENSOR_MAGNETIC_FIELD | SensorManager.SENSOR_ORIENTATION, SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onStop()
	{
		mSensorManager.unregisterListener(mGraphView);
		super.onStop();
	}

	public Managers getManagers()
	{
		return ((TrueSculptApp) getApplicationContext()).getManagers();
	}
}
