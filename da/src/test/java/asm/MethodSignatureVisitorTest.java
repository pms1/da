package asm;

import org.junit.Test;
import org.objectweb.asm.signature.SignatureReader;

public class MethodSignatureVisitorTest {
	@Test
	public void test1() {

		MethodSignatureVisitor v = new MethodSignatureVisitor();
		new SignatureReader("()TV;^TX;").accept(new DumpSignatureVisitor(""));
		new SignatureReader("()TV;^TX;").accept(v);
		System.out.println(v.getResult());
	}
}
