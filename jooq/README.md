## jOOQ extension

Extending Micro with support for the [jOOQ](http://www.jooq.org/). From the **jOOQ** site:

    jOOQ generates Java code from your database and lets 
    you build typesafe SQL queries through its fluent API.

### Build from source

    # check if the `${MICRO_HOME}` environment variable is defined
    # then:

    $ cd jooq
    $ ant clean; ant dist

### Installation
Copy (or create symbolic links) the  `jooq` folder to your application's extensions folder. And create a `jOOQ.yml` configuration file. The `extensions` folder will contain at least the following:

    extensions/
      ├── jOOQ.yml
      ├── jooq/
      └── ...

An example of `jOOQ.yml` that can be used with the H2 database:

    class: ca.simplegames.micro.extensions.JOOQExtension
    options:
      # use one of these supported opensource SQL dialects:
      # - CUBRID, Derby, Firebird, H2, HSQLDB, MariaDB, MySQL, Postgres, SQLite
      dialect: H2 # case sensitive, please
      models: models
      debug: true
      db: db.yml
      

Copy the `gen_build.xml` and the `gen.sh` files from the distribution folder to your `WEB-INF` folder and optionally rename `gen_build.xml` to `build.xml` (or something more suitable). The `gen.sh` is a temporary workaround for this issue: [JooQ Ant Codegeneration not working](http://stackoverflow.com/questions/18185040/jooq-ant-codegeneration-not-working).

Now you can generate code that will help you talk 'Java' to you database :) For more details about the `jOOQ` code generator, please follow the original [documentation](http://www.jooq.org/doc/3.3/manual/code-generation/).

### jOOQ and Micro web applications

This version is using a managed database connection pool, with the help of: [BoneCP](http://jolbox.com/). We may add a JNDI connector in a future release.

Create a database connection configuration file: `WEB-INF/config/db.yml`, and describe your connectors, example:

    production:
      driver: org.h2.Driver
      url: jdbc:h2:~/test_production
      user: sa
      password:
      pool: 15

    development:
      driver: org.h2.Driver
      url: jdbc:h2:~/test_development
      user: sa
      password:
      pool: 5

    test:
      driver: org.h2.Driver
      url: jdbc:h2:~/test_test
      user: sa
      password:
      pool: 1

Micro will select the connector corresponding to its running mode. Please help the current implementation to "learn" about the databased dialect you're using. You can do this in your `config/extensions/jOOQ.yml` file. For the database connectors defined in the example above, you'll specify the H2 dialect, like this:

    class: ca.simplegames.micro.extensions.JOOQExtension
    options:
      # use one of these supported opensource SQL dialects:
      # - CUBRID, Derby, Firebird, H2, HSQLDB, MariaDB, MySQL, Postgres, SQLite
      dialect: H2 # case sensitive, please
      models: models
      debug: true
      db: db.yml

> This is a temporary workaround until I figure out a better integration with jOOQ.

Edit the `application.bsh` startup controller and require the `jooq_m` extension, example:

    site.ExtensionsManager
        .require("i18N")
        .require("jOOQ");

Create a new folder in your app path: `WEB-INF/models`, and add or create your model classes there. You need to tell `jOOQ` some things about your database connection, and for that you'll have to create a file: `codegen.xml` in your WEB-INF folder. Example:

    contents of a demo WEB-INF/codegen.xml:
    ---------------------------------------
    
    <configuration>
      <jdbc>
        <driver>org.h2.Driver</driver>
        <url>jdbc:h2:test_development</url>
        <user>sa</user>
        <password></password>
      </jdbc>

      <generator>
        <database>
          <name>org.jooq.util.h2.H2Database</name>
          <includes>.*</includes>
          <excludes></excludes>
          <inputSchema>PUBLIC</inputSchema>
        </database>

        <generate>
        </generate>

        <target>
          <packageName>models</packageName>
          <directory>./</directory>
        </target>
      </generator>
    </configuration>


With this setup you can generate the Java model classes and compile them as simple as this:

    $ cd WEB-INF
    $ ant
or in case you're using the `gen_build.xml` file:    

    $ ant -f gen_build.xml

> todo: add more examples

You can find a full URL Shortener demo web application developed with jOOQ and Micro, on Github: [jooq_m_url_shortener](https://github.com/florinpatrascu/jooq_m_url_shortener).

### License
**Apache License 2**, see the `LICENSE` file in this folder.
