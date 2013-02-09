## `activejdbc` extension 

Extending Micro with support for the [Activejdbc](http://code.google.com/p/activejdbc/).

### Build from source

    # check if the `${MICRO_HOME}` environment variable is defined
    # then:

    $ cd st
    $ ant clean; ant dist
    
### Install and use
Copy (or create symbolic links) the `activejdbc_m` folder and `activejdbc_m.yml` file to your application extensions folder. The `extensions` folder will contain at least the following:

    extensions/
      ├── activejdbc_m/ 
      ├── activejdbc_m.yml
      └── ...

Create a databse connection configuration file: `config/db.yml`, example:

    development:
      driver: org.h2.Driver
      url: jdbc:h2:~/test
      user: sa
      password:

Create a new folder in your app path: `WEB-INF/models`, and add your model classes here. Then you will have to instrument them with the ANT task provided for your convenience:

    $ cd config/extensions/activejdbc_m
    $ ant instrument


Edit the `application.bsh` startup controller and required the `st` extension, example:

    site.ExtensionsManager
        .require("i18N")
        .require("activejdbc_m"); // <-- just added

restart the app and go to: `http://localhost:8080/cache`. The following interface will be shown:

### License
**Apache License 2**, see the `LICENSE` file in this folder.
