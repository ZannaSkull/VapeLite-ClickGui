package at.thvf.volve.api.clickgui.values;
import at.thvf.volve.api.clickgui.RenderUtil;
import at.thvf.volve.api.module.value.Numbers;
import at.thvf.volve.api.module.value.Value;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.math.BigDecimal;

import static at.thvf.volve.api.clickgui.MainWindow.mainColor;

public class NumberRender extends ValueRender {
    public NumberRender(Value v, float x, float y) {
        super(v, x, y);
    }

    @Override
    public void onRender(float valueX, float valueY) {
        this.valueX = valueX;
        this.valueY = valueY;
        float present = 90 * (((Number) value.getValue()).floatValue() - ((Numbers) value).getMinimum().floatValue()) / (((Numbers) value).getMaximum().floatValue() - ((Numbers) value).getMinimum().floatValue());
        Minecraft.getMinecraft().fontRendererObj.drawString(value.getDisplayName(), (int) (x + 5), (int) (valueY - 5), new Color(115,115,115).getRGB());
        Minecraft.getMinecraft().fontRendererObj.drawString(value.getValue().toString(), (int) (x + 95 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(value.getValue().toString())), (int) (valueY - 5), new Color(115,115,115).getRGB());
        value.animX1 = present;

        if (value.animX != value.animX1) {
            value.animX += (value.animX1 - value.animX) / 30;
        }

        RenderUtil.drawRect(x + 5, valueY + 3, x + 95, valueY + 4.5f, new Color(222, 222, 222));
        RenderUtil.drawRect(x + 5, valueY + 3, x + 5 + value.animX, valueY + 4.5f, mainColor);

        if ((isHovered(x + 5, valueY + 0.5f, x + 95, valueY + 6.5f, mouseX, mouseY) || value.drag) && Mouse.isButtonDown(0)) {
            value.drag = true;
            float render2 = ((Numbers) value).getMinimum().floatValue();
            double max = ((Numbers) value).getMaximum().doubleValue();
            float inc = (((Numbers<?>) value).getIncrement()).floatValue();
            double valAbs = (double) mouseX - x - 5;
            double perc = valAbs / (((x + 95) - (x + 5)));
            perc = Math.min(Math.max(0.0D, perc), 1.0D);
            double valRel = (max - render2) * perc;
            float val = (float) (render2 + valRel);
            val = (float) (Math.round(val * (1.0D / inc)) / (1.0D / inc));
            BigDecimal b = new BigDecimal(val);
            double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            ((Numbers) value).setValue(f1);

            RenderUtil.smoothCircle(x + 5 + value.animX, valueY + 3.5f, 4, mainColor);
            RenderUtil.smoothCircle(x + 5 + value.animX, valueY + 3.5f, 3, -1);
        } else {

            RenderUtil.smoothCircle(x + 5 + value.animX, valueY + 3.5f, 4, new Color(200, 200, 200).getRGB());
            RenderUtil.smoothCircle(x + 5 + value.animX, valueY + 3.5f, 3, -1);
        }

    }

    @Override
    public void onClicked(float x, float y, int clickType) {

    }

    @Override
    public void onMouseMove(float x, float y, int clickType) {
        mouseX = x;
        mouseY = y;
        if (value.drag && !Mouse.isButtonDown(0)) {
            value.drag = false;
        }

    }
}
