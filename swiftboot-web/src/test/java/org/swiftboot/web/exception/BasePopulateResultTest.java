package org.swiftboot.web.exception;

import org.junit.jupiter.api.Test;
import org.swiftboot.web.model.entity.BaseEntity;
import org.swiftboot.web.result.BasePopulateResult;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Allen 2019-03-18
 **/
public class BasePopulateResultTest {

    @Test
    public void test() {

        BasePopulateResult<TestEntity> result = new TestResult();

        TestEntity entity = new TestEntity();
        entity.setName("测试实体类");
        TestItemEntity entityItem1 = new TestItemEntity();
        TestItemEntity entityItem2 = new TestItemEntity();
        entityItem1.setName("实体类子项1");
        entityItem2.setName("实体类子项2");
        entity.getItems().add(entityItem1);
        entity.getItems().add(entityItem2);
        // populate
        ((TestResult) result).setItems(new HashSet<>());// 初始化空的集合
        result.populateByEntity(entity);
        System.out.println(entity.getName());
        for (TestItemResult item : ((TestResult) result).getItems()) {
            System.out.println(item.getName());
        }

    }

    public static class TestEntity extends BaseEntity {
        @Column
        String name;

        @OneToMany
        Set<TestItemEntity> items = new HashSet<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<TestItemEntity> getItems() {
            return items;
        }

        public void setItems(Set<TestItemEntity> items) {
            this.items = items;
        }
    }

    public static class TestItemEntity extends BaseEntity {
        @Column
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class TestResult extends BasePopulateResult<TestEntity> {
        String name;

        Set<TestItemResult> items;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<TestItemResult> getItems() {
            return items;
        }

        public void setItems(Set<TestItemResult> items) {
            this.items = items;
        }
    }

    public static class TestItemResult extends BasePopulateResult<TestItemEntity> {
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}