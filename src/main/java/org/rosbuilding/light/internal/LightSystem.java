/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.rosbuilding.light.internal;

import java.io.IOException;
import java.util.List;

import org.rosbuilding.common.System;
import org.rosbuilding.light.LightNode;
import org.rosbuilding.light.driver.LightDriver;

import smarthome_light_msgs.msg.LightAction;
import smarthome_light_msgs.msg.StateData;

/**
*
* @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
*
*/
public class LightSystem extends System<StateData, LightAction> {

    public static final String OP_LIGHT = "light";

    private static final String METHOD_LIGHT = "light";
    private static final String PROTO_LIGHT = METHOD_LIGHT + "://";
    private static final String RGB    = "rgb";
    private static final String HSB    = "hsb";

    private LightDriver driver;

    /**
     * Light node.
     */
    private LightNode node;

    public LightSystem(LightNode node) {
        this.node = node;
        this.driver = node.getDriver();
    }

    @Override
    protected void initializeAvailableMethods(List<String> availableMethods) {
        availableMethods.add(OP_LIGHT);
    }

    @Override
    public void load(StateData stateData) {
        // TODO Auto-generated method stub
    }

    @Override
    public void callbackCmdAction(LightAction message, StateData stateData) throws IOException, InterruptedException {
        this.node.logD("Receive message");

//        if (message.getState() == ON) {
//            this.driver.powerOn();
//        } else if (message.getState() == OFF) {
//            this.driver.powerOff();
//        } else {
        this.node.logD("Call light driver to change HSB color");

        this.driver.setHSV(
                message.getHsb().getHue(),
                message.getHsb().getSaturation(),
                message.getHsb().getBrightness());
//        }
    }

}
