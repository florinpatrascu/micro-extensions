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

package ca.simplegames.micro.viewers.mustache;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.cache.MicroCache;
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.utils.CollectionUtils;
import ca.simplegames.micro.utils.IO;
import ca.simplegames.micro.viewers.ViewException;
import ca.simplegames.micro.viewers.ViewRenderer;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * A Mustache(.java) view renderer, using:
 * - https://github.com/spullara/mustache.java
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-02-03 7:42 PM)
 */
public class MustacheViewRenderer implements ViewRenderer {
    protected static final Log log = LogFactory.getLog(MustacheViewRenderer.class);

    public static final String NAME = "mustache";
    public static final String KEY_SEP = ":";
    private static MustacheFactory mf = new DefaultMustacheFactory();
    private MicroCache mustaches = null;

    @Override
    public long render(String path, Repository repository, MicroContext context, Writer out) throws FileNotFoundException, ViewException {

        if (repository != null && out != null) {

            try {
                Mustache mustache;
                String key = repository.getName() + KEY_SEP + path;

                StringWriter sw = new StringWriter();
                StringReader source = new StringReader(repository.read(path));
                if (mustaches != null) {
                    Element mustacheElement = (Element) mustaches.get(key);
                    if (mustacheElement == null) {
                        mustache = mf.compile(source, Globals.UTF8);
                        mustaches.put(key, new Element(NAME, mustache));
                    }else{
                        mustache = (Mustache) mustacheElement.getObjectValue();
                    }
                } else {
                    mustache = mf.compile(source, Globals.UTF8);
                }

                if (!CollectionUtils.isEmpty(context.getMap())) {
                    mustache.execute(sw, context.getMap());
                }

                return IO.copy(new StringReader(sw.toString()), out);

            } catch (FileNotFoundException e) {
                throw new FileNotFoundException(String.format("%s not found.", path));
            } catch (Exception e) {
                throw new ViewException(e.getMessage());
            }
        }
        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadConfiguration(SiteContext site, Map<String, Object> configuration) throws Exception {
        String cacheName = "views";

        if (configuration != null) {
            cacheName = (String) configuration.get("cache");
        }

        if (site.isProduction()) {
            try {
                mustaches = site.getCacheManager().getCache(cacheName);
            } catch (Exception e) {
                log.error(String.format("Can't create the cache: `%s`; Mustache will run without the cache.",
                        cacheName));
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
