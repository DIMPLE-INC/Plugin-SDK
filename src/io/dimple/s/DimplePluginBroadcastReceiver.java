package io.dimple.s;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Dimple plugin broadcast receiver. You need to extend this class to use it. Please add
 * <pre>
 * {@code
 *
 *  <receiver android:name="ReceiverName">
 *      <intent-filter>
 *          <action android:name="io.dimple.action.PLUGIN_LONGPRESS"/>
 *      </intent-filter>
 *  </receiver>
 * }
 * </pre>
 * to AndroidManifest.xml to receive Dimple long-press events (replacing ReceiverName with your receiver).
 * @author Elviss Kustans
 * @version 0.9.6
 */
public abstract class DimplePluginBroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		final String action = intent.getAction();
		//Checking if action is correct
		if (!DimpleConst.ACTION_PLUGIN_LONGPRESS.equals(action))
		{
			return;
		}

		String eventType = intent.getStringExtra(DimpleConst.EXTRA_BC_EVENT_KEY);
		long holdLength = intent.getLongExtra(DimpleConst.EXTRA_BC_TIME_KEY, 0);
		byte[] data = intent.getByteArrayExtra(DimpleConst.EXTRA_BC_DATA_KEY);


		//Don't use switch: java6 compatibility
		if (DimpleConst.EXTRA_BC_EVENT_DOWN.equals(eventType))
		{
			//Hold length always 0 here
			onDimpleDown(context, data);
		}
		else if (DimpleConst.EXTRA_BC_EVENT_HOLD.equals(eventType))
		{
			long timeSinceLastHoldEvent = intent.getLongExtra(DimpleConst.EXTRA_BC_HOLD_INTERVAL_KEY, 0);
			onDimpleHold(context, holdLength, data, timeSinceLastHoldEvent);

		}
		else if (DimpleConst.EXTRA_BC_EVENT_UP.equals(eventType))
		{
			onDimpleUp(context, holdLength, data);
		}

	}

	/**
	 * Called when Dimple is pressed down.
	 *
	 * @param context Context from Broadcast Receiver
	 * @param data    Data sent to Dimple when plugin was created
	 */
	public abstract void onDimpleDown(Context context, byte[] data);

	/**
	 * Called regularly while Dimple is pressed down. Will not be called if interval was set to negative value during setup.
	 *
	 * @param context                Context from Broadcast Receiver
	 * @param holdLength             Milliseconds from Dimple down press.
	 * @param data                   Data sent to Dimple when plugin was created
	 * @param timeSinceLastHoldEvent Time between this and last hold event (or down event if this is first).
	 */
	public abstract void onDimpleHold(Context context, long holdLength, byte[] data, long timeSinceLastHoldEvent);

	/**
	 * Called when Dimple is released.
	 *
	 * @param context    Context from Broadcast Receiver
	 * @param holdLength Milliseconds from Dimple down press (full press length)
	 * @param data       Data sent to Dimple when plugin was created
	 */
	public abstract void onDimpleUp(Context context, long holdLength, byte[] data);

}
