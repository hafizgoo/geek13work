package io.kimmking.kmq.core;

/**
 * @Auther: hafizgoo
 * @Date: DATE−2022/7/26 - MONTH−07 - DAY−26 - TIME−12:07
 * @Description: io.kimmking.kmq.core
 * @version: 1.0
 */


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义的 阻塞队列
 *
 * @param <T> 需要的对象类型
 */
public class MyArrayBlockingQueue<T> {

    /**
     * 对象数组，存对象
     */
    private final T[] items;

    /**
     * 锁
     */
    private Lock lock = new ReentrantLock();

    /**
     * 基于lock的 队列没有满 的条件。
     * 用于：队列满时 阻塞（await()）,不满时唤醒（signal()）
     */
    private Condition notFull = lock.newCondition();

    /**
     * 基于lock的 队列没有空 的条件。
     * 用于：队列空时 阻塞（await()）,不空时唤醒（signal()）
     */
    private Condition notEmpty = lock.newCondition();

    /**
     * 当前信息写入位置
     */
    private AtomicInteger DATA_OFFSET = new AtomicInteger(0);

    /**
     * 记录消费者消费位置
     */
    private Map<String, Integer> CONSUMER_OFFSET_MAP = new ConcurrentHashMap<String, Integer>();

    /**
     * 队列中数据的数量
     */
    private int count;

    /**
     * 待存的数据的位置
     */
    private int putIndex;

    /**
     * 待取的数据的位置
     */
    private int taskIndex;


    public MyArrayBlockingQueue(int size) {
        items = (T[]) new Object[size];
    }

    public boolean put(T t) {
        lock.lock();

        try {
            while (count == items.length) {
                //阻塞 等待“不满”的线程
                notFull.await();
            }

            items[putIndex] = t;
            //循环使用
            if (++putIndex == items.length) {
                putIndex = 0;
            }
            count++;
            //唤醒 等待“不空”的线程
            notEmpty.signal();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return false;
    }

    public T take() {
        lock.lock();

        T t = null;
        try {
            while (count == 0) {
                //阻塞 等待“不空”的线程
                notEmpty.await();
            }
            t = items[taskIndex];
            //循环使用
            if (++taskIndex == items.length) {
                taskIndex = 0;
            }
            count--;

            //唤醒 等待“不满”的线程
            notFull.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return t;
    }

}