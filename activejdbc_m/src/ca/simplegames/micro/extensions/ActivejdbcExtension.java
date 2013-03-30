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
import ca.simplegames.micro.Globals;
import ca.simplegames.micro.SiteContext;
import com.jolbox.bonecp.BoneCPDataSource;
import org.javalite.activejdbc.Base;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

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
    private BoneCPDataSource ds = null;

    @SuppressWarnings("unchecked")
    @Override
    public Extension register(String name, SiteContext site, Map<String, Object> configuration) throws Exception {
        site.with(name, this); //<- set a global attribute that can access this Extension by name
        File appPath = site.getApplicationPath();
        //File extensionPath = new File(appPath, "/extensions/" + name);
        File appConfigPath = site.getApplicationConfigPath();
        Map<String, Object> options = (Map<String, Object>) configuration.get("options");
        File dbConfigFile = new File(appConfigPath, (String) options.get("db"));
        //File modelsDir = new File(appConfigPath, (String) options.get("models"));
        debug = (Boolean) options.get("debug");

        if (dbConfigFile.exists()) {
            Map<String, Object> dbConfigForAll = (Map<String, Object>) new Yaml().load(new FileInputStream(dbConfigFile));
            Map<String, Object> dbConfig = (Map<String, Object>) dbConfigForAll.get(site.getMicroEnv());

            if (dbConfig != null) {
                String driver = (String) dbConfig.get("driver");
                Class.forName(driver);
                ds = new BoneCPDataSource();
                ds.setJdbcUrl((String) dbConfig.get("url"));
                ds.setUsername((String) dbConfig.get("user"));
                ds.setPassword(
                        dbConfig.get("password") != null ?
                                (String) dbConfig.get("password") : Globals.EMPTY_STRING);

                Integer maxConnections = dbConfig.get("pool") != null ? (Integer) dbConfig.get("pool") : 5;

                int minConnections = (int) Math.max(1, Math.ceil(maxConnections / 3)); // todo let the user configure it
                ds.setMaxConnectionsPerPartition(maxConnections);
                ds.setMinConnectionsPerPartition(minConnections);
                ds.setPartitionCount(1);
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
        before(false);
    }

    public void before(boolean manageTransaction) {
        Base.open(ds);
        if (manageTransaction) {
            Base.openTransaction();
        }
    }

    public void after() {
        after(false);
    }

    public void after(boolean manageTransaction) {
        if (Base.hasConnection()) {
            try {
                if (manageTransaction) {
                    Base.commitTransaction();
                }
            } finally {
                Base.close();
            }
        }
    }

    public void onException() {
        onException(false);
    }

    public void onException(boolean manageTransaction) {
        if (manageTransaction) {
            Base.rollbackTransaction();
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public Base getBase() {
        return new Base();
    }

    public DataSource getDS() {
        return ds;
    }
}

