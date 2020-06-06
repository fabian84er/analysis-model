package edu.hm.hafner.analysis;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests the class {@link LineRangeList}.
 *
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings({"PMD", "all"})
//CHECKSTYLE:OFF
class LineRangeListTest {
    @Test
    void shouldStoreBigValues() {
        LineRangeList list = new LineRangeList();
        LineRange range = new LineRangeBuilder().setLineRange(1350, Integer.MAX_VALUE).build();
        list.add(range);
        assertThat(list).containsExactly(range);
    }

    @Test
    void shouldStoreRangeWithOneLines() {
        LineRangeList list = new LineRangeList();
        LineRange range = new LineRangeBuilder().setSingleLine(0).build();
        list.add(range);
        assertThat(list).containsExactly(range);
    }

    @Test
    void shouldStoreRangeWithTwoLines() {
        LineRangeList list = new LineRangeList();
        LineRange range = new LineRangeBuilder().setLineRange(128, 129).build();
        list.add(range);
        assertThat(list).containsExactly(range);
    }

    @Test
    void shouldSupportSetOperations() {
        LineRangeList list = new LineRangeList();
        LineRange range = new LineRangeBuilder().setLineRange(1, 2).build();
        list.add(range);

        assertThat(list.get(0)).isEqualTo(range);
        assertThat(list.get(0)).isNotSameAs(range);
        assertThat(list).hasSize(1);

        LineRange other = new LineRangeBuilder().setLineRange(3, 4).build();
        assertThat(list.set(0, other)).isEqualTo(range);
        assertThat(list.get(0)).isEqualTo(other);
        assertThat(list.get(0)).isNotSameAs(other);
        assertThat(list).hasSize(1);

        assertThat(list.remove(0)).isEqualTo(other);
        assertThat(list).hasSize(0);
    }

    /** Tests the internal buffer resize operation. */
    @Test
    void shouldResizeCorrectly() {
        LineRangeList list = new LineRangeList();
        for (int i = 0; i < 100; i++) {
            list.add(new LineRangeBuilder().setLineRange(i * 2, i * 2 + 1).build());
        }
        list.trim();
        assertThat(list).hasSize(100);

        for (int i = 0; i < 100; i++) {
            assertThat(list.get(i)).isEqualTo(new LineRangeBuilder().setLineRange(i * 2, i * 2 + 1).build());
            assertThat(list.contains(new LineRangeBuilder().setLineRange(i * 2, i * 2 + 1).build())).isTrue();
        }

        assertThat(list).hasSize(100);
    }

    @Test
    void shouldProvideContains() {
        LineRangeList last = createThreeElements();
        last.remove(new LineRangeBuilder().setLineRange(4, 5).build());
        assertThat(last).containsExactly(new LineRangeBuilder().setLineRange(0, 1).build(), new LineRangeBuilder().setLineRange(2, 3).build());

        LineRangeList middle = createThreeElements();
        middle.remove(new LineRangeBuilder().setLineRange(2, 3).build());
        assertThat(middle).containsExactly(new LineRangeBuilder().setLineRange(0, 1).build(), new LineRangeBuilder().setLineRange(4, 5).build());

        LineRangeList first = createThreeElements();
        assertThat(first.contains(new LineRange(0, 1))).isTrue();
        assertThat(first.contains(new LineRange(2, 3))).isTrue();
        assertThat(first.contains(new LineRange(4, 5))).isTrue();

        first.remove(new LineRange(0, 1));
        assertThat(first).containsExactly(new LineRangeBuilder().setLineRange(2, 3).build(), new LineRangeBuilder().setLineRange(4, 5).build());

        assertThat(first.contains(new LineRangeBuilder().setLineRange(2, 3).build())).isTrue();
        assertThat(first.contains(new LineRangeBuilder().setLineRange(0, 1).build())).isFalse();
    }
    @Test
    void shouldStoreNegativeValuesZero() {
        LineRange sut = new LineRangeBuilder().setLineRange(-1, Integer.MAX_VALUE).build();

        assertThat(sut.getStart()).isEqualTo(0);
    }
    @Test
    void shouldStoreBiggerEndValuesChnage() {
        LineRange sut = new LineRangeBuilder().setLineRange(125, 100).build();

        assertThat(sut.getStart()).isEqualTo(100);
        assertThat(sut.getEnd()).isEqualTo(125);
    }
    @Test
    void shouldStoreSingleLine() {
        LineRange sut = new LineRangeBuilder().setSingleLine(125).build();

        assertThat(sut.getStart()).isEqualTo(125);
        assertThat(sut.getEnd()).isEqualTo(125);
    }

    private LineRangeList createThreeElements() {
        LineRangeList range = new LineRangeList();
        range.add(new LineRangeBuilder().setLineRange(0, 1).build());
        range.add(new LineRangeBuilder().setLineRange(2, 3).build());
        range.add(new LineRangeBuilder().setLineRange(4, 5).build());
        assertThat(range).containsExactly(new LineRangeBuilder().setLineRange(0, 1).build(),
                new LineRangeBuilder().setLineRange(2, 3).build(),
                new LineRangeBuilder().setLineRange(4, 5).build());
        return range;
    }
}
