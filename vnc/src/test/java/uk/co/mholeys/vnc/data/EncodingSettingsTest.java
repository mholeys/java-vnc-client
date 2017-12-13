package src.uk.co.mholeys.vnc.data;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import uk.co.mholeys.vnc.data.Encoding;
import uk.co.mholeys.vnc.data.EncodingSettings;

public class EncodingSettingsTest {

	@Test
	public void testAddSingleToEmpty() {
		EncodingSettings es = new EncodingSettings();
		es.addEncoding(Encoding.TIGHT_ENCODING);
		
		assertEquals(es.getEncodings().get(0), Encoding.TIGHT_ENCODING);
	}
	
	@Test
	public void testAddMultipleToEmpty() {
		EncodingSettings es = new EncodingSettings();
		es.addEncoding(Encoding.TIGHT_ENCODING);
		es.addEncoding(Encoding.ZLIB_ENCODING);
		es.addEncoding(Encoding.COPY_RECT_ENCODING);
		es.addEncoding(Encoding.COMPRESSION_LEVEL_0_PSEUDO_ENCODING);
		
		assertEquals(es.getEncodings().get(0), Encoding.TIGHT_ENCODING);
		assertEquals(es.getEncodings().get(1), Encoding.ZLIB_ENCODING);
		assertEquals(es.getEncodings().get(2), Encoding.COPY_RECT_ENCODING);
		assertEquals(es.getEncodings().get(3), Encoding.COMPRESSION_LEVEL_0_PSEUDO_ENCODING);
	}
	
	@Test
	public void testAddArrayToEmpty() {
		EncodingSettings es = new EncodingSettings();
		ArrayList<Encoding> eList = new ArrayList<Encoding>();
		
		eList.add(Encoding.APPLE1_ENCODING);
		eList.add(Encoding.JPEG_QUALITY_LEVEL_1_PSEUDO_ENCODING);
		eList.add(Encoding.OLIVE_CALL_CONTROL_ENCODING);
		
		es.addEncoding(eList);		
		
		assertEquals(es.getEncodings().get(0), Encoding.APPLE1_ENCODING);
		assertEquals(es.getEncodings().get(1), Encoding.JPEG_QUALITY_LEVEL_1_PSEUDO_ENCODING);
		assertEquals(es.getEncodings().get(2), Encoding.OLIVE_CALL_CONTROL_ENCODING);
	}
	
	@Test
	public void testAddSingleToPrexisting() {
		EncodingSettings es = new EncodingSettings();
		es.addEncoding(Encoding.COMPRESSION_LEVEL_0_PSEUDO_ENCODING);
		es.addEncoding(Encoding.COPY_RECT_ENCODING);
		
		assertEquals(es.getEncodings().get(0), Encoding.COMPRESSION_LEVEL_0_PSEUDO_ENCODING);
		assertEquals(es.getEncodings().get(1), Encoding.COPY_RECT_ENCODING);
		
		es.addEncoding(Encoding.APPLE1_ENCODING);
			
		
		assertEquals(es.getEncodings().get(0), Encoding.COMPRESSION_LEVEL_0_PSEUDO_ENCODING);
		assertEquals(es.getEncodings().get(1), Encoding.COPY_RECT_ENCODING);
		assertEquals(es.getEncodings().get(2), Encoding.APPLE1_ENCODING);
	}
	
	@Test
	public void testAddMultipleToPrexisting() {
		EncodingSettings es = new EncodingSettings();
		es.addEncoding(Encoding.COMPRESSION_LEVEL_0_PSEUDO_ENCODING);
		es.addEncoding(Encoding.COPY_RECT_ENCODING);
		
		assertEquals(es.getEncodings().get(0), Encoding.COMPRESSION_LEVEL_0_PSEUDO_ENCODING);
		assertEquals(es.getEncodings().get(1), Encoding.COPY_RECT_ENCODING);
		
		es.addEncoding(Encoding.APPLE1_ENCODING);
		es.addEncoding(Encoding.JPEG_QUALITY_LEVEL_1_PSEUDO_ENCODING);
			
		
		assertEquals(es.getEncodings().get(0), Encoding.COMPRESSION_LEVEL_0_PSEUDO_ENCODING);
		assertEquals(es.getEncodings().get(1), Encoding.COPY_RECT_ENCODING);
		assertEquals(es.getEncodings().get(2), Encoding.APPLE1_ENCODING);
		assertEquals(es.getEncodings().get(3), Encoding.JPEG_QUALITY_LEVEL_1_PSEUDO_ENCODING);
	}
	
	@Test
	public void testAddArrayToPrexisting() {
		EncodingSettings es = new EncodingSettings();
		es.addEncoding(Encoding.COMPRESSION_LEVEL_0_PSEUDO_ENCODING);
		es.addEncoding(Encoding.COPY_RECT_ENCODING);
		
		ArrayList<Encoding> eList = new ArrayList<Encoding>();
		
		eList.add(Encoding.APPLE1_ENCODING);
		eList.add(Encoding.JPEG_QUALITY_LEVEL_1_PSEUDO_ENCODING);
		eList.add(Encoding.OLIVE_CALL_CONTROL_ENCODING);
		
		es.addEncoding(eList);		
		
		assertEquals(es.getEncodings().get(0), Encoding.COMPRESSION_LEVEL_0_PSEUDO_ENCODING);
		assertEquals(es.getEncodings().get(1), Encoding.COPY_RECT_ENCODING);
		assertEquals(es.getEncodings().get(2), Encoding.APPLE1_ENCODING);
		assertEquals(es.getEncodings().get(3), Encoding.JPEG_QUALITY_LEVEL_1_PSEUDO_ENCODING);
		assertEquals(es.getEncodings().get(4), Encoding.OLIVE_CALL_CONTROL_ENCODING);
	}
	
	@Test
	public void testInitallyEmpty() {
		EncodingSettings es = new EncodingSettings();
		
		assertTrue(es.getEncodings().isEmpty());
	}
	

}
