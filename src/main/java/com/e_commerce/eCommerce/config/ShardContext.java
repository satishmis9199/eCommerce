
package com.e_commerce.eCommerce.config;

import java.util.HashSet;
import java.util.Set;

public final class ShardContext {
    private static final ThreadLocal<Integer> CURRENT=new ThreadLocal<>();
    private static final Set<Integer> availbleShard = new HashSet<>(Set.of(1, 2));

    private ShardContext() {
    }
    public static void setShardId(int shardId) {
        if (!availbleShard.contains(shardId)) {
            throw new IllegalArgumentException(
                    "Invalid shard ID: " + shardId
            );
        }
        CURRENT.set(shardId);
    }

    public static Integer getShardId() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
