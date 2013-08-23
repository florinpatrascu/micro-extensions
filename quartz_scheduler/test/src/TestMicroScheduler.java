import ca.simplegames.micro.Globals;
import ca.simplegames.micro.Micro;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.extensions.ExtensionsManager;
import ca.simplegames.micro.extensions.quartz.MicroJob;
import ca.simplegames.micro.extensions.quartz.MicroScheduler;
import ca.simplegames.micro.extensions.quartz.MicroSchedulerExtension;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.quartz.DateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.fail;

/**
 * Test class for validating the functionality of the Quartz-based Micro extension
 * <p/>
 * see: http://www.tutorialspoint.com/junit/junit_using_assertion.htm, for more details about JUnit
 * <p/>
 * Warning!
 * - this unit will only work if you have defined the "EXTENSIONS_FOLDER" in your environment
 * and it is available when you run this test
 * - run your tests from the extension's own folder, provided you have a similar file layout
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-08-21 9:44 PM)
 */

@RunWith(JUnit4.class)
public class TestMicroScheduler {
  protected static final Logger log = LoggerFactory.getLogger(TestMicroScheduler.class);
  public static final String QUARTZIFIED = "quartzified";
  public static Micro micro;
  private static SiteContext SITE;
  private static String EXTENSION_CONFIG_NAME = "quartz_scheduler";
  private static String EXTENSION_NAME = "MicroScheduler";
  private static MicroSchedulerExtension microScheduler;
  private static MicroContext genericContext;
  Calendar calendar = Calendar.getInstance(); // a calendar using the default time zone and locale.

  @Before
  public void cleanTheGlobalVars() {
    if (SITE.get(QUARTZIFIED) != null) {
      SITE.remove(QUARTZIFIED);
    }
  }

  @Test
  public void testInitialConfiguration() {
    Assert.assertTrue(microScheduler.getName().equalsIgnoreCase(EXTENSION_NAME));
  }

  @Test
  public void testDelayedController() throws Exception {
    //junit.framework.Assert.fail("implement me");
    SITE.getControllerManager().execute("TestSetGlobalVar.bsh", genericContext);
    Assert.assertNotNull("improper result from running the test controller", SITE.get(QUARTZIFIED));
    SITE.remove(QUARTZIFIED);
    log.info("Running a delayed controller asynchronously");
    calendar.add(Calendar.SECOND, 5);
    microScheduler.getScheduler().enqueue("TestSetGlobalVar.bsh", calendar.getTimeInMillis());
    Assert.assertNull("the test controller was executed prematurely", SITE.get(QUARTZIFIED));
    Thread.sleep(6000);
    Assert.assertNotNull("the test controller wasn't running at all?", SITE.get(QUARTZIFIED));
  }

  @Test
  /**
   * create and schedule a job to run after a delay of 2 seconds, find it before it runs
   */
  public void findJob() throws Exception {
    MicroJob job = microScheduler.getScheduler().enqueue("TestSetGlobalVar.bsh",
        DateBuilder.futureDate(2, DateBuilder.IntervalUnit.SECOND).getTime());

    MicroJob sameJob = microScheduler.getScheduler().findJob(job.getName(), job.getQueue());
    Assert.assertNotNull("job cannot be found after its creation", sameJob);
    Thread.sleep(3000);
  }


  @Test
  /**
   * create few jobs in the default queue {@link ca.simplegames.micro.extensions.quartz.MicroScheduler.DEFAULT_QUEUE_NAME}
   * and try to find their keys before they run
   */
  public void findJobKeys() throws Exception {
    MicroJob job1 = microScheduler.getScheduler().enqueue("TestSetGlobalVar.bsh",
        DateBuilder.futureDate(3, DateBuilder.IntervalUnit.SECOND).getTime());

    MicroJob job2 = microScheduler.getScheduler().enqueue("TestSetGlobalVar.bsh",
        DateBuilder.futureDate(2, DateBuilder.IntervalUnit.SECOND).getTime());

    List<String> jobKeys = microScheduler.getScheduler().findJobsMatchingQueue(MicroScheduler.DEFAULT_QUEUE_NAME);
    Assert.assertNotNull("can't find any jobs in the default queue", jobKeys);

    Assert.assertTrue("job1 can't be found", jobKeys.contains(job1.getName()));
    Assert.assertTrue("job2 can't be found", jobKeys.contains(job2.getName()));

    microScheduler.getScheduler().deleteJob(job1);
    microScheduler.getScheduler().deleteJob(job2);
  }

  @Test
  /**
   * create and schedule a job to run after a delay of 4 seconds. Delete the job before it has a chance
   * to run. Wait for 6 seconds and test to check if it was successfully deleted.
   */
  public void deleteJob() throws Exception {
    MicroJob job = microScheduler.getScheduler().enqueue("TestSetGlobalVar.bsh",
        DateBuilder.futureDate(4, DateBuilder.IntervalUnit.SECOND).getTime());

    MicroJob sameJob = microScheduler.getScheduler().findJob(job.getName(), job.getQueue());
    Assert.assertNotNull("job cannot be found after its creation", sameJob);

    microScheduler.getScheduler().deleteJob(job);
    Thread.sleep(6000);
    Assert.assertNull("the controller was not supposed to run after deletion", SITE.get(QUARTZIFIED));
  }

  @BeforeClass
  public static void loadMicro() throws Exception {
    File webAppPath = new File("test/webapp");
    micro = new Micro(webAppPath.getPath(), null, "./lib");
    Assert.assertNotNull(micro);

    SITE = micro.getSite();
    Assert.assertNotNull(SITE);
    SITE.setMicroEnv(Globals.TEST);

    String extensionsFolder = System.getenv("EXTENSIONS_FOLDER");
    Assert.assertNotNull("Must specify the extensions folder. Missing.", extensionsFolder);

    File extensionConfigFile = new File(extensionsFolder + File.separator + EXTENSION_CONFIG_NAME + ".yml");
    SITE.setExtensionsManager(
        new ExtensionsManager(SITE, new File[]{extensionConfigFile})
    );
    SITE.getExtensionsManager().require(EXTENSION_CONFIG_NAME);

    Assert.assertNotNull("Micro 'SITE' initialization failed", micro.getSite().getWebInfPath());
    Assert.assertTrue("Micro is not pointing to the correct test web app",
        SITE.getWebInfPath().getAbsolutePath().contains("webapp/WEB-INF"));
    Assert.assertTrue("Micro test web app is not properly defined",
        SITE.getWebInfPath().exists());

    microScheduler = (MicroSchedulerExtension) SITE.get(EXTENSION_NAME);
    Assert.assertNotNull(String.format("The %s extension, was not loaded.", EXTENSION_NAME), microScheduler);
    try {
      microScheduler.getScheduler().start();
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

    genericContext = new MicroContext();
    genericContext.with(Globals.SITE, SITE)
        .with(Globals.LOG, log)
        .with(Globals.MICRO_ENV, SITE.getMicroEnv());
  }

  @AfterClass
  public static void tearDown() {
    try {
      microScheduler.getScheduler().stop();
    } catch (Exception e) {
      e.printStackTrace();
      fail(String.format("Can't stop the scheduler properly; %s", e.getMessage()));
    }
  }
}
