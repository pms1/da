package asm;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.Type;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

public class InnerClass
{

    Type name;

    List < TypeArgument > typeArguments = new LinkedList <>();

    @Override
    public String toString()
    {
        return "InnerClass " + name + " " + typeArguments;
    }

    String asJava()
    {
        StringBuilder b = new StringBuilder();
        b.append(name);
        if (typeArguments.size() != 0)
        {
            b.append("<");
            Joiner.on(",").appendTo(b, Collections2.transform(typeArguments, new Function < TypeArgument, String >()
            {

                @Override
                public String apply(TypeArgument input)
                {
                    return input.asJava();
                }

            }));
            b.append(">");
        }
        return b.toString();
    }
}