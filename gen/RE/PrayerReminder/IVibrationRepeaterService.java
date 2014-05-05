/*___Generated_by_IDEA___*/

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Eisl\\IdeaProjects\\PrayerReminder\\src\\RE\\PrayerReminder\\IVibrationRepeaterService.aidl
 */
package RE.PrayerReminder;
public interface IVibrationRepeaterService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements RE.PrayerReminder.IVibrationRepeaterService
{
private static final java.lang.String DESCRIPTOR = "RE.PrayerReminder.IVibrationRepeaterService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an RE.PrayerReminder.IVibrationRepeaterService interface,
 * generating a proxy if needed.
 */
public static RE.PrayerReminder.IVibrationRepeaterService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof RE.PrayerReminder.IVibrationRepeaterService))) {
return ((RE.PrayerReminder.IVibrationRepeaterService)iin);
}
return new RE.PrayerReminder.IVibrationRepeaterService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements RE.PrayerReminder.IVibrationRepeaterService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
}
}
}
