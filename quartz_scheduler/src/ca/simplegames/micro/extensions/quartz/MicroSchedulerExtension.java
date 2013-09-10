package ca.simplegames.micro.extensions.quartz;

import ca.simplegames.micro.Extension;
import ca.simplegames.micro.SiteContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This is a Micro extension using Quartz 2.2.x for scheduling simple or complex jobs that
 * are defined as standard Micro Controllers
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-08-19 11:40 PM)
 */
public class MicroSchedulerExtension implements Extension {
  private static final String EXTENSION_NAME = "MicroScheduler";
  private Logger log = LoggerFactory.getLogger(getClass());
  private MicroScheduler scheduler;
  private String name;


  @Override
  public Extension register(String name, SiteContext site, Map<String, Object> config) throws Exception {
    log.info("Loading the scheduler default config from: " + name);
    this.name = StringUtils.defaultString((String) config.get("alias"), EXTENSION_NAME);
    this.scheduler = MicroSchedulerAPI.setSite(site);
    site.with(this.name, this); //<- publish the Scheduler extension

    // todo: custom configurable, let the user define the default queue name, etc.

    log.info("Micro scheduler initialized, but not started. Use scheduler.start() for that!");
    return this;
  }

  public MicroScheduler getScheduler() {
    return scheduler;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void shutdown() {
    try {
      scheduler.stop();
      log.info("Scheduler stopped, as requested.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
