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
    
    static class ClassGetter extends SecurityManager {
        public String getClassName() {
            try {
                Field f = ClassLoader.class.getDeclaredField("classes");
                f.setAccessible(true);
                ClassLoader cl = Thread.currentThread().getContextClassLoader();

                Vector<Class> classes =  (Vector<Class>) f.get(cl);
                for (Class clazz : classes) {
                    if (Model.class.isAssignableFrom(clazz) && clazz != null && !clazz.equals(Model.class)) {
                        return clazz.getName();
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            throw new InitException("failed to determine Model class name, are you sure models have been instrumented?");
        }
    }
