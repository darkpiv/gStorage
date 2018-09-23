package com.orhanobut.hawk;

import android.content.Context;

import com.google.gson.Gson;

public class HawkBuilder {

    /**
     * NEVER ever change STORAGE_TAG_DO_NOT_CHANGE and TAG_INFO.
     * It will break backward compatibility in terms of keeping previous data
     */
    private static final String STORAGE_TAG_DO_NOT_CHANGE = "Hawk2";

    private Context context;
    private Storage cryptoStorage;
    private Converter converter;
    private Parser parser;
    private Encryption encryption;
    private Serializer serializer;
    private LogInterceptor logInterceptor;

    public HawkBuilder(Context context) {
        HawkUtils.INSTANCE.checkNull("Context", context);

        this.context = context.getApplicationContext();
    }

    LogInterceptor getLogInterceptor() {
        if (logInterceptor == null) {
            logInterceptor = new LogInterceptor() {
                @Override
                public void onLog(String message) {
                    //empty implementation
                }
            };
        }
        return logInterceptor;
    }

    public HawkBuilder setLogInterceptor(LogInterceptor logInterceptor) {
        this.logInterceptor = logInterceptor;
        return this;
    }

    Storage getStorage() {
        if (cryptoStorage == null) {
            cryptoStorage = new SharedPreferencesStorage(context, STORAGE_TAG_DO_NOT_CHANGE);
        }
        return cryptoStorage;
    }

    public HawkBuilder setStorage(Storage storage) {
        this.cryptoStorage = storage;
        return this;
    }

    Converter getConverter() {
        if (converter == null) {
            converter = new HawkConverter(getParser());
        }
        return converter;
    }

    public HawkBuilder setConverter(Converter converter) {
        this.converter = converter;
        return this;
    }

    Parser getParser() {
        if (parser == null) {
            parser = new GsonParser(new Gson());
        }
        return parser;
    }

    public HawkBuilder setParser(Parser parser) {
        this.parser = parser;
        return this;
    }

    Encryption getEncryption() {
        if (encryption == null) {
            encryption = new ConcealEncryption(context);
            if (!encryption.init()) {
                encryption = new NoEncryption();
            }
        }
        return encryption;
    }

    public HawkBuilder setEncryption(Encryption encryption) {
        this.encryption = encryption;
        return this;
    }

    Serializer getSerializer() {
        if (serializer == null) {
            serializer = new HawkSerializer(getLogInterceptor());
        }
        return serializer;
    }

    public HawkBuilder setSerializer(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }

    public void build() {
        Hawk.INSTANCE.build(this);
    }
}
