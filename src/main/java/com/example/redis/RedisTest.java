package com.example.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Component
public class RedisTest implements ApplicationRunner {

    private final RedissonClient allocationRedissonClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ElapsedTimeUtils.start();

        // 락을 획득하고 약 1분간 유지한다
        // 다른 서버에서 그 사이에 조회를 시도한다.
        try {
            tryLock();
        } finally {
            ElapsedTimeUtils.finish();
        }
    }

    private void tryLock() throws InterruptedException {
        //RAtomicLong beforeValue = allocationRedissonClient.getAtomicLong("test:key");
        //beforeValue.set(0L);

        RLock lock = allocationRedissonClient.getLock("test:lock:key");
        try {
            for (int i=0; i<100000; i++) {
                // waitTime: Lock 을 차지하기 위해 기다리는 시간
                // leaseTime: Lock 차지 후 점유하고 있을 최대 시간(만약 내부 로직이 leaseTime 보다 빨리 끝나면 끝나자마자 Lock 을 반납한다
                if (lock.tryLock(1, 2, TimeUnit.SECONDS)) {
                    //System.out.println("Start");
                    //Thread.sleep(1000 * 25);
                    //System.out.println("Success");
                    RAtomicLong value = allocationRedissonClient.getAtomicLong("test:key");
                    //System.out.println(value.incrementAndGet());
                    value.getAndIncrement();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * leaseTime 이 초과된 후 unlock() 실행 시 -> IllegalStateException 예외발생한다.
     * 해결방법: isHeldByCurrentThread() 체크 후 unlock() 을 실행한다.
     */
    private void overTimeLogicAndThrowIllegalStateException() {
        RLock lock = allocationRedissonClient.getLock("test:lock:key01");
        try {
            if (lock.tryLock(1, 35, TimeUnit.SECONDS)) {
                Thread.sleep(1000 * 30);
                RAtomicLong value = allocationRedissonClient.getAtomicLong("test:key01");
                value.getAndIncrement();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // ✅ isHeldByCurrentThread() 체크 후 unlock() 을 실행한다.
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }




    private void getAndSetRedis() {

        RBucket<Object> bucket = allocationRedissonClient.getBucket("test:key", new StringCodec());
        System.out.println("Before:" + bucket.get());
        bucket.set("test2");
        System.out.println("After:" + bucket.get());
    }
}
