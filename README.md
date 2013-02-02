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
 2. Edit the `application.bsh` startup controller and require the extension, example:
    <pre><code>
     site.ExtensionsManager
         .require("i18N")         // <--
         .require("cache_admin"); // <--
    </code></pre>
 3. restart the app

### Extensions

 - [Cache admin](cache-admin/README) - minimalistic admin interface for the Micro cache.

