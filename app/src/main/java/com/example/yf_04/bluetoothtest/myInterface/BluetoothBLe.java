package com.example.yf_04.bluetoothtest.myInterface;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanCallback;
import android.content.Context;

import java.util.List;
import java.util.Map;

/**
 * Created by mohuaiyuan on 2017/8/25.
 */

/**
 *  BLE(Bluetooth Low Energy)interface ,include all of the operations about Bluetooth LE
 */
public interface BluetoothBLe {

    /**
     * Check if the current device support Bluetooth LE
     * @return true ,if the current device support Bluetooth LE
     */
    boolean isSupportBle(Context context);

    /**
     * Init bluetooth and get BluetoothAdapter
     * @param context
     * @return BluetoothAdapter
     */
    BluetoothAdapter init(Context context);

    /**
     * Open the Bluetooth LE
     * @param bluetoothAdapter
     * @return true ,if open bluetooth LE successful
     */
    boolean openBLE(BluetoothAdapter bluetoothAdapter);

    /**
     * Scan Bluetooth LE device
     * @param bluetoothAdapter
     * @param scanCallback:Bluetooth LE scan callbacks,add in api level 21 ,be used in api level 21 and above levels
     * @param leScanCallback:used to deliver LE scan results,add in api leval 18,be used in below api level 21
     */
    void startScan(BluetoothAdapter bluetoothAdapter, ScanCallback scanCallback, BluetoothAdapter.LeScanCallback leScanCallback);

    /**
     * Connect back to remote device.
     * @param context
     * @param deviceAddress :the mac address of remote device
     * @param bluetoothAdapter
     * @param bluetoothGattCallback
     * @return BluetoothGatt
     */
    BluetoothGatt connect(Context context, final String deviceAddress, BluetoothAdapter bluetoothAdapter, BluetoothGattCallback bluetoothGattCallback);

    /**
     * Connect back to remote device by the list of device mac address
     * @param context
     * @param deviceAddress :the mac address of remote device
     * @param bluetoothAdapter
     * @param bluetoothGattCallback
     * @return :Map<String ,BluetoothGatt> ,key:the mac address of remote device ,value :bluetoothGatt
     */
    Map<String ,BluetoothGatt> connect(Context context, final List<String> deviceAddress, BluetoothAdapter bluetoothAdapter, BluetoothGattCallback bluetoothGattCallback);

    /**
     * Discovers services offered by a remote device as well as their characteristics and descriptors.
     * @param bluetoothGatt
     * @return true,if successful to discover services
     */
    boolean discoverServices(BluetoothGatt bluetoothGatt);

    /**
     * Retrieves a list of supported GATT services on the connected device.
     *
     * @param bluetoothGatt
     * @return
     */
    List<BluetoothGattService> getGattServices(BluetoothGatt bluetoothGatt);

    /**
     * Returns a list of characteristics included in bluetoothGattService.
     * @param bluetoothGattService
     * @return list of characteristics included in bluetoothGattService
     */
    List<BluetoothGattCharacteristic> getCharacteristics(BluetoothGattService bluetoothGattService);

    /**
     * Returns a list of characteristics with write property included in bluetoothGattService
     * @param bluetoothGattService
     * @return :a list of characteristics with write property included in bluetoothGattService
     */
    List<BluetoothGattCharacteristic> getWritePropertyCharacteristics(BluetoothGattService bluetoothGattService);

    /**
     * Returns a list of characteristics with notify property included in bluetoothGattService
     * @param bluetoothGattService
     * @return :a list of characteristics with notify property included in bluetoothGattService
     */
    List<BluetoothGattCharacteristic> getNotifyPropertyCharacteristics(BluetoothGattService bluetoothGattService);

    /**
     * Writes a given characteristic and its values to the associated remote device.
     * @param bluetoothGatt
     * @param characteristic :a given characteristic
     * @param byteArray: values of a given characteristic
     * @return true, if the write operation was initiated successfully
     */
    boolean writeCharacteristic(BluetoothGatt bluetoothGatt,BluetoothGattCharacteristic characteristic, byte[] byteArray);

    /**
     * Open or close the notify to receiver data from characteristic.
     * @param bluetoothGatt
     * @param characteristic
     * @param enabled :true ,open notify ;false ,close notify .
     * @return true ,if the operation successful.
     */
    boolean setCharacteristicNotification(BluetoothGatt bluetoothGatt,BluetoothGattCharacteristic characteristic, boolean enabled);

    /**
     * Get the notify data from characteristic .
     * @param bluetoothGatt
     * @param characteristic
     * @return notify data
     */
    byte[]  getNotifyData(BluetoothGatt bluetoothGatt,BluetoothGattCharacteristic characteristic);


}
