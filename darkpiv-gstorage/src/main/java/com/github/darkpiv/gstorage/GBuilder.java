package com.github.darkpiv.gstorage;

import android.content.Context;
import com.google.gson.Gson;

public class GBuilder {

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

  public GBuilder(Context context) {
    GUtils.INSTANCE.checkNull("Context", context);

    this.context = context.getApplicationContext();
  }

  public LogInterceptor getLogInterceptor() {
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

  public GBuilder setLogInterceptor(LogInterceptor logInterceptor) {
    this.logInterceptor = logInterceptor;
    return this;
  }

  public Storage getStorage() {
    if (cryptoStorage == null) {
      cryptoStorage = new SharedPreferencesStorage(context, STORAGE_TAG_DO_NOT_CHANGE);
    }
    return cryptoStorage;
  }

  public GBuilder setStorage(Storage storage) {
    this.cryptoStorage = storage;
    return this;
  }

  public Converter getConverter() {
    if (converter == null) {
      converter = new GConverter(getParser());
    }
    return converter;
  }

  public GBuilder setConverter(Converter converter) {
    this.converter = converter;
    return this;
  }

  public Parser getParser() {
    if (parser == null) {
      parser = new GsonParser(new Gson());
    }
    return parser;
  }

  public GBuilder setParser(Parser parser) {
    this.parser = parser;
    return this;
  }

  public Encryption getEncryption() {
    if (encryption == null) {
      encryption = new ConcealEncryption(context);
      if (!encryption.init()) {
        encryption = new NoEncryption();
      }
    }
    return encryption;
  }

  public GBuilder setEncryption(Encryption encryption) {
    this.encryption = encryption;
    return this;
  }

  public Serializer getSerializer() {
    if (serializer == null) {
      serializer = new GSerializer(getLogInterceptor());
    }
    return serializer;
  }

  public GBuilder setSerializer(Serializer serializer) {
    this.serializer = serializer;
    return this;
  }

  public void build() {
    GStorage.build(this);
  }
}
