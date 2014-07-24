/*
 * JRecordBind, fixed-length file (un)marshaller
 * Copyright 2009, Assist s.r.l., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package it.assist.jrecordbind.test;

import static org.junit.Assert.*;
import it.assist.jrecordbind.Marshaller;
import it.assist_si.schemas.jrb.short_prop_name.ShortPropNameRecord;

import java.io.InputStreamReader;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

public class ShortPropertyNameRecordMarshallTest {

  private Marshaller<ShortPropNameRecord> marshaller;
  private ShortPropNameRecord record;
  private StringWriter stringWriter;

  @Test
  public void marshallOne() throws Exception {
    marshaller.marshall(record, stringWriter);

    assertEquals("A NAME    \n", stringWriter.toString());

    assertEquals(11, stringWriter.toString().length());
  }

  @Before
  public void setUp() throws Exception {
    record = new ShortPropNameRecord();
    record.setAString("A NAME");

    marshaller = new Marshaller<ShortPropNameRecord>(Marshaller.class.getClassLoader(), new InputStreamReader(ShortPropertyNameRecordMarshallTest.class
        .getResourceAsStream("/ShortPropertyName.def.xsd")));

    stringWriter = new StringWriter();
  }
}
