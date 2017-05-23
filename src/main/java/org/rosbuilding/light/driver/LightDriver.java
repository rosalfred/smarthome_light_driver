package org.rosbuilding.light.driver;

public interface LightDriver {
    void powerOn();
    void powerOff();
    void setRGB(int red, int green, int blue);
    void setHSV(float h, float s, float v);
    void setBrightness(int brightness);
}
