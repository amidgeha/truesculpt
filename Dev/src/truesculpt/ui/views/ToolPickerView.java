package truesculpt.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ToolPickerView extends ImageView
{
	public interface OnToolPickChangedListener
	{
		void ToolValueChanged(int value);

		void ToolChangeStart(int value);

		void ToolChangeStop(int value);
	}

	public interface OnDoubleClickListener
	{
		void onDoubleClick(float value);
	}

	float mOrig_x = 0;
	float mOrig_y = 0;
	private int mnElemCount = 0;
	private int mCurrentValue = 0;
	private int mStartValue = 0;
	private int mOldValue = 0;
	private final int PixelAmplitude = 50;
	private final long mTapTapTimeThresold = 300;// ms
	private final int mnPixelDeadZone = 100;
	private long mLastTapTapTime = 0;

	private OnToolPickChangedListener mListener = null;
	private OnDoubleClickListener mDoubleClickListener = null;

	public ToolPickerView(Context c, AttributeSet attrs)
	{
		super(c, attrs);
		invalidate();
	}

	private enum State
	{
		START, CHANGE, STOP
	};

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		boolean bRes = false;

		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;

		float x = event.getX();
		float y = event.getY();

		switch (actionCode)
		{
		case MotionEvent.ACTION_DOWN:
		{
			mOrig_x = x;
			mOrig_y = y;

			long curTapTapTime = System.currentTimeMillis();
			if ((curTapTapTime - mLastTapTapTime) < mTapTapTimeThresold)
			{
				if (mDoubleClickListener != null)
				{
					mDoubleClickListener.onDoubleClick(mOldValue);
				}
			}
			else
			{
				mOldValue = mCurrentValue;
				updateToolValue(x, y, State.START);
			}
			mLastTapTapTime = curTapTapTime;

			bRes = true;
			break;
		}
		case MotionEvent.ACTION_UP:
		{
			updateToolValue(x, y, State.STOP);
			bRes = true;
			break;
		}
		case MotionEvent.ACTION_MOVE:
		{
			updateToolValue(x, y, State.CHANGE);
			bRes = true;
			break;
		}
		}

		return bRes;
	}

	private void updateToolValue(float x, float y, State state)
	{
		if (state == State.START)
		{
			mStartValue = mCurrentValue;
		}
		int newValue = mStartValue;

		float distX = x - mOrig_x;
		float distY = mOrig_y - y;
		float pixelDist = (float) Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));

		if (pixelDist >= mnPixelDeadZone)
		{
			newValue = (int) (mStartValue + Math.floor((pixelDist - mnPixelDeadZone) / PixelAmplitude));
			newValue = newValue % mnElemCount;
			Log.i("TOOLPICKER", "New value=" + newValue + ", CurrentValue=" + mCurrentValue + ", pixelDist=" + pixelDist);
		}

		// if (newValue != mCurrentValue)
		{
			if (mListener != null)
			{
				switch (state)
				{
				case START:
					mListener.ToolChangeStart(newValue);
					break;
				case CHANGE:
					mListener.ToolValueChanged(newValue);
					break;
				case STOP:
					mListener.ToolChangeStop(newValue);
					break;
				}
			}
		}
	}

	public void setCurrentValue(int currentValue, int imageID)
	{
		mCurrentValue = currentValue;

		setBackgroundResource(imageID);

		invalidate();
	}

	public float getCurrentValue()
	{
		return mCurrentValue;
	}

	public void setToolChangeListener(OnToolPickChangedListener mListener)
	{
		this.mListener = mListener;
	}

	public void setDoubleClickListener(OnDoubleClickListener listener)
	{
		mDoubleClickListener = listener;
	}

	public int getElemCount()
	{
		return mnElemCount;
	}

	public void setElemCount(int nElemCount)
	{
		this.mnElemCount = nElemCount;
	}

}
