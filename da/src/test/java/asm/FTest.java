package asm;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;

public class FTest {
	public List<List<?>> l1;

	static public class X<P1> {
		class Y {

		}

		public Y y;
	}

	@Test
	public void t1() throws Exception {
		Type type = X.class.getField("y").getGenericType();
		System.err.println("T " + type + " " + type.getClass());
		ParameterizedType tt = (ParameterizedType) type;
		System.err.println(Arrays.toString(tt.getActualTypeArguments()));

		new ClassReader(X.class.getName()).accept(new ClassVisitor(Opcodes.ASM5) {
			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {

				// ClassSignatureVisitor v = new ClassSignatureVisitor();
				// new SignatureReader(signature).accept(v);
				// ClassSignature cs = v.getResult();
				//
				// System.err.println(cs);
			}

			@Override
			public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
				System.err.println("S=" + signature);

				if (signature != null) {
					TypeSignatureVisitor v = new TypeSignatureVisitor();
					new SignatureReader(signature).acceptType(v);
					JavaType cs = v.getResult();
					System.err.println("S=" + cs);
					System.err.println("S=" + ((ParameterizedType) cs).getRawType());

					org.objectweb.asm.Type tt = org.objectweb.asm.Type.getType(desc);
					System.err.println("S=" + tt);
					new RawType(tt);
				}
				return null;
			}
		}, 0);
	}
}
