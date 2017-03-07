package com.baeldung.um.web;

import com.baeldung.um.persistence.model.Principal;
import com.baeldung.um.persistence.model.Privilege;
import com.baeldung.um.persistence.model.Role;
import com.baeldung.um.web.dto.UserDto;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

public class KryoHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final MediaType KRYO = new MediaType("application", "x-kryo");

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(KryoHttpMessageConverter::createKryo);

    public KryoHttpMessageConverter() {
        super(KRYO);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Object.class.isAssignableFrom(clazz);
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        final Input input = new Input(inputMessage.getBody());
        return kryoThreadLocal.get().readClassAndObject(input);
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        final Output output = new Output(outputMessage.getBody());
        kryoThreadLocal.get().writeClassAndObject(output, o);
        output.flush();
    }

    @Override
    protected MediaType getDefaultContentType(final Object o) throws IOException {
        return KRYO;
    }

    private static final Kryo createKryo() {
        final Kryo kryo = new Kryo();
        kryo.register(UserDto.class, 1);
        kryo.register(Role.class, 2);
        kryo.register(Privilege.class, 3);
        kryo.register(Principal.class, 4);
        return kryo;
    }
}
