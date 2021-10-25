package net.ccbluex.liquidbounce.patcher.tweaker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PatcherTweaker {
    static void invokeExit() {
        try {
            final Class<?> aClass = Class.forName("java.lang.Shutdown");
            final Method exit = aClass.getDeclaredMethod("exit", int.class);
            exit.setAccessible(true);
            exit.invoke(null, 0);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
