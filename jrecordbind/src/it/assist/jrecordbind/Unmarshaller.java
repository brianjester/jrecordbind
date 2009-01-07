package it.assist.jrecordbind;

import it.assist.jrecordbind.RecordDefinition.Property;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Unmarshalls a source reader into beans. The constructor takes the record definition file, while the {@link #unmarshall(Reader)} takes the fixed length file. You'll get back an Iterator instance: each next() call will give back the next bean

 * @author Federico Fissore
 */
public class Unmarshaller<E> extends AbstractUnMarshaller {

  private final class UnmarshallerIterator<T> implements Iterator<T> {

    private final StringBuilder buffer;
    private final ConvertersCache converters;
    private final Pattern globalPattern;
    private final BufferedReader reader;
    private final RegexGenerator regexGenerator;

    public UnmarshallerIterator(ConvertersCache converters, StringBuilder buffer, BufferedReader reader) {
      this.reader = reader;
      this.buffer = buffer;
      this.converters = converters;
      this.regexGenerator = new RegexGenerator();
      this.globalPattern = regexGenerator.deepPattern(definition);
    }

    public boolean hasNext() {
      String current = null;
      Matcher matcher = null;
      while ((!(matcher = globalPattern.matcher(buffer)).find() || matcher.end() == (buffer.length() - 1))
          && (current = readLine()) != null) {
        buffer.append(current).append("\n");
      }

      return globalPattern.matcher(buffer).find();
    }

    public T next() {
      try {
        Object record = Class.forName(definition.getClassName()).newInstance();
        Matcher globalMatcher = globalPattern.matcher(buffer);
        if (globalMatcher.find()) {
          StringBuilder currentBuffer = new StringBuilder(buffer.substring(globalMatcher.start(), globalMatcher.end()));

          recursive(record, definition, currentBuffer);

          buffer.delete(globalMatcher.start(), globalMatcher.end() + 1);
        }
        return (T) record;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private void recursive(Object record, RecordDefinition currentDefinition, StringBuilder currentBuffer)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException,
        ClassNotFoundException {
      Matcher matcher = regexGenerator.localPattern(currentDefinition).matcher(currentBuffer);
      matcher.find();
      int groupCount = 1;
      for (Iterator<Property> iter = currentDefinition.getProperties().iterator(); iter.hasNext();) {
        Property property = iter.next();
        Object convert = converters.get(property.getConverter()).convert(matcher.group(groupCount++));
        PropertyUtils.setProperty(record, property.getName(), convert);
      }
      currentBuffer.delete(matcher.start(), matcher.end());
      for (RecordDefinition subDefinition : currentDefinition.getSubRecords()) {
        while (regexGenerator.deepPattern(subDefinition).matcher(currentBuffer).find()) {
          Matcher subMatcher = regexGenerator.deepPattern(subDefinition).matcher(currentBuffer);
          subMatcher.find();
          StringBuilder subBuffer = new StringBuilder(currentBuffer.substring(subMatcher.start(), subMatcher.end()));
          Object subRecord = Class.forName(subDefinition.getClassName()).newInstance();
          recursive(subRecord, subDefinition, subBuffer);
          currentBuffer.delete(subMatcher.start(), subMatcher.end());
          Object property = PropertyUtils.getProperty(record, subDefinition.getName());
          if (property instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) property;
            collection.add(subRecord);
          } else {
            PropertyUtils.setProperty(record, subDefinition.getName(), subRecord);
          }
        }
      }
    }

    private String readLine() {
      try {
        return reader.readLine();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    public void remove() {
    }

  }

  private StringBuilder buffer;

  /**
   * Creates a new unmarshaller, reading the configuration specified in the definition properties file given as input
   * @param input the definition properties file
   * @param junk 
   * @param string 
   */
  public Unmarshaller(Reader input) {
    super(input);
    this.buffer = new StringBuilder();
  }

  public String getCurrentJunk() {
    return buffer.toString();
  }

  /**
   * Unmarshalls the input fixed length file, a bean at a time
   * @param input the input fixed length file
   * @return an Iterator: each next() call will give back the next bean
   */
  public Iterator<E> unmarshall(Reader input) {
    return new UnmarshallerIterator<E>(converters, buffer, new BufferedReader(input));
  }

  /**
   * Unmarshalls the whole file and give back a list of bean. USE WITH CAUTION: for big files, this will lead to out of memory errors
   * @param input the input fixed length file
   * @return a list of beans
   */
  public List<E> unmarshallAll(Reader input) {
    List<E> result = new LinkedList<E>();
    for (Iterator<E> iter = unmarshall(input); iter.hasNext();) {
      result.add(iter.next());
    }
    return result;
  }

}
