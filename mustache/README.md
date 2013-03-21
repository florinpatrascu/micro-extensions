## The `Mustache` engine

This extension is adding the [Mustache.java](https://github.com/spullara/mustache.java) rendering support to Micro.

### Build from source

Check if the `${MICRO_HOME}` environment variable is defined:

      $ echo $MICRO_HOME 
      ~/projects/micro
     
Then:
   
      $ cd mustache
      $ ant dist
    
### Use it with a (µ)Micro web application
Copy (or create symbolic links to) the `mustache` folder and `mustache.yml` file to your application extensions folder. The `extensions` folder will contain at least the following:

    extensions/
      ├── mustache/ 
      ├── mustache.yml
      └── ...

The `mustache.yml` may contain various configuration options for the `Mustache` engine. Variant:

    class: ca.simplegames.micro.extensions.MustacheExtension
    engine:
      name: mustache
      class: ca.simplegames.micro.viewers.mustache.MustacheViewRenderer
      options:
        cache: views 
        #      ^^^^^ the name of the cache used for the compiled mustaches.

Edit the `application.bsh` startup controller and require the `mustache` extension, example:

    site.ExtensionsManager
        .require("mustache");

Visit [Mustache](http://mustache.github.com/) to learn more about this template engine.

### License
**Apache License 2**, see the [LICENSE](LICENSE) file in this folder.
