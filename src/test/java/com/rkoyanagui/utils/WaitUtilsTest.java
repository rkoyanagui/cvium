package com.rkoyanagui.utils;

import static com.rkoyanagui.utils.WaitUtils.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class WaitUtilsTest
{

  @Test
  void succeed()
  {
    assertThat(await().maxNumOfAttempts(1).until(() -> true).perform(), is(true));
  }

  @Test
  void fail()
  {
    assertThat(await().maxNumOfAttempts(1).until(() -> false).perform(), is(false));
  }

  @Test
  void maxAttempts()
  {
    final AtomicInteger i = new AtomicInteger();

    final WaitUtils.ConditionalWaitBuilder await = await()
        .unlimitedTimeout()
        .maxNumOfAttempts(3).orElseDo(() -> i.incrementAndGet());

    assertThat(await.until(() -> false).perform(), is(false));

    assertThat(i.get(), is(equalTo(2)));
  }

  @Test
  void ignoreExceptionIn_Until()
  {
    assertThat(await()
        .timeout(1)
        .clearIgnoredExceptions()
        .ignoredException(ArithmeticException.class)
        .until(() -> 1 / 0 == 0)
        .perform(), is(false));
  }

  @Test
  void doNotIgnoreExceptionIn_Until()
  {
    final WaitUtils.ConditionalWaitBuilder await = await()
        .timeout(1)
        .clearIgnoredExceptions()
        .until(() -> 1 / 0 == 0);
    assertThrows(ArithmeticException.class, () -> await
        .perform());
  }

  @Test
  void ignoreExceptionIn_OrElseDo()
  {
    assertThat(await()
        .timeout(1)
        .clearIgnoredExceptions()
        .ignoredException(ArithmeticException.class)
        .until(() -> false)
        .orElseDo(() -> {
          int i = 1 / 0;
        })
        .perform(), is(false));
  }

  @Test
  void doNotIgnoreExceptionIn_OrElseDo()
  {
    final WaitUtils.ConditionalWaitBuilder await = await()
        .timeout(1)
        .clearIgnoredExceptions()
        .until(() -> false)
        .orElseDo(() -> {
          int i = 1 / 0;
        });
    assertThrows(ArithmeticException.class, () -> await
        .perform());
  }

}
