package de.arvato.mybe.base;

import de.arvato.mybe.backend.remote.RemoteAction;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;


public final class RemoteActionClient {

    private JMXServiceURL serviceURL;

    public RemoteActionClient(String host, Integer port) throws IOException {
        Objects.requireNonNull(host, "host value was missing");
        Objects.requireNonNull(port, "port value was missing");
        serviceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
    }

    public void shutdown() {
        execute(RemoteAction::shutdown);
    }

    private void execute(Consumer<RemoteAction> consumer) {
        try (JMXConnector connector = JMXConnectorFactory.connect(serviceURL, null)) {
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            ObjectName objectName = new ObjectName("de.arvato.atlcloud.remote:name=remoteAction,type=RemoteActionBean");
            RemoteAction remoteAction = JMX.newMBeanProxy(connection, objectName, RemoteAction.class, true);
            consumer.accept(remoteAction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
