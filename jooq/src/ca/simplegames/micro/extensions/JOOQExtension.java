/*
 * Copyright (c)2014 Florin T.Pătraşcu
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
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the support for the jOOQ library. Micro will set a global attribute available to the entire
 * web application that will give access to this extension's main functions:
 * - before
 * - after
 * - onException
 * <p/>
 * See: http://www.jooq.org/doc/3.3/manual/getting-started/tutorials/ for more details about jOOQ
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2014-03-29)
 */
public class JOOQExtension implements Extension {
  public static final Logger log = LoggerFactory.getLogger(JOOQExtension.class);

  private String name;
  private boolean debug;

  // todo: Create a jOOQ configuration based on the database.yml file
  // private Configuration configuration;
  private BoneCPDataSource ds = null;
  private Map<String, SQLDialect> dialects = new HashMap<String, SQLDialect>();
  private SQLDialect dialect = SQLDialect.H2;

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
                (String) dbConfig.get("password") : Globals.EMPTY_STRING
        );

        Integer maxConnections = dbConfig.get("pool") != null ? (Integer) dbConfig.get("pool") : 5;

        int minConnections = (int) Math.max(1, Math.ceil(maxConnections / 3));
        ds.setMaxConnectionsPerPartition(maxConnections);
        ds.setMinConnectionsPerPartition(minConnections);
        ds.setPartitionCount(1);
        this.name = name;


        // Define a lookup for the support open source databases:
        // - CUBRID, Derby, Firebird, H2, HSQLDB, MariaDB, MySQL, Postgres, SQLite
        dialects.put("H2", SQLDialect.H2);
        dialects.put("CUBRID", SQLDialect.CUBRID);
        dialects.put("Derby", SQLDialect.DERBY);
        dialects.put("Firebird", SQLDialect.FIREBIRD);
        dialects.put("HSQLDB", SQLDialect.HSQLDB);
        dialects.put("MariaDB", SQLDialect.MARIADB);
        dialects.put("MySQL", SQLDialect.MYSQL);
        dialects.put("Postgres", SQLDialect.POSTGRES);
        dialects.put("SQLite", SQLDialect.SQLITE);

        dialect = dialects.get(options.get("dialect"));


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

  public MDSL getDSL() throws Exception {
    return new MDSL(ds, dialect);
  }


  public void after(MDSL dsl) {
    after(dsl, false);
  }

  public void after(MDSL dsl, boolean manageTransaction) {
    if (dsl != null && dsl.hasConnection()) {
      try {
        if (manageTransaction) {
          /// commitTransaction(); how??
        }
      } finally {
        dsl.closeConnection();
      }
    }
  }

  public void onException(MDSL dsl) {
    onException(dsl, false);
  }

  public void onException(MDSL dsl, boolean manageTransaction) {
    if (dsl != null) {
      if (manageTransaction) {
        // how??? rollbackTransaction();
      }
      dsl.closeConnection();
    }
  }

  public boolean isDebug() {
    return debug;
  }

  public DataSource getDS() {
    return ds;
  }

  @Override
  public void shutdown() {
    // todo: identify the best way to notify jOOQ about an imminent shutdown
  }

  /**
   * mini wrapper that Controllers can use around, until I figure out a better solution
   * Thinking the jOOQ's own DSL should expose a close connection method or such?!
   */
  public class MDSL {
    private Connection connection;
    private DSLContext create;

    public MDSL(DataSource ds, SQLDialect dialect) throws Exception {
      connection = ds.getConnection();
      create = DSL.using(connection, dialect);
    }

    public Connection getConnection() {
      return connection;
    }

    public DSLContext getCreate() {
      return create;
    }

    public boolean hasConnection() {
      boolean connectionStatus = false;

      try {
        connectionStatus = connection != null && !connection.isClosed();
      } catch (SQLException e) {
        log.error("connection state error: " + e.getSQLState());
        e.printStackTrace();
      }

      return connectionStatus;
    }

    public void closeConnection() {
      if (hasConnection()) {
        try {
          connection.close();
        } catch (SQLException e) {
          log.error("cannot close the connection, state: " + e.getSQLState());
          e.printStackTrace();
        }
      }
    }
  }
}

