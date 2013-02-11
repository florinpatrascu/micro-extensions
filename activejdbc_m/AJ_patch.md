### Activejdbc (AJ) patch ###

*dev notes*

I had to patch AJ's `org.javalite.activejdbc.Model.ClassGetter` as an interim solution until I can find a better way to tame its classloader.


## Old code
    
    static class ClassGetter extends SecurityManager {
        public String getClassName() {
            Class[] classes = getClassContext();
            for (Class clazz : classes) {
                if (Model.class.isAssignableFrom(clazz) && 
                        clazz != null && !clazz.equals(Model.class)) {
                    return clazz.getName();
                }
            }
            throw new InitException( \
              "failed to determine Model class name, are you sure \
                      models have been instrumented?");
        }
    }

    

## New code
    
    static Reflections reflections = new Reflections("");
    static class ClassGetter extends SecurityManager {
        public String getClassName() {
            Set<Class<? extends Model>> classes = 
                    reflections.getSubTypesOf(Model.class);

            for (Class klass : classes) {
                if (Model.class.isAssignableFrom(klass) 
                      && klass != null && !klass.equals(Model.class)) {
                    return klass.getName();
                }
            }
            throw new InitException( \
              "failed to determine Model class name, are you sure \
                      models have been instrumented?");
        }
    }

the new code depends on the [Reflections](http://code.google.com/p/reflections/) library.
    
    <dependency>
       <groupId>org.reflections</groupId>
       <artifactId>reflections</artifactId>
       <version>0.9.8</version>
    </dependency>
    

The dependencies will go away as soon as AJ can be used in its native form, provided the classloader issue is solved.