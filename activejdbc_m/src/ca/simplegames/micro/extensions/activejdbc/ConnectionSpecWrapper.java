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

package ca.simplegames.micro.extensions.activejdbc;

import org.javalite.activejdbc.ConnectionSpec;

/**
 * adapted from activeweb's {@code org.javalite.activeweb.ConnectionSpecWrapper}
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-02-07 9:52 PM)
 */
public class ConnectionSpecWrapper {

    private String dbName = "default";
    private ConnectionSpec connectionSpec;

    public ConnectionSpec getConnectionSpec() {
        return connectionSpec;
    }

    // in the original implementation, this is not a 'public' method
    public void setConnectionSpec(ConnectionSpec connectionSpec) {
        this.connectionSpec = connectionSpec;
    }

    public String getDbName() {
        return dbName;
    }

    void setDbName(String dbName) {
        if (dbName != null) {
            this.dbName = dbName;
        }
    }
}
