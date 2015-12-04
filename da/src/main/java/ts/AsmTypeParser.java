package ts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;

import com.github.da.TypeUtil;

import asm.ArrayType;
import asm.BaseType;
import asm.ClassSignature;
import asm.ClassSignatureVisitor;
import asm.ClassType;
import asm.JavaType;
import asm.MethodSignature;
import asm.MethodSignatureVisitor;
import asm.ParameterizedType;
import asm.RawType;
import asm.TypeSignatureVisitor;
import asm.TypeVariable;

public class AsmTypeParser {

	public static ClassSignature parseClassSignature(String name, String signature, String superName,
			String[] interfaces) {

		if (signature != null) {
			ClassSignatureVisitor v = new ClassSignatureVisitor();
			new SignatureReader(signature).accept(v);
			ClassSignature cs = v.getResult();

			if (!(cs.getSuperclass() instanceof ParameterizedType))
				throw new Error();

			if (true) {
				if (!((ParameterizedType) cs.getSuperclass()).getRawType().equals(Type.getObjectType(superName))) {
					throw new Error();
				}

				for (int i = 0; i != interfaces.length; ++i) {
					if (!((ParameterizedType) cs.getInterfaces().get(i)).getRawType()
							.equals(Type.getObjectType(interfaces[i]))) {
						throw new Error();
					}
				}

			}

			return cs;
		} else {
			List<ClassType> xinterfaces = new ArrayList<>(interfaces.length);
			for (String inter : interfaces)
				xinterfaces.add(new RawType(Type.getObjectType(inter)));
			xinterfaces = Collections.unmodifiableList(xinterfaces);

			return new ClassSignature(Collections.emptyList(), new RawType(Type.getObjectType(superName)), xinterfaces);
		}
	}

	public static JavaType parseFieldSignature(String desc, String signature) {

		if (signature != null) {
			TypeSignatureVisitor v = new TypeSignatureVisitor();
			new SignatureReader(signature).acceptType(v);
			JavaType result = v.getResult();

			if (true)
				if (result instanceof ParameterizedType) {
					if (!((ParameterizedType) result).getRawType().equals(Type.getType(desc)))
						throw new Error();
				} else if (result instanceof ArrayType) {
				} else if (result instanceof TypeVariable) {
				} else
					throw new Error(desc + " " + signature + " " + result);

			return result;
		} else {
			return convert(Type.getType(desc));
		}

	}

	static JavaType convert(Type t) {
		switch (t.getSort()) {
		case Type.ARRAY:
			return new ArrayType(convert(TypeUtil.contentType(t)));
		case Type.METHOD:
			throw new Error();
		case Type.VOID:
			return null;
		case Type.OBJECT:
			return new RawType(t);
		default:
			if (t.getDescriptor().length() != 1)
				throw new Error();
			return new BaseType(t.getDescriptor().charAt(0));
		}
	}

	public static MethodSignature parseMethodSignature(String desc, String signature, String[] exceptions) {
		if (signature != null) {
			MethodSignatureVisitor v = new MethodSignatureVisitor();
			new SignatureReader(signature).accept(v);
			return v.getResult();
		} else {
			Type method = Type.getMethodType(desc);

			List<JavaType> parameters = new LinkedList<>();
			for (Type t : method.getArgumentTypes())
				parameters.add(convert(t));

			List<JavaType> exceptionTypes = new LinkedList<>();
			if (exceptions != null)
				for (String t : exceptions)
					exceptionTypes.add(new RawType(Type.getObjectType(t)));

			return new MethodSignature(Collections.emptyList(), parameters, convert(method.getReturnType()),
					exceptionTypes);
		}
	}
}
