package ca.simplegames.micro.extensions.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Intercepts the calls from Quartz and trigger the execution of a micro controller
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-08-17 8:07 PM)
 */
public class MicroJobController implements Job {
  public void execute(JobExecutionContext ctx) throws JobExecutionException {
    MicroSchedulerAPI scheduler = MicroSchedulerAPI.getInstance();
    JobDataMap jobData = ctx.getJobDetail().getJobDataMap();
    MicroJob job = null;

    try {

      job = (MicroJob) jobData.get(MicroScheduler.MICRO_JOB_ATTRIBUTE_NAME);
      scheduler.execute(job);

    } catch (Exception e) {
      e.printStackTrace();
      throw new JobExecutionException(e.getMessage() + "; cannot execute this job:" + job);
    }
  }
}
