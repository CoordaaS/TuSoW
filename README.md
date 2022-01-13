# Coordination with TuSoW

![The Coordination logo](https://gitlab.com/pika-lab/tuples/coordination/-/raw/develop/icon.svg)

The _Coordination with TuSoW_ Project is made up of several modules generally tackling with the coordination of multi-threaded, 
multi-processes, distributed, or multi-agent applications through **LINDA** _tuple spaces_.

### Project Map

![Coordination project map](https://gitlab.com/pika-lab/courses/ds/projects/ds-project-cavallari-ay2122/-/raw/feature/grpc/project-map.svg)

### Quick Links

* [GitLab Repository](https://gitlab.com/pika-lab/tuples/coordination) (the one used by developers)
* [GitHub Repository](https://github.com/CoordaaS/TuSoW/) (the public one, where releases are hosted)

## Modules `linda-*`

Module `linda-core` and its implementations (currently, `linda-logic` and `linda-text`) are aimed at letting developers
use tuple spaces programmatically in concurrent, local (i.e., **non-distributed**) applications.

## Module `tusow-service` and remote tuples spaces in `linda-*-client`

TuSoW (Tuple Spaces over the Web) is a Web wrapper for tuple spaces exposing them to the Internet as a Web Service,
through a ReST-ful API.

TuSoW wraps _all_ current implementations of `linda-core` (currently, `linda-logic` and `linda-text`).

You can either interact with TuSoW through any HTTP client or employ our implementations, which supports the usage of 
remote tuple spaces using the exact same API used for local ones.
This is the purpose of modules `linda-logic-client` and `linda-text-client`, which are the remote equivalents of 
`linda-logic` and `linda-text`, respectively.

Finally, module `tusow-cli` is aimed at letting users interact with TuSoW tuple spaced through a command line interface.

### Playground

You can start a TuSoW service by running:
```bash
./gradlew tusow
```
which will start a TuSoW service on the default port 8080.
If you want it to run on another port, you can start it this way:
```bash
./gradlew tusow --port <OTHER_PORT>
```

You can play with TuSoW through our CLI.
Let the CLI help messages drive your understanding of how the CLI works:
```bash
./gradlew tusow-cli:run --args="[COMMAND] [--help]"
```
where apexes are required and square brackets represent optionality.
