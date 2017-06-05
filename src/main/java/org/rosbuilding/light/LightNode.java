/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.rosbuilding.light;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.node.Node;

import org.rosbuilding.common.BaseDriverNode;
import org.rosbuilding.common.light.LightMessageConverter;
import org.rosbuilding.common.light.LightStateDataComparator;
import org.rosbuilding.light.driver.LightDriver;
import org.rosbuilding.light.driver.lyt8266.Lyt8266UdpDriver;
import org.rosbuilding.light.internal.LightSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smarthome_light_msgs.msg.LightAction;
import smarthome_light_msgs.msg.StateData;

/**
 * Light ROS Node.
 *
 * @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
 * @author Mickael Gaillard <mick.gaillard@gmail.com>
 *
 */
public class LightNode extends BaseDriverNode<LightConfig, StateData, LightAction> {

    private static final Logger logger = LoggerFactory.getLogger(LightNode.class);

    private LightDriver driver;

    public LightNode() {
        super(
            new LightStateDataComparator(),
            new LightMessageConverter(),
            LightAction.class.getName(),
            StateData.class.getName());

        LightNode.logger.debug("Light Node Initialized.");
    }

    @Override
    public void onStart(Node connectedNode) {
        LightNode.logger.debug("onStart event !");
        super.onStart(connectedNode);

        this.driver = new Lyt8266UdpDriver(this);
    }

//    @Override
//    public void onShutdown() {
//        LightNode.logger.debug("onShutdown event !");
//        super.onShutdown();
//    }

    @Override
    protected void onConnected() {
        LightNode.logger.debug("onConnected event !");
//        this.getStateData().setState(StateData.ENABLE);
    }

    @Override
    protected void onDisconnected() {
        LightNode.logger.debug("onDisconnected event !");
//        this.getStateData().setState(StateData.UNKNOWN);
    }

    @Override
    protected boolean connect() {
        boolean isConnected = false;

        this.logI(String.format("Connecting to %s:%s...", this.configuration.getHost(), this.configuration.getPort()));
        LightNode.logger.debug(String.format("Connecting to %s:%s...", this.configuration.getHost(), this.configuration.getPort()));

        try {
            //this.getStateData().setState(StateData.INIT);
            isConnected = true;
            this.logI("\tConnected done.");
        } catch (Exception e) {
            //this.getStateData().setState(StateData.SHUTDOWN);
            try {
                Thread.sleep(10000 / this.configuration.getRate());
            } catch (InterruptedException ex) {
                this.logE(ex);
            }
        }

        return isConnected;
    }

    @Override
    protected void initialize() {
        LightNode.logger.debug("Custom Managed Node.");
        super.initialize();

        this.addModule(new LightSystem(this));
    }

    @Override
    protected LightConfig makeConfiguration() {
        LightNode.logger.debug("Make Configuration.");
        return new LightConfig(this.getConnectedNode());
    }

    public LightDriver getDriver() {
        return this.driver;
    }

    public static void main(String[] args) throws InterruptedException {
        RCLJava.rclJavaInit(args);

        final LightNode light = new LightNode();	//TODO lazy instance.
        final Node node = RCLJava.createNode("light");

        light.onStart(node);
        light.onStarted();

        RCLJava.spin(node);

        light.onShutdown();
        light.onShutdowned();

        RCLJava.shutdown();
    }
}
