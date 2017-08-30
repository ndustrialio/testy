package com.ndustrialio.testy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spotify.docker.client.messages.ContainerInfo;

/**
 * Created by jmhunt on 8/28/17.
 */
@JsonIgnoreProperties("containerInfo")
public class TestyService
{
    public String name, version;

    public String[] ports;

    public ContainerInfo containerInfo;

    public String toString()
    {
        return String.format("%s:%s", name, version);
    }

}
