package org.rosbuilding.light.driver.lyt8266;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.rosbuilding.light.LightConfig;
import org.rosbuilding.light.LightNode;
import org.rosbuilding.light.driver.LightDriver;

public class Lyt8266UdpDriver implements LightDriver {

    private LightNode node;
    private LightConfig config;
    private DatagramSocket socket;

    public Lyt8266UdpDriver(LightNode node) {
        this.node = node;
        this.config = node.getConfiguration();

        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            this.node.logE(e);
        }
    }

    @Override
    public void powerOn() {
        this.setBrightness(255);
    }

    @Override
    public void powerOff() {
        this.setBrightness(0);
    }

    @Override
    public void setBrightness(int brightness) {
        this.sendData(String.format("+4,%d,\r\n", brightness));
    }

    @Override
    public void setRGB(int red, int green, int blue) {
        this.sendData(String.format("+1,%d,%d,%d,\r\n", red, green, blue));
    }

    /**
     * Convert HSV (hue-saturation-value) components to a RGB color.
     * If hsv values are out of range, they are pinned.
     *
     * @param hue is Hue [0 .. 360)
     * @param saturation is Saturation [0...100]
     * @param brightness is Value (brightness) [0...100]
     *
     * @return the resulting RGB color
     */
    @Override
    public void setHSV(float hue, float saturation, float brightness) {
        float r, g, b;

        float h = hue / 360f;
        float s = saturation / 100f;
        float v = brightness / 100f;

        if (s == 0) {
            r = v * 255;
            g = v * 255;
            b = v * 255;
        } else {
            float var_h = h * 6;
            if (var_h == 6) var_h = 0;
            int var_i = (int) Math.floor((double) var_h);
            float var_1 = v * (1 - s);
            float var_2 = v * (1 - s * (var_h - var_i));
            float var_3 = v * (1 - s * (1 - (var_h - var_i)));

            float var_r;
            float var_g;
            float var_b;
            if (var_i == 0) {
                var_r = v;
                var_g = var_3;
                var_b = var_1;
            } else if (var_i == 1) {
                var_r = var_2;
                var_g = v;
                var_b = var_1;
            } else if (var_i == 2) {
                var_r = var_1;
                var_g = v;
                var_b = var_3;
            } else if (var_i == 3) {
                var_r = var_1;
                var_g = var_2;
                var_b = v;
            } else if (var_i == 4) {
                var_r = var_3;
                var_g = var_1;
                var_b = v;
            } else {
                var_r = v;
                var_g = var_1;
                var_b = var_2;
            }

            r = var_r * 255; // RGB results from 0 to 255
            g = var_g * 255;
            b = var_b * 255;
        }

        this.node.logD(String.format("Set color HSV %f %f %f to RGB %d %d %d",
                hue, saturation, brightness, (int) r, (int) g, (int) b));
        this.setRGB((int) r, (int) g, (int) b);
    }

    /**
     * Convert HSL (hue-saturation-lightness) components to a RGB color.
     * If hss values are out of range, they are pinned.
     *
     * @param h is Hue [0 .. 360)
     * @param s is Saturation [0...100]
     * @param l is Lightness [0...100]
     *
     * @return the resulting RGB color
     */
    @Override
    public void setHSL(int h, int s, int l) {
        int r = 0, g = 0, b = 0;

        final float c = (1f - Math.abs(2 * l - 1f)) * (s / 100f);
        final float m = l - 0.5f * c;
        final float x = c * (1f - Math.abs((h / 60f % 2f) - 1f));
        final int hueSegment = (int) h / 60;

        switch (hueSegment) {
            case 0:
                r = Math.round(255 * (c + m));
                g = Math.round(255 * (x + m));
                b = Math.round(255 * m);
                break;
            case 1:
                r = Math.round(255 * (x + m));
                g = Math.round(255 * (c + m));
                b = Math.round(255 * m);
                break;
            case 2:
                r = Math.round(255 * m);
                g = Math.round(255 * (c + m));
                b = Math.round(255 * (x + m));
                break;
            case 3:
                r = Math.round(255 * m);
                g = Math.round(255 * (x + m));
                b = Math.round(255 * (c + m));
                break;
            case 4:
                r = Math.round(255 * (x + m));
                g = Math.round(255 * m);
                b = Math.round(255 * (c + m));
                break;
            case 5:
            case 6:
                r = Math.round(255 * (c + m));
                g = Math.round(255 * m);
                b = Math.round(255 * (x + m));
                break;
        }

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        this.setRGB(r, g, b);
    }

    private void sendData(String data) {
        byte[] buffer = data.getBytes();

        try {
            DatagramPacket dataSent = new DatagramPacket(
                    buffer,
                    buffer.length,
                    new InetSocketAddress(this.config.getHost(), (int) this.config.getPort()));

            this.socket.send(dataSent);
        } catch (IOException e) {
            this.node.logE(e);
        }
    }
}
