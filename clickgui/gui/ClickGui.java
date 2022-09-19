package at.thvf.volve.api.clickgui.gui;

import at.thvf.volve.Volve;
import at.thvf.volve.api.VolveAPI;
import at.thvf.volve.api.clickgui.RenderUtil;
import at.thvf.volve.api.clickgui.TimerUtil;
import at.thvf.volve.api.clickgui.font.FontLoaders;
import at.thvf.volve.api.clickgui.gui.theme.Theme;
import at.thvf.volve.api.module.impl.settings.ModuleSettings;
import at.thvf.volve.api.module.value.Mode;
import at.thvf.volve.api.module.value.Numbers;
import at.thvf.volve.api.module.value.Option;
import at.thvf.volve.api.module.value.Value;
import at.thvf.volve.api.module.Module;
import at.thvf.volve.api.utils.ColorUtils;
import at.thvf.volve.api.utils.RenderUtils;
import at.thvf.volve.settings.SettingsEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ClickGui extends GuiScreen {

    private boolean close = false;
    private boolean closed;


    private float dragX, dragY;
    private boolean drag = false;
    private int valuemodx = 0;
    private static float modsRole, modsRoleNow;
    private static float valueRoleNow, valueRole;

    public static ArrayList<Config> configs = new ArrayList<Config>();
    public static EmptyInputBox configInputBox;


    public float lastPercent;
    public float percent;
    public float percent2;
    public float lastPercent2;
    public float outro;
    public float lastOutro;

    static float windowX = 200, windowY = 200;
    static float width = 500, height = 310;

    static ClickType selectType = ClickType.Home;
    static Module.Category modCategory = Module.Category.MOVEMENT;
    static Module selectMod;

    float[] typeXAnim = new float[]{windowX + 10, windowX + 10, windowX + 10, windowX + 10};

    float hy = windowY + 40;

    TimerUtil valuetimer = new TimerUtil();

    public static Theme theme = new Theme();

    @Override
    public void initGui() {
        super.initGui();
        percent = 1.33f;
        lastPercent = 1f;
        percent2 = 1.33f;
        lastPercent2 = 1f;
        outro = 1;
        lastOutro = 1;
        valuetimer.reset();
        configs.clear();
        theme.setDark();
    }


    public float smoothTrans(double current, double last) {
        return (float) (current + (last - current) / (Minecraft.getDebugFPS() / 8));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sr = new ScaledResolution(mc);



        this.drawWorldBackground(0);
        VolveAPI.getParticleSystem().render(mouseX, mouseY);
        RenderUtils.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(152, 130, 227).getRGB(), new Color(0, 0, 0, 30).getRGB());



        float outro = smoothTrans(this.outro, lastOutro);

        if(mc.theWorld != null) {

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


            if (percent <= 1.5 && close) {
                percent = smoothTrans(this.percent, 2);
                percent2 = smoothTrans(this.percent2, 2);
            }

            if (percent >= 1.4 && close) {
                percent = 1.5f;
                closed = true;
                mc.currentScreen = null;
            }
        }


        if (isHovered(windowX, windowY, windowX + width, windowY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            if (dragX == 0 && dragY == 0) {
                dragX = mouseX - windowX;
                dragY = mouseY - windowY;
            } else {
                windowX = mouseX - dragX;
                windowY = mouseY - dragY;
            }
            drag = true;
        } else if (dragX != 0 || dragY != 0) {
            dragX = 0;
            dragY = 0;
        }

        //滚动
        int dWheel = Mouse.getDWheel();
        //绘制主窗口
        RenderUtil.drawRoundedRect(windowX, windowY, windowX + width, windowY + height, 4, theme.BG.getRGB());
        if (selectMod == null) {
            //FontLoaders.arial18.drawString(Client.CLIENT_NAME + " " + Client.VERSION, windowX + 15, windowY + height - 20, theme.FONT.getRGB());
        }
        //绘制顶部图标
        float typeX = windowX + 20;
        int i = 0;
        for (Enum<?> e : ClickType.values()) {
            if (!isHovered(windowX, windowY, windowX + width, windowY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                if (typeXAnim[i] != typeX) {
                    typeXAnim[i] += (typeX - typeXAnim[i]) / 20;
                }
            } else {
                if (typeXAnim[i] != typeX) {
                    typeXAnim[i] = typeX;
                }
            }
            if (e != ClickType.Settings) {
                if (e == selectType) {
//                    RenderUtil.drawCustomImageAlpha(typeXAnim[i], windowY + 10, 16, 16, new ResourceLocation("client/" + e.name() + ".png"), theme.FONT_C.getRGB(),255);
                    FontLoaders.arial16.drawString("User: " + Minecraft.getMinecraft().session.getUsername(), (int) (typeXAnim[i] - 10), (int) (windowY + 295), theme.FONT_C.getRGB());
                    typeX += (32 + mc.fontRendererObj.getStringWidth(e.name() + " "));
                } else {
//                    RenderUtil.drawCustomImageAlpha(typeXAnim[i], windowY + 10, 16, 16, new ResourceLocation("client/" + e.name() + ".png"), theme.FONT.getRGB(),255);
                    FontLoaders.arial16.drawString(e.name(), (int) (typeXAnim[i] + 20), (int) (windowY + 15), theme.FONT_C.getRGB());
                    typeX += (32 + mc.fontRendererObj.getStringWidth(e.name() + " "));
                }
            }
//            } else {
//                RenderUtil.drawCustomImageAlpha(windowX + width - 20, windowY + 10, 16, 16, new ResourceLocation("client/" + e.name() + ".png"), e == selectType ? new Color(255, 255, 255).getRGB() : new Color(79, 80, 86).getRGB(),255);
//            }
            i++;
        }


        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(0, 2 * ((int) (sr.getScaledHeight_double() - (windowY + height))) + 50, (int) (sr.getScaledWidth_double() * 2), (int) ((height) * 2) - 160);
        if (selectType == ClickType.Home) {
            if (selectMod == null) {
                //绘制类型列表
                float cateY = windowY + 65;
                for (Module.Category m : Module.Category.values()) {
                    if (m == modCategory) {
                        RenderUtil.drawRect(0, 0, 0, 0, -1);
                        //RenderUtil.drawRoundedRect(windowX + 7, hy - 8, windowX + 90, hy + mc.fontRendererObj.FONT_HEIGHT -8 / 2 + 10, 0.15f, ColorUtils.reAlpha(theme.BG_3.brighter().brighter().getRGB(), 0.1f));
                        RenderUtil.drawRoundedRect(windowX + 7, hy - 2, windowX + 9, hy + mc.fontRendererObj.FONT_HEIGHT -8 / 2 + 5, 0.15f, theme.BG_3);

                        FontLoaders.arial16.drawString(m.name() + " (" + VolveAPI.getModuleManager().getModulesInCategory(m).size() + ")", (int) (windowX + 20), (int) cateY, theme.FONT_C.getRGB());

                        if (isHovered(windowX, windowY, windowX + width, windowY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                            hy = cateY;
                        } else {
                            if (hy != cateY) {
                                hy += (cateY - hy) / 20;
                            }
                        }
                    } else {
                        FontLoaders.arial16.drawString(m.name(), (int) (windowX + 20), (int) cateY, theme.FONT.getRGB());
                    }


                    cateY += 25;
                }

                //RenderUtil.drawCustomImage(windowX + 10, windowY + height - 64, 20, 20, new ResourceLocation("client/superskidder.jpg"));

                //FontLoaders.arial16.drawString("TEST", (int) (windowX + 35), (int) (windowY + height - 60), theme.FONT_C.getRGB());
                //FontLoaders.arial16.drawString("#0000", (int) (windowX + 35), (int) (windowY + height - 50), theme.FONT.getRGB());


            }
            if (selectMod != null) {
                if (valuemodx > -80) {
                    valuemodx -= 5;
                }
            } else {
                if (valuemodx < 20) {
                    valuemodx += 5;
                }
            }

            if (selectMod != null) {

                RenderUtil.drawRoundedRect(windowX + 430 + valuemodx, windowY + 60, windowX + width, windowY + height - 20, 3, theme.Modules.getRGB());
                RenderUtil.drawGradientSideways(windowX + 428 + valuemodx, windowY + 60, windowX + 430 + valuemodx, windowY + height - 20, theme.Modules.getRGB(), theme.BG_4.getRGB());
                RenderUtil.drawGradientRect(windowX + 430 + valuemodx, windowY + height - 20, windowX + width, windowY + height - 17, theme.Modules.getRGB(), theme.BG_4.getRGB());

                RenderUtil.drawRoundedRect(windowX + 430 + valuemodx, windowY + 60, windowX + width, windowY + 85, 3, theme.Modules_C);
                RenderUtil.drawGradientRect(windowX + 430 + valuemodx, windowY + 85, windowX + width, windowY + 87, theme.Modules.getRGB(), theme.BG_4.getRGB());

                FontLoaders.arial16.drawString(selectMod.getName(), (int) (windowX + 460 + valuemodx), (int) (windowY + 70), theme.FONT.getRGB());
                RenderUtil.drawCustomImage(windowX + 440 + valuemodx, windowY + 68, 8, 8, new ResourceLocation("client/back.png"), theme.FONT_C.getRGB());

                if (isHovered(windowX + 430 + (int) valuemodx, windowY + 60, windowX + width, windowY + height - 20, mouseX, mouseY)) {
                    if (dWheel < 0 && Math.abs(valueRole) + 170 < (selectMod.values.size() * 25)) {
                        valueRole -= 32;
                    }
                    if (dWheel > 0 && valueRole < 0) {
                        valueRole += 32;
                    }
                }

                if (valueRoleNow != valueRole) {
                    valueRoleNow += (valueRole - valueRoleNow) / 20;
                    valueRoleNow = (int) valueRoleNow;
                }

                float valuey = windowY + 100 + valueRoleNow;

                if (selectMod == null) {
                    return;
                }

                if (selectMod.values.size() == 0) {
                    FontLoaders.arial16.drawString("No Settings here", (int) (windowX + 445 + valuemodx), (int) (valuey + 4), theme.FONT_C.getRGB());
                }

                for (Value v : selectMod.values) {
                    if (v instanceof Option) {
                        if (valuey + 4 > windowY + 100) {
                            if (((Boolean) v.getValue())) {
                                FontLoaders.arial16.drawString(v.getName(), (int) (windowX + 445 + valuemodx), (int) (valuey + 4), theme.FONT_C.getRGB());
                                v.optionAnim = 100;
                                RenderUtil.drawRoundedRect(windowX + width - 30, valuey + 2, windowX + width - 10, valuey + 12, 4, ColorUtils.reAlpha(new Color(33, 94, 181).getRGB(), (v.optionAnimNow / 100f)));
                                RenderUtil.smoothCircle(windowX + width - 25 + 10 * (v.optionAnimNow / 100f), valuey + 7, 3.5f, new Color(255, 255, 255).getRGB());
                            } else {
                                FontLoaders.arial16.drawString(v.getName(), (int) (windowX + 445 + valuemodx), (int) (valuey + 4), theme.FONT.getRGB());
                                v.optionAnim = 0;
//                                RenderUtil.drawRoundedRect(windowX + width - 30, valuey + 2, windowX + width - 10, valuey + 12, 4,new Color(59, 60, 65).getRGB());
                                RenderUtil.drawRoundedRect(windowX + width - 30, valuey + 2, windowX + width - 10, valuey + 12, 4, theme.Option_U_B);
                                RenderUtil.smoothCircle(windowX + width - 25 + 10 * (v.optionAnimNow / 100f), valuey + 7, 3.5f, theme.Option_U_C);
                            }
                            if (isHovered(windowX + width - 30, valuey + 2, windowX + width - 10, valuey + 12, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                                if (valuetimer.delay(300)) {
                                    v.setValue(!(Boolean) v.getValue());
                                    valuetimer.reset();
                                }
                            }
                        }

                        if (v.optionAnimNow != v.optionAnim) {
                            v.optionAnimNow += (v.optionAnim - v.optionAnimNow) / 20;
                        }
                        valuey += 25;
                    }
                }
                for (Value v : selectMod.values) {
                    if (v instanceof Numbers) {
                        if (valuey + 4 > windowY + 100) {

                            float present = (float) (((windowX + width - 11) - (windowX + 450 + valuemodx))
                                    * (((Number) v.getValue()).floatValue() - ((Numbers) v).getMinimum().floatValue())
                                    / (((Numbers) v).getMaximum().floatValue() - ((Numbers) v).getMinimum().floatValue()));

                            DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

                            FontLoaders.arial16.drawString(v.getDisplayName(), (int) (windowX + 445 + valuemodx), (int) (valuey + 5), theme.FONT.getRGB());
                            RenderUtils.drawCenteredString(decimalFormat.format(v.getValue()), windowX + width - 20, valuey + 5, theme.FONT_C);
                            RenderUtil.drawRect(windowX + 450 + valuemodx, valuey + 20, windowX + width - 11, valuey + 21.5f, theme.Option_U_B);
                            RenderUtil.drawRect(windowX + 450 + valuemodx, valuey + 20, windowX + 450 + valuemodx + present, valuey + 21.5f, theme.Option_B);

                            RenderUtil.smoothCircle(windowX + 450 + valuemodx + present, valuey + 21f, 4.5f, theme.BG_4);
                            RenderUtil.smoothCircle(windowX + 450 + valuemodx + present, valuey + 21f, 4, theme.Option_U_C);
                            RenderUtil.smoothCircle(windowX + 450 + valuemodx + present, valuey + 21f, 2, theme.Option_B);


                            if (isHovered(windowX + 450 + valuemodx, valuey + 18, windowX + width - 11, valuey + 23.5f, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                                float render2 = ((Numbers) v).getMinimum().floatValue();
                                double max = ((Numbers) v).getMaximum().doubleValue();
                                double inc = 0.1;
                                double valAbs = (double) mouseX - ((double) (windowX + 450 + valuemodx));
                                double perc = valAbs / (((windowX + width - 11) - (windowX + 450 + valuemodx)));
                                perc = Math.min(Math.max(0.0D, perc), 1.0D);
                                double valRel = (max - render2) * perc;
                                double val = render2 + valRel;
                                val = (double) Math.round(val * (1.0D / inc)) / (1.0D / inc);
                                ((Numbers) v).setValue(Double.valueOf(val));
                            }
                        }
                        valuey += 25;
                    }
                }
                for (Value v : selectMod.values) {
                    if (v instanceof Mode) {
                        Mode<?> modeValue = (Mode<?>) v;

                        if (valuey + 4 > windowY + 100 & valuey < (windowY + height)) {
                            RenderUtil.drawRoundedRect(windowX + 445 + valuemodx, valuey + 2, windowX + width - 5, valuey + 22, 4, theme.BG_4);
                            RenderUtil.drawRoundedRect(windowX + 446 + valuemodx, valuey + 3, windowX + width - 6, valuey + 21, 4, theme.BG);
                            FontLoaders.arial16.drawString(modeValue.getModeAsString(), (int) (windowX + 455 + valuemodx), (int) (valuey + 10), theme.FONT.getRGB());
                            FontLoaders.arial16.drawString(">", (int) (windowX + width - 15), (int) (valuey + 9), theme.FONT_C.getRGB());
                            if (isHovered(windowX + 445 + valuemodx, valuey + 2, windowX + width - 5, valuey + 22, mouseX, mouseY) && Mouse.isButtonDown(0) && valuetimer.delay(300)) {
                                if (Arrays.binarySearch(modeValue.getModes(), modeValue.value) < modeValue.getModes().length - 1) {
                                    v.setValue(modeValue
                                            .getModes()[Arrays.binarySearch(modeValue.getModes(), (v.getValue())) + 1]);
                                } else {
                                    v.setValue(modeValue.getModes()[0]);
                                }
                                valuetimer.reset();
                            }
                        }
                        valuey += 25;
                    }
                }
            }
            if (isHovered(windowX + 435 + valuemodx, windowY + 65, windowX + 435 + valuemodx + 16, windowY + 65 + 16, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                selectMod = null;
                valuetimer.reset();
            }
            float modY = windowY + 70 + modsRoleNow;
            for (Module m : VolveAPI.getModuleManager().modules) {
                if (m.getCategory() != modCategory)
                    continue;

                if (isHovered(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                    if (valuetimer.delay(300) && modY + 40 > (windowY + 70) && modY < (windowY + height)) {
                        //m.setEnabled(!m.isEnabled());
                        m.toggle();
                        valuetimer.reset();
                    }
                } else if (isHovered(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, mouseX, mouseY) && Mouse.isButtonDown(1)) {
                    if (valuetimer.delay(300)) {
                        if (selectMod != m) {
                            valueRole = 0;
                            selectMod = m;
                        } else if (selectMod == m) {
                            selectMod = null;
                        }
                        valuetimer.reset();
                    }
                }
                if (m.isEnabled()) {
//                    RenderUtil.drawRoundedRect(windowX + 99.9f + valuemodx, modY - 10.1f, windowX + 425.1f + valuemodx, modY + 25.1f, 4, theme.BG_2);
                    RenderUtil.drawRoundedRect(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, 4, theme.Modules_C);
                } else {
//                    RenderUtil.drawRoundedRect(windowX + 99.9f + valuemodx, modY - 10.1f, windowX + 425.1f + valuemodx, modY + 25.1f, 4, theme.BG_2);
                    RenderUtil.drawRoundedRect(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, 4, theme.Modules);
                    //RenderUtil.drawGradientRect(windowX + 105 + valuemodx, modY + 25, windowX + 420 + valuemodx, modY + 27, theme.Modules.getRGB(), theme.BG_4.getRGB());
                }
//                }

                //三个点
                RenderUtil.drawRoundedRect(windowX + 410 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, 4, theme.BG_4);
                FontLoaders.arial16.drawString(".", (int) (windowX + 416 + valuemodx), (int) (modY - 5), theme.FONT.getRGB());
                FontLoaders.arial16.drawString(".", (int) (windowX + 416 + valuemodx), (int) (modY - 1), theme.FONT.getRGB());
                FontLoaders.arial16.drawString(".", (int) (windowX + 416 + valuemodx), (int) (modY + 3), theme.FONT.getRGB());

                if (isHovered(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, mouseX, mouseY) || m.isEnabled()) {
                    FontLoaders.arial16.drawString(m.getName(), (int) (windowX + 141 + valuemodx), (int) (modY + 5), theme.FONT_C.getRGB());
                } else {
                    FontLoaders.arial16.drawString(m.getName(), (int) (windowX + 140 + valuemodx), (int) (modY + 5), theme.FONT.getRGB());
                }

                FontLoaders.arial16.drawString(m.getDescription(), (int) (windowX + 220 + valuemodx), (int) (modY + 5), theme.FONT.getRGB());

                if (m.isEnabled()) {
                    RenderUtil.drawRoundedRect(windowX + 100 + valuemodx, modY - 10, windowX + 125 + valuemodx, modY + 25, 4, ColorUtils.reAlpha(theme.BG_3.getRGB(), (m.optionAnimNow / 100f)));
                    RenderUtil.drawCustomImage(windowX + 105 + valuemodx, modY, 16, 16, new ResourceLocation("client/module.png"), new Color(220, 220, 220).getRGB());
                    m.optionAnim = 100;

                    RenderUtil.drawRoundedRect(windowX + 380 + valuemodx, modY + 2, windowX + 400 + valuemodx, modY + 12, 4, ColorUtils.reAlpha(theme.Option_B.getRGB(), m.optionAnimNow / 100f));
                    RenderUtil.smoothCircle(windowX + 385 + 10 * m.optionAnimNow / 100 + valuemodx, modY + 7, 3.5f, theme.Option_C);
                } else {
                    RenderUtil.drawRoundedRect(windowX + 100 + valuemodx, modY - 10, windowX + 125 + valuemodx, modY + 25, 4, theme.BG_2);
                    RenderUtil.drawCustomImage(windowX + 105 + valuemodx, modY, 16, 16, new ResourceLocation("client/module.png"), theme.BG_4.getRGB());
                    m.optionAnim = 0;

                    RenderUtil.drawRoundedRect(windowX + 380 + valuemodx, modY + 2, windowX + 400 + valuemodx, modY + 12, 4, theme.BG_2);
//                    RenderUtil.drawRoundedRect(windowX + 381 + valuemodx, modY + 3, windowX + 399 + valuemodx, modY + 11, 4, new Color(29, 27, 31).getRGB());
                    RenderUtil.smoothCircle(windowX + 385 + 10 * m.optionAnimNow / 100 + valuemodx, modY + 7, 3.5f, theme.Option_U_C);
                }

                if (m.optionAnimNow != m.optionAnim) {
                    m.optionAnimNow += (m.optionAnim - m.optionAnimNow) / 20;
                }


                modY += 40;
            }
            //滚动
            if (isHovered(windowX + 100 + valuemodx, windowY + 60, windowX + 425 + valuemodx, windowY + height, mouseX, mouseY)) {
                if (dWheel < 0 && Math.abs(modsRole) + 220 < (VolveAPI.getModuleManager().getModulesInCategory(modCategory).size() * 40)) {
                    modsRole -= 32;
                }
                if (dWheel > 0 && modsRole < 0) {
                    modsRole += 32;
                }
            }

            if (modsRoleNow != modsRole) {
                modsRoleNow += (modsRole - modsRoleNow) / 20;
                modsRoleNow = (int) modsRoleNow;
            }


        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        int dWheel2 = Mouse.getDWheel();
        if (isHovered(windowX + 100 + valuemodx, windowY + 60, windowX + 425 + valuemodx, windowY + height, mouseX, mouseY)) {
            if (dWheel2 < 0 && Math.abs(modsRole) + 220 < (VolveAPI.getModuleManager().getModulesInCategory(modCategory).size() * 40)) {
                modsRole -= 16;
            }
            if (dWheel2 > 0 && modsRole < 0) {
                modsRole += 16;
            }
        }

        if (modsRoleNow != modsRole) {
            modsRoleNow += (modsRole - modsRoleNow) / 20;
            modsRoleNow = (int) modsRoleNow;
        }

    }

    public int findArray(float[] a, float b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
//        Client.musicPanel.onMouse(mouseX,mouseY,mouseButton);
        //顶部图标
        float typeX = windowX + 20;
        for (Enum<?> e : ClickType.values()) {
            if (e != ClickType.Settings) {
                if (e == selectType) {
                    if (isHovered(typeX, windowY + 10, typeX + 16 + mc.fontRendererObj.getStringWidth(e.name() + " "), windowY + 10 + 16, mouseX, mouseY)) {
                        selectType = (ClickType) e;
                    }
                    typeX += (32 + mc.fontRendererObj.getStringWidth(e.name() + " "));
                } else {
                    if (isHovered(typeX, windowY + 10, typeX + 16, windowY + 10 + 16, mouseX, mouseY)) {
                        selectType = (ClickType) e;
                    }
                    typeX += (32 + mc.fontRendererObj.getStringWidth(e.name() + " "));
                }
            } else {
                if (isHovered(windowX + width - 32, windowY + 10, windowX + width, windowY + 10 + 16, mouseX, mouseY)) {
                    selectType = (ClickType) e;
                }
            }
        }

        if (selectType == ClickType.Home) {
            //类型列表
            float cateY = windowY + 65;
            for (Module.Category m : Module.Category.values()) {

                if (isHovered(windowX, cateY - 8, windowX + 50, cateY + mc.fontRendererObj.FONT_HEIGHT -8 / 2 + 8, mouseX, mouseY)) {
                    if (modCategory != m) {
                        modsRole = 0;
                    }

                    modCategory = m;
                    for (Module mod : VolveAPI.getModuleManager().modules) {
                        mod.optionAnim = 0;
                        mod.optionAnimNow = 0;

                    }
                }

                cateY += 25;
            }

        }


    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {

        if (!closed && keyCode == Keyboard.KEY_ESCAPE) {
            close = true;
            mc.mouseHelper.grabMouseCursor();
            mc.inGameHasFocus = true;
            return;
        }


        if (close) {

            this.mc.displayGuiScreen((GuiScreen) null);
        }

        try {
            super.keyTyped(typedChar, keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    @Override
    public void onGuiClosed() {

    }
}
