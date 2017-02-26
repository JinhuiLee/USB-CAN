package com.itu;

public class VCI_FILTER_CONFIG {
    public byte Enable;
    public byte FilterIndex;
    public byte FilterMode;
    public byte ExtFrame;
    public int ID_Std_Ext;
    public int ID_IDE;
    public int ID_RTR;
    public int MASK_Std_Ext;
    public int MASK_IDE;
    public int MASK_RTR;
    public int Reserved;

    public VCI_FILTER_CONFIG() {
        this.Enable = 1;
        this.FilterIndex = 0;
        this.FilterMode = 0;
        this.ExtFrame = 0;
        this.ID_Std_Ext = 0;
        this.ID_IDE = 0;
        this.ID_RTR = 0;
        this.MASK_Std_Ext = 0;
        this.MASK_IDE = 0;
        this.MASK_RTR = 0;
        this.Reserved = 0;
    }
}
