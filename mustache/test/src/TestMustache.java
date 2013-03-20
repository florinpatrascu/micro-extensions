
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
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-03-19 5:54 PM)
 */
public class TestMustache {
    protected static final Log log = LogFactory.getLog(TestMustache.class);
    public static Micro micro;
    private static SiteContext SITE;
    private static String EXTENSION_NAME = "mustache";


    @Test
    public void testMustache() throws Exception {
        Context<String> input = new MapContext<String>()
                .with(Rack.REQUEST_METHOD, "GET")
                .with(Rack.PATH_INFO, "/index.html");

        RackResponse response = micro.call(input);
        final String bodyAsString = RackResponse.getBodyAsString(response);
        Assert.assertEquals("The rendered result is not right",bodyAsString,
                micro.getSite().getRepositoryManager().getRepository("partials").read("simple.txt"));
    }

    @BeforeClass
    public static void setup() throws Exception {
        File webAppPath = new File("webapp");
        micro = new Micro(webAppPath.getPath(), null, "../../lib");
        Assert.assertNotNull(micro);

        SITE = micro.getSite();
        Assert.assertNotNull(SITE);
        SITE.setMicroEnv(Globals.TEST);

        String extensionsFolder = System.getenv("EXTENSIONS_FOLDER");
        Assert.assertNotNull("Must specify the extensions folder. Missing.", extensionsFolder);

        File extensionConfigFile = new File(extensionsFolder + EXTENSION_NAME + ".yml");
        SITE.setExtensionsManager(
                new ExtensionsManager(SITE, new File[]{extensionConfigFile})
        );
        SITE.getExtensionsManager().require(EXTENSION_NAME);

        Assert.assertNotNull("Micro 'SITE' initialization failed", micro.getSite().getWebInfPath());
        Assert.assertTrue("Micro is not pointing to the correct test web app",
                SITE.getWebInfPath().getAbsolutePath().contains("webapp/WEB-INF"));
        Assert.assertTrue("Micro test web app is not properly defined",
                SITE.getWebInfPath().exists());
    }
}
