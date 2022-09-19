package at.thvf.volve.api.clickgui;

import at.thvf.volve.api.VolveAPI;
import at.thvf.volve.api.module.Module;
import at.thvf.volve.api.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class MainWindow extends GuiScreen {
    public static float x = 100;
    public static float y = 100;
    public static float width = 700;
    public static float height = 200;

    public static Color mainColor;

    public boolean drag;
    public float dragX = 0, dragY = 0;

    public static Module.Category currentCategory = Module.Category.MOVEMENT;

    public TimerUtil timer = new TimerUtil();

    public float lastPercent;
    public float percent;
    public float percent2;
    public float lastPercent2;
    public float outro;
    public float lastOutro;
//    private boolean close = false;
//    private boolean closed;

    public static ArrayList<ModuleWindow> mods = new ArrayList<ModuleWindow>();
    private float rollX = 0;
    private float totalWidth;
    private float rollX2;

    private boolean close = false;
    private boolean closed;

    @Override
    public void initGui() {
        super.initGui();

        percent = 1.33f;
        lastPercent = 1f;
        percent2 = 1.33f;
        lastPercent2 = 1f;
        outro = 1;
        lastOutro = 1;

//        if (mods.size() == 0) {
        mods.clear();
        for (Module module : VolveAPI.getModuleManager().modules) {
            mods.add(new ModuleWindow(module, x, y + 50, mainColor));
        }
//        }


        percent = 1.33f;
        lastPercent = 0.98f;

        percent2 = 0.98f;
        lastPercent2 = 1f;

        outro = 1;
        lastOutro = 1;

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
//        if ((!closed && keyCode == Keyboard.KEY_ESCAPE)) {
//            percent = percent2;
//            close = true;
//            return;
//        }


        super.keyTyped(typedChar, keyCode);
    }

    public float smoothTrans(double current, double last){
        return (float) (current + (last - current) / (Minecraft.getDebugFPS() / 10));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sr = new ScaledResolution(mc);

        this.drawWorldBackground(0);
        RenderUtils.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), mainColor.getRGB(), new Color(0, 0, 0, 30).getRGB());
        VolveAPI.getParticleSystem().render(mouseX, mouseY);

        float outro = smoothTrans(this.outro, lastOutro);
        if (mc.currentScreen == null) {
            GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
            GlStateManager.scale(outro, outro, 0);
            GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
        }


        //animation
        percent = smoothTrans(this.percent, lastPercent);
        percent2 = smoothTrans(this.percent2, lastPercent2);


        if (percent > 0.98) {
            GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
            GlStateManager.scale(percent, percent, 0);
            GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
        } else {
            if (percent2 <= 1) {
                GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
                GlStateManager.scale(percent2, percent2, 0);
                GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
            }
        }


        if(percent <= 1.5 && close) {
            percent = smoothTrans(this.percent, 2);
            percent2 = smoothTrans(this.percent2, 2);
        }

        if(percent >= 1.4  &&  close){
            percent = 1.5f;
            closed = true;
            mc.currentScreen = null;
        }

        width = 500;
        height = 300;
        if (dragX != 0 && drag) {
            x = mouseX - dragX;
        }

        if (dragY != 0 && drag) {
            y = mouseY - dragY;
        }

        float wheel = Mouse.getDWheel();
        if (isHovered(x, y + 35, x + width, y + height, mouseX, mouseY)) {//滚动
            if (wheel > 0 && rollX < 0) {
                rollX += 16;
            } else if (wheel < 0 && rollX + x + totalWidth > x + width + 20) {
                rollX -= 16;
            }
        }

        if (isHovered(x, y, x + 70, y + 35, mouseX, mouseY) && Mouse.isButtonDown(0)) {//拖动主窗口
            drag = true;
            if (dragX == 0) {
                dragX = mouseX - x;
            }
            if (dragY == 0) {
                dragY = mouseY - y;
            }

        } else if (dragX != 0 || dragY != 0) {
            dragX = 0;
            dragY = 0;
            drag = false;
        }
        //Background
        RenderUtil.drawRoundedRect(x, y, x + width, y + height, 2, VolveAPI.getColor());
        RenderUtil.drawRoundedRect(x, y, x + width, y + 35, 2, VolveAPI.getColor());


        float cateX = x + 90;
        for (Module.Category e : Module.Category.values()) {
            mc.fontRendererObj.drawString(e.name(), (int) cateX, (int) (y + 15), e == currentCategory ? mainColor.getRGB() : new Color(170, 170, 170).getRGB());

            if (isHovered(cateX, y, cateX + mc.fontRendererObj.getStringWidth(e.name()), y + 35, mouseX, mouseY) && Mouse.isButtonDown(0) && timer.delay(200) && currentCategory != e) {
                mods.clear();
                for (Module module : VolveAPI.moduleManager.modules) {
                    mods.add(new ModuleWindow(module, 0, 0, mainColor));
                }
                totalWidth = 0;
                rollX = 0;
//                rollX2 = 0;
                currentCategory = e;
                timer.reset();
            }

            cateX += mc.fontRendererObj.getStringWidth(e.name()) + 15;
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.doGlScissor(x, y + 35, width, height);
        //Slider
        if (width / totalWidth <= 1) {
            RenderUtil.drawRect(x + ((-rollX2) / totalWidth) * width + 20, y + height - 3, x + ((-rollX2) / totalWidth) * width + width / totalWidth * width - 20, y + height - 1.8f, new Color(106, 145, 235, 200));
        }

        if (rollX2 != rollX) {
            rollX2 += (rollX - rollX2) / 10f;
        }

        //功能列表
        float modsX = x + 10, modsY = y + 45;
        for (ModuleWindow mw : mods) {
            if (mw.mod.getCategory() == currentCategory) {

                if (modsY + mw.height2 + 25 + 10 > y + height) {
                    modsX += 110;
                    modsY = y + 45;
                }
                if (totalWidth < modsX) {
                    totalWidth = modsX + 110;
                }


                mw.x = modsX + (int) (rollX2 * 10) / 10f;

                if (mw.y == 0) {
                    mw.y = modsY + 15;
                }
                if (Math.abs(mw.y - modsY) < 0.1) {
                    mw.y = modsY;
                }
                if (!drag) {
                    if (mw.y != modsY) {
                        mw.y += (modsY - mw.y) / 30;
                    }
                } else {
                    mw.y = modsY;
                }


//                mw.x = modsX;
//                mw.y = modsY;
                mw.drawModule(mouseX, mouseY);
                if (modsY + mw.height2 <= y + height) {
                    modsY += mw.height2 + (mw.mod.values.size() == 0 ? 25 : 20) + 10;
                }
            }
        }


        GL11.glDisable(GL11.GL_SCISSOR_TEST);

    }

    public static boolean isHovered(float x, float y, float x1, float y1, float mouseX, float mouseY) {
        if (mouseX > x && mouseY > y && mouseX < x1 && mouseY < y1) {
            return true;
        }
        return false;
    }

}
