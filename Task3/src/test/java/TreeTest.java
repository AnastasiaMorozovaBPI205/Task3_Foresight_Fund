import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class TreeTest {
    @Test
    void buildTreeTest1() throws IOException {
        Tree tree = new Tree("классификатор1", "классификатор2");
        tree.buildTree("test1.xlsx");

        assertEquals(Arrays.stream((new String[]{"имя1", "имя4", "имя3", "имя2"})).toList(), tree.getChildren());

        assertEquals(Arrays.stream((new String[]{"имя1", "имя4"})).toList(),
                tree.getChildren(new Tree.StringPair("классификатор1", "класс11"),
                        new Tree.StringPair("классификатор2", "класс21")));

        assertEquals(new ArrayList<String>(),
                tree.getChildren(new Tree.StringPair("классификатор1", "класс12"),
                        new Tree.StringPair("классификатор2", "класс22")));

        assertEquals(Arrays.stream((new String[]{"имя1", "имя4", "имя3"})).toList(),
                tree.getChildren(new Tree.StringPair("классификатор1", "класс11")));
    }

    @Test
    void buildTreeTest2() throws IOException {
        Tree tree = new Tree("классификатор2", "классификатор1");
        tree.buildTree("test1.xlsx");

        assertEquals(Arrays.stream((new String[]{"имя1", "имя4", "имя2", "имя3"})).toList(), tree.getChildren());
    }

    @Test
    void buildTreeTest3() throws IOException {
        Tree tree = new Tree("классификатор1", "классификатор2", "классификатор3", "классификатор4");
        tree.buildTree("test2.xlsx");

        assertEquals(Arrays.stream((new String[]{"имя1", "имя2"})).toList(),
                tree.getChildren(new Tree.StringPair("классификатор2", "класс21"),
                        new Tree.StringPair("классификатор4", "класс43")));
    }
}