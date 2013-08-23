package ca.simplegames.micro.extensions.quartz;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.utils.Assert;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Adding support for creating a Quartz standard scheduler from a Micro web app
 * <p/>
 * More Quartz related docs: http://www.quartz-scheduler.org/docs/quick_start_guide.html
 * <p/>
 * todo: refactor most of the methods implementations and clean up the code inside
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-08-15 10:55 PM)
 */
public class MicroSchedulerAPI implements MicroScheduler {
  private SchedulerFactory schedulerFactory;
  private Scheduler scheduler;
  private static MicroSchedulerAPI instance = new MicroSchedulerAPI();
  private static SiteContext site = null;

  private MicroSchedulerAPI() {
  }

  public static MicroSchedulerAPI getInstance() {
    return instance;
  }

  @Override
  public MicroJob enqueue(String controller, Map<String, Object> attributes, String description, long runAt) throws Exception {
    Assert.notNull(controller, "invalid controller name");

    MicroJob job = new MicroJob(controller, attributes, runAt);
    job.setDescription(description);
    JobKey key = schedule(job, new Date(runAt));
    // todo: decide what to do with the job key returned from Quartz
    return job;
  }

  @Override
  public MicroJob enqueue(String controller, long runAt) throws Exception {
    Assert.notNull(controller, "invalid controller name");

    MicroJob job = new MicroJob(controller, runAt);
    JobKey key = schedule(job, new Date(runAt));
    // todo: decide what to do with the job key returned from Quartz
    return job;
  }

  @Override
  public MicroJob enqueue(String controller, Map<String, Object> attributes, long runAt) throws Exception {
    Assert.notNull(controller, "invalid controller name");
    Assert.notNull(attributes, "invalid job data model");

    MicroJob job = new MicroJob(controller, attributes, runAt);
    JobKey key = schedule(job, new Date(runAt));
    // todo: decide what to do with the job key returned from Quartz
    return job;
  }

  @Override
  public MicroJob enqueue(String controller, Map<String, Object> attributes, long runAt, int interval, int repeatCount) throws Exception {
    Assert.notNull(controller, "invalid controller name");

    MicroJob job = new MicroJob(controller, attributes, runAt);
    JobKey key = schedule(job, new Date(runAt), interval, repeatCount);
    // todo: decide what to do with the job key returned from Quartz
    return job;

  }

  @Override
  public MicroJob enqueue(String controller, Map<String, Object> attributes, String cronSchedule) throws Exception {
    Assert.notNull(controller, "invalid controller name");
    Assert.notNull(cronSchedule, "invalid Cron schedule");

    MicroJob job = new MicroJob(controller, attributes);
    JobKey key = schedule(job, cronSchedule);
    // todo: decide what to do with the job key returned from Quartz
    return job;
  }

  @Override
  public void enqueue(MicroJob job) throws Exception {
    schedule(job, new Date(System.currentTimeMillis() + DEFAULT_EXECUTION_DELAY));
  }

  @Override
  public void deleteJob(MicroJob job) throws Exception {
    Assert.notNull(job, "invalid job");
    Assert.notNull(job.getName(), "invalid job name");
    Assert.notNull(job.getQueue(), "invalid job queue name");

    if (!scheduler.deleteJob(new JobKey(job.getName(), job.getQueue()))) {
      throw new Exception(String.format("cannot_delete_job: %s, group: %s", job.getName(), job.getQueue()));
    }

  }

  @Override
  public MicroJob findJob(String jobName, String jobQueue) throws Exception {
    Assert.notNull(jobName, "invalid job name");
    Assert.notNull(jobQueue, "invalid job queue name");

    JobKey jobKey = new JobKey(jobName, jobQueue);
    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
    MicroJob microJob = null;

    if (jobDetail != null && !jobDetail.getJobDataMap().isEmpty()) {
      microJob = (MicroJob) jobDetail.getJobDataMap().get(MicroScheduler.MICRO_JOB_ATTRIBUTE_NAME);
    }
    return microJob;
  }

  @Override
  public List<String> findJobsMatchingQueue(String queueName) throws Exception {
    List<String> jobNames = null;
    Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(queueName));

    if (jobKeys != null && !jobKeys.isEmpty()) {
      jobNames = new ArrayList<String>(jobKeys.size());
      for (JobKey jobKey : jobKeys) {
        jobNames.add(jobKey.getName());
      }
    }

    return jobNames;
  }

  @Override
  public List<String> getJobGroupNames() throws Exception {
    return scheduler.getJobGroupNames();
  }

  public void start() throws Exception {
    schedulerFactory = new StdSchedulerFactory();
    scheduler = schedulerFactory.getScheduler();
    scheduler.start();
    scheduler.resumeAll();
    MicroScheduler.LOG.info("MicroSchedulerAPI started.");
  }

  public void stop() throws Exception {
    if (scheduler.isStarted()) {
      scheduler.pauseAll();
      scheduler.shutdown(true);
    }
    MicroScheduler.LOG.info("MicroSchedulerAPI stopped.");
  }

  @Override
  public void execute(MicroJob job) throws Exception {
    Assert.notNull(job, "the job cannot be null");
    MicroContext context = new MicroContext<String>(
        job.getAttributes() != null && !job.getAttributes().isEmpty() ?
            job.getAttributes() : new HashMap<String, Object>()
    );

    context.with(Globals.SITE, site)
        .with(Globals.LOG, LoggerFactory.getLogger(job.getController()))
        .with(Globals.MICRO_ENV, site.getMicroEnv())
            // .with(Globals.CONTEXT, context) <-- don't, please!
        .with(MICRO_JOB_ATTRIBUTE_NAME, job);

    // if (job.getAttributes() != null && !job.getAttributes().isEmpty()) {
    //   context.getMap().putAll(job.getAttributes());
    // }

    for (Repository repository : site.getRepositoryManager().getRepositories()) {
      context.with(repository.getName(), repository.getRepositoryWrapper(context));
    }

    job.setScheduler(this);  //not sure why I am doing this, probably a convenience for future improvements?
    site.getControllerManager().execute(job.getController(), context);
  }

  public String getName() throws Exception {
    return scheduler.getSchedulerName();
  }

  /**
   * will schedule a job to run every so often (interval), for a total of times specified by the repeatCount:
   *
   * @param job         data that is transferred to the controller at the execution time
   * @param runTime     the scheduled datetime to run at
   * @param interval    how often to run this job (seconds)
   * @param repeatCount run for this total of times
   * @return details about the job
   * @throws Exception
   */
  private JobKey schedule(MicroJob job, Date runTime, int interval, int repeatCount) throws Exception {
    Trigger trigger = null;
    TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
        .withIdentity(TRIGGER_NAME_PREFIX + job.getName(), TRIGGER_GROUP_NAME_PREFIX + job.getQueue())
        .startAt(runTime);

    if (interval > 0 && repeatCount > 0) {
      trigger = triggerBuilder
          .withSchedule(SimpleScheduleBuilder.simpleSchedule()
              .withIntervalInSeconds(interval)
              .withRepeatCount(repeatCount))
          .build();
    } else {
      trigger = triggerBuilder.build();
    }

    return createQuartzJob(job, trigger);
  }

  private JobKey schedule(MicroJob job, String cron) throws Exception {
    CronTrigger trigger = TriggerBuilder.newTrigger()
        .withIdentity(TRIGGER_NAME_PREFIX + job.getName(), TRIGGER_GROUP_NAME_PREFIX + job.getQueue())
        .withSchedule(CronScheduleBuilder.cronSchedule(cron))
        .build();

    return createQuartzJob(job, trigger);
  }

  @SuppressWarnings("unchecked")
  private JobKey createQuartzJob(MicroJob job, Trigger trigger) throws SchedulerException {
    Map jobData = new HashMap();
    jobData.put(MICRO_JOB_ATTRIBUTE_NAME, job);


    JobDetail jobDetail = JobBuilder.newJob(MicroJobController.class)
        .withIdentity(job.getName(), job.getQueue())
        .setJobData(new JobDataMap(jobData))
        .build();

    scheduler.scheduleJob(jobDetail, trigger);
    return jobDetail.getKey();
  }

  private JobKey schedule(MicroJob job, Date runTime) throws Exception {
    return schedule(job, runTime, 0, 0);
  }

  public Scheduler getQuartzScheduler() {
    return scheduler;
  }

  public SchedulerFactory getQuartzSchedulerFactory() {
    return schedulerFactory;
  }

  public static MicroSchedulerAPI setSite(SiteContext site) {
    MicroSchedulerAPI.site = site;
    return instance;
  }
}
