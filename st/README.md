## StringTemplate engine

This extension is adding the [StringTemplate](http://www.stringtemplate.org/) rendering support to Micro.

### Build from source

    # check if the `${MICRO_HOME}` environment variable is defined
    # then:

    $ cd st
    $ ant clean; ant dist
    
### Install
Copy (or create symbolic links) the `st` folder and `st.yml` file to your application extensions folder. The `extensions` folder will contain at least the following:

    extensions/
      ├── st/ 
      ├── st.yml
      └── ...
  
Edit the `application.bsh` startup controller and required the `st` extension, example:

    site.ExtensionsManager
        .require("i18N")
        .require("st"); // <-- just added
        
That's all. The `ST` engine will be automatically configured by this extension and made available to the entire application.

Restart the app and use your ST views.

### License
**Apache License 2**, see the `LICENSE` file in this folder.
