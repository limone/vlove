package vlove.virt.logging;

import java.io.IOException;
import java.io.OutputStream;

import jline.console.ConsoleReader;
import ch.qos.logback.core.OutputStreamAppender;

public class JlineAppender<E> extends OutputStreamAppender<E> {
  private final ConsoleReader reader;

  public JlineAppender(ConsoleReader reader) {
    this.reader = reader;
  }

  @Override
  public void start() {
    setOutputStream(new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        reader.putString(String.valueOf(b));
      }
    });
  }
}