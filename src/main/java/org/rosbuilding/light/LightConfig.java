/**
 * This file is part of the Alfred package.
 *
 * (c) Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.rosbuilding.light;

import org.ros2.rcljava.node.Node;
import org.rosbuilding.common.NodeDriverConnectedConfig;

/**
*
* @author Erwan Le Huitouze <erwan.lehuitouze@gmail.com>
*
*/
public class LightConfig extends NodeDriverConnectedConfig {

    public LightConfig(final Node connectedNode) {
        super(
                connectedNode,
                "/home/salon",
                "light",
                "fixed_frame",
                1,
                "00:00:00:00:00:00",
                "192.168.0.115",
                8899L,
                "admin",
                "admin");
    }
}
