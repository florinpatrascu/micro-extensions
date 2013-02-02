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

package ca.simplegames.micro.extensions.cache;


import ca.simplegames.micro.Extension;
import ca.simplegames.micro.Route;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.route.RouteWrapper;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-30 9:05 PM)
 */
public class AdminExtension implements Extension {
    private String name;

    @SuppressWarnings("unchecked")
    @Override
    public Extension register(String name, SiteContext site, Map<String, Object> configuration) throws Exception {
        File extensionPath = new File(site.getApplicationConfigPath(), "/extensions/cache_admin");
        this.name = name;

        site.getControllerManager().addPathToControllers(new File(extensionPath, "/controllers")) ;
        site.getRepositoryManager().addRepositories(
                extensionPath, (Map<String, Object>) configuration.get("repositories"));

        for (Map<String, Object> routeMap : (List<Map<String, Object>>)configuration.get("routes")) {
            try {
                String routePath = (String) routeMap.get("route");
                Route route = new RouteWrapper(routePath, routeMap);
                site.getRouteManager().add(route);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    @Override
    public String getName() {
        return name;
    }
}
