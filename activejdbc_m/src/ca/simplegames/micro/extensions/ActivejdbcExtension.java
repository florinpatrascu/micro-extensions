/*
 * Copyright (c)2013 Florin T.Pătraşcu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.simplegames.micro.extensions;

import ca.simplegames.micro.Extension;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.extensions.activejdbc.ConnectionSpecWrapper;
import org.javalite.activejdbc.ConnectionJdbcSpec;
import org.javalite.activejdbc.DB;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Adding support for the activejdbc library. Will set a global attribute that is available site wide
 * giving access to this extension's main functions:
 * - before
 * - after
 * - onException
 * <p/>
 * See: http://code.google.com/p/activejdbc/ for more details about acivejdbc
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-02-06 9:23 PM)
 */
public class ActivejdbcExtension implements Extension {
    public static final String DEFAULT_DB_NAME = "default";
    private String name;
    private boolean debug;
    private static Map<String, List<ConnectionSpecWrapper>> connectionWrappers = new HashMap<String, List<ConnectionSpecWrapper>>();
    private SiteContext site;


    @SuppressWarnings("unchecked")
    @Override
    public Extension register(String name, SiteContext site, Map<String, Object> configuration) throws Exception {
        this.site = site;
        site.with(name, this); //<- set a global attribute that can access this Extension by name
        File appPath = site.getApplicationPath();
        File extensionPath = new File(appPath, "/extensions/" + name);
        File appConfigPath = site.getApplicationConfigPath();
        Map<String, Object> options = (Map<String, Object>) configuration.get("options");
        File dbConfigFile = new File(appConfigPath, (String) options.get("db"));
        File modelsDir = new File(appConfigPath, (String) options.get("models"));
        debug = (Boolean) options.get("debug");

        if (dbConfigFile.exists()) {
            Map<String, Object> dbConfigForAll = (Map<String, Object>) new Yaml().load(new FileInputStream(dbConfigFile));
            Map<String, Object> dbConfig = (Map<String, Object>) dbConfigForAll.get(site.getMicroEnv());
            ConnectionSpecWrapper jdbcConnectionWrapper = new ConnectionSpecWrapper();
            //todo: check if jdbc/jndi and implement each case

            if (dbConfig != null) {
                jdbcConnectionWrapper.setConnectionSpec(
                        new ConnectionJdbcSpec(
                                (String) dbConfig.get("driver"), (String) dbConfig.get("url"),
                                (String) dbConfig.get("user"), (String) dbConfig.get("password")));

                // todo add multiple database connections
                connectionWrappers.put(site.getMicroEnv(), Collections.singletonList(jdbcConnectionWrapper));
                this.name = name;
            } else {
                throw new ExceptionInInitializerError(
                        String.format("Unable to create a database connection wrapper for: %s", site.getMicroEnv()));
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("%s, not found.", dbConfigFile.getAbsolutePath()));
        }
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    public void before() {
        before(null, false);
    }

    public void before(String dbName, boolean manageTransaction) {
        String dbNameWithDefault = dbName == null ? DEFAULT_DB_NAME : dbName;
        List<ConnectionSpecWrapper> connectionWrappers = getConnectionWrappers(dbNameWithDefault);

        for (ConnectionSpecWrapper connectionWrapper : connectionWrappers) {
            DB db = new DB(connectionWrapper.getDbName());
            db.open(connectionWrapper.getConnectionSpec());
            if (manageTransaction) {
                db.openTransaction();
            }
        }

    }

    public void after() {
        after(null, false);
    }

    public void after(String dbName, boolean manageTransaction) {
        String dbNameWithDefault = dbName == null ? DEFAULT_DB_NAME : dbName;
        List<ConnectionSpecWrapper> connectionWrappers = getConnectionWrappers(dbNameWithDefault);
        if (connectionWrappers != null && !connectionWrappers.isEmpty()) {
            for (ConnectionSpecWrapper connectionWrapper : connectionWrappers) {
                DB db = new DB(connectionWrapper.getDbName());
                if (manageTransaction) {
                    db.commitTransaction();
                }
                db.close();
            }
        }
    }

    public void onException() {
        onException(null, false);
    }

    public void onException(String dbName, boolean manageTransaction) {

        String dbNameWithDefault = dbName == null ? DEFAULT_DB_NAME : dbName;
        List<ConnectionSpecWrapper> connectionWrappers = getConnectionWrappers(dbNameWithDefault);

        if (connectionWrappers != null && !connectionWrappers.isEmpty()) {
            for (ConnectionSpecWrapper connectionWrapper : connectionWrappers) {
                DB db = new DB(connectionWrapper.getDbName());
                if (manageTransaction) {
                    db.rollbackTransaction();
                }
                db.close();
            }
        }
    }

    /**
     * If dbName not provided, returns all connections which are not for testing.
     *
     * @return all the connections for a db name in a given environment
     */
    private List<ConnectionSpecWrapper> getConnectionWrappers(String dbName) {
        List<ConnectionSpecWrapper> result = new LinkedList<ConnectionSpecWrapper>();

        for (ConnectionSpecWrapper connectionWrapper : connectionWrappers.get(site.getMicroEnv())) {
            if ((dbName == null || dbName.equals(connectionWrapper.getDbName())))
                result.add(connectionWrapper);
        }
        return result;
    }

    public boolean isDebug() {
        return debug;
    }
}

