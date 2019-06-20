package com.track.toy.test.core.asserts;

import java.util.List;

public enum TestAssertType implements ITestAssertJudge {
    AND {
        @Override
        public boolean judge(Object source, Object target) {
            return true;
        }
    },
    OR {
        @Override
        public boolean judge(Object source, Object target) {
            return true;
        }
    },
    GTE {
        @Override
        public boolean judge(Object source, Object target) {
            return ((Comparable) source).compareTo((Comparable) target) >= 0;
        }
    },
    GT {
        @Override
        public boolean judge(Object source, Object target) {
            return ((Comparable) source).compareTo((Comparable) target) > 0;
        }
    },
    LTE {
        @Override
        public boolean judge(Object source, Object target) {
            return ((Comparable) source).compareTo((Comparable) target) <= 0;
        }
    },
    LT {
        @Override
        public boolean judge(Object source, Object target) {
            return ((Comparable) source).compareTo((Comparable) target) < 0;
        }
    },
    NOT {
        public boolean judge(Object source, Object target) {
            if (source == null) {
                return target == null;
            }
            return !source.equals(target);
        }
    },
    CONTAINS {
        @Override
        public boolean judge(Object source, Object target) {
            return ((List) source).contains(target);
        }
    },
    NOT_NULL {
        @Override
        public boolean judge(Object source, Object target) {
            return source != null;
        }
    },
    IS_NUMBER {
        @Override
        public boolean judge(Object source, Object target) {
            if (source == null) {
                return false;
            }
            try {
                Double.valueOf(source.toString());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    },

    //=============================================================================
    ;

    public static TestAssertType getFromType(String type) {
        if (type == null) {
            throw new RuntimeException("TestAssertType is null");
        }
        TestAssertType[] types = TestAssertType.values();
        for (TestAssertType assertType : types) {
            if (assertType.name().toLowerCase().equals(type.toLowerCase())) {
                return assertType;
            }
        }
        throw new RuntimeException("TestAssertType not found , type = " + type);
    }
}
