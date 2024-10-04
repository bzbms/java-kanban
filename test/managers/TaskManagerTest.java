package managers;

import org.junit.jupiter.api.Test;

abstract class TaskManagerTest<T extends TaskManager> {

    @Test
    public abstract void epicStatusCalculating();

    @Test
    public abstract void timeMustIntersect();

    @Test
    public abstract void timeMustNotIntersect();

}