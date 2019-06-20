package com.track.toy.test.core.asserts;

import com.track.toy.test.core.node.TestNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Data
public class GroupTestAssert extends TestAssert {
    public static final GroupTestAssert DEFAULT_TRUE_ASSERT = new GroupTestAssert();

    private boolean isAnd = true;
    private List<TestAssert> children = new ArrayList<>();

    //组合断言，有或和且的选择
    public final boolean asserts(TestNode testNode) {
        if (children.isEmpty()) {
            return true;
        }
        Stream<TestAssert> stream = children.stream();
        Predicate<TestAssert> predicate = child -> child.asserts(testNode);
        this.isSuccess = isAnd ? stream.allMatch(predicate) : stream.anyMatch(predicate);
        return this.isSuccess;
    }

    public void addChild(TestAssert testAssert) {
        children.add(testAssert);
    }

}
