package edu.mit.mitmobile2.people.model;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

public enum MITPersonAttribute {
    EMAIL("email"),
    PHONE("phone"),
    FAX("fax"),
    HOMEPHONE("home"),
    OFFICE("office"),
    ADDRESS("address"),
    WEBSITE("website");

    private final String keyName;
    private final String getterName;
    private final String setterName;

    MITPersonAttribute(String keyName) {
        this.keyName = keyName;
        this.getterName = "get" + Character.toUpperCase(keyName.charAt(0)) + keyName.substring(1);
        this.setterName = "set" + Character.toUpperCase(keyName.charAt(0)) + keyName.substring(1);
    }

    public String getSetterName() {
        return setterName;
    }

    public String getGetterName() {
        return getterName;
    }

    public String getKeyName() {
        return keyName;
    }

    public Object invokeGetterOn(MITPerson person) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return (Object) person.getClass().getMethod(this.getterName).invoke(person);
    }

    public static MITPersonAttribute[] getAttributesOn(MITPerson person) {
        List<MITPersonAttribute> attrs = new LinkedList<>();

        for (MITPersonAttribute attr : values()) {
            Object val = null;
            try {
                val = attr.invokeGetterOn(person);
            } catch (NoSuchMethodException e) {
                Log.e(MITPersonAttribute.class.getSimpleName(), "METHOD EXCEPTION ON "+person, e);
            } catch (InvocationTargetException e) {
                Log.e(MITPersonAttribute.class.getSimpleName(), "TARGET EXCEPTION ON " + person, e);
            } catch (IllegalAccessException e) {
                Log.e(MITPersonAttribute.class.getSimpleName(), "ACCESS EXCEPTION ON " + person, e);
            }

            boolean valid = val != null && (
                (val instanceof CharSequence && !TextUtils.isEmpty((CharSequence) val)) ||
                (val instanceof List && ((List) val).size() > 0)
            );

            if (valid) {
                attrs.add(attr);
            }
        }

        if (attrs.size() > 0) {
            return attrs.toArray(new MITPersonAttribute[attrs.size()]);
        }

        return new MITPersonAttribute[0];
    }

    @Override
    public String toString() {
        return "MITPersonAttribute{" +
                "keyName='" + keyName + '\'' +
                ", getterName='" + getterName + '\'' +
                ", setterName='" + setterName + '\'' +
                '}';
    }
}
