package com.itu;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.usb4java.BufferUtils;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

public class Usbcan {
	

	/** The ADB input endpoint of the Samsung Galaxy Nexus. */
    private static final byte IN_ENDPOINT = (byte) 0x82;

    /** The ADB output endpoint of the Samsung Galaxy Nexus. */
    private static final byte OUT_ENDPOINT = 0x02;
    
    private static final int TIMEOUT = 5000;
    
	static public Device findDevice(short vendorId, short productId)
	{
	    // Read the USB device list
	    DeviceList list = new DeviceList();
	    int result = LibUsb.getDeviceList(null, list);
	    if (result < 0) throw new LibUsbException("Unable to get device list", result);

	    try
	    {
	        // Iterate over all devices and scan for the right one
	        for (Device device: list)
	        {
	            DeviceDescriptor descriptor = new DeviceDescriptor();
	            result = LibUsb.getDeviceDescriptor(device, descriptor);
	            
//	            System.out.println(descriptor.idVendor());
//	            System.out.println(descriptor.idProduct());
	            
	            if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to read device descriptor", result);
	            if ( descriptor.idVendor() == vendorId && descriptor.idProduct() == productId) { 
	            	System.out.println("found can bus");
	            	return device;
	            	
	            }
	        }
	    }
	    finally
	    {
	        // Ensure the allocated device list is freed
	    	//System.out.println("Exception");
	        //LibUsb.freeDeviceList(list, true);
	    }

	    // Device not found
	    return null;
	}
	
	public static int USB_SendMsg(DeviceHandle handle, S_MSG msg) {
        int ret;
        byte[] SendBuff = new byte[300];
        int i = 0;
        SendBuff[i++] = (byte)(msg.message >> 24);
        SendBuff[i++] = (byte)(msg.message >> 16);
        SendBuff[i++] = (byte)(msg.message >> 8);
        SendBuff[i++] = (byte)(msg.message >> 0);
        SendBuff[i++] = msg.PackNum;
        SendBuff[i++] = (byte)(msg.size >> 8);
        SendBuff[i++] = (byte)(msg.size >> 0);
        if (msg.size > 0) {
            int j = 0;
            while (j < msg.size) {
                SendBuff[i++] = msg.buffer[j];
                ++j;
            }
        }
        write(handle, SendBuff);
        return 0;
    }
	
	public static void write(DeviceHandle handle, byte[] data)
    {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(data.length);
        
        buffer.put(data);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle, OUT_ENDPOINT, buffer,
            transferred, TIMEOUT);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to send data", result);
        }
        System.out.println(transferred.get() + " bytes sent to device");
    }

    /**
     * Reads some data from the device.
     * 
     * @param handle
     *            The device handle.
     * @param size
     *            The number of bytes to read from the device.
     * @return The read data.
     */
    public static ByteBuffer read(DeviceHandle handle, int size)
    {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(size).order(
            ByteOrder.LITTLE_ENDIAN);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle, IN_ENDPOINT, buffer,
            transferred, TIMEOUT);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to read data", result);
        }
        System.out.println(transferred.get() + " bytes read from device");
        return buffer;
    }
	
	
	public static void main(String[] args) {
		UsbDriver usbDriver = new UsbDriver();
		ControlCAN controlCan = new ControlCAN(usbDriver);
		// Scan device
        Device mUsbDevice = controlCan.VCI_ScanDevice();
        if(mUsbDevice == null){
            System.out.println("No device connected");
            return;
        }

        // Open device
        int ret = controlCan.VCI_OpenDevice();
        if(ret != ErrorType.ERR_SUCCESS){
            System.out.println("Open device error!\n");
            System.out.println(String.format("Error code: %d\n",ret));
            return;
        }else{
            System.out.println("Open device success!\n");
        }

        VCI_INIT_CONFIG_EX CAN_InitEx = new VCI_INIT_CONFIG_EX();
        CAN_InitEx.CAN_ABOM = 0;//Automatic bus-off management
        // Loop back
        CAN_InitEx.CAN_Mode = 0;
        //1Mbps
        CAN_InitEx.CAN_BRP = 12;
        CAN_InitEx.CAN_BS1 = 4;
        CAN_InitEx.CAN_BS2 = 1;
        CAN_InitEx.CAN_SJW = 1;
        CAN_InitEx.CAN_NART = 1;//No automatic retransmission
        CAN_InitEx.CAN_RFLM = 0;//Receive FIFO locked mode
        CAN_InitEx.CAN_TXFP = 1;//Transmit FIFO priority
        CAN_InitEx.CAN_RELAY = 0;
        ret = controlCan.VCI_InitCANEx((byte)0, CAN_InitEx);
        if(ret != ErrorType.ERR_SUCCESS){
            System.out.println("Init device failed!\n");
            System.out.println(String.format("Error code: %d\n",ret));
            return;
        }else{
            System.out.println("Init device success!\n");
        }
        //Set filter
        VCI_FILTER_CONFIG CAN_FilterConfig = new VCI_FILTER_CONFIG();
        CAN_FilterConfig.FilterIndex = 0;
        CAN_FilterConfig.Enable = 1;//Enable
        CAN_FilterConfig.ExtFrame = 1;
        CAN_FilterConfig.FilterMode = 0;
        CAN_FilterConfig.ID_IDE = 0;
        CAN_FilterConfig.ID_RTR = 0;
        CAN_FilterConfig.ID_Std_Ext = 1;
        CAN_FilterConfig.MASK_IDE = 0;
        CAN_FilterConfig.MASK_RTR = 0;
        CAN_FilterConfig.MASK_Std_Ext = 1;
        ret = controlCan.VCI_SetFilter((byte)0, CAN_FilterConfig);
        if(ret != ErrorType.ERR_SUCCESS){
            System.out.println("Set filter failed!\n");
            System.out.println(String.format("Error code: %d\n",ret));
            return;
        }else{
            System.out.println("Set filter success!\n");
        }

        // Start CAN
        ret = controlCan.VCI_StartCAN((byte)0);
        if(ret != ErrorType.ERR_SUCCESS){
            System.out.println("Start CAN failed!\n");
            System.out.println(String.format("Error code: %d\n",ret));
            return;
        }else{
            System.out.println("Start CAN success!\n");
        }

        while (true) {
        
        	try {
                Thread.sleep(1000);
            }catch (InterruptedException e) {
                return;
            }	
        	
        ControlCAN.VCI_CAN_OBJ CAN_SendData[] =  new ControlCAN.VCI_CAN_OBJ[1];
        for (int i = 0; i < CAN_SendData.length; i++) {
            CAN_SendData[i] = controlCan.new VCI_CAN_OBJ();
            CAN_SendData[i].DataLen = 8;
            CAN_SendData[i].Data = new byte[8];
            for (int j = 0; j < CAN_SendData[i].DataLen; j++) {
                CAN_SendData[i].Data[j] = (byte) (8-j);
        }
        CAN_SendData[i].ExternFlag = 0;
        CAN_SendData[i].RemoteFlag = 0;
        CAN_SendData[i].ID = 0x45A;
        //CAN_SendData[i].SendType = 2;
        CAN_SendData[i].SendType = 0;
        }
        ret = controlCan.VCI_Transmit((byte)0, CAN_SendData, CAN_SendData.length);
        if(ret != ErrorType.ERR_SUCCESS){
            System.out.println("Send CAN data failed!\n");
            System.out.println(String.format("Error code: %d\n",ret));
            //return;
        }else{
            System.out.println("Send CAN data success!\n");
        }

        
        
        
        
       
        	
        	
        	
        	
        	
	        // CAN read data
	        for (int i = 0; i < CAN_SendData.length; i++)
	        {
	            CAN_SendData[i] = controlCan.new VCI_CAN_OBJ();
	            CAN_SendData[i].Data = new byte[8];
	        }
	
	        int ReadDataNum;
	        int DataNum = controlCan.VCI_GetReceiveNum((byte)0);
	        if(DataNum > 0)
	        {
	            ReadDataNum = controlCan.VCI_Receive((byte)0, CAN_SendData, CAN_SendData.length);
	            for(int i = 0; i < ReadDataNum; i++)
	            {
	                System.out.println("");
	                System.out.println("--CAN_ReceiveData.RemoteFlag = "
	                        + String.format("%d", CAN_SendData[i].RemoteFlag) + "\n");
	                System.out.println("--CAN_ReceiveData.ExternFlag = "
	                        + String.format("%d", CAN_SendData[i].ExternFlag) + "\n");
	                System.out.println("--CAN_ReceiveData.ID = 0x"
	                        + String.format("%x", CAN_SendData[i].ID) + "\n");
	                System.out.println("--CAN_ReceiveData.DataLen = "
	                        + String.format("%d", CAN_SendData[i].DataLen) + "\n");
	                System.out.println("--CAN_ReceiveData.Data:");
	                for(int j = 0; j < CAN_SendData[i].DataLen; j++){
	                    System.out.print(String.format("%x",CAN_SendData[i].Data[j]) + " ");
	                }
	                System.out.println();
	                System.out.println("--CAN_ReceiveData.TimeStamp = "+ String.format("%d", CAN_SendData[i].TimeStamp));
	            }
	        }
	        
	        //controlCan.VCI_ResetCAN((byte)0);
        
        
        }
        	
        //Stop receive can data
        
	        
	
       
		 
	}
}
