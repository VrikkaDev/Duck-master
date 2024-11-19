package net.VrikkaDuck.duck.handler.common;

import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class TickHandler {

    private static TickHandler instance;
    public static TickHandler INSTANCE(){
        if(instance == null){
            instance = new TickHandler();
        }
        return instance;
    }

    private static class ScheduledTask {
        Runnable function; // The function to execute
        int ticksRemaining; // Ticks left until execution

        public ScheduledTask(Runnable function, int ticksRemaining) {
            this.function = function;
            this.ticksRemaining = ticksRemaining;
        }
    }

    private final List<ScheduledTask> tasks = new ArrayList<>();

    public TickHandler(){
    }

    public void Tick(){
        for (int i = 0; i < tasks.size(); i++) {
            ScheduledTask task = tasks.get(i);
            task.ticksRemaining--;

            if (task.ticksRemaining <= 0) {
                task.function.run();
                tasks.remove(i);
                i--;
            }
        }
    }

    public void AddTickDelay(Runnable func, Integer delay){
        if (delay <= 0) {
            new ScheduledTask(func, delay).function.run();
            return;
        }
        tasks.add(new ScheduledTask(func, delay));
    }
}

