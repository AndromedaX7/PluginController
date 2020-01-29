package com.me.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;


public class ClassParser {
    byte[] code;
    int codeLength = -1;
    int codeIndex = -1;
    int constantsCount = 0;
    int currentConstants = 0;
    RandomAccessFile raf;
    private String superClassName;
    private ArrayList<ConstantPoolRef> constantPool = new ArrayList<>();


    public ClassParser(File file ) throws IOException {
        raf = new RandomAccessFile(file, "rwd");
        FileInputStream fin = new FileInputStream(file);
        int available = fin.available();
        code = new byte[available];
        fin.read(code);
        fin.close();
        codeLength = code.length;
    }


    public void parse() {
        try {
            codeIndex = 0;
            String magic = getRange(4);
            if (magic.equals("cafebabe")) {//is java byte code
                System.out.println("I am a class file !");
                System.out.println("minor:" + getLong(2));
                System.out.println("major:" + getLong(2));
                constantsCount = (int) getLong(2);
                System.out.println("constants pool count " + constantsCount);
                while (currentConstants < constantsCount - 1) {
                    currentConstants++;
                    byte next = next();
                    ConstantPoolRef ref = new ConstantPoolRef(next, new ConstantPoolRef.ConstantsSetter() {
                        @Override
                        public String getElement(int len) {
                            return getRange(len);
                        }

                        @Override
                        public String getString(int len) {
                            return ClassParser.this.getString(len);
                        }

                        @Override
                        public int getNumber(int len) {
                            return (int) getLong(len);
                        }
                    });
                    constantPool.add(ref);


                }
                long access = getLong(2);
                long thisClass = getLong(2);
                long superClass = getLong(2);
                System.out.println("access:" + Long
                        .toHexString(access) + "  thisClass:" + thisClass + "  superClass:" + superClass);
                System.out.println();

                ConstantPoolRef poolRef = constantPool.get((int) (superClass - 1));
                ConstantPoolRef item =constantPool.get((int) (poolRef.arg1-1));
                System.out.println("superClass::"+ item.element);

                if (item.element.equals("androidx/appcompat/app/AppCompatActivity")){
                    superClassName="com/me/pluginlib/activity/PluginAppCompatActivity";
                }else if (item.element.equals("android/content/BroadcastReceiver")){
                    superClassName="com/me/pluginlib/receiver/PluginReceiver";
                }else if (item.element.equals("android/app/Service")){
                    superClassName="com/me/pluginlib/service/PluginService";
                }else {
                    return;
                }

                System.out.println( "------- start transform class ------- ");
                System.out.println("modify class:"+ constantPool.get((int) ( constantPool.get((int) (thisClass - 1)).arg1-1)).element);

                byte[] new_magic = new byte[8];

                for (int i = 0; i < new_magic.length; i++) {
                    new_magic[i] = code[i];
                }


                int cCount = constantsCount + 1;

                int byte1 = 0;
                int byte2 = cCount & 0xff;
                if (cCount > 255) {
                    byte1 = cCount >> 8 & 0xff;
                }

                byte[] constantsCount = new byte[]{
                        (byte) byte1, (byte) byte2
                };

                ArrayList<byte[]> bCache = new ArrayList<>();
                for (int i = 0; i < constantPool.size(); i++) {
                    byte[] bytes = constantPool.get(i).toBytes();
                    bCache.add(bytes);
                }


                byte itemAppend2 = (byte) (this.constantsCount & 0xff);
                byte itemAppend1 = (byte) (this.constantsCount >> 8 & 0xff);
                byte[] bytes = bCache.get((int) superClass - 1);
                bytes[1] = itemAppend1;
                bytes[2] = itemAppend2;
                bCache.set((int) superClass - 1, bytes);

                byte[] utf_8content = superClassName.getBytes();

                int itemAppendUtf2 = utf_8content.length & 0xff;
                int itemAppendUtf1 = utf_8content.length >> 8 & 0xff;


                byte[] utf_8head = new byte[3 + utf_8content.length];
                utf_8head[0] = 1;
                utf_8head[1] = (byte) itemAppendUtf1;
                utf_8head[2] = (byte) itemAppendUtf2;

                for (int i = 0; i < utf_8content.length; i++) {
                    utf_8head[3 + i] = utf_8content[i];
                }


                byte access2 = (byte) (access & 0xff);
                byte access1 = (byte) (access >> 8 & 0xff);
                byte thisClassByte2 = (byte) (thisClass & 0xff);
                byte thisClassByte1 = (byte) (thisClass >> 8 & 0xff);
                byte superClassByte2 = (byte) (superClass & 0xff);
                byte superClassByte1 = (byte) (superClass >> 8 & 0xff);
                byte[] accessFlag = new byte[]{
                        access1, access2, thisClassByte1, thisClassByte2, superClassByte1, superClassByte2
                };

                byte[] end = new byte[code.length - codeIndex];
                for (int i = 0; i < end.length; i++) {
                    end[i] = code[i + codeIndex];
                }
                raf.write(new_magic);
                raf.write(constantsCount);
                for (int i = 0; i < bCache.size(); i++) {
                    raf.write(bCache.get(i));
                }

                raf.write(utf_8head);
                raf.write(accessFlag);
                raf.write(end);
                raf.close();

                System.out.println( "------- end transform class ------- ");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String trans(byte current) {
        return Integer.toHexString(current & 0xff);
    }


    private boolean hasNext() {
        return codeIndex + 1 < codeLength;
    }

    private byte next() {
        if (codeIndex + 1 < codeLength) {
            byte c = code[codeIndex];
            codeIndex++;
            return c;
        }
        return -1;
    }

    private int toSign(byte current) {
        return current & 0xff;
    }

    private String getRange(int len) {
        StringBuffer buff = new StringBuffer();
        for (int i = codeIndex; i < codeIndex + len; i++) {
            buff.append(trans(code[i]));
        }
        codeIndex += len;
        return buff.toString();
    }

    private String getString(int len) {
        byte[] tmp = new byte[len];
        for (int i = 0; i < len; i++) {
            tmp[i] = code[codeIndex + i];
        }
        codeIndex += len;
        return new String(tmp);
    }

    private long getLong(int len) {
        if (len <= 0) {
            throw new IllegalArgumentException("len mast > 0 ,but len is" + len);
        }
        long num = 0;
        if (len > 1) {

            String range = getRange(len);
            num = Integer.parseInt(range, 16);
            return num;
        } else {
            num = Integer.parseInt(getRange(len));
            return num;
        }
    }


    public static class ConstantPoolRef {
        Type type;
        String element = "";
        long arg1 = 0;
        long arg2 = 0;
        private ConstantsSetter setter;

        public ConstantPoolRef(int code, ConstantsSetter setter) {
            this.setter = setter;
            this.type = gen(code);
            System.out.println("Type:" + type.name() + "  args1:" + arg1 + "  args2:" + arg2 + "  utf-8:" + element);
        }

        private Type gen(int code) {
            switch (code) {
                case 1:
                    int len = setter.getNumber(2);
                    element = setter.getString(len);
                    return Type.UTF_8;
                case 3:
                    arg1 = setter.getNumber(4);
                    return Type.INTEGER;
                case 4:
                    arg1 = setter.getNumber(4);
                    return Type.FLOAT;
                case 5:
                    arg1 = setter.getNumber(8);
                    return Type.LONG;
                case 6:
                    arg1 = setter.getNumber(8);
                    return Type.DOUBLE;
                case 7:
                    arg1 = setter.getNumber(2);
                    return Type.CLASS;
                case 8:
                    arg1 = setter.getNumber(2);
                    return Type.STRING;
                case 9:
                    arg1 = setter.getNumber(2);
                    arg2 = setter.getNumber(2);
                    return Type.FIELD_REF;
                case 10:
                    arg1 = setter.getNumber(2);
                    arg2 = setter.getNumber(2);
                    return Type.METHOD_REF;
                case 11:
                    arg1 = setter.getNumber(2);
                    arg2 = setter.getNumber(2);
                    return Type.INTERFACE_METHOD_REF;
                case 12:
                    arg1 = setter.getNumber(2);
                    arg2 = setter.getNumber(2);
                    return Type.NAME_AND_TYPE;
                default:
                    return Type.UNKNOWN;
            }

        }


        private byte[] toBytes() {
            byte[] bytes;
            byte _byte1 = 0;
            byte _byte2 = 0;
            byte _byte3 = 0;
            byte _byte4 = 0;
            byte _byte5 = 0;
            byte _byte6 = 0;
            byte _byte7 = 0;
            byte _byte8 = 0;
            switch (type) {
                case UTF_8:
                    byte[] utf_8content = element.getBytes();
                    int length = utf_8content.length;
                    int byte1 = 0;
                    int byte2 = length & 0xff;
                    if (length > 255) {
                        byte1 = length >> 8 & 0xff;
                    }
                    byte[] utf_8head = new byte[3 + length];
                    System.arraycopy(utf_8content, 0, utf_8head, 3, utf_8head.length - 3);

                    utf_8head[0] = 1;
                    utf_8head[1] = (byte) byte1;
                    utf_8head[2] = (byte) byte2;

                    return utf_8head;
                case INTEGER:
                    _byte4 = (byte) (arg1 & 0xff);
                    _byte3 = (byte) (arg1 >> 8 & 0xff);
                    _byte2 = (byte) (arg1 >> 16 & 0xff);
                    _byte1 = (byte) (arg1 >> 24 & 0xff);

                    return new byte[]{
                            3, _byte1, _byte2, _byte3, _byte4
                    };
                case FLOAT:
                    _byte4 = (byte) (arg1 & 0xff);
                    _byte3 = (byte) (arg1 >> 8 & 0xff);
                    _byte2 = (byte) (arg1 >> 16 & 0xff);
                    _byte1 = (byte) (arg1 >> 24 & 0xff);

                    return new byte[]{
                            4, _byte1, _byte2, _byte3, _byte4
                    };
                case LONG:
                    _byte8 = (byte) (arg1 & 0xff);
                    _byte7 = (byte) (arg1 >> 8 & 0xff);
                    _byte6 = (byte) (arg1 >> 16 & 0xff);
                    _byte5 = (byte) (arg1 >> 24 & 0xff);
                    _byte4 = (byte) (arg1 >> 32 & 0xff);
                    _byte3 = (byte) (arg1 >> 40 & 0xff);
                    _byte2 = (byte) (arg1 >> 48 & 0xff);
                    _byte1 = (byte) (arg1 >> 56 & 0xff);

                    return new byte[]{
                            5, _byte1, _byte2, _byte3, _byte4,
                            _byte5, _byte6, _byte7, _byte8
                    };
                case DOUBLE:
                    _byte8 = (byte) (arg1 & 0xff);
                    _byte7 = (byte) (arg1 >> 8 & 0xff);
                    _byte6 = (byte) (arg1 >> 16 & 0xff);
                    _byte5 = (byte) (arg1 >> 24 & 0xff);
                    _byte4 = (byte) (arg1 >> 32 & 0xff);
                    _byte3 = (byte) (arg1 >> 40 & 0xff);
                    _byte2 = (byte) (arg1 >> 48 & 0xff);
                    _byte1 = (byte) (arg1 >> 56 & 0xff);

                    return new byte[]{
                            6, _byte1, _byte2, _byte3, _byte4,
                            _byte5, _byte6, _byte7, _byte8
                    };
                case CLASS:
                    _byte8 = (byte) (arg1 & 0xff);
                    _byte7 = (byte) (arg1 >> 8 & 0xff);
                    return new byte[]{
                            7, _byte7, _byte8
                    };
                case STRING:
                    _byte8 = (byte) (arg1 & 0xff);
                    _byte7 = (byte) (arg1 >> 8 & 0xff);
                    return new byte[]{
                            8, _byte7, _byte8
                    };
                case FIELD_REF:
                    _byte8 = (byte) (arg2 & 0xff);
                    _byte7 = (byte) (arg2 >> 8 & 0xff);

                    _byte6 = (byte) (arg1 & 0xff);
                    _byte5 = (byte) (arg1 >> 8 & 0xff);

                    return new byte[]{
                            9, _byte5, _byte6, _byte7, _byte8
                    };
                case METHOD_REF:
                    _byte8 = (byte) (arg2 & 0xff);
                    _byte7 = (byte) (arg2 >> 8 & 0xff);

                    _byte6 = (byte) (arg1 & 0xff);
                    _byte5 = (byte) (arg1 >> 8 & 0xff);

                    return new byte[]{
                            10, _byte5, _byte6, _byte7, _byte8
                    };
                case INTERFACE_METHOD_REF:
                    _byte8 = (byte) (arg2 & 0xff);
                    _byte7 = (byte) (arg2 >> 8 & 0xff);

                    _byte6 = (byte) (arg1 & 0xff);
                    _byte5 = (byte) (arg1 >> 8 & 0xff);

                    return new byte[]{
                            11, _byte5, _byte6, _byte7, _byte8
                    };
                case NAME_AND_TYPE:
                    _byte8 = (byte) (arg2 & 0xff);
                    _byte7 = (byte) (arg2 >> 8 & 0xff);

                    _byte6 = (byte) (arg1 & 0xff);
                    _byte5 = (byte) (arg1 >> 8 & 0xff);

                    return new byte[]{
                            12, _byte5, _byte6, _byte7, _byte8
                    };
                default:
                    return new byte[0];

            }

        }

        enum TypeRef {
            INDEX,
            STRING
        }


        enum Type {
            UNKNOWN,
            UTF_8,
            INTEGER, FLOAT, LONG, DOUBLE, CLASS, STRING, FIELD_REF, METHOD_REF, INTERFACE_METHOD_REF, NAME_AND_TYPE,
        }

        public interface ConstantsSetter {
            String getElement(int len);

            String getString(int len);

            int getNumber(int len);
        }
    }
}
