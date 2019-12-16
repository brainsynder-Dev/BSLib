package lib.brainsynder.particle;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ParticleReflection {
        private static HashMap<Class<? extends Entity>, Method> handles = new HashMap<>();

        public static Object getHandle(Entity entity) {
            try {
                if (handles.get(entity.getClass()) != null) {
                    return handles.get(entity.getClass()).invoke(entity);
                }
                Method entity_getHandle = entity.getClass().getMethod("getHandle");
                handles.put(entity.getClass(), entity_getHandle);
                return entity_getHandle.invoke(entity);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        public static void sendPacket(Player p, Object packet)
                throws IllegalArgumentException {
            Field player_connection;
            Method player_sendPacket = null;
            try {
                player_connection = ParticleReflection.getHandle(p).getClass().getField("playerConnection");
                Method[] arrayOfMethod;
                int j = (arrayOfMethod = player_connection.get(ParticleReflection.getHandle(p)).getClass().getMethods()).length;
                for (int i = 0; i < j; i++) {
                    Method m = arrayOfMethod[i];
                    if (m.getName().equalsIgnoreCase("sendPacket")) {
                        player_sendPacket = m;
                    }
                }
                if (player_sendPacket != null) {
                    player_sendPacket.invoke(player_connection.get(ParticleReflection.getHandle(p)), packet);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException ex) {
                ex.printStackTrace();
            }
        }
    }