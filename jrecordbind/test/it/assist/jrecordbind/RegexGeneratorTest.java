package it.assist.jrecordbind;

import java.io.InputStreamReader;

import junit.framework.TestCase;

public class RegexGeneratorTest extends TestCase {

  public void testDelimiter() throws Exception {
    DefinitionLoader definitionLoader = new DefinitionLoader();
    definitionLoader.load(new InputStreamReader(RegexGeneratorTest.class.getResourceAsStream("/delimiter.def.xsd")));

    RegexGenerator regexGenerator = new RegexGenerator();

    assertEquals("([\\w\\W]{10})\\|([\\w\\W]{10})\\|([\\w\\W]{10})", regexGenerator.deepPattern(
        definitionLoader.getDefinition()).pattern());

    assertEquals("([\\w\\W]{10})\\|([\\w\\W]{10})\\|([\\w\\W]{10})", regexGenerator.localPattern(
        definitionLoader.getDefinition()).pattern());
  }

  public void testHierarchical() throws Exception {
    DefinitionLoader definitionLoader = new DefinitionLoader();
    definitionLoader.load(new InputStreamReader(RegexGeneratorTest.class.getResourceAsStream("/hierarchical.def.xsd")));

    RegexGenerator regexGenerator = new RegexGenerator();

    assertEquals(
        "(000)\\|([\\w\\W]{10})\\|([\\w\\W]{10})\\|([\\w\\W]{10})(\\n(A00)\\|([\\w\\W]{10})\\|([\\w\\W]{10})[ ]{11}(\\n(A01)[ ]{33}){1,1}){0,}(\\n(B01)\\|([\\w\\W]{8})[ ]{24}){1,1}",
        regexGenerator.deepPattern(definitionLoader.getDefinition()).pattern());

    assertEquals("(000)\\|([\\w\\W]{10})\\|([\\w\\W]{10})\\|([\\w\\W]{10})", regexGenerator.localPattern(
        definitionLoader.getDefinition()).pattern());

    assertEquals("(A00)\\|([\\w\\W]{10})\\|([\\w\\W]{10})[ ]{11}", regexGenerator.localPattern(
        definitionLoader.getDefinition().getSubRecords().get(0)).pattern());

    assertEquals("(A01)[ ]{33}", regexGenerator.localPattern(
        definitionLoader.getDefinition().getSubRecords().get(0).getSubRecords().get(0)).pattern());

    assertEquals("(B01)\\|([\\w\\W]{8})[ ]{24}", regexGenerator.localPattern(
        definitionLoader.getDefinition().getSubRecords().get(1)).pattern());
  }

  public void testMultiRow() throws Exception {
    DefinitionLoader definitionLoader = new DefinitionLoader();
    definitionLoader.load(new InputStreamReader(RegexGeneratorTest.class.getResourceAsStream("/multi-row.def.xsd")));

    RegexGenerator regexGenerator = new RegexGenerator();

    assertEquals(
        "([\\w\\W]{20})([\\w\\W]{20})([\\w\\W]{16})([\\w\\W]{8})([\\w\\W]{2})([\\w\\W]{3})\\n([\\w\\W]{20})([\\w\\W]{20})[ ]{29}",
        regexGenerator.deepPattern(definitionLoader.getDefinition()).pattern());

    assertEquals(
        "([\\w\\W]{20})([\\w\\W]{20})([\\w\\W]{16})([\\w\\W]{8})([\\w\\W]{2})([\\w\\W]{3})\\n([\\w\\W]{20})([\\w\\W]{20})[ ]{29}",
        regexGenerator.localPattern(definitionLoader.getDefinition()).pattern());
  }

  public void testSimple() throws Exception {
    DefinitionLoader definitionLoader = new DefinitionLoader();
    definitionLoader.load(new InputStreamReader(RegexGeneratorTest.class.getResourceAsStream("/simple.def.xsd")));

    RegexGenerator regexGenerator = new RegexGenerator();

    assertEquals("([\\w\\W]{20})([\\w\\W]{20})([\\w\\W]{16})([\\w\\W]{8})([\\w\\W]{2})([\\w\\W]{3})[ ]{31}",
        regexGenerator.deepPattern(definitionLoader.getDefinition()).pattern());

    assertEquals("([\\w\\W]{20})([\\w\\W]{20})([\\w\\W]{16})([\\w\\W]{8})([\\w\\W]{2})([\\w\\W]{3})[ ]{31}",
        regexGenerator.localPattern(definitionLoader.getDefinition()).pattern());
  }

}
