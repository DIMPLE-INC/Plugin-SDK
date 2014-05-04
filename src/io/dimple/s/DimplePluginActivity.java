package io.dimple.s;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Plugin activity allows creation of Dimple plugins. You need to extend this class. Please add
 * <pre>
 * {@code
 *
 *  <activity android:name="ActivityName">
 *      <intent-filter>
 *          <action android:name="io.dimple.action.PLUGIN"/>
 *          <category android:name="android.intent.category.DEFAULT"/>
 *      </intent-filter>
 *  </activity>
 * }
 * </pre>
 * to list this activity as simple plugin or
 * <pre>
 * {@code
 *
 *  <activity android:name="ActivityName">
 *      <intent-filter>
 *          <action android:name="io.dimple.action.PLUGIN_LONGPRESS"/>
 *          <category android:name="android.intent.category.DEFAULT"/>
 *      </intent-filter>
 *  </activity>
 * }
 * </pre>
 * to list this activity as long-press plugin in Dimple (replacing ActivityName with your activity).
 * @author Elviss Kustans
 * @version 0.9.6
 */
public abstract class DimplePluginActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		final Bundle extras = getIntent().getExtras();

		//Is plugin longpress?
		boolean longPress = DimpleConst.ACTION_PLUGIN_LONGPRESS.equals(getIntent().getAction());

		//If not, it should be called with PLUGIN action
		if (!longPress && !DimpleConst.ACTION_PLUGIN.equals(getIntent().getAction()))
		{
			finish();
			return;
		}

		//Do we have TYPE?
		if (extras != null && extras.containsKey(DimpleConst.EXTRA_TYPE_KEY))
		{
			//We call createPlugin() if type is create.
			if (DimpleConst.EXTRA_TYPE_VALUE_CREATE.equals(extras.getString(DimpleConst.EXTRA_TYPE_KEY)))
			{
				int bytesLeft = extras.getInt(DimpleConst.EXTRA_MEMORY_KEY);
				createPlugin(longPress, bytesLeft);
			}
			else if (DimpleConst.EXTRA_TYPE_VALUE_EXECUTE.equals(extras.getString(DimpleConst.EXTRA_TYPE_KEY)))
			{
				execute(extras.getByteArray(DimpleConst.EXTRA_DATA_KEY));
			}
			else
			{
				finish();
			}

		}
		else
		{
			finish();
		}
	}


	/**
	 * Override this function to execute action on plugin.
	 *
	 * @param data Data that was set to Dimple
	 */
	@SuppressWarnings("UnusedDeclaration")
	public void execute(byte[] data)
	{

	}

	public abstract void createPlugin(boolean longPress, int memorySize);


	/**
	 * Cancels plugin creation and finishes activity
	 */
	@SuppressWarnings("unused")
	public void cancelCreatingPlugin()
	{
		setResult(RESULT_CANCELED);
		finish();
	}


	/**
	 * Sets activity result and finishes activity. Usuable for both long-press and simple plugins.
	 *
	 * @param data data to send back to Dimple. This data will be sent back to app when plugin is launched.
	 */
	@SuppressWarnings("unused")
	public void finishCreatingPlugin(byte[] data)
	{
		Intent result = new Intent();
		result.putExtra(DimpleConst.EXTRA_DATA_KEY, data);

		setResult(RESULT_OK, result);
		finish();
	}

	/**
	 * Sets activity result and finishes activity. Usuable only with long-press activity.
	 *
	 * @param data     data to send back to Dimple. This data will be sent back to app when plugin is launched.
	 * @param interval interval between hold events. Interval can be:
	 *                 negative = don't send hold events,
	 *                 0 = send as fast as possible,
	 *                 >0 = try to send hold event once each @interval milliseconds (exact send rate not guaranteed, depends on RF communication delays)
	 */
	@SuppressWarnings("unused")
	public void finishCreatingPlugin(byte[] data, long interval)
	{
		Intent result = new Intent();
		result.putExtra(DimpleConst.EXTRA_DATA_KEY, data);
		result.putExtra(DimpleConst.EXTRA_EVENTINTERVAL_KEY, interval);

		setResult(RESULT_OK, result);
		finish();
	}

}
