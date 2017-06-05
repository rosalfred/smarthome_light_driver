package org.rosbuilding.light.driver;

public interface LightDriver {
    void powerOn();
    void powerOff();
    void setRGB(int red, int green, int blue);
    void setHSV(float h, float s, float v);
    void setHSL(int h, int s, int l);
    void setBrightness(int brightness);
}
