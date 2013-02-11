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

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.Micro;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.extensions.ExtensionsManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jrack.Context;
import org.jrack.Rack;
import org.jrack.RackResponse;
import org.jrack.context.MapContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

/**
 * Activejdbc integration test
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-02-08)
 */
public class ActivejdbcTest {
    protected static final Log log = LogFactory.getLog(ActivejdbcTest.class);
    public static Micro micro;
    private static SiteContext SITE;
    private static String EXTENSION_NAME ="activejdbc_m";


    @BeforeClass
    public static void setup() throws Exception {
        File webAppPath = new File("files");
        micro = new Micro(webAppPath.getPath(), null, "../../lib");
        Assert.assertNotNull(micro);
        SITE = micro.getSite();
        Assert.assertNotNull(SITE);
        SITE.setMicroEnv(Globals.TEST);
        // overwrite the ExtensionManager
        // todo: design a nicer support for integrating with the extensions testing units
        String extensionsFolder = System.getenv("EXTENSIONS_FOLDER");
        Assert.assertNotNull("Must specify the extensions folder. Missing.", extensionsFolder);
        File extensionConfigFile = new File(extensionsFolder + EXTENSION_NAME +".yml");
        SITE.setExtensionsManager(
                new ExtensionsManager(SITE, new File[]{extensionConfigFile})
        );
        SITE.getExtensionsManager().require(EXTENSION_NAME);

        Assert.assertNotNull("Micro 'SITE' initialization failed", micro.getSite().getWebInfPath());
        Assert.assertTrue("Micro is not pointing to the correct test web app",
                SITE.getWebInfPath().getAbsolutePath().contains("files/WEB-INF"));
        Assert.assertTrue("Micro test web app is not properly defined",
                SITE.getWebInfPath().exists());
    }


    /**
     * test if Micro can render the home page; it is dynamic content
     * <p/>
     * Method: call(Context<String> input)
     */
    @Test
    public void testMicroContent() throws Exception {
        Context<String> input = new MapContext<String>()
                .with(Rack.REQUEST_METHOD, "GET")
                .with(Rack.PATH_INFO, "/index.html");

        RackResponse response = micro.call(input);
        Assert.assertTrue(RackResponse.getBodyAsString(response).contains("Welcome!"));
    }

}
