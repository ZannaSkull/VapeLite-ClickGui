package at.thvf.volve.api.clickgui.values;

import at.thvf.volve.api.clickgui.RenderUtil;
import at.thvf.volve.api.module.value.Mode;
import at.thvf.volve.api.module.value.Value;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

import java.awt.*;

import static at.thvf.volve.api.clickgui.MainWindow.mainColor;

public class ModeRender extends ValueRender {
    public ModeRender(Value v, float x, float y) {
        super(v, x, y);
    }

    @Override
    public void onRender(float valueX, float valueY) {
        float valueY1 = valueY;
        for (String m : ((Mode<?>) this.value).getModes()) {
            RenderUtil.smoothCircle(x + 8, valueY1 + 2, 4, mainColor);


            if (((Mode<?>) this.value).getValue().toString() == (m)) {
                RenderUtil.smoothCircle(x + 8f, valueY1 + 2f, 2.8f, new Color(255, 255, 255));

            }
//                    RenderUtil.drawRect(x + 6, valueY + 2, x + 11, valueY + 7, new Color(255, 255, 255));
            Minecraft.getMinecraft().fontRendererObj.drawString(m, (int) (x + 16), (int) valueY1, new Color(115, 115, 115).getRGB());
            if (isHovered(x + 5, valueY1, x + 95, valueY1 + 3, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                ((Mode<?>) this.value).setMode(m);
            }
            valueY1 += 20;
        }
    }

    @Override
    public void onClicked(float x, float y, int clickType) {

    }

    @Override
    public void onMouseMove(float x, float y, int clickType) {
        this.mouseX = x;
        this.mouseY = y;

    }
}
