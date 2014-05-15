/*___Generated_by_IDEA___*/

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\InHisLight\\PrayerReminder\\src\\re\\breathpray\\com\\IVibrationRepeaterService.aidl
 */
package re.breathpray.com;
public interface IVibrationRepeaterService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements re.breathpray.com.IVibrationRepeaterService
{
private static final java.lang.String DESCRIPTOR = "re.breathpray.com.IVibrationRepeaterService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an re.breathpray.com.IVibrationRepeaterService interface,
 * generating a proxy if needed.
 */
public static re.breathpray.com.IVibrationRepeaterService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof re.breathpray.com.IVibrationRepeaterService))) {
return ((re.breathpray.com.IVibrationRepeaterService)iin);
}
return new re.breathpray.com.IVibrationRepeaterService.Stub.Proxy(obj);
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
private static class Proxy implements re.breathpray.com.IVibrationRepeaterService
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
