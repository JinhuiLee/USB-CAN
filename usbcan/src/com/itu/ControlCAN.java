package com.itu;

/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.PendingIntent
 *  android.hardware.usb.UsbDevice
 *  android.hardware.usb.UsbManager
 */

import com.itu.S_MSG;
import com.itu.UsbDriver;
import com.itu.UserType;
import java.util.LinkedList;
import java.util.Queue;

import org.usb4java.Device;

public class ControlCAN {
    UsbDriver USB_Driver;
    UserType userType = new UserType();
    Thread Can1Thread;
    Thread Can2Thread;
    private volatile boolean Can1ExitFlag = false;
    private volatile boolean Can2ExitFlag = false;
    Queue<VCI_CAN_OBJ> CAN1_DataBufQueue = new LinkedList<VCI_CAN_OBJ>();
    Queue<VCI_CAN_OBJ> CAN2_DataBufQueue = new LinkedList<VCI_CAN_OBJ>();

//    public ControlCAN(UsbManager usbManager, PendingIntent pendingIntent) {
//        this.USB_Driver = new UsbDriver(usbManager, pendingIntent);
//    }

    public ControlCAN(UsbDriver USBDriver) {
        this.USB_Driver = USBDriver;
    }

    public Device VCI_ScanDevice() {
        return this.USB_Driver.ScanDevices();
    }

    public int VCI_OpenDevice() {
        return this.USB_Driver.OpenDevice();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int VCI_InitCANEx(byte CANIndex, VCI_INIT_CONFIG_EX pInitConfig) {
        int i;
        S_MSG msg = new S_MSG(64);
        int n = i = 0;
        i = (short)(n + 1);
        msg.buffer[n] = (byte)(CANIndex + 1);
        int n2 = i;
        i = (short)(n2 + 1);
        msg.buffer[n2] = 0;
        int n3 = i;
        i = (short)(n3 + 1);
        msg.buffer[n3] = (byte)(pInitConfig.CAN_ABOM % 2);
        int n4 = i;
        i = (short)(n4 + 1);
        msg.buffer[n4] = 0;
        int n5 = i;
        i = (short)(n5 + 1);
        msg.buffer[n5] = (byte)(pInitConfig.CAN_NART % 2);
        int n6 = i;
        i = (short)(n6 + 1);
        msg.buffer[n6] = (byte)(pInitConfig.CAN_RFLM % 2);
        int n7 = i;
        i = (short)(n7 + 1);
        msg.buffer[n7] = (byte)(pInitConfig.CAN_TXFP % 2);
        int n8 = i;
        i = (short)(n8 + 1);
        msg.buffer[n8] = (byte)(pInitConfig.CAN_Mode % 4);
        int n9 = i;
        i = (short)(n9 + 1);
        msg.buffer[n9] = (byte) (pInitConfig.CAN_SJW > 0 ? (pInitConfig.CAN_SJW - 1) % 4 : 0);
        int n10 = i;
        i = (short)(n10 + 1);
        msg.buffer[n10] = (byte) (pInitConfig.CAN_BS1 > 0 ? (pInitConfig.CAN_BS1 - 1) % 16 : 0);
        int n11 = i;
        i = (short)(n11 + 1);
        msg.buffer[n11] = (byte) (pInitConfig.CAN_BS2 > 0 ? (pInitConfig.CAN_BS2 - 1) % 8 : 0);
        int n12 = i;
        i = (short)(n12 + 1);
        msg.buffer[n12] = (byte)(pInitConfig.CAN_BRP >> 8);
        int n13 = i;
        i = (short)(n13 + 1);
        msg.buffer[n13] = (byte)pInitConfig.CAN_BRP;
        int n14 = i;
        i = (short)(n14 + 1);
        msg.buffer[n14] = pInitConfig.CAN_RELAY;
        msg.size = (short) i;
        msg.PackNum = 0;
        msg.message = msg.MSG.MSG_VT_CAN_INIT;
        ControlCAN controlCAN = this;
        synchronized (controlCAN) {
            block5 : {
                int ret = this.USB_Driver.USB_SendMsg(msg);
                if (ret != 0) {
                    return ret;
                }
                if (msg.STATUS.MSG_CAN_NINT_OK == this.USB_Driver.USB_GetStatus()) break block5;
                return -10;
            }
            return 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int VCI_SetFilter(byte CANIndex, VCI_FILTER_CONFIG pFilter) {
        S_MSG msg = new S_MSG(64);
        int i = 0;
        int ID_StdId = 0;
        int ID_ExtId = 0;
        int MASK_StdId = 0;
        int MASK_ExtId = 0;
        int n = i;
        i = (short)(n + 1);
        msg.buffer[n] = (byte)(CANIndex + 1);
        pFilter.FilterIndex = CANIndex == 1 ? (byte)(pFilter.FilterIndex % 14 + 14) : (byte)(pFilter.FilterIndex % 14);
        int n2 = i;
        i = (short)(n2 + 1);
        msg.buffer[n2] = pFilter.FilterIndex;
        int n3 = i;
        i = (short)(n3 + 1);
        msg.buffer[n3] = pFilter.FilterMode;
        int n4 = i;
        i = (short)(n4 + 1);
        msg.buffer[n4] = 1;
        if (pFilter.ExtFrame == 1) {
            ID_ExtId = pFilter.ID_Std_Ext & 262143;
            ID_StdId = pFilter.ID_Std_Ext >> 18 & 2047;
        } else {
            ID_ExtId = 0;
            ID_StdId = pFilter.ID_Std_Ext & 2047;
        }
        if (pFilter.FilterMode == 0) {
            if (pFilter.ExtFrame == 1) {
                MASK_ExtId = pFilter.MASK_Std_Ext & 262143;
                MASK_StdId = pFilter.MASK_Std_Ext >> 18 & 2047;
            } else {
                MASK_ExtId = 0;
                MASK_StdId = pFilter.MASK_Std_Ext & 2047;
            }
        }
        int CAN_FilterIdHigh = (ID_StdId & 2047) << 5 | ID_ExtId >> 13 & 31;
        int CAN_FilterIdLow = (ID_ExtId & 8191) << 3 | (pFilter.ID_IDE & 1) << 2 | (pFilter.ID_RTR & 1) << 1;
        int CAN_FilterMaskIdHigh = (MASK_StdId & 2047) << 5 | MASK_ExtId >> 13 & 31;
        int CAN_FilterMaskIdLow = (MASK_ExtId & 8191) << 3 | (pFilter.MASK_IDE & 1) << 2 | (pFilter.MASK_RTR & 1) << 1;
        int n5 = i;
        i = (short)(n5 + 1);
        msg.buffer[n5] = (byte)(CAN_FilterIdHigh >> 8);
        int n6 = i;
        i = (short)(n6 + 1);
        msg.buffer[n6] = (byte)CAN_FilterIdHigh;
        int n7 = i;
        i = (short)(n7 + 1);
        msg.buffer[n7] = (byte)(CAN_FilterIdLow >> 8);
        int n8 = i;
        i = (short)(n8 + 1);
        msg.buffer[n8] = (byte)CAN_FilterIdLow;
        int n9 = i;
        i = (short)(n9 + 1);
        msg.buffer[n9] = (byte)(CAN_FilterMaskIdHigh >> 8);
        int n10 = i;
        i = (short)(n10 + 1);
        msg.buffer[n10] = (byte)CAN_FilterMaskIdHigh;
        int n11 = i;
        i = (short)(n11 + 1);
        msg.buffer[n11] = (byte)(CAN_FilterMaskIdLow >> 8);
        int n12 = i;
        i = (short)(n12 + 1);
        msg.buffer[n12] = (byte)CAN_FilterMaskIdLow;
        if ((pFilter.FilterIndex & 1) == 0) {
            int n13 = i;
            i = (short)(n13 + 1);
            msg.buffer[n13] = 0;
            int n14 = i;
            i = (short)(n14 + 1);
            msg.buffer[n14] = 1;
        } else {
            int n15 = i;
            i = (short)(n15 + 1);
            msg.buffer[n15] = 0;
            int n16 = i;
            i = (short)(n16 + 1);
            msg.buffer[n16] = 0;
        }
        int n17 = i;
        i = (short)(n17 + 1);
        msg.buffer[n17] = pFilter.Enable;
        msg.size = (short) i;
        msg.PackNum = 0;
        msg.message = msg.MSG.MSG_VT_CAN_INIT_FILTER;
        ControlCAN controlCAN = this;
        synchronized (controlCAN) {
            block12 : {
                int ret = this.USB_Driver.USB_SendMsg(msg);
                if (ret != 0) {
                    return ret;
                }
                if (msg.STATUS.MSG_CAN_NINT_OK == this.USB_Driver.USB_GetStatus()) break block12;
                return -10;
            }
        }
        return 0;
    }

    private int CopyCanDataToBuffer(byte[] DataBuff, VCI_CAN_OBJ[] TxMessage, int Len, byte CANIndex, int RemainSpace) {
        int i = 0;
        int j = 0;
        int DLC = 0;
        int DataIndex = 0;
        while (Len > 0) {
            int n = DLC = TxMessage[DataIndex].DataLen > 8 ? 8 : TxMessage[DataIndex].DataLen;
            if (RemainSpace < DLC + 5) break;
            DataBuff[i++] = (byte)(CANIndex + 1 & 3 | (TxMessage[DataIndex].RemoteFlag & 1) << 2 | (TxMessage[DataIndex].ExternFlag & 1) << 3 | (DLC & 255) << 4);
            --RemainSpace;
            if (TxMessage[DataIndex].ExternFlag == 1) {
                DataBuff[i++] = (byte)(TxMessage[DataIndex].ID >> 0);
                DataBuff[i++] = (byte)(TxMessage[DataIndex].ID >> 8);
                DataBuff[i++] = (byte)(TxMessage[DataIndex].ID >> 16);
                DataBuff[i++] = (byte)(TxMessage[DataIndex].ID >> 24);
                RemainSpace -= 4;
            } else {
                DataBuff[i++] = (byte)(TxMessage[DataIndex].ID >> 0);
                DataBuff[i++] = (byte)(TxMessage[DataIndex].ID >> 8);
                RemainSpace -= 2;
            }
            j = 0;
            while (j < DLC) {
                DataBuff[i++] = TxMessage[DataIndex].Data[j];
                ++j;
            }
            RemainSpace -= j;
            ++DataIndex;
            --Len;
        }
        return i;
    }

    private int CopyCanDataFromBuffer(byte[] DataBuff, short DataSize, byte CANIndex) {
        short i = 0;
        while (i < DataSize) {
            int j;
            VCI_CAN_OBJ pReceive = new VCI_CAN_OBJ();
            pReceive.ExternFlag = (byte)(DataBuff[i] >> 3 & 1);
            pReceive.RemoteFlag = (byte)(DataBuff[i] >> 2 & 1);
            pReceive.DataLen = (byte)(DataBuff[i] >> 4 & 15);
            pReceive.TimeFlag = 1;
            i = (short)(i + 1);
            if (pReceive.ExternFlag == 0) {
                pReceive.ID = ((DataBuff[i] & 255) << 0) + ((DataBuff[i + 1] & 255) << 8) & 2047;
                i = (short)(i + 2);
                pReceive.TimeStamp = ((DataBuff[i] & 255) << 0) + ((DataBuff[i + 1] & 255) << 8) + ((DataBuff[i + 2] & 255) << 16) + ((DataBuff[i + 3] & 255) << 24);
                i = (short)(i + 4);
                if (pReceive.RemoteFlag == 0 && pReceive.DataLen > 0) {
                    j = 0;
                    j = 0;
                    while (j < pReceive.DataLen) {
                        pReceive.Data[j] = DataBuff[i + j];
                        ++j;
                    }
                    i = (short)(i + j);
                }
            } else {
                pReceive.ID = ((DataBuff[i] & 255) << 0) + ((DataBuff[i + 1] & 255) << 8) + ((DataBuff[i + 2] & 255) << 16) + ((DataBuff[i + 3] & 255) << 24) & 536870911;
                i = (short)(i + 4);
                pReceive.TimeStamp = ((DataBuff[i] & 255) << 0) + ((DataBuff[i + 1] & 255) << 8) + ((DataBuff[i + 2] & 255) << 16) + ((DataBuff[i + 3] & 255) << 24);
                i = (short)(i + 4);
                if (pReceive.RemoteFlag == 0 && pReceive.DataLen > 0) {
                    j = 0;
                    j = 0;
                    while (j < pReceive.DataLen) {
                        pReceive.Data[j] = DataBuff[i + j];
                        ++j;
                    }
                    i = (short)(i + j);
                }
            }
            if (CANIndex == 0) {
                this.CAN1_DataBufQueue.offer(pReceive);
                continue;
            }
            this.CAN2_DataBufQueue.offer(pReceive);
        }
        return i;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int VCI_Transmit(byte CANIndex, VCI_CAN_OBJ[] pSend, int Len) {
        S_MSG msg = new S_MSG(10240);
        int bufferSpace = 10233;
        int copyDataNum = this.CopyCanDataToBuffer(msg.buffer, pSend, Len, CANIndex, bufferSpace);
        msg.size = (short)copyDataNum;
        msg.PackNum = 0;
        msg.message = msg.MSG.MSG_VT_CAN_WRITE_DATAS;
        ControlCAN controlCAN = this;
        synchronized (controlCAN) {
            block5 : {
                int ret = this.USB_Driver.USB_SendMsg(msg);
                if (ret != 0) {
                    return ret;
                }
                if (msg.STATUS.MSG_CAN_WRITE_DATA_OK == this.USB_Driver.USB_GetStatus()) break block5;
                return -10;
            }
            return 0;
        }
    }

    public int VCI_StartCAN(byte CANIndex) {
        if (CANIndex == 0) {
            this.Can1ExitFlag = false;
            this.Can1Thread = new Thread(new VCI_Can1ReadThread());
            if (this.Can1Thread == null) {
                return -10;
            }
            this.Can1Thread.start();
        } else {
            this.Can2ExitFlag = false;
            this.Can2Thread = new Thread(new VCI_Can2ReadThread());
            if (this.Can2Thread == null) {
                return -10;
            }
            this.Can2Thread.start();
        }
        return 0;
    }

    public int VCI_ResetCAN(byte CANIndex) {
        if (CANIndex == 0) {
            System.out.println("Request Stop VCI_Can1ReadThread");
            if (!this.Can1ExitFlag) {
                this.Can1ExitFlag = true;
                try {
                    this.Can1Thread.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Request Stop VCI_Can2ReadThread");
            if (!this.Can2ExitFlag) {
                this.Can2ExitFlag = true;
                try {
                    this.Can2Thread.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int VCI_Receive(byte CANIndex, VCI_CAN_OBJ[] pReceive, int Len) {
        int i = 0;
        if (CANIndex == 0) {
            ControlCAN controlCAN = this;
            synchronized (controlCAN) {
                while (this.CAN1_DataBufQueue.size() > 0 && i < Len) {
                    pReceive[i++] = this.CAN1_DataBufQueue.poll();
                }
            }
            return i;
        }
        ControlCAN controlCAN = this;
        synchronized (controlCAN) {
            while (this.CAN2_DataBufQueue.size() > 0 && i < Len) {
                pReceive[i++] = this.CAN2_DataBufQueue.poll();
            }
        }
        return i;
    }

    public int VCI_GetReceiveNum(byte CANIndex) {
        if (CANIndex == 0) {
            return this.CAN1_DataBufQueue.size();
        }
        return this.CAN2_DataBufQueue.size();
    }

    public int VCI_ClearBuffer(byte CANIndex) {
        if (CANIndex == 0) {
            this.CAN1_DataBufQueue.clear();
        } else {
            this.CAN2_DataBufQueue.clear();
        }
        return 0;
    }

    public int VCI_CloseDevice() {
        this.VCI_ResetCAN((byte)0);
        this.VCI_ResetCAN((byte)1);
        return 0;
    }

    static /* synthetic */ void access$1(ControlCAN controlCAN, boolean bl) {
        controlCAN.Can1ExitFlag = bl;
    }

    static /* synthetic */ void access$4(ControlCAN controlCAN, boolean bl) {
        controlCAN.Can2ExitFlag = bl;
    }

    public static interface PVCI_RECEIVE_CALLBACK {
        public void ReceiveCallback(byte var1, int var2);
    }

    public class VCI_BOARD_INFO_EX {
        public byte[] ProductName;
        public byte[] FirmwareVersion;
        public byte[] HardwareVersion;
        public byte[] SerialNumber;

        public VCI_BOARD_INFO_EX() {
            this.ProductName = new byte[32];
            this.FirmwareVersion = new byte[4];
            this.HardwareVersion = new byte[4];
            this.SerialNumber = new byte[12];
        }
    }

    public class VCI_CAN_OBJ {
        public int ID;
        public int TimeStamp;
        public byte TimeFlag;
        public byte SendType;
        public byte RemoteFlag;
        public byte ExternFlag;
        public byte DataLen;
        public byte[] Data;
        public byte[] Reserved;

        public VCI_CAN_OBJ() {
            this.Data = new byte[20];
            this.Reserved = new byte[3];
        }
    }

    public class VCI_CAN_STATUS {
        public byte ErrInterrupt;
        public byte regMode;
        public byte regStatus;
        public byte regALCapture;
        public byte regECCapture;
        public byte regEWLimit;
        public byte regRECounter;
        public byte regTECounter;
        public int regESR;
        public int regTSR;
        public int BufferSize;
        public int Reserved;
    }

    class VCI_Can1ReadThread
    extends Thread {
        S_MSG msg;
        int ret;
        short i;
        byte[] pReadData;

        VCI_Can1ReadThread() {
            this.msg = new S_MSG(10240);
            this.pReadData = new byte[10240];
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            System.out.println("Creating VCI_Can1ReadThread");
            while (!ControlCAN.this.Can1ExitFlag) {
                //System.out.println("runing VCI_Can1ReadThread");
                try {
                    Thread.sleep(20);
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                VCI_Can1ReadThread e2 = this;
                synchronized (e2) {
                    short s = this.i = 0;
                    this.i = (short)(s + 1);
                    this.msg.buffer[s] = 1;
                    this.msg.size = this.i;
                    this.msg.message = this.msg.MSG.MSG_VT_CAN_GET_DATAS_NUM;
                    this.msg.PackNum = 0;
                    this.ret = ControlCAN.this.USB_Driver.USB_SendMsg(this.msg);
                    if (this.ret != 0) {
                        ControlCAN.access$1(ControlCAN.this, true);
                        break;
                    }
                    if (ControlCAN.this.USB_Driver.USB_GetStatus() != 0) {
                        short s2 = this.i = 0;
                        this.i = (short)(s2 + 1);
                        this.msg.buffer[s2] = 1;
                        this.msg.size = this.i;
                        this.msg.message = this.msg.MSG.MSG_VT_CAN_READ_DATAS_EX;
                        this.msg.PackNum = 0;
                        this.ret = ControlCAN.this.USB_Driver.USB_SendMsg(this.msg);
                        if (this.ret != 0) {
                            ControlCAN.access$1(ControlCAN.this, true);
                            break;
                        }
//                        UserType userType = ControlCAN.this.UserType;
//                        userType.getClass();
                        //UserType.PS_MSG GetMsg = new UserType.
                        UserType.PS_MSG GetMsg = userType.new PS_MSG(userType, this.pReadData);
                        this.ret = ControlCAN.this.USB_Driver.USB_GetMsgEx(GetMsg);
                        if (this.ret != 0) {
                            ControlCAN.access$1(ControlCAN.this, true);
                            break;
                        }
                        if (GetMsg.value.status == GetMsg.value.STATUS.MSG_CAN_READ_DATA_OK) {
                            ControlCAN.this.CopyCanDataFromBuffer(this.pReadData, GetMsg.value.size, (byte)0);
                            continue;
                        }
                        ControlCAN.access$1(ControlCAN.this, true);
                        break;
                    }
                    continue;
                }
            }
        }
    }

    class VCI_Can2ReadThread
    extends Thread {
        S_MSG msg;
        int ret;
        short i;
        byte[] pReadData;

        VCI_Can2ReadThread() {
            this.msg = new S_MSG(10240);
            this.pReadData = new byte[10240];
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            System.out.println("Creating VCI_Can2ReadThread");
            while (!ControlCAN.this.Can2ExitFlag) {
                try {
                    Thread.sleep(20);
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                VCI_Can2ReadThread e2 = this;
                synchronized (e2) {
                    short s = this.i = 0;
                    this.i = (short)(s + 1);
                    this.msg.buffer[s] = 2;
                    this.msg.size = this.i;
                    this.msg.message = this.msg.MSG.MSG_VT_CAN_GET_DATAS_NUM;
                    this.msg.PackNum = 0;
                    this.ret = ControlCAN.this.USB_Driver.USB_SendMsg(this.msg);
                    if (this.ret != 0) {
                        ControlCAN.access$4(ControlCAN.this, true);
                        break;
                    }
                    if (ControlCAN.this.USB_Driver.USB_GetStatus() != 0) {
                        short s2 = this.i = 0;
                        this.i = (short)(s2 + 1);
                        this.msg.buffer[s2] = 2;
                        this.msg.size = this.i;
                        this.msg.message = this.msg.MSG.MSG_VT_CAN_READ_DATAS_EX;
                        this.msg.PackNum = 0;
                        this.ret = ControlCAN.this.USB_Driver.USB_SendMsg(this.msg);
                        if (this.ret != 0) {
                            ControlCAN.access$4(ControlCAN.this, true);
                            break;
                        }
                        
                        UserType.PS_MSG GetMsg = userType.new PS_MSG(userType, this.pReadData);
                        //UserType.PS_MSG GetMsg = new UserType.PS_MSG(userType, this.pReadData);
                        this.ret = ControlCAN.this.USB_Driver.USB_GetMsgEx(GetMsg);
                        if (this.ret != 0) {
                            ControlCAN.access$4(ControlCAN.this, true);
                            break;
                        }
                        if (GetMsg.value.status == GetMsg.value.STATUS.MSG_CAN_READ_DATA_OK) {
                            ControlCAN.this.CopyCanDataFromBuffer(this.pReadData, GetMsg.value.size, (byte)1);
                            continue;
                        }
                        ControlCAN.access$4(ControlCAN.this, true);
                        break;
                    }
                    continue;
                }
            }
        }
    }

    

}


