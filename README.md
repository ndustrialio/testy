# Purpose
Testy helps you set up dependencies to run your tests.  It's kinda like docker compose, but simpler and probably worse.

# Dependencies
Testy should only need docker running to do its job.

# Usage
Define your test dependencies in yaml:

```
services:
  - name: cassandra
    version: latest
    ports:
      - 9042
      - 9160
  - name: postgres
    version: 9.6.0
    ports:
      - 5432

  - name: redis
    version: latest
    ports:
      - 6739


```
Name is the image name on DockerHub, version is the tag, and any ports you specify are forwarded to the host.

Call init() to pull the images, and create/start containers based on them.  Call shutdown() to stop/remove those
containers.  If you forget to call shutdown() extra containers will be left on your system.

