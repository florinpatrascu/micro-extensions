## The `Mustache` engine

This extension is adding the [Mustache.java](https://github.com/spullara/mustache.java) rendering support to Micro.

### Build from source

    # check if the `${MICRO_HOME}` environment variable is defined
    # then:

    $ cd mustache
    $ ant dist
    
### Install
Copy (or create symbolic links to) the `mustache` folder and `mustache.yml` file to your application extensions folder. The `extensions` folder will contain at least the following:

    extensions/
      ├── mustache/ 
      ├── mustache.yml
      └── ...

The `mustache.yml` may contain various configuration options for the `Mustache` engine. Variant:

    class: ca.simplegames.micro.extensions.MustacheExtension
    engine:
      name: st
      class: ca.simplegames.micro.viewers.mustache.MustacheViewRenderer
      options: {}

Edit the `application.bsh` startup controller and require the `st` extension, example:

    site.ExtensionsManager
        .require("i18N")
        .require("Mustache");
        
That's all. 

### License
**Apache License 2**, see the `LICENSE` file in this folder.
