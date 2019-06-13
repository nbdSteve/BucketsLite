package dev.nuer.bl.nbtapi;

import java.lang.reflect.Constructor;

/**
 * NBTDataAPI
 *
 * Created by tr7zw
 */
public enum ObjectCreator {
    NMS_NBTTAGCOMPOUND(ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz()),
    NMS_BLOCKPOSITION(ClassWrapper.NMS_BLOCKPOSITION.getClazz(), int.class, int.class, int.class);

    private Constructor<?> construct;

    ObjectCreator(Class<?> clazz, Class<?>... args){
        try{
            construct = clazz.getConstructor(args);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public Object getInstance(Object... args){
        try{
            return construct.newInstance(args);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}