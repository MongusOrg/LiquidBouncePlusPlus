/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.util.enhancement;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public interface Enhancement {

    AtomicInteger counter = new AtomicInteger(0);
    ThreadPoolExecutor POOL = new ThreadPoolExecutor(50, 50,
        0L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        r -> new Thread(r, String.format("Patcher Concurrency Thread %s", counter.incrementAndGet())));

    String getName();

    default void tick() {
    }
}