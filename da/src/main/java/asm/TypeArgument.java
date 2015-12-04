package asm;

public class TypeArgument
{

    JavaType type;

    Character wildchar;

    @Override
    public String toString()
    {
        return "TypeArgument " + type + " " + wildchar;
    }

    public String asJava()
    {
        StringBuilder b = new StringBuilder();
        if (wildchar == null)
        {
            if (type != null)
                throw new Error();
            b.append("?");
        }
        else if (wildchar == '=')
        {
            b.append(type.asJava());
        }
        else if (wildchar == '-')
        {
            b.append("? super ").append(type.asJava());
        }
        else if (wildchar == '+')
        {
            b.append("? extends ").append(type.asJava());
        }
        else
        {
            throw new Error(">" + wildchar + "<");
        }
        return b.toString();
    }

    /**
     * @return the type
     */
    public JavaType getType()
    {
        return type;
    }

    /**
     * @return the wildchar
     */
    public Character getWildchar()
    {
        return wildchar;
    }
}