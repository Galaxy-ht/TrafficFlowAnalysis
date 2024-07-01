package com.egon.schema;



import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

/**
 *
 *   User  Student  Teacher
 *   每一个类都有类名，构造方法，属性
 *   我自己写了一个类，这个类的类名叫做Class ,这个类中保存类的共性
 *
 **/
public class JSONDeserializationSchema<T> implements DeserializationSchema<T> {

    private Class<T> clazz;
    public JSONDeserializationSchema(Class<T> clazz) {
        this.clazz = clazz;
    }

    // 上面这个  主要的
    @Override
    public T deserialize(byte[] message)  {
        String jsonStr = new String(message);
        return JSON.parseObject(jsonStr, clazz);
    }

    // 这个不用实现
    @Override
    public boolean isEndOfStream(T nextElement) {
        return false;
    }

    // 实现这个  次要
    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(clazz);
    }
}
