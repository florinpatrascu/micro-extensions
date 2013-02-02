## Cache-admin

This is a very basic administrative extension for displaying various cache statistics and clearing the cache in a Micro web application.
It is also illustrating the flexibility of the extensions support in the Micro framework. This extension is showing you how to define your own repositories and create your own UI, create Routes and publish local static content such as: `.css` and `.png` files, all these completely isolated from the host application. It also demonstrates the ability of Micro's class loader to use 3rd party libraries.

### Build from source

    # check if the `${MICRO_HOME}` environment variable is defined
    # then:

    $ cd cache_admin
    $ ant clean; ant dist
    
### Use
Copy (or create symbolic links) the `cache-admin` folder and `cache-admin.yml` file to your application extensions folder. The `extensions` folder will contain at least the following:

    extensions/
      ├── cache_admin/ 
      ├── cache_admin.yml
      └── i18N.yml
  
Edit the `application.bsh` startup controller and required the `cache_admin` extension, example:

    site.ExtensionsManager
        .require("i18N")
        .require("cache_admin"); // <-- just added

restart the app and go to: `http://localhost:8080/cache`. The following interface will be shown:

![screen](https://www.evernote.com/shard/s31/sh/36458891-8828-40ef-9c61-dcfb439961c8/66099e368840e0e7545d5e7a395afd0a/res/889bbec7-ecd4-4c42-91c2-1f350b91bcda/skitch.png)
  
Please secure or hide the access to this extension when running in `production` mode.

### License
**Apache License 2**, see the `LICENSE` file in this folder.
