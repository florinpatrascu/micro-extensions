package ca.simplegames.micro.extensions.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Adding support for scheduling Micro controllers using the Quartz framework
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-08-15 9:17 PM)
 */
public interface MicroScheduler {
  public static final Logger LOG = LoggerFactory.getLogger(MicroScheduler.class);
  public static final String DEFAULT_QUEUE_NAME = "MICRO_QUEUE";
  public static final String MICRO_JOB_ATTRIBUTE_NAME = "job";
  public static final String TRIGGER_NAME_PREFIX = "MICRO_TRIGGER_";
  public static final String TRIGGER_GROUP_NAME_PREFIX = "MICRO_TRIGGER_GROUP_";
  public static final long DEFAULT_EXECUTION_DELAY = 1000L;  // 1 second

  /**
   * will schedule a job to run at a specific date and time
   *
   * @param controller  data that is transferred to the controller at the execution time
   * @param attributes  the data that will be provided to the controller at the run time
   * @param description a meaningful description of the job
   * @param runAt       the scheduled datetime to run at
   * @return a new MicroJob instance
   * @throws Exception
   */
  public MicroJob enqueue(String controller, Map<String, Object> attributes, String description, long runAt) throws Exception;

  /**
   * simplified utility method for scheduling a job
   *
   * @param controller data that is transferred to the controller at the execution time
   * @param runAt      the scheduled datetime to run at
   * @return a new MicroJob instance
   * @throws Exception
   */
  public MicroJob enqueue(String controller, long runAt) throws Exception;

  /**
   * simplified utility method for scheduling a job
   *
   * @param controller data that is transferred to the controller at the execution time
   * @param attributes the data that will be provided to the controller at the run time
   * @param runAt      the scheduled datetime to run at
   * @return a new MicroJob instance
   * @throws Exception
   */
  public MicroJob enqueue(String controller, Map<String, Object> attributes, long runAt) throws Exception;

  /**
   * will schedule a job to run every so often (interval), for a total of times specified by the repeatCount:
   *
   * @param controller  data that is transferred to the controller at the execution time
   * @param attributes  the data that will be provided to the controller at the run time
   * @param runAt       the scheduled datetime to run at
   * @param interval    how often to run this job (seconds)
   * @param repeatCount run for this total of times
   * @return a MicroJob instance
   * @throws Exception
   */
  public MicroJob enqueue(String controller, Map<String, Object> attributes, long runAt, int interval, int repeatCount) throws Exception;

  /**
   * will enqueue a job that will run according to a Cron schedule
   *
   * @param controller   data that is transferred to the controller at the execution time
   * @param attributes   the data that will be provided to the controller at the run time
   * @param cronSchedule a string containing a Cron definition. Example: "0,30 * * ? * MON-FRI", to schedule the
   *                     job to run every 30 seconds on Weekdays (Monday through Friday)
   * @return a MicroJob instance
   * @throws Exception
   */
  public MicroJob enqueue(String controller, Map<String, Object> attributes, String cronSchedule) throws Exception;

  public void enqueue(MicroJob job) throws Exception;

  public void deleteJob(MicroJob job) throws Exception;

  /**
   * find a job given its name and the queue name
   *
   * @param jobName  the job name, UUID
   * @param jobQueue the name of the queue
   * @return a job
   * @throws Exception
   */
  public MicroJob findJob(String jobName, String jobQueue) throws Exception;

  /**
   * get the names of all the Jobs in the matching groups.
   *
   *
   * @param queueName the name of the queue
   * @return all the job names in the matching groups.
   */
  public List<String> findJobsMatchingQueue(String queueName) throws Exception;

  /**
   * Get the names of all known Job groups.
   *
   * @return all the known Job groups.
   * @throws Exception
   */
  public  List<String> getJobGroupNames() throws Exception;

  public void start() throws Exception;

  public void stop() throws Exception;

  public void execute(MicroJob job) throws Exception;

  // public Scheduler getQuartzScheduler();
  // public SchedulerFactory getQuartzSchedulerFactory();
}
