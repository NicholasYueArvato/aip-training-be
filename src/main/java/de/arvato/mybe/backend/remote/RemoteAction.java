package de.arvato.mybe.backend.remote;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(
        objectName="de.arvato.csdb.tnt.remote:name=remoteAction,type=RemoteActionBean",
        description="Remote Action Bean to control Spring App")
public interface RemoteAction {

    @ManagedOperation
    void shutdown();
}
