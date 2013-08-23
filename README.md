## Micro extensions
Extensions available for the Micro framework and links to extensions in the wild.

### Using extensions
Using an extension is usually as simple as copying a folder and requiring a an extension name. Consult these steps if you run into problems:

 1. Install the extensions by copying the extension folder and the extension `.yml` configuration file to your `WEB-INF/config/extensions` folder. Example:
  <pre><code>
        extensions/
          ├── cache_admin/
          ├── cache_admin.yml
          └── i18N.yml
  </code></pre>
 2. Edit the `application.bsh` startup controller and require the desired extension, example:
    <pre><code>
     site.ExtensionsManager
         .require("i18N")         // <--
         .require("cache_admin"); // <--
    </code></pre>
 3. restart the app

### Extensions

 - [Quartz Scheduler](quartz_scheduler/) - a Micro extension using [Quartz 2.2.x](http://quartz-scheduler.org/api/2.2.0/) for scheduling simple or complex jobs that are defined as standard [Micro Controllers](http://micro-docs.simplegames.ca/controllers.md).
 - [Mustache renderer](mustache/) - adding the [Mustache.java](https://github.com/spullara/mustache.java) rendering support to Micro.
 - [ActiveJDBC-M](activejdbc_m/) - [ActiveJDBC](https://code.google.com/p/activejdbc/) is a Java implementation of the Active Record design pattern.
 - [Cache admin](cache_admin/) - minimalistic admin interface for the Micro cache.
 - [ST](st/) - extend Micro with the [StringTemplate](http://www.stringtemplate.org/) template engine.

