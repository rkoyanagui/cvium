package com.rkoyanagui.utils;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.hamcrest.Matchers.greaterThan;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Predicate;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;

/**
 * This class's intent is the same as {@link org.awaitility.Awaitility}'s, that is, to wait for some
 * asynchronous operation to conclude and/or to fulfil some condition, with some extra utilities.
 */
public class WaitUtils
{

  protected WaitUtils()
  {
  }

  protected static final String BLEW_MAX_NUM_ATTEMPTS_MSG =
      "Exceeded the maximum allowed number of attempts (%d).";
  protected static final int UNLIMITED = Integer.MAX_VALUE;
  public static final int DEFAULT_MAX_ATTEMPTS = 2;
  public static final int DEFAULT_INITIAL_DELAY = 0;
  public static final int DEFAULT_POLL_INTERVAL = 2_000;
  public static final int DEFAULT_TIMEOUT = 60;
  public static final Collection<Class<? extends Throwable>> DEFAULT_IGNORED_EXCEPTIONS =
      ImmutableList.of(NotFoundException.class, StaleElementReferenceException.class);
  public static final Runnable DO_NOTHING = () -> {};

  /**
   * Waits until a {@code condition} is fulfilled. Can be limited to a maximum number of attempts
   * {@code maxAttempts}. After an {@code initialDelay}, in milliseconds, it starts testing the
   * condition, and after each {@code pollInterval}, in milliseconds, it retests it. Can be limited
   * to a maximum waiting time {@code timeout}, in seconds. In every iteration, if the condition is
   * false, then an optional alternative action {@code orElseDo} is taken; but, if the condition is
   * true, then the loop is exited and {@link Boolean#TRUE} is returned. If the condition remains
   * false until the maximum number of attempts or the maximum waiting time are reached, then {@link
   * Boolean#FALSE} is returned. <p/><b>Attention:</b> the {@code initialDelay} counts as waiting
   * time, as concerns the {@code timeout}.
   *
   * @param params maximum number of attempts, number of milliseconds of delay before the first
   *               attempt, number of milliseconds of interval between each new attempt, maximum
   *               number of seconds to wait until the condition is fulfilled, a boolean-producing
   *               function, an action to be taken every time the condition fails.
   * @return the final result of the condition under evaluation
   */
  protected static boolean await(final ConditionalWaitBuilder params)
  {
    final Integer maxAttempts = params.maxAttempts;
    final Integer initialDelay = params.initialDelay;
    final Integer pollInterval = params.pollInterval;
    final Integer timeout = params.timeout;
    final Collection<Class<? extends Throwable>> ignoredExceptions = params.ignoredExceptions;
    // Awaitility's regular 'ignoreException' methods aren't working for 'element not found' type
    // exceptions for some unknown reason. So, they have to be ignored 'manually'. Hence, the
    // method 'ignoreExceptions' below.
    final Callable<Boolean> condition = ignoreExceptions(params.condition, ignoredExceptions);
    final Runnable orElseDo = ignoreExceptions(params.orElseDo, ignoredExceptions);
    final LongAdder counter = new LongAdder();
    final Predicate<Boolean> processTheAttempt =
        conditionResult -> processTheAttempt(conditionResult, orElseDo, counter, maxAttempts);
    try
    {
      return Awaitility.await()
          .pollDelay(ofMillis(initialDelay))
          .pollInterval(ofMillis(pollInterval))
          .atMost(ofSeconds(timeout))
          .until(condition, processTheAttempt);
    }
    catch (ConditionTimeoutException ignored)
    {
      /* Not necessarily a showstopper. It is better to leave it up to the user to decide what to do
      when the condition returns a false result in the end, rather than throw an exception from
      inside this method. */
      return false;
    }
  }

  protected static Callable<Boolean> ignoreExceptions(final Callable<Boolean> condition,
                                                      final Collection<Class<? extends Throwable>> xs)
  {
    return () -> {
      try
      {
        return condition.call();
      }
      catch (Exception x)
      {
        if (Objects.nonNull(xs) && !xs.isEmpty())
        {
          for (Class<? extends Throwable> x1 : xs)
          {
            if (x1.isAssignableFrom(x.getClass()))
            {
              return false;
            }
          }
        }
        throw x;
      }
    };
  }

  protected static Runnable ignoreExceptions(final Runnable runnable,
                                             final Collection<Class<? extends Throwable>> xs)
  {
    return () -> {
      try
      {
        runnable.run();
      }
      catch (Exception x)
      {
        if (Objects.nonNull(xs) && !xs.isEmpty())
        {
          for (Class<? extends Throwable> x1 : xs)
          {
            if (x1.isAssignableFrom(x.getClass()))
            {
              return;
            }
          }
        }
        throw x;
      }
    };
  }

  protected static Boolean processTheAttempt(final Boolean conditionResult,
                                             final Runnable orElseDo,
                                             final LongAdder counter,
                                             final Integer maxAttempts)
  {
    counter.increment();
    if (Boolean.FALSE.equals(conditionResult))
    {
      if (counter.intValue() >= maxAttempts)
      {
        final String msg = String.format(BLEW_MAX_NUM_ATTEMPTS_MSG, maxAttempts);
        throw new ConditionTimeoutException(msg);
      }
      orElseDo.run();
    }
    return conditionResult;
  }

  /**
   * Prevents the continuation of a program's execution for a fixed number of seconds.
   *
   * @param seconds number of seconds to wait
   */
  public static void await(final long seconds)
  {
    final long pollInterval = 1_000L;
    final LongAdder counter = new LongAdder();
    final ScheduledFuture<?> scheduledFuture = Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(
            counter::increment,
            0L,
            pollInterval,
            TimeUnit.MILLISECONDS);
    Awaitility.await()
        .pollDelay(ofMillis(0L))
        .pollInterval(ofMillis(pollInterval))
        .timeout(ofSeconds(UNLIMITED))
        .untilAdder(counter, greaterThan(seconds));
    scheduledFuture.cancel(true);
  }

  /**
   * Starts building a conditional 'wait' statement as per {@link WaitUtils#await(ConditionalWaitBuilder)}.
   * This is a 'fluent API', that is, to add new parameters, you can keep chaining the builder
   * {@link ConditionalWaitBuilder}'s methods, and in the end call {@link
   * ConditionalWaitBuilder#perform()}.
   *
   * @return a conditional 'wait' statement builder
   */
  public static ConditionalWaitBuilder await()
  {
    return new ConditionalWaitBuilder();
  }

  /**
   * A builder for the parameters of {@link WaitUtils#await(ConditionalWaitBuilder)}, for
   * convenience, and better readability.
   */
  public static class ConditionalWaitBuilder
  {

    protected Integer maxAttempts = DEFAULT_MAX_ATTEMPTS;
    protected Integer initialDelay = DEFAULT_INITIAL_DELAY;
    protected Integer pollInterval = DEFAULT_POLL_INTERVAL;
    protected Integer timeout = DEFAULT_TIMEOUT;
    protected Collection<Class<? extends Throwable>> ignoredExceptions = DEFAULT_IGNORED_EXCEPTIONS;
    protected Callable<Boolean> condition;
    protected Runnable orElseDo = DO_NOTHING;

    protected ConditionalWaitBuilder()
    {
    }

    /**
     * Sets a maximum number of attempts to evaluate the condition, after which the loop is exited
     * and the result is returned as {@link Boolean#FALSE}. The default value is {@link
     * #DEFAULT_MAX_ATTEMPTS}. <p/> If both {@link #maxNumOfAttempts(Integer)} and {@link
     * #timeout(Integer)} have been set, then the loop ends whenever any of the two limits is
     * reached.
     *
     * @param maxAttempts maximum number of attempts
     * @return the builder
     */
    public ConditionalWaitBuilder maxNumOfAttempts(Integer maxAttempts)
    {
      this.maxAttempts = maxAttempts;
      return this;
    }

    /**
     * Sets the maximum number of attempts as unlimited. So the only limiting factor is defined by
     * {@link #timeout(Integer)}. <p/> If both {@link #unlimitedNumOfAttempts()} and {@link
     * #unlimitedTimeout()} have been set, then the loop will only ever end when the condition is
     * evaluated as {@link Boolean#TRUE}.
     *
     * @return the builder
     */
    public ConditionalWaitBuilder unlimitedNumOfAttempts()
    {
      this.maxAttempts = UNLIMITED;
      return this;
    }

    /**
     * Sets a delay before the start of the condition evaluation loop. The default value is {@link
     * #DEFAULT_INITIAL_DELAY}.
     *
     * @param initialDelay number of milliseconds of delay before the first attempt
     * @return the builder
     */
    public ConditionalWaitBuilder initialDelay(Integer initialDelay)
    {
      this.initialDelay = initialDelay;
      return this;
    }

    /**
     * Sets a time interval between each iteration of the condition evaluation loop. The default
     * value is {@link #DEFAULT_POLL_INTERVAL}.
     *
     * @param pollInterval number of milliseconds of interval between each new attempt
     * @return the builder
     */
    public ConditionalWaitBuilder pollInterval(Integer pollInterval)
    {
      this.pollInterval = pollInterval;
      return this;
    }

    /**
     * Sets an overall time limit for the condition evaluation loop, after which the loop is exited
     * and the result is returned as {@link Boolean#FALSE}. The default value is {@link
     * #DEFAULT_TIMEOUT}. <p/> If both {@link #maxNumOfAttempts(Integer)} and {@link
     * #timeout(Integer)} have been set, then the loop ends whenever any of the two limits is
     * reached.
     *
     * @param timeout maximum number of seconds to wait until the condition is fulfilled
     * @return the builder
     */
    public ConditionalWaitBuilder timeout(Integer timeout)
    {
      this.timeout = timeout;
      return this;
    }

    /**
     * Adds a single exception to the list of ignored exceptions. When the condition is evaluated,
     * if an exception is thrown, and it is assignable (an instance of) one of the exceptions to be
     * ignored, then the condition simply returns {@link Boolean#FALSE} and the loop proceeds to the
     * next attempt. The default value is {@link #DEFAULT_IGNORED_EXCEPTIONS}.
     *
     * @param ignoredException exception to be added to the list of ignored exceptions
     * @return the builder
     */
    public ConditionalWaitBuilder ignoredException(Class<? extends Throwable> ignoredException)
    {
      this.ignoredExceptions = ImmutableList.<Class<? extends Throwable>>builder()
          .addAll(this.ignoredExceptions)
          .add(ignoredException)
          .build();
      return this;
    }

    /**
     * Adds a collection of exceptions to the list of ignored exceptions. When the condition is
     * evaluated, if an exception is thrown, and it is assignable (an instance of) one of the
     * exceptions to be ignored, then the condition simply returns {@link Boolean#FALSE} and the
     * loop proceeds to the next attempt. The default value is {@link #DEFAULT_IGNORED_EXCEPTIONS}.
     *
     * @param ignoredExceptions exceptions to be added to the list of ignored exceptions
     * @return the builder
     */
    public ConditionalWaitBuilder ignoredExceptions(Collection<Class<? extends Throwable>> ignoredExceptions)
    {
      this.ignoredExceptions = ImmutableList.<Class<? extends Throwable>>builder()
          .addAll(this.ignoredExceptions)
          .addAll(ignoredExceptions)
          .build();
      return this;
    }

    /**
     * Clears the list of ignored exceptions, so it is empty, so none will be ignored. When the
     * condition is evaluated, if an exception is thrown, and it is assignable (an instance of) one
     * of the exceptions to be ignored, then the condition simply returns {@link Boolean#FALSE} and
     * the loop proceeds to the next attempt. The default value is {@link
     * #DEFAULT_IGNORED_EXCEPTIONS}.
     *
     * @return the builder
     */
    public ConditionalWaitBuilder clearIgnoredExceptions()
    {
      this.ignoredExceptions = ImmutableList.of();
      return this;
    }

    /**
     * Sets the timeout as unlimited. So the only limiting factor is defined by {@link
     * #maxNumOfAttempts(Integer)}. <p/> If both {@link #unlimitedNumOfAttempts()} and {@link
     * #unlimitedTimeout()} have been set, then the loop will only ever end when the condition is
     * evaluated as {@link Boolean#TRUE}.
     *
     * @return the builder
     */
    public ConditionalWaitBuilder unlimitedTimeout()
    {
      this.timeout = UNLIMITED;
      return this;
    }

    /**
     * Sets a boolean-producing function as the condition under evaluation. The loop ends when the
     * condition returns {@link Boolean#TRUE}. Example using Java 8 lambda expressions:
     * <pre>
     * {@code
     * await()
     *   .until(() -> doSomething())
     *   .perform();
     * }
     * </pre>
     *
     * @param condition a boolean-producing function
     * @return the builder
     */
    public ConditionalWaitBuilder until(Callable<Boolean> condition)
    {
      this.condition = condition;
      return this;
    }

    /**
     * Sets what alternative action should be taken every time the condition fails. The default
     * value is {@link #DO_NOTHING}. <b>Attention:</b> if the condition fails and it happens to be
     * the last iteration (meaning {@link #maxNumOfAttempts(Integer)} or {@link #timeout(Integer)}
     * have been reached), then, after this last attempt, the alternative action is not taken.
     * Example using Java 8 lambda expressions:
     * <pre>
     * {@code
     * await()
     *   .until(() -> doSomething())
     *   .orElseDo(() -> somethingElse())
     *   .perform();
     * }
     * </pre>
     *
     * @param orElseDo an action to be taken every time the condition fails
     * @return the builder
     */
    public ConditionalWaitBuilder orElseDo(Runnable orElseDo)
    {
      this.orElseDo = orElseDo;
      return this;
    }

    /**
     * Feeds the builder's parameters into {@link WaitUtils#await(ConditionalWaitBuilder)}.
     *
     * @return the final result of the condition under evaluation
     */
    public boolean perform()
    {
      return WaitUtils.await(this);
    }

  }

}
