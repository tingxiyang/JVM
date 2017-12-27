package runtimedata.heap;

import classfile.attribute.CodeAttribute;
import classfile.MemberInfo;

import java.util.ArrayList;

/**
 * Author: zhangxin
 * Time: 2017/5/19 0019.
 * Desc: 方法的抽象,是在class中定义的方法,包括静态的和非静态的
 */
public class Zmethod extends ClassMember {
    private int maxStack;
    private int maxLocals;
    private byte[] code;        //如果没有code属性,取值为null;不过就算是空方法也有一个return 语句;
    private MethodDescriptor parsedDescriptor;  //解析后的方法描述符
    private int argSlotCount;   //方法所需的参数个数;对于非静态方法,至少是1个(this)

    private Zmethod(Zclass clazz, MemberInfo classFileMethod) {
        super(clazz, classFileMethod);
        copyAttributes(classFileMethod);
        parsedDescriptor = new MethodDescriptor(this.descriptor);
        argSlotCount = calcArgSlotCount(parsedDescriptor.getParameterTypes());
    }

    //该方法用来初始化成员变量：maxStack，maxLocals，code
    private void copyAttributes(MemberInfo classFileMethod) {
        CodeAttribute codeAttribute = classFileMethod.getCodeAttribute();
        if (codeAttribute != null) {
            maxStack = codeAttribute.getMaxStack();
            maxLocals = codeAttribute.getMaxLocals();
            code = codeAttribute.getCode();
        }
    }

    private int calcArgSlotCount(ArrayList<String> args) {
        int slotCount = 0;
        //如果当前方法不是静态方法，那么其第一个参数为 ‘this’引用
        if (!isStatic()) {
            slotCount++;
        }
        for (String arg : args) {
            slotCount++;
            if ("J".equals(arg) || "D".equals(arg)) {
                slotCount++;
            }
        }
        return slotCount;
    }

    public static Zmethod[] makeMethods(Zclass zclass, MemberInfo[] classFileMethods) {
        Zmethod[] methods = new Zmethod[classFileMethods.length];
        for (int i = 0; i < methods.length; i++) {
            Zmethod method = new Zmethod(zclass, classFileMethods[i]);
            methods[i] = method;
        }
        return methods;
    }


    public boolean isSynchronized() {
        return 0 != (accessFlags & AccessFlag.ACC_SYNCHRONIZED);
    }

    public boolean isBridge() {
        return 0 != (accessFlags & AccessFlag.ACC_BRIDGE);
    }

    public boolean isVarargs() {
        return 0 != (accessFlags & AccessFlag.ACC_VARARGS);
    }

    public boolean isNative() {
        return 0 != (accessFlags & AccessFlag.ACC_NATIVE);
    }

    public boolean isAbstract() {
        return 0 != (accessFlags & AccessFlag.ACC_ABSTRACT);
    }

    public boolean isStrict() {
        return 0 != (accessFlags & AccessFlag.ACC_STRICT);
    }

    public int getMaxStack() {
        return maxStack;
    }

    public int getMaxLocals() {
        return maxLocals;
    }

    public byte[] getCode() {
        return code;
    }

    public int getArgSlotCount() {
        return argSlotCount;
    }
}
