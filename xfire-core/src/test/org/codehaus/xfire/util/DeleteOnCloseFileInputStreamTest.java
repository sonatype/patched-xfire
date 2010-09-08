package org.codehaus.xfire.util;

import java.io.File;

import junit.framework.TestCase;

public class DeleteOnCloseFileInputStreamTest extends TestCase {
	public void testIt() throws Exception {
		String property = System.getProperty("java.io.tmpdir");
		File tempFile = new File(property, getClass().getName());
		assertTrue(tempFile.createNewFile());
		DeleteOnCloseFileInputStream is = new DeleteOnCloseFileInputStream(tempFile);
		is.close();
		assertFalse("file was not deleted.", tempFile.exists());
	}
}
