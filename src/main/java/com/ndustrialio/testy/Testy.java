package com.ndustrialio.testy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by jmhunt on 8/28/17.
 */
public class Testy
{
    private TestyConfiguration _config;

    private DockerClient _client;

    public Testy(String yaml) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        _config = mapper.readValue(new File(yaml), TestyConfiguration.class);

        _client = DefaultDockerClient.builder().uri("unix:///var/run/docker.sock").build();

    }

    public void init() throws DockerException, InterruptedException
    {
        System.out.println("Testy initializing...");
        System.out.println("Pulling service images..");

        for (TestyService service : _config.services)
        {
            System.out.println(String.format("Pulling image %s", service.toString()));

            _client.pull(service.toString());

        }


        // Construct containers

        System.out.println("Building containers..");

        for (TestyService service : _config.services)
        {
            System.out.println("Building container for: " + String.format("%s:%s", service.name, service.version));
            // Construct port mappings
            Map<String, List<PortBinding>> portBindings = new HashMap<>();

            Arrays.asList(service.ports)
                    .forEach(port -> {
                        System.out.println("Mapping port: " + port);

                        portBindings
                                .computeIfAbsent(port,  p -> new ArrayList<>())
                                .add(PortBinding.of("0.0.0.0", port));
                            });

            HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

            ContainerConfig containerConfig = ContainerConfig.builder()
                    .hostConfig(hostConfig)
                    .image(service.toString())
                    .exposedPorts(service.ports)
                    .build();

            ContainerCreation containerCreation = _client.createContainer(containerConfig);

            service.containerInfo = _client.inspectContainer(containerCreation.id());
        }

        System.out.println("Launching containers.. ");

        for (TestyService service : _config.services)
        {
            _client.startContainer(service.containerInfo.id());

            System.out.println("Launched container id: " + service.containerInfo.id() + " for image: " + service.toString() + ", name: " + service.containerInfo.name());

        }
    }

    public void shutdown() throws DockerException, InterruptedException
    {
        System.out.println("Testy shutting down..");

        for (TestyService service : _config.services)
        {
            System.out.println("Stopping container: " + service.containerInfo.id());

            _client.killContainer(service.containerInfo.id());

            System.out.println("Removing container: " + service.containerInfo.id());

            _client.removeContainer(service.containerInfo.id());
        }
    }

}
