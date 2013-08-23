## Micro Quartz Scheduler

This is a Micro extension using [Quartz 2.2.x](http://quartz-scheduler.org/api/2.2.0/) for scheduling simple or complex jobs that are defined as standard [Micro Controllers](http://micro-docs.simplegames.ca/controllers.md).

## Using the scheduler
Like any other [Micro Extensions](http://micro-docs.simplegames.ca/extensions.md) you will have to declare and load the `quartz_scheduler` into your local Micro web application. This is easily done in the startup controller `config/application.bsh`. Excerpt:

    site.ExtensionsManager
        .require("quartz_scheduler");

The design of this extension is such that it will allow you initialize other site components **before** starting the Quartz scheduler, a useful feature when those components are required by the controllers running at scheduled times. When you are ready, start the scheduler:

    site.get("MicroScheduler").Scheduler.start();

You have to do this just once. Similarly, if you want to stop the scheduler:

    site.get("MicroScheduler").Scheduler.stop();

To configure Quartz, please visit their site for learning the ins and the outs of this framework, here's a quick link to get you started: [Quartz Quick Start Guide](http://quartz-scheduler.org/documentation/quartz-2.2.x/quick-start)

## Examples
You can imagine various scenarios, this README is only covering some of the most common use cases. Also, there is a very basic demo web application demonstrating some of the use cases. You can find the demo app [here](...todo...).

**Delayed controllers**

Some of you are familiar with the [delayed_job](https://rubygems.org/gems/delayed_job) gem from Ruby, implementation encapsulating the common pattern of asynchronously executing longer tasks in the background. Our scheduler is a tentative to help you achieve the same behavior. The simples and most common use case, is to run a controller asynchronously at a specific date/time or after a small period of time. Provided you have the `MicroScheduler` enabled in your Micro web application, you can simply do this (excerpt from a .BSH scripting controller):

    Scheduler = site.get("MicroScheduler").Scheduler;
    calendar = Calendar.getInstance();
    calendar = calendar.add(Calendar.SECOND, 5);

    Scheduler.enqueue("HelloWorld.bsh", calendar.getTimeInMillis());

The code above will schedule the `HelloWorld.bsh` controller to run after 5 seconds, while the main script is continuing the execution without interruption. Or with less lines of code if you use the [JodaTime](http://www.joda.org/joda-time/quickstart.html) library, my preferred method for playing with the date and time objects in Java. Excerpt:

    Scheduler.enqueue("HelloWorld.bsh", new DateTime().plusSeconds(5).Millis);

You can also find some useful date/time utility methods in [Quartz itself](http://quartz-scheduler.org/api/2.2.0/org/quartz/DateBuilder.html), but I won't cover those here.

**Run a controller at a specific time**

And this is how you can schedule a controller to be executed at a specific date and time. For this example I am using
a Beanshell controller and JodaTime.

    Scheduler = site.get("MicroScheduler").Scheduler;
    birthday  = new DateTime(2014, 5, 13, 12, 0, 0, 0)

    Scheduler.enqueue("HappyBirthDay.bsh", birthday.getTimeInMillis());

The `HappyBirthDay.bsh` controller will be executed on the 13th of May, 2014 at exactly 12:00am. Warning, you will have to use a durable Quartz job store, otherwise you'll not be able to persist your jobs and you'll lose your scheduling if the web app is restarted. See the [JDBCJobStore](http://quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-09), for more details.

**Scheduling a controller execution with a Cron-like syntax**

Most of you are familiar with [Cron](http://en.wikipedia.org/wiki/Cron); the unix utility created by [Brian Kernighan](http://en.wikipedia.org/wiki/Brian_Kernighan) very many moons ago. Our micro scheduler allows you to plan controller executions using the familiar Cron syntax, example:

    // Schedule a controller that fires at 10:30, 11:30, 12:30, and 13:30, on every Wednesday and Friday.
    Scheduler = site.get("MicroScheduler").Scheduler;
    jobData = new HashMap();
    jobData.put("foo", "bar");

    Scheduler.enqueue("TimeDoodle.bsh", jobData, "0 30 10-13 ? * WED,FRI");

The `TimeDoodle.bsh` controller may look like this:

    import org.joda.time.DateTime;
    dt = new DateTime();
    log.info("Time is: "+dt.toString("yyyy-MM-dd HH:mm:ss")+", and I know who 'foo' is! Foo is: "+context.get("foo"));

When executed, the: `TimeDoodle.bsh` controller, will display:

    Time is 2013-08-30 10:30:00 and I know who 'foo' is! Foo is: bar

## Running the unit tests
This extension is using [JUnit 4](http://junit.org/) for testing. To run the tests provided, follow these simple steps:

    $ cd quartz_scheduler
    $ ant test

### Links
 - [Micro](http://micro-docs.simplegames.ca/), the framework; [source](https://github.com/florinpatrascu/micro) on Github.
 - [Quartz 2 API docs](http://quartz-scheduler.org/api/2.2.0/)
 - [JodaTime](http://www.joda.org/joda-time/)
 - [JUnit 4 - Getting started](https://github.com/junit-team/junit/wiki/Getting-started)

