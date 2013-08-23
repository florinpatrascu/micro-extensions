package ca.simplegames.micro.extensions.quartz;

import java.util.Map;
import java.util.UUID;

/**
 * Micro job - the smallest definition of a worker. The result of instantiating this class is a MicroJob object that
 * will have a random name using UUID {@link UUID}, will contain a reference to a Micro controller
 * {@link ca.simplegames.micro.Controller} and that will be enqueued to run in a queue (or the default queue {@link MicroScheduler})
 * at a later time, or now if the time is not specified. The controller will be executed asynchronously by the Micro
 * framework, or by a custom user code.
 *
 * PRO Tip:
 *  - Only Store Primitive Data Types (including Strings) In the job attributes, this will
 *  avoid unnecessary data serialization issues short and long-term.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-08-15 9:22 PM)
 */
public class MicroJob {
  private String name = UUID.randomUUID().toString();
  private String queue;
  private String controller;
  private String description;
  private Map<String, Object> attributes;
  private long runAt;
  private MicroScheduler scheduler;

  /**
   * creates a new MicroJob instance that will have a random name using UUID {@link UUID} and that
   * will be enqueued in the Default queue {@link MicroScheduler}
   *
   * @param controller the name of the Micro controller
   * @param runAt      the time the controller is scheduled to run
   */
  public MicroJob(String controller, long runAt) {
    this.name = UUID.randomUUID().toString();
    this.queue = MicroScheduler.DEFAULT_QUEUE_NAME;
    this.controller = controller;
    this.runAt = runAt;
  }

  /**
   * creates a new MicroJob instance that will have a random name using UUID {@link UUID} and that
   * will be enqueued in the Default queue {@link MicroScheduler}
   *
   * @param controller the name of the Micro controller
   * @param attributes the attributes that will be transferred to the Controller at the run time.
   * @param runAt      the time the controller is scheduled to run
   */
  public MicroJob(String controller, Map<String, Object> attributes, long runAt) {
    this.name = UUID.randomUUID().toString();
    this.queue = MicroScheduler.DEFAULT_QUEUE_NAME;
    this.controller = controller;
    this.attributes = attributes;
    this.runAt = runAt;
  }


  /**
   * creates a new MicroJob instance that will have a random name using UUID {@link UUID} and that
   * will be enqueued in the Default queue {@link MicroScheduler} to run as soon as possible, asynchronously
   *
   * @param controller the name of the Micro controller
   * @param attributes the attributes that will be transferred to the Controller at the run time.
   */
  public MicroJob(String controller, Map<String, Object> attributes) {
    this.name = UUID.randomUUID().toString();
    this.queue = MicroScheduler.DEFAULT_QUEUE_NAME;
    this.controller = controller;
    this.attributes = attributes;
    this.runAt = 0;
  }

  public String getName() {
    return name;
  }

  public String getQueue() {
    return queue;
  }

  public void setQueue(String queue) {
    this.queue = queue;
  }

  public String getController() {
    return controller;
  }

  public void setController(String controller) {
    this.controller = controller;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public long getRunAt() {
    return runAt;
  }

  public void setRunAt(long runAt) {
    this.runAt = runAt;
  }

  public MicroScheduler getScheduler() {
    return scheduler;
  }

  public void setScheduler(MicroScheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public String toString() {
    return "MicroJob{" +
        "name='" + name + '\'' +
        ", queue='" + queue + '\'' +
        ", controller='" + controller + '\'' +
        ", description='" + description + '\'' +
        ", runAt=" + runAt +
        '}';
  }
}
