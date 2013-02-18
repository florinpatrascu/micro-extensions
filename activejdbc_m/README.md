## `activejdbc` extension 

Extending Micro with support for the [Activejdbc](http://code.google.com/p/activejdbc/); AJ. ActiveJDBC is a Java implementation of the Active Record design pattern.

### Build from source

    # check if the `${MICRO_HOME}` environment variable is defined
    # then:

    $ cd st
    $ ant clean; ant dist
    
### Installation
The integration with Activejdbc (AJ) is still experimental, we're still trying to optimize the installation procedure. Compared with other extensions, installing AJ is not yet straightforward, please stay tuned for updates, meanwhile you can follow the steps described throughout this document.

Copy (or create symbolic links) the `activejdbc_m.yml` file to your application extensions folder. The `extensions` folder will contain at least the following:

    extensions/
      ├── activejdbc_m.yml
      └── ...

Copy all the libraries files distributed under the `lib` folder of the `activejdbc_m` extension to your web app `WEB-INF/lib` folder. This step is required until we solve a classloader issue.

Copy the `build_models.xml` from the extension folder to your `WEB-INF` folder and rename it to `build.xml`. This file is required for instrumenting your class files. Details below. 

### Activejdbc and the Micro web applications

This version is using a database connection pool, with the help of: [BoneCP](http://jolbox.com/). A future release will allow you to connect to a datasource using JNDI as well.
  
Create a database connection configuration file: `WEB-INF/config/db.yml`, and describe your connectors, example:

    production:
      driver: org.h2.Driver
      url: jdbc:h2:~/mydb_production
      user: sa
      password:
      pool: 15      
      
    development:
      driver: org.h2.Driver
      url: jdbc:h2:~/mydb_development
      user: sa
      password:
      pool: 5
      
    test:
      driver: org.h2.Driver
      url: jdbc:h2:~/mydb_test
      user: sa
      password:
      pool: 1      

Micro will select the connector that correspond to its running mode. 

Edit the `application.bsh` startup controller and require the `activejdbc_m` extension, example:

    site.ExtensionsManager
        .require("i18N")
        .require("activejdbc_m");

Create a new folder in your app path: `WEB-INF/models`, and add or create your model classes there. You will have to instrument your models before using them. ActiveJDBC requires instrumentation of class files after they are compiled. You can read more about this by following this link: [What is instrumentation?](https://code.google.com/p/activejdbc/wiki/Instrumentation)

With our setup you can compile and instrument your models as simple as this:

    $ cd WEB-INF
    $ ant

If you are familiar with ActiveJDBC, then you can also use your instrumented models as a `.jar` file deployed to your `WEB-INF/lib` folder.

Stop the web app before instrumenting your classes. A future release of Micro will use an agent that will reload the classes for you without restarting the entire app.

### Use the `activejdbc_m` extension in your web application.
Provided your database connectivity is properly configured and that your models are instrumented and available in the classpath, you can now start using the `activejdbc_m` µ extension. There are countless ways of using Activejdbc in a µ web application, even without this extension, so it is totally up to you. Before going further let's check we have all the gears in place. This is the structure of our web application:

    .
    ├── README.md
    ├── WEB-INF
    │   ├── articles_development.h2.db
    │   ├── articles_production.h2.db
    │   ├── articles_test.h2.db
    │   ├── build.xml
    │   ├── classes
    │   │   └── ehcache.xml
    │   ├── config
    │   │   ├── application.bsh
    │   │   ├── db.yml
    │   │   ├── extensions
    │   │   │   ├── activejdbc_m.yml
    │   │   │   └── i18N.yml
    │   │   ├── locales
    │   │   │   ├── messages_en.properties
    │   │   │   └── messages_fr.properties
    │   │   ├── micro-config.yml
    │   │   └── routes.yml
    │   ├── controllers
    │   │   ├── CreateArticle.bsh
    │   │   ├── DeleteAll.bsh
    │   │   ├── FindArticles.bsh
    │   │   └── filters
    │   │       ├── After.bsh
    │   │       └── Before.bsh
    │   ├── lib
    │   │   ├── LICENSE
    │   │   ├── activejdbc-1.4.7-SNAPSHOT.jar
    │   │   ├── activejdbc-instrumentation-1.4.7-SNAPSHOT.jar
    │   │   ├── activejdbc_m-0.1.1.jar
    │   │   ├── bonecp-0.7.1.RELEASE.jar
    │   │   ├── guava-11.0.2.jar
    │   │   ├── h2-1.3.170.jar
    │   │   ├── javalite-common-1.4.7-SNAPSHOT.jar
    │   │   └── javassist-3.17.1-GA.jar
    │   ├── models
    │   │   └── Article.java
    │   ├── views
    │   │   ├── content
    │   │   │   ├── config
    │   │   │   │   ├── delete_all.yml
    │   │   │   │   ├── index.yml
    │   │   │   │   └── new.yml
    │   │   │   ├── delete_all.html
    │   │   │   ├── index.html
    │   │   │   └── new.html
    │   │   ├── partials
    │   │   │   └── footer.html
    │   │   └── templates
    │   │       ├── 404.html
    │   │       ├── 500.html
    │   │       └── default.html
    │   └── web.xml
    ├── favicon.ico
    ├── images
    ├── jetty.xml
    ├── js
    ├── run.sh
    └── styles
        └── main.css


Let's say we have a database containing a simple table called: `articles`. 

    > select * from articles;
    +-----------+-----------------------+----------+
    | id        | title                 |  author  |
    +-----------+-----------------------+----------+
              1  Micro and Activejdbc      florin
              2  µExtensions in practice   florin

We'll use scripting Controllers for simplicity; Beanshell, in this case.

First let's instrument our classes:

    $ cd WEB-INF
    $ ant

    Buildfile: WEB-INF/build.xml

    compile:
        [javac] Compiling 1 source file to WEB-INF/classes

    instrument:
         [java] **************************** START INSTRUMENTATION **************
         [java] Directory: classes
         [java] Found model: models.Article
         [java] Instrumented class: models.Article in directory: WEB-INF/classes/
         [java] **************************** END INSTRUMENTATION ****************

    BUILD SUCCESSFUL
    Total time: 0 seconds
    
Now we can start the application by executing the `run.sh` script from the root of the app (or `micro start`):

    $ ./run.sh


You can find this demo on Github:

### License
**Apache License 2**, see the `LICENSE` file in this folder.
