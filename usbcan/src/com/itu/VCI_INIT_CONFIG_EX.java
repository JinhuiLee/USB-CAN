package com.itu;

public class VCI_INIT_CONFIG_EX {
    public int CAN_BRP;
    public byte CAN_SJW;
    public byte CAN_BS1;
    public byte CAN_BS2;
    public byte CAN_Mode;
    public byte CAN_ABOM;
    public byte CAN_NART;
    public byte CAN_RFLM;
    public byte CAN_TXFP;
    public byte CAN_RELAY;
    public int Reserved;

    public VCI_INIT_CONFIG_EX() {
        this.CAN_BRP = 9;
        this.CAN_SJW = 1;
        this.CAN_BS1 = 2;
        this.CAN_BS2 = 1;
        this.CAN_Mode = 1;
        this.CAN_ABOM = 0;
        this.CAN_NART = 1;
        this.CAN_RFLM = 1;
        this.CAN_TXFP = 1;
        this.CAN_RELAY = 0;
        this.Reserved = 0;
    }
}
