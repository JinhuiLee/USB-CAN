package com.itu;

/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.PendingIntent
 *  android.hardware.usb.UsbDevice
 *  android.hardware.usb.UsbDeviceConnection
 *  android.hardware.usb.UsbEndpoint
 *  android.hardware.usb.UsbInterface
 *  android.hardware.usb.UsbManager
 *  android.util.Log
 */

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;

//public class UsbDriver {
////    private UsbManager usbManager;
////    private UsbDevice usbDevice;
////    private UsbInterface usbInterface;
////    private UsbEndpoint BulkInEndpoint;
////    private UsbEndpoint BulkOutEndpoint;
////    private UsbDeviceConnection connection;
////    private PendingIntent pendingIntent;
//    UserType UserType = new UserType();
//
//    public UsbDriver(UsbManager usbManager, PendingIntent pendingIntent) {
//        this.usbManager = usbManager;
//        this.pendingIntent = pendingIntent;
//        this.usbDevice = this.ScanDevices();
//        if (this.usbDevice != null && !usbManager.hasPermission(this.usbDevice)) {
//            usbManager.requestPermission(this.usbDevice, pendingIntent);
//        }
//    }
//
//    public UsbDevice ScanDevices() {
//        HashMap map = this.usbManager.getDeviceList();
//        for (UsbDevice device : map.values()) {
//            Log.e((String)"device", (String)("vid:" + device.getVendorId() + "   pid:" + device.getProductId() + "   " + device.getDeviceName()));
//            if (1155 != device.getVendorId() || 1638 != device.getProductId()) continue;
//            this.usbDevice = device;
//            System.out.println("Start ScanDevices");
//            this.usbManager.requestPermission(this.usbDevice, this.pendingIntent);
//            return this.usbDevice;
//        }
//        return null;
//    }
//
//    public int OpenDevice() {
//        this.usbInterface = this.usbDevice.getInterface(0);
//        int i = 0;
//        i = 0;
//        while (i < this.usbInterface.getEndpointCount()) {
//            UsbEndpoint ep = this.usbInterface.getEndpoint(i);
//            if (ep.getType() == 2) {
//                if (ep.getDirection() == 0) {
//                    this.BulkOutEndpoint = ep;
//                } else {
//                    this.BulkInEndpoint = ep;
//                }
//            }
//            ++i;
//        }
//        if (this.BulkOutEndpoint == null || this.BulkInEndpoint == null) {
//            return -8;
//        }
//        if (this.usbManager.hasPermission(this.usbDevice)) {
//            this.connection = this.usbManager.openDevice(this.usbDevice);
//            if (this.connection == null) {
//                return -8;
//            }
//        } else {
//            return -19;
//        }
//        this.connection.claimInterface(this.usbInterface, true);
//        return 0;
//    }
//
//    public int USBWriteData(byte[] writebuffer, int length, int timeout) {
//        int count = this.connection.bulkTransfer(this.BulkOutEndpoint, writebuffer, length, timeout);
//        if (length % 64 == 0) {
//            this.connection.bulkTransfer(this.BulkOutEndpoint, writebuffer, 0, timeout);
//        }
//        return count;
//    }
//
//    public int USBReadData(byte[] readbuffer, int length, int timeout) {
//        return this.connection.bulkTransfer(this.BulkInEndpoint, readbuffer, length, timeout);
//    }
//
//    public int USB_SendMsg(S_MSG msg) {
//        int ret;
//        byte[] SendBuff = new byte[10240];
//        int i = 0;
//        SendBuff[i++] = (byte)(msg.message >> 24);
//        SendBuff[i++] = (byte)(msg.message >> 16);
//        SendBuff[i++] = (byte)(msg.message >> 8);
//        SendBuff[i++] = (byte)(msg.message >> 0);
//        SendBuff[i++] = msg.PackNum;
//        SendBuff[i++] = (byte)(msg.size >> 8);
//        SendBuff[i++] = (byte)(msg.size >> 0);
//        if (msg.size > 0) {
//            int j = 0;
//            while (j < msg.size) {
//                SendBuff[i++] = msg.buffer[j];
//                ++j;
//            }
//        }
//        if ((ret = this.USBWriteData(SendBuff, i, 500)) != i) {
//            return -5;
//        }
//        return 0;
//    }
//
//    public int USB_GetMsg(UserType.PS_MSG msg) {
//        byte[] GetBuff = new byte[10240];
//        int i = 0;
//        int ret = this.USBReadData(GetBuff, 512, 1000);
//        if (ret >= 7) {
//            msg.value.message = GetBuff[0] << 24 | GetBuff[1] << 16 | GetBuff[2] << 8 | GetBuff[3] << 0;
//            msg.value.status = GetBuff[4];
//            int temp = GetBuff[5] * 256 & 65280;
//            msg.value.size = (short)(temp += GetBuff[6] & 255);
//            if (msg.value.size > 0) {
//                i = 0;
//                while (i < msg.value.size) {
//                    msg.value.buffer[i] = GetBuff[7 + i];
//                    ++i;
//                }
//            }
//            return 0;
//        }
//        return -6;
//    }
//
//    public int USB_GetMsgWithSize(UserType.PS_MSG msg) {
//        byte[] GetBuff = new byte[msg.value.size + 7];
//        int i = 0;
//        int ret = this.USBReadData(GetBuff, msg.value.size + 7, 1000);
//        if (ret >= 7) {
//            msg.value.message = GetBuff[0] << 24 | GetBuff[1] << 16 | GetBuff[2] << 8 | GetBuff[3] << 0;
//            msg.value.status = GetBuff[4];
//            int temp = GetBuff[5] * 256 & 65280;
//            msg.value.size = (short)(temp += GetBuff[6] & 255);
//            if (msg.value.size > 0) {
//                i = 0;
//                while (i < msg.value.size) {
//                    msg.value.buffer[i] = GetBuff[7 + i];
//                    ++i;
//                }
//            }
//            return 0;
//        }
//        return -6;
//    }
//
//    public int USB_GetMsgEx(UserType.PS_MSG msg) {
//        byte[] GetBuff = new byte[10240];
//        int i = 0;
//        int ret = this.USBReadData(GetBuff, 7, 1000);
//        if (ret >= 7) {
//            msg.value.message = GetBuff[0] << 24 | GetBuff[1] << 16 | GetBuff[2] << 8 | GetBuff[3] << 0;
//            msg.value.status = GetBuff[4];
//            int temp = GetBuff[5] * 256 & 65280;
//            msg.value.size = (short)(temp += GetBuff[6] & 255);
//            if (msg.value.size > 0) {
//                ret = this.USBReadData(GetBuff, msg.value.size, 1000);
//                i = 0;
//                while (i < ret) {
//                    msg.value.buffer[i] = GetBuff[i];
//                    ++i;
//                }
//            }
//            return 0;
//        }
//        System.out.printf("USB_GetMsgEx %d\n", ret);
//        return -6;
//    }
//
//    public byte USB_GetStatus() {
//        byte[] GetBuff = new byte[1];
//        int ret = this.USBReadData(GetBuff, 1, 1000);
//        if (ret != 1) {
//            return -6;
//        }
//        return GetBuff[0];
//    }
//}

