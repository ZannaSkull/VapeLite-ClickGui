package at.thvf.volve.api.clickgui.gui.theme;


import java.awt.*;

public class Theme {
    public Color BG_4 = new Color(32,31,35);
    public Color BG = new Color(21, 22, 25);
    public Color BG_2 = new Color(37, 35, 39);
    public Color BG_3 = new Color(41, 117, 221);
    public Color Modules = new Color(32, 31, 33);
    public Color Modules_C = new Color(40, 40, 40);
    public Color Option_U_B = new Color(16, 16, 16);
    public Color Option_U_C = new Color(57, 58, 63);
    public Color Option_B = new Color(33, 94, 181);
    public Color Option_C = new Color(247, 249, 252);
    public Color FONT_C = new Color(255, 255, 255);
    public Color FONT = new Color(85, 86, 90);

    
    public void setWhite(){
        BG = new Color(241,243,247);
        BG_2 = new Color(170, 170, 170);
        BG_3 = new Color(41, 117, 221);
        Modules = new Color(251,251,251);
        Modules_C = new Color(245,247,248);
        Option_U_B = new Color(238, 238, 238);
        Option_U_C = new Color(255, 255, 255);
        Option_B = new Color(33, 94, 181);
        Option_C = new Color(247, 249, 252);
        FONT_C = new Color(26, 26, 26);
        FONT = new Color(108, 108, 108);
        BG_4 = new Color(219, 219, 219);
    }
    
    public void setDark(){
        BG_4 = new Color(72,72,72);
        BG = new Color(32,32,32);
        BG_2 = new Color(37, 35, 39);
        BG_3 = new Color(95, 61, 239);
        Modules = new Color(43,43,43);
        Modules_C = new Color(53,53,53);
        Option_U_B = new Color(69,69,69);
        Option_U_C = new Color(206,206,206);
        Option_B = new Color(95, 61, 239);
        Option_C = new Color(206,206,206);
        FONT = new Color(224,224,224);
        FONT_C = new Color(255,255,255);
    }

}
