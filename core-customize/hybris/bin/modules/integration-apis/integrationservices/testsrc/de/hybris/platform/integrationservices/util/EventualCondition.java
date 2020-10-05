/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util;

import java.time.Duration;

/**
 * A test condition that is evaluated repeatedly during a reasonable period of time. It's intended to assert asynchronous changes.
 * Default evaluation period is 10 seconds and the condition is evaluated approximately every 500 milliseconds.
 */
public class EventualCondition
{
	private final Duration expectationPeriod;
	private final long pollingIntervalMillis;


	private EventualCondition(final Duration period)
	{
		expectationPeriod = period;
		pollingIntervalMillis = 500;
	}

	/**
	 * Creates evaluation condition with default evaluation period and interval.
	 *
	 * @return a condition that is evaluated about every 500 milliseconds within 10 seconds period.
	 */
	public static EventualCondition eventualCondition()
	{
		return new EventualCondition(Duration.ofSeconds(10));
	}

	/**
	 * Evaluates the conditions represented by the code body.  For example, let's say we have
	 * a class Job that changes its state asynchronously. In this case this method can be used like this:
	 * <pre>
	 *       Job job = new Job
	 *
	 *       // start the job
	 *       job.start();
	 *
	 *       // assert the asynchronous condition that the job eventually finishes.
	 *       eventualCondition().expect(() -> assertEquals("finished", job.getState());
	 * </pre>
	 *
	 * @param body a code to evaluate. It should throw an exception when the condition is not met.
	 */
	public void expect(final Runnable body)
	{
		long deadLine = System.currentTimeMillis() + expectationPeriod.toMillis();
		while (System.currentTimeMillis() < deadLine)
		{
			try
			{
				body.run();
				return;
			}
			catch (final Throwable ex)
			{
				final long now = System.currentTimeMillis();
				if (now >= deadLine)
				{
					throw ex;
				}
				final long timeout = Math.min(pollingIntervalMillis, deadLine - now);
				try
				{
					Thread.sleep(timeout);
				}
				catch (final InterruptedException e)
				{
					// go back to the loop condition check
				}
			}
		}

	}
}
